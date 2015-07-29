package apitest;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ozzca_000.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import YelpData.Business;
import YelpData.BusinessData;
import apitest.settings.ViewBadList;
import database.DbAbstractionLayer;
import foodroulette.appstate.FoodRouletteApplication;
import foodroulette.callbacks.BusinessRunnable;
import foodroulette.callbacks.LocationRunnable;
import foodroulette.locationutils.LocationTools;
import revolverwheel.revolver.RevolverActivity;

public class MapsActivity extends ActionBarActivity
{
    private Camera mCamera;
    private final static String LOG_TAG = "FlashLight";
    private Vibrator myVib;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private Marker mMarker;
    private Marker businessMarker;


    //private TextView businessTitleTextView = (TextView) findViewById(R.id.businessTitle);

    private LocationRunnable _locationChangeCallBack;

    //list of businesses to sort
    private List<Business> businessByDistance = new ArrayList<>();
    private int businessIndex = 0;
    //store reference to global appstate, access application-wide data here
    private FoodRouletteApplication _appState;
    final LinkedBlockingQueue<Runnable> shotQ = new LinkedBlockingQueue<>();


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

        //needed for displaying first business's name on maps activity
        //TextView businessTitleTextView = (TextView) findViewById(R.id.businessTitle);
       // businessTitleTextView.setText(businessByDistance.get(businessIndex).name);
        setTitle(businessByDistance.get(businessIndex).name);

        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        Button blacklist = (Button) findViewById(R.id.blacklistbutton); //blacklist button
        Button skip = (Button) findViewById(R.id.button);  //skip button
        Button back = (Button) findViewById(R.id.back);

        //start up the camera
        try
        {
            mCamera = Camera.open();
        } catch (Exception e)
        {
            Log.e(LOG_TAG, "Impossible d'ouvrir la camera");
        }

