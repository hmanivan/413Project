package apitest;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.ozzca_000.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import YelpData.BusinessData;
import foodroulette.appstate.FoodRouletteApplication;
import foodroulette.asynctasks.YelpSearchAsyncTask;
import foodroulette.callbacks.BusinessRunnable;
import foodroulette.callbacks.LocationRunnable;
import foodroulette.locationutils.LocationService;
//import com.example.ozzca_000.R;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private Marker mMarker;

    private LocationRunnable _locationChangeCallBack;

    //store reference to global appstate, access application-wide data here
    private FoodRouletteApplication _appState;

    private void registerLocationChangeCallback() {
        if (_locationChangeCallBack == null) {
            _locationChangeCallBack = new LocationRunnable() {
                @Override
                public void runWithLocation(double latitude, double longitude) {
                    updateMarker(latitude, longitude);
                }
            };
            //register callback with appstate
            _appState.addLocationChangedCallback(_locationChangeCallBack);
        }
        LocationService.startLocationService(_appState);
    }

    private void unregisterLocationChangeCallback() {
        if (_locationChangeCallBack != null) {
            _appState.removeLocationChangedCallback(_locationChangeCallBack);
            _locationChangeCallBack = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting the reference to global appstate
        _appState = ((FoodRouletteApplication) getApplicationContext());

        registerLocationChangeCallback();

        // linking maps activity with the UI layout
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerLocationChangeCallback();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterLocationChangeCallback();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerLocationChangeCallback();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerLocationChangeCallback();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterLocationChangeCallback();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link # setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
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
                fetchBusinessData();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void fetchBusinessData() {
        new YelpSearchAsyncTask(new BusinessRunnable() {
            @Override
            public void runWithBusiness(BusinessData business) {
                //write code to do something with business, save it or whatever
            }
        }).execute("dildo", "San Francisco, CA");

    }

    private void updateMarker(double latitude, double longitude) {
        if (mMarker == null) {
            MarkerOptions options = new MarkerOptions();
            options.title("SamPlace");
            options.position(new LatLng(latitude, longitude));
            mMarker = mMap.addMarker(options);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
        } else {
            //If you move and the marker already exists, update your position and move the map
            mMarker.setPosition(new LatLng(latitude, longitude));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
        }

    }
}
