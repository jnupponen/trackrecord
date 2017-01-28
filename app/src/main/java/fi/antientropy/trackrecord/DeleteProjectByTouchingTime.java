package fi.antientropy.trackrecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;

import fi.antientropy.trackrecord.domain.Project;
import fi.antientropy.trackrecord.persistence.Datasource;

/**
 * Created by jussi on 28/01/17.
 */

public class DeleteProjectByTouchingTime implements View.OnTouchListener {
    private static final String DELETE_PROJECT_TEXT = "Delete";
    private static final String DELETE_PROJECT_OK_BUTTON_TEXT = "Ok";
    private static final String DELETE_PROJECT_CANCEL_BUTTON_TEXT = "Cancel";

    private ProjectList.ViewHolder viewHolder;
    private Datasource datasource;
    private ProjectList projectList;
    private Activity context;
    public DeleteProjectByTouchingTime(ProjectList.ViewHolder viewHolder, Datasource datasource, ProjectList projectList, Activity context) {
        this.viewHolder = viewHolder;
        this.datasource = datasource;
        this.projectList = projectList;
        this.context = context;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Project element = (Project) viewHolder.time.getTag();
        deleteProject(element);

        return false;
    }

    private void deleteProject(final Project project) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(DELETE_PROJECT_TEXT);
        alert.setMessage(project.getName());

        alert.setPositiveButton(DELETE_PROJECT_OK_BUTTON_TEXT, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                datasource.delete(project);
                projectList.remove(project);

                projectList.notifyDataSetChanged();
            }
        });

        alert.setNegativeButton(DELETE_PROJECT_CANCEL_BUTTON_TEXT, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
}