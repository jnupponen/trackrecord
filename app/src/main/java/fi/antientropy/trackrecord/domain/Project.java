package fi.antientropy.trackrecord.domain;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.ISODateTimeFormat;

import fi.antientropy.trackrecord.MainActivity;

/**
 * Created by jussi on 2/17/15.
 */
public class Project {

    private final long id;
    private String name;
    private String start;
    private int isActive;
    private String duration;

    public Project(String name) {
        this(-1, name, "", 0, "0");
    }

    public Project(long id, String name, String start, int isActive, String duration) {

        this.id = id;
        this.name = name;
        this.start = start;
        this.isActive = isActive;
        this.duration = duration;
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public boolean isActive() {
        return isActive != 0;
    }

    public void setActive(boolean isActive) {
        if(isActive) {
            this.isActive = 1;
        }
        else {
            this.isActive = 0;
        }
    }

    public String getDuration() {
        return duration;
    }

    public String getPrintDuration() {

        DateTime now = DateTime.now();

        String startString = getStart();
        if(startString.isEmpty()) {
            startString = now.toString(ISODateTimeFormat.dateHourMinuteSecondMillis());
        }

        long lastDuration = Integer.parseInt(getDuration());
        DateTime startTime = DateTime.parse(startString);
        Interval interval = new Interval(startTime, now);

        Period period = interval.toDuration().plus(lastDuration).toPeriod();

        return period.toString(MainActivity.HOURS_MINUTES_SECONDS);
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Id: " +getId() + ",\nName: " + getName() +",\nStart: " +  getStart() + ",\nDuration:" +  getDuration() + ",\nActive: " +  isActive();
    }

    public void stopTimer() {
        setActive(false);
        long lastDuration = Integer.parseInt(getDuration());

        DateTime startTime = DateTime.parse(getStart());
        DateTime now = DateTime.now();
        Interval interval = new Interval(startTime, now);

        setDuration(String.valueOf(interval.toDurationMillis() + lastDuration));
        setStart("");
    }

    public void startTimer() {
        setActive(true);
        DateTime now = DateTime.now();
        setStart(now.toString(ISODateTimeFormat.dateHourMinuteSecondMillis()));
    }
}
