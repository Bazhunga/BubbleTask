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
    private static ArrayList<ArrayList<TaskBall>> TaskBall_ProjectList = new ArrayList<ArrayList<TaskBall>>();

    public TaskBall_Manager(){}

    //Project-level operations
    public static ArrayList<TaskBall> getProject_TaskBallList(int projectIndex) {
        return TaskBall_ProjectList.get(projectIndex);
    }

    public static void addProject_TaskBallList(String projectName){
        ArrayList<TaskBall> al = new ArrayList<TaskBall>();
        TaskBall_ProjectList.add(al);
    }
    public static void removeProject_TaskBallList(int projectIndex){
        TaskBall_ProjectList.remove(projectIndex);
    }

    //TaskBall-level operation
    public static TaskBall getTaskBall(int projectIndex, int taskBallIndex) {
        return TaskBall_ProjectList.get(projectIndex).get(taskBallIndex);
    }

    public static void addTaskBall(int projectIndex, TaskBall tb){
        TaskBall_ProjectList.get(projectIndex).add(tb);
    }

    public static void clearAll(){
        TaskBall_ProjectList.clear();
    }
    public static void clearProjectTasks(int projectIndex){
        TaskBall_ProjectList.get(projectIndex).clear();
    }

    public static ArrayList<ArrayList<TaskBall>> getListList(){
        return TaskBall_ProjectList;
    }

}
