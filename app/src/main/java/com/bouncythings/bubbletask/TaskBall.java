package com.bouncythings.bubbletask;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Random;

/**
* Created by kevin on 1/26/15.
*/
public class TaskBall {
    //User Provided
    private String taskDesc; //This will be full description of the task
    private String taskShort; //This will be autogen-ed shortened description that will appear on the ball in the Animated View
    private String dueDate;
    private int priority; //From 1 to 10

    //Application modified
    private Context c;
    private boolean isCompleted; //Marks whether the task has been completed or not
    private int colour;
    private int mass;
    private String project; //The project that the ball belongs to
    private Drawable circ;

    //Ball properties determined by the class based on priority
    private int radius;
    private int leftX;
    private int topY;
    private int rightX;
    private int bottomY;

    private int [] centre = new int [2] ; //[x, y]
    private int xVelocity;
    private int yVelocity;

    /*
    This flag is used to detect whether the circle has overlapped with a previous circle. Upon overlapping,
    if the circs do not move apart immediately, their velocities will continue to flip back and forth
    between positive and negative.

    When the pT flag is on, that means that the circle has previously experienced a collision. If on the
    next redraw it experiences another collision, then we shall not reverse the direction and keep going
    until we clear the overlap with the other circle. Then we can set the pT flag to off and regular
    collision detection can resume.
     */

    //Number of circles overlapped with it
    private ArrayList<TaskBall> tbOverlaps = new ArrayList<TaskBall>();
    //private int overlaps;
    private boolean previouslyWallTouching;

    Random rand = new Random();


    //Defining behaviour within the window
    //These will be the limits within which the ball can move
    //Each ball will be different


    public TaskBall(String taskDesc, String taskShort, int priority, int colour, Context c, String project, String dueDate) {
        this.taskDesc = taskDesc;
        this.taskShort = taskShort;
        this.priority = priority;
        this.colour = colour;
        this.project = project;
        this.dueDate = dueDate;
        circ = c.getResources().getDrawable(R.drawable.circle);
        setRadius(priority);
        setVelocity(priority);
        setCoords();
    //Determined by the class based on priority
        mass = priority * 80;
//        setOverlaps(0);
        setPreviouslyWallTouching(false);
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public int getMass() {
        return mass;
    }

    public void setCentre(int[] centre) {
        this.centre = centre;
    }

    public boolean isPreviouslyWallTouching() {
        return previouslyWallTouching;
    }

    public void setPreviouslyWallTouching(boolean previouslyWallTouching) {
        this.previouslyWallTouching = previouslyWallTouching;
    }

    public ArrayList<TaskBall> getTbOverlaps() {
        return tbOverlaps;
    }

    public void pushTbOverlaps(TaskBall overlap) {
        this.tbOverlaps.add(overlap);
    }

    public void removeTbOverlaps(TaskBall overlap) {
        this.tbOverlaps.remove(overlap);
    }


//    public int getOverlaps() {
//        return overlaps;
//    }
//
//    public void incOverlaps() {
//        this.overlaps++;
//    }
//    public void decOverlaps() {
//        this.overlaps--;
//    }
//    private void setOverlaps(int overlaps){
//        this.overlaps = overlaps;
//    }

    private void setVelocity(int priority){
        int random = rand.nextInt((10) + 1);
        int direction;
        if (random > 5){
            direction = 1;
        }
        else{
            direction = -1;
        }

        xVelocity = 65/priority * direction;

        random = rand.nextInt((10) + 1);
        if (random > 5){
            direction = 1;
        }
        else{
            direction = -1;
        }
        yVelocity = 100/priority * direction;
    }
    private void setCoords(){
        //Generate random x coordinate on screen
        leftX = com.bouncythings.bubbletask.AnimatedView.maxWidth/2;
        topY = com.bouncythings.bubbletask.AnimatedView.maxHeight/2;
        rightX = leftX + 2 * radius;
        bottomY = topY + 2 * radius;
        findCentre();
    }

    public void reverseBothVelocity(){
        xVelocity = xVelocity * -1;
        yVelocity = yVelocity * -1;
    }

    public void reverseXVelocity(){
        xVelocity = xVelocity * -1;
    }
    public void posXVelocity(){
        xVelocity = Math.abs(xVelocity);
    }
    public void negXVelocity(){
        xVelocity = Math.abs(xVelocity) * -1;
    }

    public void reverseYVelocity(){
        yVelocity = yVelocity * -1;
    }

    public void posYVelocity(){
        yVelocity = Math.abs(yVelocity);
    }
    public void negYVelocity(){
        yVelocity = Math.abs(yVelocity) * -1;
    }


    public void findCentre(){
        //Note: Top left of screen is
        //x-coordinate
        centre[0] = leftX + radius;

        //y-coordinate
        centre[1] = topY + radius;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Drawable getCirc() {
        return circ;
    }

    public void setCirc(Drawable circ) {
        this.circ = circ;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getTaskShort() {
        return taskShort;
    }

    public void setTaskShort(String taskShort) {
        this.taskShort = taskShort;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public int getRadius() {
        return radius;
    }

    private void setRadius(int priority) {
        this.radius = priority * 35;
    }


    public int getLeftX() {
        return leftX;
    }

    public int getTopY() {
        return topY;
    }

    public int getRightX() {
        return rightX;
    }

    public int getBottomY() {
        return bottomY;
    }

    public void setLeftX(int leftX) {
        this.leftX = leftX;
        setRightX();
    }

    public void setTopY(int topY) {
        this.topY = topY;
        setBottomY();
    }

    private void setRightX() {
        rightX = leftX + 2 * radius;
    }

    private void setBottomY() {
        bottomY = topY + 2 * radius;
    }

    public int [] getCentre() {
        return centre;
    }

    public int getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(int xVelocity) {
        this.xVelocity = xVelocity;
    }

    public int getyVelocity() {
        return yVelocity;
    }

    public void setyVelocity(int yVelocity) {
        this.yVelocity = yVelocity;
    }
}
