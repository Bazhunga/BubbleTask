package com.bouncythings.bubbletask;

import java.util.ArrayList;

/**
 * Created by kevin on 3/4/15.
 */
public class TaskBall_Manager {
    public static ArrayList<TaskBall> TaskBall_List = new ArrayList<TaskBall>();
    public static ArrayList<ArrayList<TaskBall>> TaskBall_ProjectList = new ArrayList<ArrayList<TaskBall>>();

    public TaskBall_Manager(){}

    public static ArrayList<TaskBall> getTaskBall_List() {
        return TaskBall_List;
    }

    public static void setTaskBall_List(ArrayList<TaskBall> taskBall_List) {
        TaskBall_List = taskBall_List;
    }

    public static void addTaskBall(TaskBall tb ){
        TaskBall_List.add(tb);
    }
    public static void deleteTaskBall(){

    }
    public static void clearTaskBallList(){
        TaskBall_List.clear();
    }

}
