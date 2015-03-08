package com.bouncythings.bubbletask;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeList extends ActionBarActivity implements NewProjectDialog.NewProjectDialogListener {
    public ArrayList<String> projectList = new ArrayList<String>();
    public static ArrayList<TaskBall> taskBallList = new ArrayList<TaskBall>();

    TaskBall_Manager taskball_manager = new TaskBall_Manager();
    public static int currentProjectIndex;

    TaskListSliderFragment task_list_view;


    Context ctxt = this;
    boolean refreshFlag = false;

    //Used to draw circles for bubble visualization
    public static int maxWidth;
    public static int maxHeight;



    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    PagerSlidingTabStrip tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);

        //Setup Tab Strip Content
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        String project = prefs.getString(getString(R.string.project_list_prefs), "[{'project':'Misc'}]");
        JSONArray jsonArray = new JSONArray();

        try{
            jsonArray = new JSONArray(project);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String projectString = jsonObject.getString("project");
                projectList.add(projectString);
            }


        } catch (JSONException e){
            e.printStackTrace();
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new TaskListPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        //mPager.setOffscreenPageLimit(2);

        currentProjectIndex = mPager.getCurrentItem();

        //Getting Display size data
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        maxWidth = size.x;
        maxHeight = size.y;

        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mPager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentProjectIndex = mPager.getCurrentItem();
                if (task_list_view != null){
                    task_list_view.switchFragments();
                }

                //readDatabase();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //DATABASING
        TaskDbHelper mDbHelper = new TaskDbHelper(this);
        SQLiteDatabase dbTask = mDbHelper.getWritableDatabase();
        mDbHelper.createDatabase(dbTask);

        //readDatabase(); //Read the database
        new LoadDatabaseTasks_complete().execute();

        //Set Up ListView

    }
    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    public class TaskListPagerAdapter extends FragmentStatePagerAdapter {
        //FragmentManager fman;
        public TaskListPagerAdapter(FragmentManager fm) {
            super(fm);
            //fman = fm;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return projectList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            task_list_view = new TaskListSliderFragment();
            return task_list_view;
        }

        @Override
        public int getCount() {
            return projectList.size();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.clear_data){
            //Drop the database
            //Instantiate the database to put information into it
            TaskDbHelper mDbHelper = new TaskDbHelper(this);
            SQLiteDatabase dbTask = mDbHelper.getWritableDatabase();
            mDbHelper.dropDatabase(dbTask);
            mDbHelper.createDatabase(dbTask);

        }

        return super.onOptionsItemSelected(item);
    }

    public void newTask(View view){
        CharSequence msg = "Creating New Task";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(ctxt, msg, duration);
        toast.show();

        DialogFragment dialog = new NewTaskDialog();
        Bundle data = new Bundle();
        data.putStringArrayList("project_list", projectList);
        data.putInt("current_project_index", currentProjectIndex);
        dialog.setArguments(data);
        dialog.show(getFragmentManager(), "NewTaskDialog");

    }
    public void newProject(View view){
        CharSequence msg = "Creating New Project";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(ctxt, msg, duration);
        toast.show();
        DialogFragment dialog = new NewProjectDialog();
        dialog.show(getFragmentManager(), "NewProjectDialog");

    }

    public void deleteProject(View view){
        //TODO: YOU NEED TO DELETE ALL THIS FROM THE DATABASE AS WELL!!

        new AlertDialog.Builder(ctxt)
                .setTitle("Delete Project")
                .setMessage("Are you sure you want to delete this project? All your tasks will be lost")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Reset the projectList and populate it again
                        projectList.remove(currentProjectIndex);
                        //Remove the project from the static project_list
                        taskball_manager.removeProject_TaskBallList(currentProjectIndex);

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
                        String project = prefs.getString(getString(R.string.project_list_prefs), "[{'project':'Misc'}]");
                        JSONArray jsonArray;

                        try{
                            jsonArray = new JSONArray(project);
                            jsonArray.remove(currentProjectIndex);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(getString(R.string.project_list_prefs), jsonArray.toString());
                            editor.commit();
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        currentProjectIndex--; //Since we've deleted a project, we move to the previous page in viewpager,
                                               //which is 1 less than the current project index
                        mPagerAdapter.notifyDataSetChanged();


                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void bubbleIt(View view){
        Intent startBubbles = new Intent(this, Animated_Bubbles.class);
        startActivity(startBubbles);
    }

    @Override
    public void onReturnValue(boolean projectCreated){
        if (projectCreated == true) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
            String project = prefs.getString(getString(R.string.project_list_prefs), "[{'project':'Misc'}]");
            JSONArray jsonArray = new JSONArray();

            try {
                projectList.clear();
                jsonArray = new JSONArray(project);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String projectString = jsonObject.getString("project");
                    projectList.add(projectString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mPagerAdapter.notifyDataSetChanged();
        }
    }




    public void refreshProjectTasks(){
        new LoadProjectTasks().execute();

    }
    public class LoadDatabaseTasks_complete extends AsyncTask<Void, Void, Void> {
        TaskDbHelper dbHelper;
        Cursor cursor_projectlist;
        TaskBall_Manager tb_manager;
        @Override
        protected Void doInBackground(Void... params){
            tb_manager = new TaskBall_Manager();
            tb_manager.clearAll();

            if (projectList.size() != 0) {
                dbHelper = new TaskDbHelper(ctxt);
                SQLiteDatabase dbTask = dbHelper.getReadableDatabase();
                String[] projection = {
                        TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COLUMN_TASK_PROJECT,
                        TaskContract.TaskEntry.COLUMN_TASK_TITLE,
                        TaskContract.TaskEntry.COLUMN_TASK_DESC,
                        TaskContract.TaskEntry.COLUMN_TASK_PRIORITY,
                        TaskContract.TaskEntry.COLUMN_TASK_DUEDATE,
                        TaskContract.TaskEntry.COLUMN_TASK_COMPLETE_STAT
                };

                //Sorting order of the resulting cursor.
                //I want the entries to be sorted by the duedate, since that's the largest influence on urgency
                //Select everything in the database where the project name matches the current focused page

                String sortOrder = TaskContract.TaskEntry.COLUMN_TASK_DUEDATE + " DESC";
                String selection = TaskContract.TaskEntry.COLUMN_TASK_PROJECT + "=?";

                for (int index = 0; index < projectList.size(); index++) {
                    String currentItemString = projectList.get(index);
                    String[] selectionArgs = {currentItemString}; //CURRENT PROJECT, update this later to ONLY INCOMPLETE ITEMS

                    tb_manager.addProject_TaskBallList(currentItemString);

                    cursor_projectlist = dbTask.query(
                            TaskContract.TaskEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );

                    //Iterate through cursor and create objects based on
                    cursor_projectlist.moveToFirst();

                    while (!cursor_projectlist.isAfterLast()) {
                        int id, task_isCompleted, task_priority;
                        long task_date;
                        String task_project, task_name, task_notes;

                        //id = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry._ID));
                        task_name = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_TITLE));
                        task_project = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_PROJECT));
                        task_date = cursor_projectlist.getLong(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DUEDATE));
                        task_priority = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_PRIORITY));
                        task_notes = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DESC));
                        task_isCompleted = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_COMPLETE_STAT));

                        TaskBall taskBall = new TaskBall(task_name, task_project, task_date, task_priority, task_notes, task_isCompleted, ctxt);

                        tb_manager.addTaskBall(index, taskBall);
                        cursor_projectlist.moveToNext();


                    }

                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result){
            cursor_projectlist.close();
            dbHelper.close();
            //Update view
            if (task_list_view != null) {
                task_list_view.switchFragments();
            }

            Log.d("Objects", tb_manager.getListList().toString());
        }
    }

    public class LoadProjectTasks extends AsyncTask<Void, Void, Void> {
        TaskDbHelper dbHelper;
        Cursor cursor_projectlist;
        TaskBall_Manager tb_manager;
        @Override
        protected Void doInBackground(Void... params){
            tb_manager = new TaskBall_Manager();
            tb_manager.clearProjectTasks(currentProjectIndex);
            dbHelper = new TaskDbHelper(ctxt);

            SQLiteDatabase dbTask = dbHelper.getReadableDatabase();
            String[] projection = {
                    TaskContract.TaskEntry._ID,
                    TaskContract.TaskEntry.COLUMN_TASK_PROJECT,
                    TaskContract.TaskEntry.COLUMN_TASK_TITLE,
                    TaskContract.TaskEntry.COLUMN_TASK_DESC,
                    TaskContract.TaskEntry.COLUMN_TASK_PRIORITY,
                    TaskContract.TaskEntry.COLUMN_TASK_DUEDATE,
                    TaskContract.TaskEntry.COLUMN_TASK_COMPLETE_STAT
            };

            String sortOrder = TaskContract.TaskEntry.COLUMN_TASK_DUEDATE + " DESC";
            String selection = TaskContract.TaskEntry.COLUMN_TASK_PROJECT + "=?";

            String currentItemString = projectList.get(currentProjectIndex);
            String[] selectionArgs = {currentItemString}; //CURRENT PROJECT, update this later to ONLY INCOMPLETE ITEMS

            tb_manager.addProject_TaskBallList(currentItemString);

            cursor_projectlist = dbTask.query(
                    TaskContract.TaskEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            //Iterate through cursor and create objects based on
            cursor_projectlist.moveToFirst();

            while (!cursor_projectlist.isAfterLast()) {
                int id, task_isCompleted, task_priority;
                long task_date;
                String task_project, task_name, task_notes;

                task_name = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_TITLE));
                task_project = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_PROJECT));
                task_date = cursor_projectlist.getLong(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DUEDATE));
                task_priority = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_PRIORITY));
                task_notes = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DESC));
                task_isCompleted = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_COMPLETE_STAT));

                TaskBall taskBall = new TaskBall(task_name, task_project, task_date, task_priority, task_notes, task_isCompleted, ctxt);

                tb_manager.addTaskBall(currentProjectIndex, taskBall);
                cursor_projectlist.moveToNext();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            cursor_projectlist.close();
            dbHelper.close();
            //Update view
            if (task_list_view != null) {
                task_list_view.switchFragments();
            }

            Log.d("Objects", tb_manager.getListList().toString());
        }
    }

}
