package fi.antientropy.trackrecord;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.List;

import fi.antientropy.trackrecord.domain.Project;
import fi.antientropy.trackrecord.persistence.Datasource;

/**
 * Created by jussi on 24/01/17.
 */

public class ChangeNameByClickingName implements View.OnClickListener {

    private Datasource datasource;
    private ProjectList projectList;
    private List<Project> list;

    public ChangeNameByClickingName(Datasource datasource, ProjectList projectList, List<Project> list) {
        this.datasource = datasource;
        this.projectList = projectList;
        this.list = list;
    }

    @Override
    public void onClick(View v) {
        final Project projectToUpdate = (Project) v.getTag();
        final String projectName = projectToUpdate.getName();

        AlertDialog.Builder alert = new AlertDialog.Builder(projectList.getContext());

        alert.setTitle(MainActivity.WRITE_TIMER_NAME_PROMPT);

        // Set an EditText view to get user input
        final EditText input = new EditText(projectList.getContext());
        input.setText(projectName);
        alert.setView(input);

        alert.setPositiveButton(MainActivity.ADD_BUTTON_TEXT, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();

                if (!projectName.contentEquals(value) && !value.trim().isEmpty()) {
                    Project update = new Project(projectToUpdate.getId(), value,
                            projectToUpdate.getStart(), 0, projectToUpdate.getDuration());
                    update.setActive(projectToUpdate.isActive());

                    Project addedModel = datasource.update(update);
                    projectList.notifyDataSetChanged();
                    list.set(projectList.getPosition(projectToUpdate), addedModel);
                }
            }
        });

        alert.setNegativeButton(MainActivity.CANCEL_BUTTON_TEXT, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        final AlertDialog dialog = alert.create();

        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        dialog.show();
    }
}
