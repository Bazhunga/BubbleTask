package com.bouncythings.bubbletask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.List;
import java.util.Date;


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
    public void fillValues(int position, View convertView) {

        TextView tv_t_name = (TextView)convertView.findViewById(R.id.t_elem_name);
        TextView tv_t_desc = (TextView)convertView.findViewById(R.id.t_elem_desc);
        TextView tv_t_duedate = (TextView)convertView.findViewById(R.id.t_elem_deadline);
        ImageView iv_t_bars = (ImageView)convertView.findViewById(R.id.t_elem_bars);

        TaskBall currentTask = data.get(position);
        String s_t_name = currentTask.getTaskName();
        String s_t_desc = currentTask.getTaskDesc();
        long l_t_deadline = currentTask.getDueDate();
        Date date = new Date();
        long currentDate = date.getTime();
        long daysUntil = (l_t_deadline - currentDate) / (1000*60*60*24);

        tv_t_name.setText(s_t_name);
        tv_t_desc.setText(s_t_desc);
        if (daysUntil > 0){
            tv_t_duedate.setText("Due in " + Math.abs(daysUntil) + " days");
        }
        else if (daysUntil == 0){
            tv_t_duedate.setText("Due today!");
        }
        else {
            tv_t_duedate.setText("Overdue by " + Math.abs(daysUntil) + " days");
        }

        int priority = currentTask.getPriority();
        switch (priority){
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
