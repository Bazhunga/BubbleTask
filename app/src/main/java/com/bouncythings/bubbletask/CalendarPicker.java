package com.bouncythings.bubbletask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CalendarPicker extends DialogFragment {


    public CalendarPicker() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.calendar_picker_dialog, null);
        final CalendarView calendar = (CalendarView) rootView.findViewById(R.id.calendarView);
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        final DateFormat mdy = new SimpleDateFormat("MMMMMMMM dd, yyyy");


        builder.setView(rootView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                        String date = "";
//                        date = mdy.format(calendar.getDate());
                        //Extremely important NTS: Use find fragment by tag to get control over the fragment functions that you're currently using
                        //You can find the tag by how you first started the fragment in .show(getfragmentmanager, tag);
                        NewTaskDialog ntd = (NewTaskDialog) getFragmentManager().findFragmentByTag("NewTaskDialog");
                        ntd.onDateReturn(calendar.getDate());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
