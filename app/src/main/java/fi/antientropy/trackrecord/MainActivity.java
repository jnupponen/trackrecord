package fi.antientropy.trackrecord;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.List;

import fi.antientropy.trackrecord.domain.Project;
import fi.antientropy.trackrecord.persistence.Datasource;


public class MainActivity extends AppCompatActivity {

    private static final String REMOVED_TIMERS_NOTIFICATION = "Removed timers!";
    public static final String WRITE_TIMER_NAME_PROMPT = "Write timer name";
    public static final String ADD_BUTTON_TEXT = "Add";
    public static final String CANCEL_BUTTON_TEXT = "Cancel";
    public static final String CONFIRM_BUTTON_TEXT = "Confirm";
    public static final String ARE_YOU_SURE_PROMPT = "Are you sure";
    public static final int CLEAR_DATABASE_ACTION = 0;
    public static final int RESET_TIMERS_ACTION = 1;

    private final Handler mHandler = new Handler();
    private Thread mUpdater = null;
    private ArrayAdapter<Project> adapter;
    private MainTimer mainTimer;
    private Datasource datasource;

    public static final PeriodFormatter HOURS_MINUTES_SECONDS = new PeriodFormatterBuilder()
            .printZeroAlways()
            .minimumPrintedDigits(2)
            .appendHours()
            .appendSeparator(":")
            .appendMinutes()
            .appendSeparator(":")
            .appendSeconds()
            .toFormatter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        JodaTimeAndroid.init(this);

        datasource = new Datasource(this);
        mainTimer = new MainTimer(datasource);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.remove_timers:
                showAreYouSureDialog(CLEAR_DATABASE_ACTION);
                return true;
            case R.id.add_timer:
                addItem();
                return true;
            case R.id.clear_timers:
                showAreYouSureDialog(RESET_TIMERS_ACTION);
                return true;
            case R.id.show_db:
                start_activity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAreYouSureDialog(final int action) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(ARE_YOU_SURE_PROMPT);
        alert.setPositiveButton(CONFIRM_BUTTON_TEXT, (dialog, whichButton) -> {
            if(action == CLEAR_DATABASE_ACTION) {
                clearDatabase();
            }
            else if(action == RESET_TIMERS_ACTION) {
                reset();
            }
        });

        alert.setNegativeButton(CANCEL_BUTTON_TEXT, (dialog, whichButton) -> {
                // Canceled.
        });

        alert.create().show();
    }

    private void start_activity() {
        Intent intent = new Intent(this, DatabaseDebugActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        datasource.open();
        adapter = new ProjectList(this, datasource);
        ListView listview = (ListView) findViewById(R.id.list);
        listview.setAdapter(adapter);

        start();
    }
    @Override
    public void onStop() {
        stop();

        datasource.close();

        super.onStop();
    }

    private void start() {
        if(mUpdater == null) {
            mUpdater = new Thread(new MyRunnable());
            mUpdater.start();
        }
    }

    private void stop() {
        if(mUpdater != null) {
            mUpdater.interrupt();
            mUpdater = null;
        }
    }

    private void updateTime(String time) {
        TextView durationText = (TextView) findViewById(R.id.tunnit);
        durationText.setText(time);
    }

    public void reset() {
        if(datasource.getCount() > 0) {
            // Clear timers.
            for(int i = 0; i < adapter.getCount(); ++i) {
                adapter.getItem(i).setDuration("0");
                adapter.getItem(i).setStart("");
                adapter.getItem(i).setActive(false);
                datasource.update(adapter.getItem(i));

            }

            adapter.clear();
            adapter.addAll(datasource.getProjects());
        }
        updateTime(mainTimer.getTime());
    }

    public void clearDatabase() {
        try {
            datasource.clear();
            reset();
            adapter.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), REMOVED_TIMERS_NOTIFICATION,Toast.LENGTH_SHORT).show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Add to db.
    public void addItem() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(WRITE_TIMER_NAME_PROMPT);

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton(ADD_BUTTON_TEXT, (dialog, whichButton) -> {
            String value = input.getText().toString();
            Project project = new Project(value);
            Project addedModel = datasource.persist(project);
            adapter.insert(addedModel, adapter.getCount());
            adapter.notifyDataSetChanged();
        });

        alert.setNegativeButton(CANCEL_BUTTON_TEXT, (dialog, whichButton) -> { });

        final AlertDialog dialog = alert.create();

        input.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        dialog.show();
    }


    public class MyRunnable implements Runnable {
        private boolean mStop = true;
        @Override
        public void run() {
            while (mStop) {
                try {
                    mHandler.post(() -> {
                        // Update UI.
                        updateTime(mainTimer.getTime());
                        adapter.notifyDataSetChanged();
                    });
                    Thread.sleep(250);
                } catch (Exception e) {
                    mStop = !mStop;
                }
            }
        }
    }

}