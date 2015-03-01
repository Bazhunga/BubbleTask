package com.bouncythings.bubbletask;

import android.provider.BaseColumns;

/**
 * Created by kevin on 2/14/15.
 */

//NTS: Specifies layout of schema in systematic and self-documenting way
//Container for constants defining names for URIs, tables and columns
public class TaskContract {
    public TaskContract(){}

    public static abstract class TaskEntry implements BaseColumns{
        public static final String TABLE_NAME                   = "Tasks";
        public static final String COLUMN_TASK_PROJECT          = "project";
        public static final String COLUMN_TASK_TITLE            = "taskname";
        public static final String COLUMN_TASK_DESC             = "notes";
        public static final String COLUMN_TASK_PRIORITY         = "priority";
        public static final String COLUMN_TASK_DUEDATE          = "duedate";
        public static final String COLUMN_TASK_COMPLETE_STAT    = "iscomplete";
        public static final String COLUMN_NAME_NULLABLE         = "";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String SQL_DATE = " INTEGER";
    private static final String SQL_INT = " INTEGER";
    private static final String SQL_BOOL = " INTEGER";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TaskEntry.TABLE_NAME + " (" +
            TaskEntry._ID + " INTEGER PRIMARY KEY," +
            TaskEntry.COLUMN_TASK_PROJECT + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_TASK_TITLE + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_TASK_DESC + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_TASK_PRIORITY + SQL_INT + COMMA_SEP +
            TaskEntry.COLUMN_TASK_DUEDATE + SQL_DATE + COMMA_SEP +
            TaskEntry.COLUMN_TASK_COMPLETE_STAT + SQL_BOOL +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

}