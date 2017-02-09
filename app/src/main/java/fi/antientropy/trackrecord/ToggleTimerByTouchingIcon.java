package fi.antientropy.trackrecord;

import android.view.MotionEvent;
import android.view.View;

import fi.antientropy.trackrecord.domain.Project;
import fi.antientropy.trackrecord.persistence.Datasource;

/**
 * Created by jussi on 28/01/17.
 */

public class ToggleTimerByTouchingIcon implements View.OnTouchListener {

    private Datasource datasource;
    private ProjectList projectList;
    public ToggleTimerByTouchingIcon(Datasource datasource, ProjectList projectList) {
        this.datasource = datasource;
        this.projectList = projectList;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Project project = (Project) v.getTag();
        Project activeElement = datasource.getActiveProject();

        if(project.isActive()) {
            project.stopTimer();
            datasource.update(project);
        }
        else {
            if(activeElement != null) {
                activeElement.stopTimer();
                datasource.update(activeElement);
            }

            project.startTimer();
            datasource.update(project);
        }

        projectList.notifyDataSetChanged();
        projectList.clear();
        projectList.addAll(datasource.getProjects());

        return false;
    }
}