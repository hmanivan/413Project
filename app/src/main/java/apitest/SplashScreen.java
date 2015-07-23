package apitest;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ozzca_000.myapplication.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import YelpData.BusinessData;
import database.DbAbstractionLayer;
import database.RestaurantDatabase;
import foodroulette.appstate.FoodRouletteApplication;
import foodroulette.asynctasks.YelpSearchAsyncTask;
import foodroulette.callbacks.BusinessRunnable;
import foodroulette.callbacks.LocationRunnable;
import foodroulette.locationutils.LocationTools;

public class SplashScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    //store reference to global appstate, access application-wide data here
    private FoodRouletteApplication _appState;
    private RestaurantDatabase restaurantDatabase;
    private SQLiteDatabase restaurantDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // setting the reference to global appstate
        _appState = ((FoodRouletteApplication) getApplicationContext());



        DbAbstractionLayer dbAbstractionLayer = DbAbstractionLayer.getDbAbstractionLayer();

        restaurantDatabase = RestaurantDatabase.getRestaurantDatabase(this);
        restaurantDb = restaurantDatabase.getWritableDatabase();


        Cursor restData = restaurantDb.rawQuery("SELECT * FROM " + restaurantDatabase.dbResTable, null);

        if (!(restData.getCount() > 0)){

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

            ContentValues dummyRestaurant = new ContentValues();

            dummyRestaurant.put(tableColumns[0], -1);
            dummyRestaurant.put(tableColumns[1], "dummyRestaurant");

            for(int i = 2; i < tableColumns.length; i++){
                dummyRestaurant.put(tableColumns[i], "");
            }

            restaurantDb.insert(RestaurantDatabase.dbResTable, null, dummyRestaurant);

            showEULAmessage();

        }
        restaurantDb.close();
        restaurantDatabase.close();
        restData.close();

    }

    public void onStart()
    {
        super.onStart();
        //check to see if location services are enabled
        LocationTools.checkLocationServicesEnabled(this);

        //get current location, with runnable to execute code on completion
        LocationTools.getCurrentLocation(_appState, new LocationRunnable()
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
                        String[] terms = new String[]{"American", "Mexican", "Italian", "Chinese", "Japanese", "Breakfast"};
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
                        Intent intent = new Intent(SplashScreen.this, revolverwheel.revolver.RevolverActivity.class);

                        //alt intent to go straight to post-roulette
                        //Intent intent = new Intent(SplashScreen.this, MapsActivity.class);

                        SplashScreen.this.startActivity(intent);
                    }

                }.start();
            }
        });

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

    private void showEULAmessage(){
        //TODO
    }
}
