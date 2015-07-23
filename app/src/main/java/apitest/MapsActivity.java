package apitest;

import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ozzca_000.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import YelpData.Business;
import YelpData.BusinessData;
import apitest.settings.SettingsActivity;
import foodroulette.appstate.FoodRouletteApplication;
import foodroulette.asynctasks.YelpSearchAsyncTask;
import foodroulette.callbacks.BusinessRunnable;
import foodroulette.callbacks.LocationRunnable;
//import com.example.ozzca_000.R;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import YelpData.Business;

public class MapsActivity extends ActionBarActivity
{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private Marker mMarker;

    private LocationRunnable _locationChangeCallBack;

    //store reference to global appstate, access application-wide data here
    private FoodRouletteApplication _appState;

//    private void registerLocationChangeCallback() {
//        if (_locationChangeCallBack == null) {
//            _locationChangeCallBack = new LocationRunnable() {
//                @Override
//                public void runWithLocation(double latitude, double longitude) {
//                    updateMarker(latitude, longitude);
//                }
//            };
//            //register callback with appstate
//            _appState.addLocationChangedCallback(_locationChangeCallBack);
//        }
//        LocationService.startLocationService(_appState);
//    }
//
//    private void unregisterLocationChangeCallback() {
//        if (_locationChangeCallBack != null) {
//            _appState.removeLocationChangedCallback(_locationChangeCallBack);
//            _locationChangeCallBack = null;
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // setting the reference to global appstate
        _appState = ((FoodRouletteApplication) getApplicationContext());

//        registerLocationChangeCallback();

        // linking maps activity with the UI layout
        setContentView(R.layout.activity_maps);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setUpMapIfNeeded();

        Button boton = (Button) findViewById(R.id.blacklistbutton);

        final int singleShot = R.raw.single_shot;
        final Context finalThis = this;

        final LinkedBlockingQueue<Runnable> shotQ = new LinkedBlockingQueue<>();

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
                            Runnable task = shotQ.take();
                            task.run();
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

        boton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Runnable task = new Runnable()
                {
                    public void run()
                    {
                        MediaPlayer mp = MediaPlayer.create(finalThis, singleShot);
                        mp.start();
                        try
                        {
                            Thread.sleep(1429);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        mp.release();
                    }
                };
                shotQ.add(task);
            }
        });
    }

    //-------------------------------------------------------------------------------------------//
    //  This is for the settings fragment tab that we want to implement (following 2 methods)    //
    //-------------------------------------------------------------------------------------------//

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent myIntent = new Intent(MapsActivity.this, SettingsActivity.class);
//        myIntent.putExtra("key", value); //Optional parameters
            MapsActivity.this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //-------------------------------------------------------------------------------------------//
    //-------------------------------------------------------------------------------------------//

    @Override
    protected void onResume()
    {
        super.onResume();
//        registerLocationChangeCallback();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
//        unregisterLocationChangeCallback();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
//        registerLocationChangeCallback();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
//        registerLocationChangeCallback();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
//        unregisterLocationChangeCallback();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link # setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded()
    {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null)
        {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
            {
//                fetchBusinessData();

                //display our location
                updateMarker(37.721627, -122.4750291);
                setupBusinessDataCallbacks();
            }
        }
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null)
        {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
            {
//                fetchBusinessData();

                //display our location
                updateMarker(_appState.latitude, _appState.longitude);
                setupBusinessDataCallbacks();
            }
        }
    }

    private void setupBusinessDataCallbacks()
    {
        int selectedCategory = _appState.rouletteSelection;
        //choose different color for each category
        final float color = 51f + (51f * (float) selectedCategory);
        _appState.addBusinessDataCallback(new BusinessRunnable()
        {
            @Override
            public void runWithBusiness(BusinessData businessData)
            {
                //store users location for comparison
                double userLat = _appState.latitude;
                double userLong = _appState.longitude;

                int businessCount = businessData.businesses.size();

                List<Business> businessByDistance = new ArrayList<>();

                for (int j = 0; j < businessCount; j++)
                {
                    Business business = businessData.businesses.get(j);

                    //get location of business
                    LatLng position = new LatLng(business.location.coordinate.latitude, business.location.coordinate.longitude);

                    //get distance from business to user
                    business.distanceToUser = offsetHypot(position.latitude, userLat, position.longitude, userLong);

                    //add business to array
                    businessByDistance.add(business);

                    //old code which displays all businesses in category
                    mMap.addMarker(new MarkerOptions()
                            .title(business.name)
                            .position(position)
                            .icon(BitmapDescriptorFactory.defaultMarker(color)));
                }

                //Custom sorting class which compares businesses by distance to user
                class BusinessComparator implements Comparator<Business> {
                    @Override
                    public int compare(Business o1, Business o2)
                    {
                        return Double.compare(o1.distanceToUser, o2.distanceToUser);
                    }
                }

                //sort list of businesses by distance to user
                Collections.sort(businessByDistance, new BusinessComparator());
            }
        }, selectedCategory);

    }

    //a faster hypot function
    public double offsetHypot(double X, double x, double Y, double y)
    {
        double retVal = 0;

        retVal = Math.sqrt((X-x)*(X-x) + (Y-y)*(Y-y));

        return retVal;
    }
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
//    private void fetchBusinessData() {
//        new YelpSearchAsyncTask(new BusinessRunnable() {
//            @Override
//            public void runWithBusiness(BusinessData business) {
//                //write code to do something with business, save it or whatever
//            }
//        }).execute("dildo", "San Francisco, CA");
//
//    }
    private void updateMarker(double latitude, double longitude)
    {
        if (mMarker == null)
        {
            MarkerOptions options = new MarkerOptions();
            options.title("SamPlace");
            options.position(new LatLng(latitude, longitude));
            mMarker = mMap.addMarker(options);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
        }
        else
        {
            //If you move and the marker already exists, update your position and move the map
            mMarker.setPosition(new LatLng(latitude, longitude));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
        }
    }
}
