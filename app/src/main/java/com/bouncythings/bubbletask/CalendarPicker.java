package com.bouncythings.bubbletask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarPicker.OnDateChosenListener} interface
 * to handle interaction events.
 */
public class CalendarPicker extends DialogFragment {

    private OnDateChosenListener mListener;

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
                        String date = "";
                        date = mdy.format(calendar.getDate());
                        CharSequence msg = date;
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(rootView.getContext(), msg, duration);
                        toast.show();

                        //Extremly important NTS: Use find fragment by tag to get control over the fragment functions that you're currently using
                        //You can find the tag by how you first started the fragment in .show(getfragmentmanager, tag);
                        NewTaskDialog ntd = (NewTaskDialog) getFragmentManager().findFragmentByTag("NewTaskDialog");
                        ntd.onDateReturn(date);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_calendar_picker, container, false);
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(String date) {
//        if (mListener != null) {
//            mListener.onDatePressed(date);
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnDateChosenListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
    public interface OnDateChosenListener {
        public void onDatePressed(String date);
    }

}
