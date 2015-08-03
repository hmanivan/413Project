package SoundUtils;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.example.ozzca_000.myapplication.R;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Sam on 8/1/2015.
 */
public class SoundPlayer
{
    private static Object lockObject = new Object();

    private static Boolean threadsInitialized = false;

    private static final LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    //resources for controlling sound, flash, vibration
    private final static String LOG_TAG = "FlashLight";
    private static Vibrator vibrator = null;
    private static Camera mCamera = null;

    public static void playGunshot(final Context context)
    {
//get the threads fired up if needed
        maybeInitializeThreads();

        //play gunshot effect
        Runnable task = new Runnable()
        {
            public void run()
            {
                MediaPlayer mp = MediaPlayer.create(context, R.raw.single_shot);
                mp.start();

                if (mCamera != null)
                {
                    Camera.Parameters params = mCamera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(params);

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(params);
                }

                //run the vibrator
                maybeInitializeVibrator(context);
                vibrator.vibrate(250);

                try
                {
                    Thread.sleep(1179);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                mp.release();
            }
        };
        taskQueue.add(task);
    }

    private static void maybeInitializeThreads()
    {
        if (!threadsInitialized)
        {
            synchronized (lockObject)
            {
                if (!threadsInitialized)
                {
                    //open the camera
                    try
                    {
                        mCamera = Camera.open();
                    } catch (Exception e)
                    {
                        Log.e(LOG_TAG, "Impossible d'ouvrir la camera");
                    }

                    //fire up Georges threadpool (dope)
                    for (int i = 0; i < 50; i++)
                    {
                        new Thread()
                        {
                            public void run()
                            {
                                while (true)
                                {
                                    try
                                    {
                                        Runnable task = taskQueue.take();
                                        task.run();
                                    } catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.start();

                        threadsInitialized = true;
                    }
                }
            }
        }
    }

    private static void maybeInitializeVibrator(Context context)
    {
        if (vibrator == null)
        {
            synchronized (lockObject)
            {
                if (vibrator == null)
                {
                    vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                }
            }
        }
    }

}
