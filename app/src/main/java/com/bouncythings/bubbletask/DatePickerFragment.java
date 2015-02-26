package com.bouncythings.bubbletask;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kevin on 2/16/15.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    DateDialogListener dateCallback;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        month++;
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
        DateFormat mdy = new SimpleDateFormat("MMMMMMMM dd, yyyy");
        String date = String.valueOf(month) + "-" + String.valueOf(day) + "-" + String.valueOf(year);
        Date selectedDate = new Date();
        try {
            selectedDate = df.parse(date);
        } catch (java.text.ParseException e){
            e.printStackTrace();
        }
        DateDialogListener dateListener = (DateDialogListener) getActivity();
        dateListener.onDateReturn(mdy.format(selectedDate));

    }
    public interface DateDialogListener{
        public void onDateReturn(String date);
    }

}
