package com.bouncythings.bubbletask;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class TaskListSliderFragment extends Fragment {

    TaskListSwipeAdapter swipe_adapter;

    public TaskListSliderFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_task_page, container, false);
        ListView swipe_listview = (ListView) rootView.findViewById(R.id.swipe_listView);

        TaskBall_Manager tbm = new TaskBall_Manager();

        swipe_adapter = new TaskListSwipeAdapter(rootView.getContext(), tbm.getProject_TaskBallList(HomeList.currentProjectIndex));
        swipe_listview.setAdapter(swipe_adapter);
        swipe_listview.setDividerHeight(0);
        swipe_listview.setDivider(null);

        return rootView;
    }

    public void updateData(){
        swipe_adapter.notifyDataSetChanged();
    }


}
