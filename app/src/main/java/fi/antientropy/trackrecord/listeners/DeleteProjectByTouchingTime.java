package fi.antientropy.trackrecord.listeners;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;

import fi.antientropy.trackrecord.ProjectList;
import fi.antientropy.trackrecord.domain.Project;
import fi.antientropy.trackrecord.persistence.Datasource;

/**
 * Created by jussi on 28/01/17.
 */

public class DeleteProjectByTouchingTime implements View.OnTouchListener {
    private static final String DELETE_PROJECT_TEXT = "Delete";
    private static final String DELETE_PROJECT_OK_BUTTON_TEXT = "Ok";
    private static final String DELETE_PROJECT_CANCEL_BUTTON_TEXT = "Cancel";

    private Datasource datasource;
    private ProjectList projectList;

    public DeleteProjectByTouchingTime(Datasource datasource, ProjectList projectList) {
        this.datasource = datasource;
        this.projectList = projectList;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Project project = (Project) v.getTag();
        AlertDialog.Builder alert = new AlertDialog.Builder(projectList.getContext());

        alert.setTitle(DELETE_PROJECT_TEXT);
        alert.setMessage(project.getName());

        alert.setPositiveButton(DELETE_PROJECT_OK_BUTTON_TEXT, (dialog, whichButton) -> {
            datasource.delete(project);
            projectList.remove(project);
            projectList.notifyDataSetChanged();
        });

        alert.setNegativeButton(DELETE_PROJECT_CANCEL_BUTTON_TEXT, (dialog, whichButton) -> {});

        alert.show();

        return false;
    }
}