package com.bouncythings.bubbletask;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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


    Context ctxt = this;
    boolean refreshFlag = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);
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


        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mPager);

        //DATABASING
        readDatabase(); //Read the database



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

        return super.onOptionsItemSelected(item);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    public class TaskListPagerAdapter extends FragmentStatePagerAdapter {

        public TaskListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return projectList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return new TaskListSliderFragment();
        }

        @Override
        public int getCount() {
            return projectList.size();
        }
    }

    public void newTask(View view){
        CharSequence msg = "Creating New Task";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(ctxt, msg, duration);
        toast.show();

        final int currentProject = mPager.getCurrentItem();

        DialogFragment dialog = new NewTaskDialog();
        Bundle data = new Bundle();
        data.putStringArrayList("project_list", projectList);
        data.putInt("current_project_index", currentProject);
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
        //Find the current item index (int)
        final int currentProject = mPager.getCurrentItem();

        CharSequence msg = "Delete this project: " + String.valueOf(currentProject);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(ctxt, msg, duration);
        toast.show();

        new AlertDialog.Builder(ctxt)
                .setTitle("Delete Project")
                .setMessage("Are you sure you want to delete this project? All your tasks will be lost")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Reset the projectList and populate it again
                        projectList.remove(currentProject);

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
                        String project = prefs.getString(getString(R.string.project_list_prefs), "[{'project':'Misc'}]");
                        JSONArray jsonArray;

                        try{
                            jsonArray = new JSONArray(project);
                            jsonArray.remove(currentProject);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(getString(R.string.project_list_prefs), jsonArray.toString());
                            editor.commit();
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

//                        finish();
//                        startActivity(getIntent());
//                        overridePendingTransition(android.R.anim.fade_out,android.R.anim.fade_in);
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

    public void readDatabase(){

        TaskDbHelper dbHelper = new TaskDbHelper(this);
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
        int currentItemIndex = mPager.getCurrentItem();
        String currentItemString = projectList.get(currentItemIndex);
        String [] selectionArgs = {currentItemString}; //CURRENT PROJECT
        Log.d("Projection", String.valueOf(projection.toString()));
        Log.d("currenItemString", String.valueOf(currentItemString));

        Cursor cursor_projectlist = dbTask.query (
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
        int projectCount = cursor_projectlist.getCount();

        while (!cursor_projectlist.isAfterLast()){
            int id, task_isCompleted, task_priority;
            long task_date;
            String task_project, task_title, task_notes;
            id = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry._ID));
            task_isCompleted = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_COMPLETE_STAT));
            task_priority = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_PRIORITY));
            task_date = cursor_projectlist.getLong(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DUEDATE));
            task_project = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_PROJECT)) ;
            task_title = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_TITLE));
            task_notes = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DESC));
            CharSequence msg = "Number of entries: " + projectCount +
                    "Priority: " + task_priority + "\r\n"
                    + "PName: " + task_project + "\r\n"
                    + "TName: " + task_title + "\r\n"
                    + "Ldate: " + task_date + "\r\n"
                    + "TNotes: " + task_notes;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, msg, duration);
            toast.show();
            cursor_projectlist.moveToNext();
        }


        cursor_projectlist.close();
        dbTask.close();

    }
}
