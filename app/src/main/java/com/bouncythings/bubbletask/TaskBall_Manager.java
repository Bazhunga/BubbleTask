package com.bouncythings.bubbletask;

import java.util.ArrayList;

/**
 * Created by kevin on 3/4/15.
 */
public class TaskBall_Manager {
    //Manages all the taskballs of the various projects
    /*
    The TaskBall_Manager will be an array list of TaskBalls, with each element corresponding to
    a project tab in the application.
    Each element will be an ArrayList of taskballs that correspond to the tasks in the project tab.

    Updating Rules
    1. Recreate the manager on startup of the application. This means recreating the arraylist of
       arraylists and the taskballs in each arraylist
    2. When the project is removed, remove the project from the taskball_list too (no need to recreate
       everything
    3. When adding a Task, append the resulting taskball to the taskball_list
    4. Upon completing a task or deleting the task, remove the taskball
    5. Upon editing the task, remove and re-add the taskball
     */
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

    public static void clearTaskBallList(){
        TaskBall_List.clear();
    }
    public static void deleteProject(){

    }

}
