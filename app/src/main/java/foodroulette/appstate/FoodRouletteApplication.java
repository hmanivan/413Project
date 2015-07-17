package foodroulette.appstate;

import android.app.Application;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import foodroulette.callbacks.LocationRunnable;

/**
 * Created by Sam on 6/27/2015.
 */
public class FoodRouletteApplication extends Application {
    //This is the app state (tumor) file where all application data can be stored, until the app is terminated
    // all non-primitives must be thread-safe

    //Create a concurrent list of location callbacks
    //This list is thread-safe, and can be accessed and manipulated from any thread
    //This list comtains the subscriobers to location data, who should be updated when location changes
    Queue<LocationRunnable> _locationChangedCallbacks = new ConcurrentLinkedQueue<LocationRunnable>();

    public boolean addLocationChangedCallback(LocationRunnable callback) {
        return _locationChangedCallbacks.add(callback);
    }

    public boolean removeLocationChangedCallback(LocationRunnable callback) {
        return _locationChangedCallbacks.remove(callback);
    }

    //On location change event, step through list of subscribers and update marker positions
    public void onLocationChange(double latitude, double longitude) {
        for (LocationRunnable runnable : _locationChangedCallbacks) {
            runnable.runWithLocation(latitude, longitude);
        }

    }

    public Thread locationServicesThread;


}



