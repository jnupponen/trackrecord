package fi.antientropy.trackrecord;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;

import fi.antientropy.trackrecord.domain.Project;
import fi.antientropy.trackrecord.persistence.Datasource;

/**
 * Created by jussi on 2/21/15.
 */
public class DatabaseDebugActivity extends ListActivity {

    private Datasource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_database_debug);

        datasource = new Datasource(this);
        datasource.open();

        ListView listview = (ListView) findViewById(android.R.id.list);
        ArrayList<Project> myList = (ArrayList)datasource.getProjects();
        ArrayAdapter<Project> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,myList);
        listview.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        datasource.close();
        super.onStop();
    }
}