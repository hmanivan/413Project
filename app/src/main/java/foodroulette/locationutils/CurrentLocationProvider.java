package foodroulette.locationutils;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import foodroulette.appstate.FoodRouletteApplication;
import foodroulette.callbacks.LocationRunnable;

/**
 * Created by Sam on 7/26/2015.
 */
public class CurrentLocationProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private LocationRunnable _callback;
    private GoogleApiClient _googleApiClient;
    private FoodRouletteApplication _context;

    public CurrentLocationProvider(LocationRunnable callback, final FoodRouletteApplication context)
    {
        _callback = callback;
        _context = context;
    }

    public void start()
    {
        _googleApiClient = new GoogleApiClient.Builder(_context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        _googleApiClient.connect();

    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);

        if (lastLocation != null)
        {
            _callback.runWithLocation(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        int j = 0;
        j++;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        int j = 0;
        j++;
    }
}
