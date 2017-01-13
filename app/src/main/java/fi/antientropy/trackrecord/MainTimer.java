package fi.antientropy.trackrecord;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.List;

import fi.antientropy.trackrecord.domain.Project;
import fi.antientropy.trackrecord.persistence.Datasource;

/**
 * Created by jussi on 2/18/15.
 */
public class MainTimer {

    private final Datasource datasource;

    public MainTimer(Datasource datasource) {
        this.datasource = datasource;
    }

    public String getTime() {
        List<Project> projects = datasource.getProjects();
        Duration duration = new Duration(0);
        for(Project project : projects) {
            if(project.isActive()) {
                DateTime startTime = DateTime.parse(project.getStart());
                Duration interval = new Duration(startTime, DateTime.now());
                duration = duration.plus(interval);
            }

            duration = duration.plus(Long.valueOf(project.getDuration()));
        }

        return duration.toPeriod().toString(MainActivity.HOURS_MINUTES_SECONDS);
    }
}
