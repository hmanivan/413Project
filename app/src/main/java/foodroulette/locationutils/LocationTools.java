package foodroulette.locationutils;

import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import apitest.SplashScreen;
import foodroulette.appstate.FoodRouletteApplication;
import foodroulette.callbacks.LocationRunnable;

/**
 * Created by Sam on 7/17/2015.
 */
public class LocationTools
{

    public static void checkLocationServicesEnabled(final SplashScreen activity, final Runnable callback)
    {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(activity)
                .addOnConnectionFailedListener(activity).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(1 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        callback.run();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try
                        {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, 1000);
                        } catch (IntentSender.SendIntentException e)
                        {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    public static void getCurrentLocation(final FoodRouletteApplication context, final LocationRunnable callback)
    {
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
                context.onLocationChange(location.getLatitude(), location.getLongitude());

                callback.runWithLocation(location.getLatitude(), location.getLongitude());
            }

        };

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        //TODO (Darin) put stuff here - configure min time and min distance

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, context.getMainLooper());
    }

    public static void getCachedLocation(final FoodRouletteApplication context, final LocationRunnable callback)
    {
        CurrentLocationProvider provider = new CurrentLocationProvider(new LocationRunnable()
        {
            @Override
            public void runWithLocation(double latitude, double longitude)
            {
                context.onLocationChange(latitude, longitude);

                callback.runWithLocation(latitude, longitude);
            }
        }, context);

        provider.start();

    }

}
