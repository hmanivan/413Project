package revolverwheel.revolver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;


import com.example.ozzca_000.myapplication.R;

import java.util.concurrent.LinkedBlockingQueue;

import SoundUtils.SoundPlayer;
import apitest.MapsActivity;
import foodroulette.appstate.FoodRouletteApplication;

/**
 * Created by Sam on 7/6/2015.
 */
public class CanvasView extends SurfaceView
{

    private Bitmap samBetterBitmap;

    Context context;

    public FoodRouletteApplication _appstate;

    private Paint mPaint;
    private long timeStamp = 0;
    private float degToSpin = 0;

    //initialize angular velocity and set initial velocity of wheel
    private float angularV = 1;

    private static float cylinderCenterX = 504;
    private static float cylinderCenterY = 515;

    private double previousAngle;
    private double previousRadius;
    private long startTouchTimeStamp;
    private long previousTimeStamp;
    private Boolean isBeingTouched = false;
    private Boolean revolverSelectionEnable = false;

    //variables used in rotation function
    float x;
    float y;
    double offsetX;
    double offsetY;
    double distanceToCenter;
    double angleToTouch;

    private Thread rotationThread;
    private Boolean threadsEnabled = true;
    //for click detection
    float angleTurned = 0;

    public void setSamBitmap(Bitmap bitmap)
    {
        samBetterBitmap = bitmap;

        //auto scaled center of the cylinder (actually works, cannot believe it)
        cylinderCenterX = (bitmap.getWidth() / 2) - ((8 * bitmap.getWidth()) / 1024);
        cylinderCenterY = (bitmap.getHeight() / 2) + ((3 * bitmap.getHeight()) / 1024);
        this.postInvalidate();
    }

    public CanvasView(final Context c, AttributeSet attrs)
    {
        super(c, attrs);
        context = c;
        _appstate = ((FoodRouletteApplication) (context.getApplicationContext()));

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);

