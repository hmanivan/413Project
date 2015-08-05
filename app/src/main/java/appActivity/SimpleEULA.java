package appActivity;

/**
 * Created by timbauer / Oswaldo Caballero on 7/28/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.example.ozzca_000.myapplication.R;

/**
 * borrowed from Donn Felker @ http://www.donnfelker.com/android-a-simple-eula-for-your-android-apps/
 */
public class SimpleEULA
{

    private String EULA_PREFIX = "eula";
    private Activity mActivity;
    private Context mContext;

    public SimpleEULA(Activity activity, Context context)
    {
        mActivity = activity;
        mContext = context;
    }

    private PackageInfo getPackageInfo()
    {
        PackageInfo pi = null;
        try
        {
            pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return pi;
    }

    public void showEULAIfNeeded()
    {
        Looper.prepare();

        PackageInfo versionInfo = getPackageInfo();

        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);

        //boolean switch which is set true, if EULA has been accepted before
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);

        if(!hasBeenShown)
        {
            //set the title for the EULA
            String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName;

            //Includes the updates as well so users know what changed.
            String message = mActivity.getString(R.string.eula);

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            // Mark this version as read.
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(eulaKey, true);
                            editor.commit();

                            //go to revolver activity
                            Intent intent = new Intent(mContext, revolverwheel.revolver.RevolverActivity.class);
                            mActivity.startActivity(intent);
                            mActivity.finish();
                            Looper.myLooper().quitSafely();

                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // Close the activity as they have declined the EULA
                            mActivity.finish();
                            Looper.myLooper().quitSafely();
                        }
                    });
            builder.create();
            builder.show();
        }
        else
        {
            //go to revolver activity
            Intent intent = new Intent(mContext, revolverwheel.revolver.RevolverActivity.class);

            //alt intent to go straight to post-roulette
            //Intent intent = new Intent(SplashScreen.this, MapsActivity.class);

            mActivity.startActivity(intent);

            mActivity.finish();
            Looper.myLooper().quitSafely();
        }
        Looper.loop();
    }
}