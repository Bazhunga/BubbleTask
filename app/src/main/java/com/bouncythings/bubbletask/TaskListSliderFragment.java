package com.bouncythings.bubbletask;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class TaskListSliderFragment extends Fragment {

    TaskListSwipeAdapter swipe_adapter;
    ViewGroup rootView;
    TaskBall_Manager tbm;
    ListView swipe_listview;

    public TaskListSliderFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_task_page, container, false);
        swipe_listview = (ListView) rootView.findViewById(R.id.swipe_listView);

        tbm = new TaskBall_Manager();

        swipe_adapter = new TaskListSwipeAdapter(rootView.getContext(), tbm.getProject_TaskBallList(HomeList.currentProjectIndex));
        swipe_listview.setAdapter(swipe_adapter);
        swipe_listview.setDividerHeight(0);
        swipe_listview.setDivider(null);

        return rootView;
    }

    public void updateData(){
        swipe_adapter.notifyDataSetChanged();
    }
    public void switchFragments(){
        swipe_adapter = new TaskListSwipeAdapter(rootView.getContext(), tbm.getProject_TaskBallList(HomeList.currentProjectIndex));
        swipe_listview.setAdapter(swipe_adapter);
    }


}
