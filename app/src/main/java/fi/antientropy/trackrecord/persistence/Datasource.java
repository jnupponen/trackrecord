package fi.antientropy.trackrecord.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fi.antientropy.trackrecord.domain.Project;

import static fi.antientropy.trackrecord.persistence.SQLiteUtils.COLUMN_ACTIVE;
import static fi.antientropy.trackrecord.persistence.SQLiteUtils.COLUMN_DURATION;
import static fi.antientropy.trackrecord.persistence.SQLiteUtils.COLUMN_ID;
import static fi.antientropy.trackrecord.persistence.SQLiteUtils.COLUMN_NAME;
import static fi.antientropy.trackrecord.persistence.SQLiteUtils.COLUMN_START;
import static fi.antientropy.trackrecord.persistence.SQLiteUtils.TABLE;

/**
 * Created by jussi on 2/17/15.
 */
public class Datasource {

    // Database fields
    private SQLiteDatabase database;
    private final SQLiteUtils dbHelper;
    private final String[] allColumns = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_START,
            COLUMN_ACTIVE,
            COLUMN_DURATION};


    public Datasource(Context context) {
        dbHelper = new SQLiteUtils(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void clear() {
        database.delete(SQLiteUtils.TABLE, null, null);

    }

    public Project persist(Project model) {

        ContentValues dbValues = new ContentValues();
        dbValues.put(COLUMN_NAME, model.getName());
        dbValues.put(COLUMN_START, model.getStart());
        dbValues.put(COLUMN_ACTIVE, model.isActive());
        dbValues.put(COLUMN_DURATION, model.getDuration());

        long insertId = database.insert(SQLiteUtils.TABLE, null,
                dbValues);

        Cursor cursor = database.query(SQLiteUtils.TABLE,
                allColumns, SQLiteUtils.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Project value = cursorToValue(cursor);
        cursor.close();

        return value;
    }

    public Project update(Project model) {

        ContentValues dbValues = new ContentValues();
        dbValues.put(COLUMN_NAME, model.getName());
        dbValues.put(COLUMN_START, model.getStart());
        dbValues.put(COLUMN_ACTIVE, model.isActive());
        dbValues.put(COLUMN_DURATION, model.getDuration());

        long updated = database.update(TABLE,dbValues, COLUMN_ID+"="+model.getId(),null);

        Project value = null;

        if(updated == 1) {
            Cursor cursor = database.query(SQLiteUtils.TABLE,
                    allColumns, SQLiteUtils.COLUMN_ID + " = " + model.getId(), null,
                    null, null, null);
            cursor.moveToFirst();
            value = cursorToValue(cursor);
            cursor.close();
        }

        return value;
    }

    public boolean delete(Project model) {

        int success = database.delete(TABLE,COLUMN_ID+"="+model.getId(),null);

        return success == 1;
    }

    public List<Project> getProjects() {

        List<Project> projects = new ArrayList<>();

        Cursor cursor = database.query(SQLiteUtils.TABLE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Project project = cursorToValue(cursor);

            projects.add(project);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return projects;
    }

    public Project getActiveProject() {

        Cursor cursor = database.query(SQLiteUtils.TABLE,
                allColumns, COLUMN_ACTIVE + "= 1", null, null, null, null);

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();

            Project project = cursorToValue(cursor);

            // make sure to close the cursor
            cursor.close();
            return project;
        }

        return null;
    }

    public int getCount() {

        Cursor cursor = database.rawQuery("select count(distinct "+COLUMN_ID+") from "+TABLE,null);

        cursor.moveToFirst();
        int count = cursor.getInt(0);

        // make sure to close the cursor
        cursor.close();

        return count;
    }

    private Project cursorToValue(Cursor cursor) {
        return new Project(
                cursor.getLong(0), cursor.getString(1),
                cursor.getString(2), cursor.getInt(3),
                cursor.getString(4)
        );
    }
}
