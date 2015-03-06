package com.bouncythings.bubbletask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;

/**
* Created by kevin on 1/26/15.
*/
public class AnimatedView extends ImageView {
    //Grab TaskBall List data from main activity
    ArrayList<TaskBall> tbList = HomeList.taskBallList;
    int cp_index = HomeList.currentProjectIndex;

    private Context mContext;
    private int xVelocity = 20;
    private int yVelocity = 10;



    // nts
    // Allows you to send and process (in this case) Runnable obj associated with thread's MessageQueue
    // Each handler instance associated with single thread and that thread's message queue
    // Create new Handler --> bound to thread creating it
    // Will deliver messages and runnables to that queue and execute them as they come out of message queue
    // In this case, it's used to enqueue the drawing of the circles on a different thread than its own

    private Handler h;
    private final int FRAME_RATE =20;


    public AnimatedView(Context context, AttributeSet attrs)  {
        super(context, attrs);
        mContext = context;
        h = new Handler();

    }
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            //Force the view to draw
            invalidate();
        }
    };
    protected void onDraw(Canvas c) {

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.fbutton_color_wet_asphalt));
        paint.setTextSize(30); //Default 30
        paint.setTextAlign(Paint.Align.CENTER);

        //You need to set the bounds of all the circ objects before drawing them
        //Need to adjust the placeDrawables such that the x and y are properties of objects

        for (int i = 0; i < tbList.size(); i++ ){
            placeDrawables(tbList.get(i));
        }

        //Draw them
        int [] textCentre = new int [2] ;
        int textX;
        TaskBall tb;

        for (int i = 0; i < tbList.size(); i++ ){
            tb = tbList.get(i);
            tb.getCirc().draw(c);
            //Draw the corresponding text
            textCentre = tb.getCentre(); //Only use the y component
            textX = tb.getLeftX();
            paint.setTextSize(tb.getTextSize());
            c.drawText(tb.getTaskName_truncated(), textCentre[0], textCentre[1], paint);
        }


        detectCollision();

        //Ensure proper placements so there are no overlaps

        //Causes runnable r to be added to message queue (run after specified amount of time elapses,
        // Which is frame rate)

        h.postDelayed(r, FRAME_RATE);

    }

    private void detectCollision(){
        /*
        Distance between 2 points is calculated by d = sqrt((x2 - x1)^2 - (y^2 - y^1)^2)
         */
        double distanceApart = 0;
        double touchingDistance = 0;

        //Coordinates of the ball centres
        int x1, y1, x2, y2;
        TaskBall circ1, circ2;

        for (int i = 0; i < tbList.size(); i++){
            for (int j = i + 1; j < tbList.size(); j++){
                circ1 = tbList.get(i);
                circ2 = tbList.get(j);

                x1 = circ1.getCentre()[0];
                y1 = circ1.getCentre()[1];
                x2 = circ2.getCentre()[0];
                y2 = circ2.getCentre()[1];
                distanceApart = calcDistanceApart(x1, y1, x2, y2);
                touchingDistance = getTouchingDistance(circ1, circ2);

                if (distanceApart <= touchingDistance && (!(circ1.getTbOverlaps().contains(circ2))) && (!(circ2.getTbOverlaps().contains(circ1)))) {
                    calcCollisionSpeed(circ1, circ2);
                    circ1.pushTbOverlaps(circ2);
                    circ2.pushTbOverlaps(circ1);
                }
                else if (distanceApart <= touchingDistance && (circ1.getTbOverlaps().contains(circ2)) && (circ2.getTbOverlaps().contains(circ1))){
                    //do nothing
                }
                else if (distanceApart >= touchingDistance) {
                    circ1.removeTbOverlaps(circ2);
                    circ2.removeTbOverlaps(circ1);
                }

            }
        }
    }
    private double calcDistanceApart(int x1, int y1, int x2, int y2){
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }
    private int getTouchingDistance (TaskBall circ1, TaskBall circ2){
        return circ1.getRadius() + circ2.getRadius();
    }

    public void calcCollisionSpeed (TaskBall circ1, TaskBall circ2) {
        //Perfectly elastic collision model
        //Remember, m1u1 + m2u2 = m1v1 + m2v2 where u is speed before collision and v is speed after collision
        /*
        Do for both x and y
        v1 = ((u1*(m1 - m2) + 2 * m2 * u2)/(m1 + m2))
        v2 = ((u2*(m2 - m1) + 2 * m1 * u1)/(m1 + m2))
         */

        int m1 = circ1.getMass();
        int m2 = circ2.getMass();

        //X-coordinates
        int u1x = circ1.getxVelocity();
        int u2x = circ2.getxVelocity();
        int v1x = ((u1x*(m1 - m2) + 2 * m2 * u2x)/(m1 + m2));
        int v2x = ((u2x*(m2 - m1) + 2 * m1 * u1x)/(m1 + m2));
        circ1.setxVelocity(v1x);
        circ2.setxVelocity(v2x);

        //Y-coordinates
        int u1y = circ1.getyVelocity();
        int u2y = circ2.getyVelocity();
        int v1y = ((u1y*(m1 - m2) + 2 * m2 * u2y)/(m1 + m2));
        int v2y = ((u2y*(m2 - m1) + 2 * m1 * u1y)/(m1 + m2));
        circ1.setyVelocity(v1y);
        circ2.setyVelocity(v2y);


    }
    private void placeDrawables(TaskBall circ){
        circ.setLeftX(circ.getLeftX() + circ.getxVelocity());
        circ.setTopY(circ.getTopY() + circ.getyVelocity());
        if (circ.getLeftX() < 0 || circ.getRightX() > this.getWidth() &&  circ.isPreviouslyWallTouching() == false){
            if (circ.getLeftX() < 0) {
                //Circle is on the left side of the screen
                circ.posXVelocity();
            }
            else{
                circ.negXVelocity();
            }
            circ.setPreviouslyWallTouching(true);
        }
        else if (circ.getLeftX() < 0 || circ.getRightX() > this.getWidth() &&  circ.isPreviouslyWallTouching() == true){
            //do nothing
        }
        else if (circ.getLeftX() > 0 || circ.getRightX() < this.getWidth()){
            circ.setPreviouslyWallTouching(false);
        }

        if (circ.getTopY() < 0 || circ.getBottomY() > this.getHeight() && circ.isPreviouslyWallTouching() == false){
            if (circ.getTopY() < 0) {
                //Circle is on the left side of the screen
                circ.posYVelocity();
            }
            else{
                circ.negYVelocity();
            }
            circ.setPreviouslyWallTouching(true);

        }
        else if (circ.getTopY() < 0 || circ.getBottomY() > this.getHeight() &&  circ.isPreviouslyWallTouching() == true){
            //do nothing
        }
        else if (circ.getTopY() > 0 || circ.getBottomY() < this.getHeight()){
            circ.setPreviouslyWallTouching(false);
        }
        circ.findCentre();


        circ.getCirc().setBounds(circ.getLeftX(), circ.getTopY(), circ.getRightX(), circ.getBottomY());
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
    }

}
