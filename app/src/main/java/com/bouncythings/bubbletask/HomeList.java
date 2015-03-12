package com.bouncythings.bubbletask;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeList extends ActionBarActivity implements NewProjectDialog.NewProjectDialogListener {
    public ArrayList<String> projectList = new ArrayList<String>();
    public static ArrayList<TaskBall> taskBallList = new ArrayList<TaskBall>();

    TaskBall_Manager taskball_manager = new TaskBall_Manager();
    public static int currentProjectIndex;

    Context ctxt = this;

    //Used to draw circles for bubble visualization
    public static int maxWidth;
    public static int maxHeight;


    //Navigation drawer variables
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Homelist_Drawer_Adapter mDrawerAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable mDrawerArrow;

    //HomeList to do tasks listview setup
    private ListView lvTasks;
    TaskListSwipeAdapter lvTasksAdapter;

    //For Editing and Deleting of Tasks
    private int mDownPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);

        //NTS: WOW YOU NEED THIS TO DISPLAY THE STUPID 3 LINES
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //NTS: Need this to display the title
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //Get all project names
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

        currentProjectIndex = 0;
        if (projectList != null && projectList.size() > 0){
            setTitle(projectList.get(currentProjectIndex).substring(0, 1).toUpperCase() + projectList.get(currentProjectIndex).substring(1));
        }

        //Getting Display size data
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        maxWidth = size.x;
        maxHeight = size.y;

        //DATABASING
        TaskDbHelper mDbHelper = new TaskDbHelper(this);
        SQLiteDatabase dbTask = mDbHelper.getWritableDatabase();
        mDbHelper.createDatabase(dbTask);

        //readDatabase(); //Read the database
        new LoadDatabaseTasks_complete().execute();


        //Setup the navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.homelist_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.homelist_left_drawer);
        mDrawerAdapter = new Homelist_Drawer_Adapter(this, projectList);

        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setDividerHeight(1);
        ColorDrawable div_color = new ColorDrawable(this.getResources().getColor(R.color.black));
        mDrawerList.setDivider(div_color);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerArrow = new DrawerArrowDrawable(this){
            @Override
            public boolean isLayoutRtl(){
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close){
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

//        //Setup swipe button listeners
//        SwipeLayout sl_task_element = (SwipeLayout)findViewById(R.id.swipe_element);
//        sl_task_element.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mDownPosition = lvTasks.getPositionForView(v);
//                Log.d("POSITION", "is " + mDownPosition);
//                return false;
//            }
//        });


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
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
            new LoadDatabaseTasks_complete().execute();
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
                        //Remove the tasks from the database
                        TaskDbHelper dbHelper = new TaskDbHelper(ctxt);
                        SQLiteDatabase dbTask = dbHelper.getWritableDatabase();
                        ArrayList<TaskBall> taskball_list = taskball_manager.getProject_TaskBallList(currentProjectIndex);

                        for (int i = 0; i < taskball_list.size(); i++){
                            int id = taskball_list.get(i).getTaskid();
                            String selection = TaskContract.TaskEntry._ID + " LIKE ?";
                            String[] selectionArgs = {String.valueOf(id)};
                            dbTask.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                        }

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
                        if (currentProjectIndex >= projectList.size() && projectList.size() != 0) {
                            //Case of deleting the last element
                            currentProjectIndex--;
                            taskBallList = taskball_manager.getListList().get(currentProjectIndex);
                            setTitle(projectList.get(currentProjectIndex));
                        }
                        else if (projectList.size() == 0){
                            currentProjectIndex = 0;
                            taskBallList = new ArrayList<>();
                            setTitle("");
                        }
                        else{
                            taskBallList = taskball_manager.getListList().get(currentProjectIndex);
                            setTitle(projectList.get(currentProjectIndex));
                        }
                        lvTasksAdapter = new TaskListSwipeAdapter(ctxt, taskBallList);
                        lvTasks.setAdapter(lvTasksAdapter);

                        mDrawerAdapter.notifyDataSetChanged();

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

    public void markAsComplete(View view){
        Log.d("Mark", "As Complete");
    }

    public void bubbleIt(View view){
        Intent startBubbles = new Intent(this, Animated_Bubbles.class);
        startActivity(startBubbles);
    }

    public void refreshProjectTasks(){
        new LoadProjectTasks().execute();

    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onReturnValue(boolean projectCreated){
        if (projectCreated) {
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
            mDrawerAdapter.notifyDataSetChanged();
            if (projectList.size() == 1) {
                //Freshly created
                setTitle(projectList.get(currentProjectIndex));
                taskBallList = taskball_manager.getListList().get(currentProjectIndex);
                lvTasksAdapter = new TaskListSwipeAdapter(ctxt, taskBallList);
                lvTasks.setAdapter(lvTasksAdapter);
//                lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        ((SwipeLayout)(lvTasks.getChildAt(position - lvTasks.getFirstVisiblePosition()))).open(true);
//                        Log.d("Position: ", "is " + position);
//                    }
//                });
                mDrawerAdapter.notifyDataSetChanged();
            }
        }
    }

    //Update the listview on click of element in the navbar
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            currentProjectIndex = position;
            taskBallList = taskball_manager.getListList().get(currentProjectIndex);

            //NTS: You have to set a different adapter. Just notifying doens't do anything
            //because it updates stuff for one particular project only.
            //Once you move to a different project via click, you need to set a different adapter so
            //it listens to the new projectg
            lvTasksAdapter = new TaskListSwipeAdapter(ctxt, taskBallList);
            lvTasks.setAdapter(lvTasksAdapter);

            mDrawerLayout.closeDrawer(Gravity.LEFT);
            mDrawerAdapter.notifyDataSetChanged();
            setTitle(projectList.get(currentProjectIndex).substring(0, 1).toUpperCase() + projectList.get(currentProjectIndex).substring(1));
        }
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

                String sortOrder = TaskContract.TaskEntry.COLUMN_TASK_DUEDATE + " DESC," + TaskContract.TaskEntry.COLUMN_TASK_COMPLETE_STAT + " DESC";
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

                        id = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry._ID));
                        task_name = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_TITLE));
                        task_project = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_PROJECT));
                        task_date = cursor_projectlist.getLong(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DUEDATE));
                        task_priority = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_PRIORITY));
                        task_notes = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DESC));
                        task_isCompleted = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_COMPLETE_STAT));

                        TaskBall taskBall = new TaskBall(id, task_name, task_project, task_date, task_priority, task_notes, task_isCompleted, ctxt);

                        tb_manager.addTaskBall(index, taskBall);
                        cursor_projectlist.moveToNext();


                    }

                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result){
            if (projectList.size() != 0) {
                cursor_projectlist.close();
                dbHelper.close();
            }
            //Setup the listview for the homelist
            lvTasks = (ListView) findViewById(R.id.todo_tasks);

            if (projectList.size() != 0){
                taskBallList = taskball_manager.getListList().get(currentProjectIndex);
            }
            else{
                taskBallList = new ArrayList<TaskBall>();
            }
            lvTasksAdapter = new TaskListSwipeAdapter(ctxt, taskBallList);
            lvTasks.setAdapter(lvTasksAdapter);

            mDrawerList.setDividerHeight(1);
            ColorDrawable div_color = new ColorDrawable(ctxt.getResources().getColor(R.color.black));
            mDrawerList.setDivider(div_color);


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

                id = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry._ID));
                task_name = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_TITLE));
                task_project = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_PROJECT));
                task_date = cursor_projectlist.getLong(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DUEDATE));
                task_priority = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_PRIORITY));
                task_notes = cursor_projectlist.getString(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DESC));
                task_isCompleted = cursor_projectlist.getInt(cursor_projectlist.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_COMPLETE_STAT));

                TaskBall taskBall = new TaskBall(id, task_name, task_project, task_date, task_priority, task_notes, task_isCompleted, ctxt);

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
            lvTasksAdapter.notifyDataSetChanged();

            Log.d("Objects", tb_manager.getListList().toString());
        }
    }

}