        setWillNotDraw(false);
    }

    //thread which calculates rotation angle for cylinder by measuring how much time has passed
    public void startRotationThread()
    {
        //reset all the initial values
        threadsEnabled = true;
        isBeingTouched = false;
        revolverSelectionEnable = false;
        timeStamp = 0;
        degToSpin = 0;
        angularV = 1;
        angleTurned = 0;
        samBetterBitmap = null;

        final CanvasView hardthis = this;
        rotationThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //android.os.Debug.waitForDebugger();
                while (threadsEnabled)
                {
                    long currTime = System.currentTimeMillis();

                    if (!isBeingTouched && timeStamp != 0)
                    {
                        //when user lifts finger, run this code to decay angular velocity and spin down the wheel
                        long timeDiff = currTime - timeStamp;
                        degToSpin += angularV * timeDiff;

                        //handle degree to spin overflows over 360, under 0
                        while (degToSpin >= 360f)
                        {
                            degToSpin -= 360f;
                        }
                        while (degToSpin <= 0f)
                        {
                            degToSpin += 360f;
                        }

                        //centering force
                        //calculate current offset to nearest selection
                        float offsetToNearestSelection = degToSpin % 60;
                        offsetToNearestSelection -= 30;
                        angularV += (offsetToNearestSelection / 7200);

                        //decay rotational velocity
                        angularV *= .98;

                        //zero out if we are on a selection
                        if (Math.abs(offsetToNearestSelection + 30) < .1 && Math.abs(angularV) < 0.05)
                        {
                            angularV = 0;

                            //select the top category if revolver select is enabled
                            if (revolverSelectionEnable)
                            {
                                angleToTouch = -90;
                                rouletteClickHandler();
                                revolverSelectionEnable = false;
                            }
                        }

                    }

                    timeStamp = currTime;

                    hardthis.postInvalidate();
                    try
                    {
                        Thread.sleep(15);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        rotationThread.start();
    }

    //halt the execution loop of the rotation thread
    public void dispose()
    {
        threadsEnabled = false;
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        //canvas.drawPath(mPath, mPaint);
        // canvas.drawPath(path, mPaint);
        if (samBetterBitmap != null)
        {
            canvas.save();

            Matrix matrix = new Matrix();
            matrix.setRotate(degToSpin, cylinderCenterX, cylinderCenterY);

            canvas.drawBitmap(samBetterBitmap, matrix, mPaint);
            canvas.restore();
        }

        //set text characteristics
        int textSize = 64;
        int textPadding = 10;
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);

        //TODO: SAM fix this shit to conform to Georges mocks
        //draw the label text around a circle
        Path pathCW = new Path();
        Path pathCCW = new Path();

        //create a matrix to rotate the path to set the text start point
        Matrix pathRotationMatrix = new Matrix();

        //add a circle to the path, centered at 0,0 and radius sufficient to match the revolver
        pathCW.addCircle(cylinderCenterX, cylinderCenterY, (cylinderCenterY - (textSize / 4)) + textPadding, Path.Direction.CW);
        pathCCW.addCircle(cylinderCenterX, cylinderCenterY, (cylinderCenterY + (textSize / 2)) + textPadding, Path.Direction.CCW);

        //apply matrix transform to CW path
        pathRotationMatrix.setRotate(295, cylinderCenterX, cylinderCenterY);
        pathCW.transform(pathRotationMatrix);

        pathRotationMatrix.setRotate(150, cylinderCenterX, cylinderCenterY);
        pathCCW.transform(pathRotationMatrix);

        //push text to screen
        canvas.drawTextOnPath("Swipe to spin!", pathCW, 0, 0, textPaint);
        canvas.drawTextOnPath("Click to select!", pathCCW, 0, 0, textPaint);
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(double angle, double radius)
    {
        previousTimeStamp = System.currentTimeMillis();
        startTouchTimeStamp = previousTimeStamp;
        previousAngle = angle;
        previousRadius = radius;
        isBeingTouched = true;

        //reset angle-based click lockout
        angleTurned = 0;

        //disable revolver selection mode on touch start
        revolverSelectionEnable = false;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(double angle, double radius)
    {
        if (isBeingTouched)
        {
            long currentTimeStamp = System.currentTimeMillis();

            //find difference in angles, correct for -180 - 180 crossing
            float angleDiff = (float) (angle - previousAngle);
            if (angleDiff > 180)
            {
                angleDiff -= 360;
            }
            else if (angleDiff < -180)
            {
                angleDiff += 360;
            }

            //rotate the wheel by the difference between the two angles
            degToSpin += angleDiff;

            //store the amount the wheel has been turned to turn off click
            angleTurned += Math.abs(angleDiff);

            //set velocity for cylinder on touch lift, degrees/second
            float timeDiff = currentTimeStamp - previousTimeStamp;
            angularV = (.8f * angularV) + (.2f * (angleDiff / timeDiff));

            previousTimeStamp = currentTimeStamp;
            previousAngle = angle;
            previousRadius = radius;
        }
    }

    // when ACTION_UP stop touch
    private void upTouch()
    {
//        Vibrator myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        isBeingTouched = false;
        long touchTime = System.currentTimeMillis() - startTouchTimeStamp;

        //a click is defined as a touch event that lasted less than 200 milliseconds, and doesn't turn the wheel
        if (touchTime < 200 && angleTurned < 10)
        {
            rouletteClickHandler();
        }

        //enable the revolver selection if the user spins the cylinder fast enough
        if (angularV > .50 || angularV < -.50)
        {
            revolverSelectionEnable = true;
        }
        else
        {
            revolverSelectionEnable = false;
        }
    }

    private void rouletteClickHandler()
    {
        //play gunshot noise/effect
        //this is using a much better external static class now
        SoundPlayer.playGunshot(context);

        // this math takes into account the current angle of the wheel to create a corrected touch angle
        double adjustedAngle = (angleToTouch + 180) - degToSpin;

        //overflow correction
        if (adjustedAngle < 0)
        {
            adjustedAngle += 360;
        }

        //begin button click listeners. Insert appropriate button onClick equivalent code within
        if (adjustedAngle <= 120 && adjustedAngle > 60)
        {
            //run code for option 1
            _appstate.rouletteSelection = 0;

            Intent intent = new Intent().setClass(getContext(), MapsActivity.class);
            ((Activity) getContext()).startActivity(intent);
        }
        else if (adjustedAngle <= 180 && adjustedAngle > 120)
        {
            //run code for option 2
            _appstate.rouletteSelection = 1;

            Intent intent = new Intent().setClass(getContext(), MapsActivity.class);
            ((Activity) getContext()).startActivity(intent);
        }
        else if (adjustedAngle <= 240 && adjustedAngle > 180)
        {
            //run code for option 3
            _appstate.rouletteSelection = 2;

            Intent intent = new Intent().setClass(getContext(), MapsActivity.class);
            ((Activity) getContext()).startActivity(intent);
        }
        else if (adjustedAngle <= 300 && adjustedAngle > 240)
        {
            //run code for option 4
            _appstate.rouletteSelection = 3;

            Intent intent = new Intent().setClass(getContext(), MapsActivity.class);
            ((Activity) getContext()).startActivity(intent);
        }
        else if (adjustedAngle <= 360 && adjustedAngle > 300)
        {
            //run code for option 5
            _appstate.rouletteSelection = 4;

            Intent intent = new Intent().setClass(getContext(), MapsActivity.class);
            ((Activity) getContext()).startActivity(intent);
        }
        else if (adjustedAngle <= 60 && adjustedAngle > 0)
        {
            //run code for option 6
            _appstate.rouletteSelection = 5;

            Intent intent = new Intent().setClass(getContext(), MapsActivity.class);
            ((Activity) getContext()).startActivity(intent);
        }
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        x = event.getX();
        y = event.getY();

        offsetX = x - cylinderCenterX;
        offsetY = y - cylinderCenterY;

        distanceToCenter = Math.sqrt((offsetX * offsetX) + (offsetY * offsetY));

        angleToTouch = (180 / Math.PI) * Math.atan2(offsetY, offsetX);

        //check to see if touch event falls within cylinder region
        if (distanceToCenter < cylinderCenterX && distanceToCenter > cylinderCenterX / 3)
        {
            //cylinder was where touch initiated
            //actions to execute if user starts touching wheel
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                startTouch(angleToTouch, distanceToCenter);
            }
        }
        //actions to execute as user moves finger around wheel
        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            moveTouch(angleToTouch, distanceToCenter);
        }
        //actions to execute when user lifts finger
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            upTouch();
        }

        return true;
    }
}