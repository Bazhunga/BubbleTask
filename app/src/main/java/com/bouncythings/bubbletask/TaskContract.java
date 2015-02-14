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
        public static final String COLUMN_TASK_TITLE            = "title";
        public static final String COLUMN_TASK_DESC             = "description";
        public static final String COLUMN_TASK_PRIORITY         = "priority";
        public static final String COLUMN_TASK_DUEDATE          = "duedate";
        public static final String COLUMN_TASK_COMPLETE_STAT    = "iscomplete";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TaskEntry.TABLE_NAME + " {" +
            TaskEntry.COLUMN_TASK_PROJECT + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_TASK_TITLE + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_TASK_DESC + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_TASK_PRIORITY + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_TASK_DUEDATE + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_TASK_COMPLETE_STAT + TEXT_TYPE + COMMA_SEP +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

}