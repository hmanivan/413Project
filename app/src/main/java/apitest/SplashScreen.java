package apitest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ozzca_000.myapplication.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.concurrent.LinkedBlockingQueue;

import SoundUtils.SoundPlayer;
import YelpData.BusinessData;
import appActivity.SimpleEULA;
import database.DbAbstractionLayer;
import database.RestaurantDatabase;
import foodroulette.appstate.FoodRouletteApplication;
import foodroulette.asynctasks.YelpSearchAsyncTask;
import foodroulette.callbacks.BusinessRunnable;
import foodroulette.callbacks.LocationRunnable;
import foodroulette.locationutils.LocationTools;

import android.view.View;
import android.os.Vibrator;

public class SplashScreen extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    //create a runnable that allows us to come back after user enables location date
    private Runnable _locationEnableCallback;

    //store reference to global appstate, access application-wide data here
    private FoodRouletteApplication _appState;
    private RestaurantDatabase restaurantDatabase;
    private SQLiteDatabase restaurantDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sploosh);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // setting the reference to global appstate
        _appState = ((FoodRouletteApplication) getApplicationContext());

        //play the gun effect
        SoundPlayer.playGunshot(_appState);

        DbAbstractionLayer dbAbstractionLayer = DbAbstractionLayer.getDbAbstractionLayer();

        restaurantDatabase = RestaurantDatabase.getRestaurantDatabase(this);
        restaurantDb = restaurantDatabase.getWritableDatabase();

        Cursor restData = restaurantDb.rawQuery("SELECT * FROM " + restaurantDatabase.dbResTable, null);

        if (!(restData.getCount() > 0))
        {

            String[] tableColumns = new String[]{
                    restaurantDatabase.id,
                    restaurantDatabase.resaurantName,
                    restaurantDatabase.displayPhone,
                    restaurantDatabase.image_url,
                    restaurantDatabase.mobile_url,
                    restaurantDatabase.phone,
                    restaurantDatabase.rating,
                    restaurantDatabase.reviewCount,
            };

        }
       // DbAbstractionLayer.deleteAllData();
        restaurantDb.close();
        restaurantDatabase.close();
        restData.close();

    }

    public void onStart()
    {
        super.onStart();

        _locationEnableCallback = new Runnable()
        {
            @Override
            public void run()
            {
                //get current location, with runnable to execute code on completion
                LocationTools.getCachedLocation(_appState, new LocationRunnable()
                {
                    @Override
                    public void runWithLocation(final double latitude, final double longitude)
                    {
                        //run this code once location data comes in
                        new Thread()
                        {

                            @Override
                            public void run()
                            {
                                String[] terms = new String[]{"Indian", "American", "Chinese", "Italian", "Japanese", "Mexican"};
                                for (int i = 0; i < 6; i++)
                                {
                                    //create index for when callback comes back
                                    final int callbackIndex = i;

                                    new YelpSearchAsyncTask(new BusinessRunnable()
                                    {
                                        @Override
                                        public void runWithBusiness(BusinessData business)
                                        {
                                            _appState.onBusinessDataReceived(business, callbackIndex);
                                        }
                                    }).execute(terms[i], Double.toString(latitude), Double.toString(longitude));
                                }

                                //go to roulette screen when position data comes back
                                showEULAmessage();
                            }

                        }.start();
                    }
                });
            }
        };

        //check to see if location services are enabled
        LocationTools.checkLocationServicesEnabled(this, _locationEnableCallback);

    }

    public void onRestart()
    {
        super.onRestart();
    }

    public void onResume()
    {
        super.onResume();
    }

    public void onPause()
    {
        super.onPause();
    }

    public void onStop()
    {
        super.onStop();
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1000 && resultCode == RESULT_OK)
        {
        _locationEnableCallback.run();
        }
    }

    @Override
    public void onConnected(Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    private void showEULAmessage()
    {
       SimpleEULA eula = new SimpleEULA(this, this);
        eula.showEULAIfNeeded();
    }

}
