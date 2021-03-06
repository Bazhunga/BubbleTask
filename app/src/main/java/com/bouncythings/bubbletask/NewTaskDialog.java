package com.bouncythings.bubbletask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.gc.materialdesign.views.Slider;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import info.hoang8f.widget.FButton;


public class NewTaskDialog extends DialogFragment{
    String szTaskName;
    String szProjectName;
    String szTaskNotes;

    int iPriority;     //Priority is 1-10
    long lDueDate;     //Store the due date in milliseconds

    final DateFormat mdy = new SimpleDateFormat("MMMMMMMM dd, yyyy");
    View rootView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.new_task_dialog, null);
        final TextView tv_priority = (TextView) rootView.findViewById(R.id.tv_priority);

        //Setting up the spinner for the project
        int index = getArguments().getInt("current_project_index");

        Log.d("Projectlist size", "" + HomeList.projectList.size());

        final Spinner sp_project = (Spinner) rootView.findViewById(R.id.project_parent);
        ArrayList<String> l_projectList = new ArrayList<>();

        //NTS: The projectList in homelist was passed into bundle and extracted here
        //     Any operation done on the arraylist here (ie. removing the master list), would
        //     change the project list back in Homelist. So you need to create a new arraylist
        //     and copy everything over so you create a completely new instance of the arraylist
        //     that you can use.
        l_projectList.addAll(getArguments().getStringArrayList("project_list"));
        l_projectList.remove(getResources().getString(R.string.master_list)); //Removes master list as being one of the project options
        ArrayAdapter<String> sp_project_adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, l_projectList);
        sp_project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_project.setAdapter(sp_project_adapter);

        sp_project.setSelection(index);

        //Layout Elements
        final MaterialEditText taskName = (MaterialEditText) rootView.findViewById(R.id.task_entry);
        final MaterialEditText dateChosen = (MaterialEditText) rootView.findViewById(R.id.dueDate);
        final MaterialEditText taskNotes = (MaterialEditText) rootView.findViewById(R.id.task_notes);
        Date date = new Date();
        String todayDate = mdy.format(date);
        dateChosen.setText(todayDate);

        //Is this a new task or is it being edited?
        String new_old_creation = getArguments().getString("new_old");

        final FButton date_picker_button = (FButton) rootView.findViewById(R.id.datePickerButton);

        date_picker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarPicker dateChooser = new CalendarPicker();
                dateChooser.show(getFragmentManager(), "DatePicker");

            }
        });

        //You can find the Slider file defined in
        //https://github.com/navasmdc/MaterialDesignLibrary/blob/master/MaterialDesign/src/com/gc/materialdesign/views/Slider.java
        //You'll see that you can setOnValueChangedListener and then pass in an OnValueChangeListener
        //NTS: OnValueChangeListener implements the function onValueChanged, so when you create a
        //new listener object, you have to implement the onValueChanged function
        final Slider prioritySlider = (Slider) rootView.findViewById(R.id.priority_slider);
        prioritySlider.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int i) {
                tv_priority.setText(Html.fromHtml("Priority: " + "<b><big> <font color='#c700ff'>" + String.valueOf(i) + "</font></big></b>"));
            }
        });

        if (new_old_creation.equals("new")) {
            //Variable initialization
            iPriority = 1;
            lDueDate = System.currentTimeMillis();
            szTaskName = "";
            szProjectName = sp_project.getSelectedItem().toString();


            //Must declare the view so that the buttons can be accessed (like the priority slider)
            tv_priority.setText(Html.fromHtml("Priority: " + "<b><big> <font color='#c700ff'>1</font></big></b>"));

            builder.setView(rootView)
                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            iPriority = prioritySlider.getValue();
                            //iDueDate is automatically updated on dialog return
                            szProjectName = sp_project.getSelectedItem().toString();
                            szTaskName = taskName.getText().toString();
                            szTaskNotes = taskNotes.getText().toString();

//                        CharSequence msg = "Priority: " + String.valueOf(iPriority) + "\r\n"
//                                            + "PName: " + szProjectName + "\r\n"
//                                            + "TName: " + szTaskName + "\r\n"
//                                            + "Ldate: " + iDueDate
//                                            + "TNotes: " + szTaskNotes;
//                        int duration = Toast.LENGTH_SHORT;
//                        Toast toast = Toast.makeText(rootView.getContext(), msg, duration);
//                        toast.show();

                            //Instantiate the database to put information into it
                            TaskDbHelper mDbHelper = new TaskDbHelper(rootView.getContext());
                            SQLiteDatabase dbTask = mDbHelper.getWritableDatabase();

                            //Map the values you want to put in
                            ContentValues values = new ContentValues();
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_PROJECT, szProjectName);
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_TITLE, szTaskName);
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_DESC, szTaskNotes);
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_PRIORITY, iPriority);
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_DUEDATE, lDueDate);
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_COMPLETE_STAT, 0);
                            long newRowId;
                            newRowId = dbTask.insert(
                                    TaskContract.TaskEntry.TABLE_NAME,
                                    TaskContract.TaskEntry.COLUMN_NAME_NULLABLE,
                                    values);

                            //Call HomeList activity to refresh the list
                            //Test this buy creating a project, adding a task and executing Bubble It!
                            //If the project has been refreshed then bubbles appear
                            ((HomeList) getActivity()).refreshProjectTasks();

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
        }
        else if (new_old_creation.equals("edit")){
            //Variable initialization
            szTaskName = getArguments().getString("taskname");
            szProjectName = getArguments().getString("project"); //Unused
            lDueDate = getArguments().getLong("deadline");
            iPriority = getArguments().getInt("priority");
            szTaskNotes = getArguments().getString("taskdesc");
            final int iID = getArguments().getInt("dbentryid");

            prioritySlider.setValue(iPriority);
            tv_priority.setText(Html.fromHtml("Priority: " + "<b><big> <font color='#c700ff'>" + String.valueOf(iPriority) + "</font></big></b>"));
            taskName.setText(szTaskName);
            dateChosen.setText(mdy.format(lDueDate));
            taskNotes.setText(szTaskNotes);

            builder.setView(rootView)
                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            iPriority = prioritySlider.getValue();
                            //iDueDate is automatically updated on dialog return
                            szProjectName = sp_project.getSelectedItem().toString();
                            szTaskName = taskName.getText().toString();
                            szTaskNotes = taskNotes.getText().toString();

                            //Instantiate the database to put information into it
                            TaskDbHelper mDbHelper = new TaskDbHelper(rootView.getContext());
                            SQLiteDatabase dbTask = mDbHelper.getReadableDatabase();

                            //Map the values you want to put in
                            ContentValues values = new ContentValues();
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_PROJECT, szProjectName);
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_TITLE, szTaskName);
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_DESC, szTaskNotes);
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_PRIORITY, iPriority);
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_DUEDATE, lDueDate);
                            values.put(TaskContract.TaskEntry.COLUMN_TASK_COMPLETE_STAT, 0);
                            String selection = TaskContract.TaskEntry._ID + " LIKE ?";
                            String [] selectionArgs = {String.valueOf(iID)};
                            dbTask.update(TaskContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);

                            //Call HomeList activity to refresh the list
                            //Test this buy creating a project, adding a task and executing Bubble It!
                            //If the project has been refreshed then bubbles appear
                            ((HomeList) getActivity()).refreshProjectTasks();

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
        }
        else{

        }

        // Create the AlertDialog object and return it
        return builder.create();


    }
    public void onDateReturn(long date){
        String szDate = mdy.format(date);
        MaterialEditText dateChosen = (MaterialEditText) rootView.findViewById(R.id.dueDate);
        dateChosen.setText(szDate);
        lDueDate = date;


    }

}
