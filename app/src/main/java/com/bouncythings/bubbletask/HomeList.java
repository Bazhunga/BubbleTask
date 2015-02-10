package com.bouncythings.bubbletask;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeList extends ActionBarActivity {
    public ArrayList<String> projectList = new ArrayList<String>();


    Context ctxt = this;
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

        private final String[] TITLES = {"School", "IBM", "Hackathons", "Android"};

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
        DialogFragment dialog = new NewTaskDialog();
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

    public void cancel(View view){

    }

    public void done (View view){

    }
}
