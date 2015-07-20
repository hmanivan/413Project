package foodroulette.locationutils;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import foodroulette.appstate.FoodRouletteApplication;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Sam on 6/27/2015.
 */
public class LocationService
{

    public static void startLocationService(final FoodRouletteApplication appState)
    {
        if (appState.locationServicesThread == null) {
            appState.locationServicesThread = new Thread()
            {
                @Override
                public void run()
                {
                    android.os.Debug.waitForDebugger();
                    LocationListener locationListener = new LocationListener()
                    {
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras)
                        {
                            //TODO(Darin) put stuff here
                        }

                        @Override
                        public void onProviderEnabled(String provider)
                        {
                            //TODO(Sam) put stuff here

                        }

                        @Override
                        public void onProviderDisabled(String provider)
                        {
                            //TODO(Sam) put stuff here
                        }

                        @Override
                        public void onLocationChanged(Location location)
                        {
                            //sends latest location change to appstate, to update callbacks
                            appState.onLocationChange(location.getLatitude(), location.getLongitude());
                        }

                    };


                    LocationManager locationManager = (LocationManager) appState.getSystemService(appState.LOCATION_SERVICE);
                    //TODO (Darin) put stuff here - configure min time and min distance

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300,
                            0, locationListener, appState.getMainLooper());
                }
            };

            appState.locationServicesThread.start();

        }
    }

}
