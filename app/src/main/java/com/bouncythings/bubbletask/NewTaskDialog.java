package com.bouncythings.bubbletask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.Slider;


public class NewTaskDialog extends DialogFragment{
    int priority;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.new_task_dialog, null);
        final TextView tv_priority = (TextView) rootView.findViewById(R.id.tv_priority);

        //Must declare the view so that the buttons can be accessed (like the priority slider)
        tv_priority.setText("Priority: 1");
        final Slider prioritySlider = (Slider) rootView.findViewById(R.id.priority_slider);
        tv_priority.setText(Html.fromHtml("Priority: " + "<b><big> <font color='#c700ff'>1</font></big></b>"));

        //You can find the Slider file defined in
        //https://github.com/navasmdc/MaterialDesignLibrary/blob/master/MaterialDesign/src/com/gc/materialdesign/views/Slider.java
        //You'll see that you can setOnValueChangedListener and then pass in an OnValueChangeListener
        //NTS: OnValueChangeListener implements the function onValueChanged, so when you create a
        //new listener object, you have to implement the onValueChanged function
        prioritySlider.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int i) {
                tv_priority.setText(Html.fromHtml("Priority: " + "<b><big> <font color='#c700ff'>" + String.valueOf(i) + "</font></big></b>"));
            }
        });

        builder.setView(rootView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        CharSequence msg = "Priority: " + String.valueOf(prioritySlider.getValue());
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(rootView.getContext(), msg, duration);
                        toast.show();

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
