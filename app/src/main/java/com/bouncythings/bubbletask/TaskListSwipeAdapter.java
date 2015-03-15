package com.bouncythings.bubbletask;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.Date;
import java.util.List;


/**
 * Created by kevin on 3/1/15.
 */
public class TaskListSwipeAdapter extends BaseSwipeAdapter{
    private Context mContext;
    private List<TaskBall> data;

    public TaskListSwipeAdapter(Context mContext, List<TaskBall> data){
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_element;
    }

    //ATTENTION: Never bind listener or fill values in generateView.
    @Override
    public View generateView(int position, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.task_element, null);
    }

    @Override
    public void fillValues(final int position, View convertView) {

        TaskBall currentTask = data.get(position);
        final Date date = new Date();
        final String s_t_name = currentTask.getTaskName();
        final String s_t_desc = currentTask.getTaskDesc();
        final String s_t_project = currentTask.getParentProject();
        final long l_t_deadline = currentTask.getDueDate();
        final int i_t_priority = currentTask.getPriority();
        final long currentDate = date.getTime();
        final long daysUntil = (l_t_deadline - currentDate) / (1000*60*60*24);
        final int completed_stat = currentTask.isCompleted(); //1 or 0 (complete/incomplete)
        final int i_t_taskEntryId = currentTask.getTaskid();


        TextView tv_t_name = (TextView)convertView.findViewById(R.id.t_elem_name);
        TextView tv_t_desc = (TextView)convertView.findViewById(R.id.t_elem_desc);
        TextView tv_t_duedate = (TextView)convertView.findViewById(R.id.t_elem_deadline);
        ImageView iv_t_bars = (ImageView)convertView.findViewById(R.id.t_elem_bars);

        ImageView iv_edit = (ImageView)convertView.findViewById(R.id.edit);
        ImageView iv_trash = (ImageView)convertView.findViewById(R.id.trash);
        ImageView iv_done = (ImageView)convertView.findViewById(R.id.done);
        LinearLayout ll_element = (LinearLayout)convertView.findViewById(R.id.surface_view);

        //Set Listeners
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Edit", "this: " + position);
                DialogFragment dialog = new NewTaskDialog();
                Bundle data = new Bundle();
                data.putString("new_old", "edit");
                data.putInt("current_project_index", HomeList.currentProjectIndex);
                data.putStringArrayList("project_list", HomeList.projectList);
                //Data to use in the newtaskdialog popup
                data.putString("taskname", s_t_name);
                data.putString("project", s_t_project);
                data.putString("taskdesc", s_t_desc);
                data.putLong("deadline", l_t_deadline);
                data.putInt("priority", i_t_priority);
                data.putInt("dbentryid", i_t_taskEntryId);


                dialog.setArguments(data);
                dialog.show(((HomeList)mContext).getFragmentManager(), "NewTaskDialog");

            }
        });
        iv_trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Delete Entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int id;
                                TaskBall_Manager tbm = new TaskBall_Manager();
                                TaskBall tb = tbm.getTaskBall(HomeList.currentProjectIndex, position);
                                id = tb.getTaskid();
                                Log.d("Trash", "Id: " + id);
                                TaskDbHelper dbHelper = new TaskDbHelper(mContext);
                                SQLiteDatabase dbTask = dbHelper.getWritableDatabase();
                                String selection = TaskContract.TaskEntry._ID + " LIKE ?";
                                String[] selectionArgs = {String.valueOf(id)};
                                dbTask.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                                ((HomeList)mContext).refreshProjectTasks(); //Calls the activity method to refresh things.

                                //TODO: Update the listview so that everything becomes closed again

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
        });
        iv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("done", "this: " + position);
                TaskDbHelper dbHelper = new TaskDbHelper(mContext);
                SQLiteDatabase dbTask = dbHelper.getReadableDatabase();

                int id;
                TaskBall_Manager tbm = new TaskBall_Manager();
                TaskBall tb = tbm.getTaskBall(HomeList.currentProjectIndex, position);
                id = tb.getTaskid();
                ContentValues value = new ContentValues();
                //Set the completion stat to 1 means that it has been completed
                value.put(TaskContract.TaskEntry.COLUMN_TASK_COMPLETE_STAT, 1);
                String selection = TaskContract.TaskEntry._ID + " LIKE ?";
                String [] selectionArgs = {String.valueOf(id)};

                dbTask.update(TaskContract.TaskEntry.TABLE_NAME, value, selection, selectionArgs);
                ((HomeList)mContext).refreshProjectTasks(); //Calls the activity method to refresh things.
            }
        });

        if (completed_stat == 1){
            ll_element.setBackgroundColor(mContext.getResources().getColor(R.color.completed_bg_green));
            tv_t_duedate.setText("Completed!");
            iv_t_bars.setVisibility(View.GONE);
            tv_t_desc.setVisibility(View.GONE);
            tv_t_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
            tv_t_duedate.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
        }
        else{
            if (daysUntil > 0){
                tv_t_duedate.setText(Math.abs(daysUntil) + " days");
            }
            else if (daysUntil == 0){
                tv_t_duedate.setText("Today!");
            }
            else {
                tv_t_duedate.setText("Overdue " + Math.abs(daysUntil) + " days");
            }
        }

        tv_t_name.setText(s_t_name);
        tv_t_desc.setText(s_t_desc);


        switch (i_t_priority){
            case 1: iv_t_bars.setImageResource(R.drawable.b1);
                break;
            case 2: iv_t_bars.setImageResource(R.drawable.b2);
                break;
            case 3: iv_t_bars.setImageResource(R.drawable.b3);
                break;
            case 4: iv_t_bars.setImageResource(R.drawable.b4);
                break;
            case 5: iv_t_bars.setImageResource(R.drawable.b5);
                break;
            case 6: iv_t_bars.setImageResource(R.drawable.b6);
                break;
            case 7: iv_t_bars.setImageResource(R.drawable.b7);
                break;
            case 8: iv_t_bars.setImageResource(R.drawable.b8);
                break;
            case 9: iv_t_bars.setImageResource(R.drawable.b9);
                break;
            case 10: iv_t_bars.setImageResource(R.drawable.b10);
                break;
            default: break;

        }


    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
