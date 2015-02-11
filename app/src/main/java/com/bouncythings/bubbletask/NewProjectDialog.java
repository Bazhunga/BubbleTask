package com.bouncythings.bubbletask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class NewProjectDialog extends DialogFragment {
    JSONArray ja = new JSONArray();
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String projectList_prefs = prefs.getString(getString(R.string.project_list_prefs), "[{'project':'Misc'}]");
        final View v = inflater.inflate(R.layout.new_project_dialog, null);
        builder.setView(v)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MaterialEditText met = (MaterialEditText) v.findViewById(R.id.project_edit_text);
                        String projectName = met.getText().toString();
                        try{
                            ja = new JSONArray(projectList_prefs);
                            JSONObject newProject = new JSONObject();
                            newProject.put("project", projectName);
                            ja.put(newProject);

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(getString(R.string.project_list_prefs), ja.toString());
                        editor.commit();

                        //Listen for the affirmation and pass that back to the activity
                        NewProjectDialogListener listener = (NewProjectDialogListener) getActivity();
                        listener.onReturnValue(true);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface NewProjectDialogListener{
        public void onReturnValue(boolean createdProject);
    }
}
