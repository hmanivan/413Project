package apitest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import YelpData.Business;
import YelpData.BusinessData;
import apitest.settings.SettingsActivity;
import database.DbAbstractionLayer;
import foodroulette.appstate.FoodRouletteApplication;
import foodroulette.asynctasks.YelpSearchAsyncTask;
import foodroulette.callbacks.BusinessRunnable;
import foodroulette.callbacks.LocationRunnable;
//import com.example.ozzca_000.R;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import YelpData.Business;

import static database.DbAbstractionLayer.addRestaurant;

public class MapsActivity extends ActionBarActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private Marker mMarker;
    private Marker businessMarker;

    private LocationRunnable _locationChangeCallBack;

    //list of businesses to sort
    private List<Business> businessByDistance = new ArrayList<>();
    private int businessIndex = 0;
    //store reference to global appstate, access application-wide data here
    private FoodRouletteApplication _appState;

    private Camera mCamera;
    private final static String LOG_TAG = "FlashLight";
    final LinkedBlockingQueue<Runnable> shotQ = new LinkedBlockingQueue<>();

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
        Button booton = (Button) findViewById(R.id.button);
        final int singleShot = R.raw.single_shot;
        final Context finalThis = this;

        final LinkedBlockingQueue<Runnable> shotQ = new LinkedBlockingQueue<>();

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
        booton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent myIntent = new Intent(MapsActivity.this, DownVotedList.class);
//        myIntent.putExtra("key", value); //Optional parameters
                MapsActivity.this.startActivity(myIntent);
            }

        });

        boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                try{
                    mCamera = Camera.open();
                } catch( Exception e ){
                    Log.e(LOG_TAG, "Impossible d'ouvrir la camera");
                }
                //code to run when click is on blacklist button
                nextBusiness();

                Runnable task = new Runnable() {
                    public void run() {
                        MediaPlayer mp = MediaPlayer.create(MapsActivity.this, R.raw.single_shot);
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

                        try {
                            Thread.sleep(1429);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mp.release();
                    }
                };
                shotQ.add(task);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Business downVoted = new Business();

                                downVoted.id = "100";
                                downVoted.name="Times House`";
                                downVoted.display_phone=" ";
                                downVoted.image_url=" ";
                                downVoted.mobile_url=" ";
                                downVoted.phone="";
                                downVoted.rating=0;
                                downVoted.review_count=0;



                                if (DbAbstractionLayer.isRestaurantInBlockedList("100", MapsActivity.this)) {
                                    Dialog d = new Dialog(MapsActivity.this);
                                    d.setContentView(R.layout.popupview);
                                    TextView txt = (TextView) d.findViewById(R.id.editText);
                                    txt.setText(getString(R.string.message));
                                    d.show();
                                } else {

                                    DbAbstractionLayer.addRestaurant(downVoted, MapsActivity.this);
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            } //end onClick
        });
        if( mCamera != null ){
            mCamera.release();
            mCamera = null;
        }
    }

//    //-------------------------------------------------------------------------------------------//
//    //  This is for the settings fragment tab that we want to implement (following 2 methods)    //
//    //-------------------------------------------------------------------------------------------//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent myIntent = new Intent(MapsActivity.this, SettingsActivity.class);
////        myIntent.putExtra("key", value); //Optional parameters
//            MapsActivity.this.startActivity(myIntent);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    //-------------------------------------------------------------------------------------------//
//    //-------------------------------------------------------------------------------------------//

    @Override
    protected void onResume() {
        super.onResume();
//        registerLocationChangeCallback();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterLocationChangeCallback();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        registerLocationChangeCallback();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        registerLocationChangeCallback();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStop() {
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
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
//                fetchBusinessData();

                //display our location
                updateMarker(_appState.latitude, _appState.longitude);
                setupBusinessDataCallbacks();
            }
        }
    }

    /*private void setupBusinessDataCallbacks() {
        for (int i = 0; i < 6; i++) {
            //create random color
            final float color = 51f + (51f * (float) i);*/
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
        //blacklist current business
        //TODO: put blacklist code <HERE>

        if (businessIndex < businessByDistance.size())
        {
            //update marker with information for next business
            Business business = businessByDistance.get(businessIndex);
            businessIndex++;
            LatLng position = new LatLng(business.location.coordinate.latitude, business.location.coordinate.longitude);
            businessMarker.setPosition(position);
            businessMarker.setTitle(business.name);

            setMapCameraPosition(position.latitude, position.longitude);
        }
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
    private void updateMarker(double latitude, double longitude) {
        if (mMarker == null) {
            MarkerOptions options = new MarkerOptions();
            options.title("You are here");
            options.position(new LatLng(latitude, longitude));
            mMarker = mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        }
        else
        {
            //If you move and the marker already exists, update your position
            mMarker.setPosition(new LatLng(latitude, longitude));
        }
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
}
