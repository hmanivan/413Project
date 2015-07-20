package apitest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ozzca_000.myapplication.R;
import foodroulette.appstate.FoodRouletteApplication;
import foodroulette.asynctasks.YelpSearchAsyncTask;
import foodroulette.callbacks.BusinessRunnable;
import foodroulette.callbacks.LocationRunnable;
import foodroulette.locationutils.LocationTools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import YelpData.BusinessData;

public class SplashScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    //store reference to global appstate, access application-wide data here
    private FoodRouletteApplication _appState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // setting the reference to global appstate
        _appState = ((FoodRouletteApplication) getApplicationContext());

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
                        for(int i = 0; i < 6; i++)
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
                    }

                }.start();

//                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
//                startActivity(intent);
            }

        });
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        SplashScreen.this.startActivity(intent);
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
}
