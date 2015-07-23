package revolverwheel.revolver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;


import com.example.ozzca_000.myapplication.R;

import java.util.concurrent.LinkedBlockingQueue;

import apitest.MapsActivity;
import foodroulette.appstate.FoodRouletteApplication;

/**
 * Created by Sam on 7/6/2015.
 */
public class CanvasView extends SurfaceView
{

    private View myView;
    private Bitmap mBitmap;
    private Bitmap samBetterBitmap;

    Context context;

    public FoodRouletteApplication _appstate;

    private Paint mPaint;
    private long timeStamp = 0;
    private float degToSpin = 0;
    private float angularV = 0;

    private static float cylinderCenterX = 504;
    private static float cylinderCenterY = 515;

    private static double previousAngle;
    private static double previousRadius;
    private static long startTouchTimeStamp;
    private static long previousTimeStamp;
    private static Boolean isBeingTouched = false;

    //variables used in rotation function
    float x;
    float y;
    double offsetX;
    double offsetY;
    double distanceToCenter;
    double angleToTouch;

    //for click detection
    float angleTurned = 0;


    final LinkedBlockingQueue<Runnable> shotQ = new LinkedBlockingQueue<>();
    private Camera mCamera;
    private final static String LOG_TAG = "FlashLight";

    public void setSamBitmap(Bitmap bitmap)
    {
        samBetterBitmap = bitmap;

        //auto scaled center of the cylinder (actually works, cannot believe it)
        cylinderCenterX = (bitmap.getWidth() / 2) - ((8 * bitmap.getWidth()) / 1024);
        cylinderCenterY = (bitmap.getHeight() / 2) + ((3 * bitmap.getHeight()) / 1024);
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
        final CanvasView hardthis = this;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    long currTime = System.currentTimeMillis();

                    if (!isBeingTouched && timeStamp != 0)
                    {
                        //when user lifts finger, run this code to decay angular velocity and spin down the wheel
                        long timeDiff = currTime - timeStamp;
                        degToSpin += angularV * timeDiff;
                        //slow rotation
                        angularV *= .98;

                        while (degToSpin >= 360f)
                        {
                            degToSpin -= 360f;
                        }
                        while (degToSpin <= -360f)
                        {
                            degToSpin += 360f;
                        }
                        //canvas.rotate(degToSpin);
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
        }).start();
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
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
        //canvas.drawBitmap(samBetterBitmap, new Rect(100, 100, 150, 150), new Rect(0, 0, 50, 50), null);
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
            } else if (angleDiff < -180)
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

            startSoundThread();

            RevolverActivity.myVib.vibrate(250);
        }
    }

    private void rouletteClickHandler()
    {
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
        } else if (adjustedAngle <= 180 && adjustedAngle > 120)
        {
            //run code for option 2
            _appstate.rouletteSelection = 1;

            Intent intent = new Intent().setClass(getContext(), MapsActivity.class);
            ((Activity) getContext()).startActivity(intent);
        } else if (adjustedAngle <= 240 && adjustedAngle > 180)
        {
            //run code for option 3
            _appstate.rouletteSelection = 2;

            Intent intent = new Intent().setClass(getContext(), MapsActivity.class);
            ((Activity) getContext()).startActivity(intent);
        } else if (adjustedAngle <= 300 && adjustedAngle > 240)
        {
            //run code for option 4
            _appstate.rouletteSelection = 3;

            Intent intent = new Intent().setClass(getContext(), MapsActivity.class);
            ((Activity) getContext()).startActivity(intent);
        } else if (adjustedAngle <= 360 && adjustedAngle > 300)
        {
            //run code for option 5
            _appstate.rouletteSelection = 4;

            Intent intent = new Intent().setClass(getContext(), MapsActivity.class);
            ((Activity) getContext()).startActivity(intent);
        } else if (adjustedAngle <= 60 && adjustedAngle > 0)
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

        //TODO: Sam- possibly figure out how to do this with trig
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

    public void startSoundThread() {

//        final Context finalThis = this;
        for (int i = 0; i < 50; i++) {
            new Thread() {
                public void run() {
                    while (true) {
                        try {
                            Runnable task = shotQ.take();
                            task.run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        Runnable task = new Runnable() {
            public void run() {
                MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.single_shot);
                try{
                    mCamera = Camera.open();
                } catch( Exception e ){
                    Log.e(LOG_TAG, "Impossible d'ouvrir la camera");
                }
                mp.start();

                if (mCamera != null) {
                    Camera.Parameters params = mCamera.getParameters();
                    params.setFlashMode( Camera.Parameters.FLASH_MODE_TORCH );
                    mCamera.setParameters( params );
                }
                if( mCamera != null ){
                    Camera.Parameters params = mCamera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters( params );
                }
                if( mCamera != null ){
                    mCamera.release();
                    mCamera = null;
                }
                try {
                    Thread.sleep(1429);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mp.release();
//                        shot.onGunshot();
            }
        };
        shotQ.add(task);
    }
}