        final int singleShot = R.raw.single_shot;
        final Context finalThis = this;

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
        skip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { //skip button, goes to next business when pressed
                nextBusiness();
            }

        });

        blacklist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //code to run when click is on blacklist button
                downVoteBusiness();

                Runnable task = new Runnable()
                {
                    public void run()
                    {
                        MediaPlayer mp = MediaPlayer.create(MapsActivity.this, R.raw.single_shot);
                        mp.start();

                        myVib.vibrate(250);
                        if (mCamera != null)
                        {
                            Camera.Parameters params = mCamera.getParameters();
                            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            mCamera.setParameters(params);
                        }
                        if (mCamera != null)
                        {
                            Camera.Parameters params = mCamera.getParameters();
                            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            mCamera.setParameters(params);
                        }

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

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { //back button, goes back to revolver wheel
                Intent intent = new Intent(MapsActivity.this, RevolverActivity.class);
                startActivity(intent);
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
        getMenuInflater().inflate(R.menu.maps_badlist, menu);
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
        if (id == R.id.action_badList)
        {
            Intent myIntent = new Intent(MapsActivity.this, ViewBadList.class);
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

        //release the camera
        if (mCamera != null)
        {
            mCamera.release();
            mCamera = null;
        }
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

        //start up the camera
        try
        {
            mCamera = Camera.open();
        } catch (Exception e)
        {
            Log.e(LOG_TAG, "Impossible d'ouvrir la camera");
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        //release the camera
        if (mCamera != null)
        {
            mCamera.release();
            mCamera = null;
        }
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
                //display our location
                updateMarker(_appState.latitude, _appState.longitude);
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
                setMapCameraPosition(businessByDistance.get(businessIndex).location.coordinate.latitude, businessByDistance.get(businessIndex).location.coordinate.longitude);

//                TextView businessTitleTextView = (TextView) findViewById(R.id.businessTitle);
//                businessTitleTextView.setText(businessByDistance.get(businessIndex).name);
                setTitle(businessByDistance.get(businessIndex).name);
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

                for (int j = 0; j < businessCount; j++)
                {
                    Business business = businessData.businesses.get(j);

                    //get location of business
                    LatLng position = new LatLng(business.location.coordinate.latitude, business.location.coordinate.longitude);

                    //get distance from business to user
                    business.distanceToUser = offsetHypot(position.latitude, userLat, position.longitude, userLong);

                    //add business to array
                    businessByDistance.add(business);
                }

//Custom sorting class which compares businesses by distance to user
                class BusinessComparator implements Comparator<Business>
                {
                    @Override
                    public int compare(Business o1, Business o2)
                    {
                        return Double.compare(o1.distanceToUser, o2.distanceToUser);
                    }
                }

//sort list of businesses by distance to user
                Collections.sort(businessByDistance, new BusinessComparator());

                //old code which displays all businesses in category
                LatLng position = new LatLng(businessByDistance.get(businessIndex).location.coordinate.latitude, businessByDistance.get(businessIndex).location.coordinate.longitude);
                businessMarker = mMap.addMarker(new MarkerOptions()
                        .title(businessByDistance.get(businessIndex).name)
                        .position(position)
                        .icon(BitmapDescriptorFactory.defaultMarker(color)));
                businessMarker.isVisible();
                //setMapCameraPosition(position.latitude, position.longitude);
            }
        }, selectedCategory);

    }

    //a faster hypot function
    public double offsetHypot(double X, double x, double Y, double y)
    {
        double retVal = 0;

        retVal = Math.sqrt((X - x) * (X - x) + (Y - y) * (Y - y));

        return retVal;
    }

    public void nextBusiness()
    {

        if (businessIndex < businessByDistance.size() - 1)
        {
            //update marker with information for next business
            businessIndex++;
            Business business = businessByDistance.get(businessIndex);

            if (business != null)
            {
                LatLng position = new LatLng(business.location.coordinate.latitude, business.location.coordinate.longitude);
                businessMarker.setPosition(position);
                businessMarker.setTitle(business.name);

                setMapCameraPosition(position.latitude, position.longitude);
//                TextView businessTitleTextView = (TextView) findViewById(R.id.businessTitle);
//                businessTitleTextView.setText(business.name);
                setTitle(businessByDistance.get(businessIndex).name);

            }
        }
        else
        {
            businessIndex = 0;
        }
    }

    public void downVoteBusiness()
    {
        //get current business
        Business business = businessByDistance.get(businessIndex);

        if (business != null)
        {
            //add current business to blacklist
            DbAbstractionLayer.addRestaurant(business, this);

            if (businessIndex < businessByDistance.size() - 1)
            {
                //update marker with information for next business
                businessIndex++;

                //get the next business out
                business = businessByDistance.get(businessIndex);

                //get the position and title of the next business to update the marker
                LatLng position = new LatLng(business.location.coordinate.latitude, business.location.coordinate.longitude);
                businessMarker.setPosition(position);
                businessMarker.setTitle(business.name);

                setMapCameraPosition(position.latitude, position.longitude);
//                TextView businessTitleTextView = (TextView) findViewById(R.id.businessTitle);
//                businessTitleTextView.setText(business.name);
                setTitle(businessByDistance.get(businessIndex).name);
            }
            else
            {
                businessIndex = 0;
            }
        }
    }

    private void updateMarker(double latitude, double longitude)
    {
        if (mMarker == null)
        {
            MarkerOptions options = new MarkerOptions();
            options.title("You are here");
            options.position(new LatLng(latitude, longitude));
            mMarker = mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));
        }
        else
        {
            //If you move and the marker already exists, update your position
            mMarker.setPosition(new LatLng(latitude, longitude));
        }
        LocationTools.getCurrentLocation(_appState, new LocationRunnable()
        {
            @Override
            public void runWithLocation(double latitude, double longitude)
            {
                mMarker.setPosition(new LatLng(latitude, longitude));
            }
        });
    }

    public LatLngBounds getLatLngBounds(double lat1, double long1, double lat2, double long2)
    {
        LatLng latLng1, latLng2;

        if (lat1 < lat2)
        {
            if (long1 < long2)
            {
                latLng1 = new LatLng(lat1, long1);
                latLng2 = new LatLng(lat2, long2);
            }
            else
            {
                latLng1 = new LatLng(lat1, long2);
                latLng2 = new LatLng(lat2, long1);
            }
        }
        else
        {
            if (long1 < long2)
            {
                latLng1 = new LatLng(lat2, long1);
                latLng2 = new LatLng(lat1, long2);
            }
            else
            {
                latLng1 = new LatLng(lat2, long2);
                latLng2 = new LatLng(lat1, long1);
            }
        }
        return new LatLngBounds(latLng1, latLng2);
    }

    public void setMapCameraPosition(double latitude, double longitude)
    {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(
                getLatLngBounds(_appState.latitude, _appState.longitude, latitude, longitude),
                ((findViewById(R.id.map).getWidth()) / 3),
                ((findViewById(R.id.map).getHeight()) / 3),
                0));
    }

    public void shotThread()
    {
        try
        {
            mCamera = Camera.open();
        } catch (Exception e)
        {
            Log.e(LOG_TAG, "Impossible d'ouvrir la camera");
        }
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
        Runnable task = new Runnable()
        {
            public void run()
            {
                MediaPlayer mp = MediaPlayer.create(MapsActivity.this, R.raw.single_shot);

                try
                {
                    mCamera = Camera.open();
                } catch (Exception e)
                {
                    Log.e(LOG_TAG, "Impossible d'ouvrir la camera");
                }


                mp.start();
                myVib.vibrate(250);
                if (mCamera != null)
                {
                    Camera.Parameters params = mCamera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(params);
                }
                if (mCamera != null)
                {
                    Camera.Parameters params = mCamera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(params);
                }
                if (mCamera != null)
                {
                    mCamera.release();
                    mCamera = null;
                }
                try
                {
                    Thread.sleep(1429);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                mp.release();
//                        shot.onGunshot();
                if (mCamera != null)
                {
                    mCamera.release();
                    mCamera = null;
                }
            }
        };
        shotQ.add(task);
    }
}
