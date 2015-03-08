package com.bouncythings.bubbletask;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kevin on 1/26/15.
 */
public class TaskBall { //implements Parcelable{
    //User Provided
    private int taskid;
    private String taskName;
    private String parentProject;
    private long dueDate;
    private int priority; //From 1 to 10
    private String taskDesc; //This will be full description of the task
    private int isCompleted; //Marks whether the task has been completed or not

    //Application modified
    private String taskName_truncated;
    private Context c;
    private int colour;
    private int mass;
    private Drawable circ;
    private String taskShort; //This will be autogen-ed shortened description that will appear on the ball in the Animated View
    private int textSize;

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


    public TaskBall(int id, String taskName, String parentProject, long dueDate, int priority, String taskDesc, int isCompleted, Context c) {
        this.taskid = taskid;
        this.taskName = taskName;
        setTaskName_truncated(taskName);
        this.parentProject = parentProject;
        this.dueDate = dueDate;
        this.priority = priority;
        this.taskDesc = taskDesc;
        this.isCompleted = isCompleted;
        circ = c.getResources().getDrawable(R.drawable.circle);
        setMass();
        setRadius(priority);
        setVelocity(priority);
        setCoords();
        setPreviouslyWallTouching(false);
        setTextSize();
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize() {
        this.textSize = (int) (70 * Math.log10( priority + 0.778));
    }

    public String getTaskName_truncated() {
        return taskName_truncated;
    }

    public void setTaskName_truncated(String taskName) {
        if (taskName.length() <= 20){
            this.taskName_truncated = taskName;
        }
        else {
            this.taskName_truncated = taskName.substring(0, 18) + "...";
        }
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getParentProject() {
        return parentProject;
    }

    public void setParentProject(String parentProject) {
        this.parentProject = parentProject;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
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

    private void setMass(){
        mass = priority * 80;
    }

    private void setVelocity(int priority){
        int random = rand.nextInt((10) + 1);
        int direction;
        if (random > 5){
            direction = 1;
        }
        else{
            direction = -1;
        }

        xVelocity = 20/priority * direction;

        random = rand.nextInt((10) + 1);
        if (random > 5){
            direction = 1;
        }
        else{
            direction = -1;
        }
        yVelocity = 25/priority * direction;
    }
    private void setCoords(){
        //Generate random x coordinate on screen
        leftX = HomeList.maxWidth/2;
        topY = HomeList.maxHeight/2;
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

    public int isCompleted() {
        return isCompleted;
    }

    public void setCompleted(int isCompleted) {
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
        //Requirements
        // 1. Vastness in sizing depends on number of tasks and urgency. Closer deadlline + higher priority = larger ball
        // 2. When there are a lot of balls the total size needs to be scaled down
        // 3. Speed must decrease as #balls increase
        // 4. Size: 3 balls: upper threshold is 450, 4 balls: 300, 5 balls:
        if (priority <= 3 ){
            this.radius = (int) (priority * 0.3 * 110);
        }
        else{
            this.radius = (int) (priority * 0.25 * 110);
        }
         //(Math.atan(priority) * 100);
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

//    //Parceling
//
//    // Parcelling part
//    public TaskBall(Parcel in){
//        String[] data = new String[3];
//        in.readStringArray(data);
//
//        this.taskid = Integer.parseInt(data[0]);
//        this.taskName = data[1];
//        this.parentProject = data[2];
//        this.dueDate = Long.parseLong(data[3]);
//        this.priority = Integer.parseInt(data[4]);
//        this.taskDesc = data[5];
//        this.isCompleted = Integer.parseInt(data[6]);
//        circ = c.getResources().getDrawable(R.drawable.circle);
//    }
//
//    @Override
//    public int describeContents(){
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeStringArray(new String[] {String.valueOf(this.taskid),
//                this.taskName,
//                this.parentProject,
//                String.valueOf(this.dueDate),
//                String.valueOf(this.priority),
//                this.taskDesc,
//                String.valueOf(this.isCompleted )});
//    }
//
//
//
//    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
//        public TaskBall createFromParcel(Parcel in) {
//            return new TaskBall(in);
//        }
//
//        public TaskBall[] newArray(int size) {
//            return new TaskBall[size];
//        }
//    };
}
