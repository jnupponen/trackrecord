package fi.antientropy.trackrecord;

import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;

import fi.antientropy.trackrecord.domain.Project;
import fi.antientropy.trackrecord.persistence.Datasource;

/**
 * Created by jussi on 28/01/17.
 */

public class ToggleTimerByTouchingIcon implements View.OnTouchListener {

    private ProjectList.ViewHolder viewHolder;
    private Datasource datasource;
    private ProjectList projectList;
    public ToggleTimerByTouchingIcon(ProjectList.ViewHolder viewHolder, Datasource datasource, ProjectList projectList) {
        this.viewHolder = viewHolder;
        this.datasource = datasource;
        this.projectList = projectList;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Project element = (Project) viewHolder.image.getTag();
        Project activeElement = datasource.getActiveProject();

        if(element.isActive()) {
            element.stopTimer();
            datasource.update(element);
        }
        else {
            if(activeElement != null) {
                activeElement.stopTimer();
                datasource.update(activeElement);
            }

            element.startTimer();
            datasource.update(element);
        }

        projectList.notifyDataSetChanged();
        projectList.clear();
        projectList.addAll(datasource.getProjects());

        return false;
    }
}