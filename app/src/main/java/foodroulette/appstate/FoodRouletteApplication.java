package foodroulette.appstate;

import android.app.Application;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import foodroulette.callbacks.LocationRunnable;

import android.location.LocationManager;

import foodroulette.callbacks.BusinessRunnable;
import foodroulette.callbacks.LocationRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;

import YelpData.BusinessData;

/**
 * Created by Sam on 6/27/2015.
 */
public class FoodRouletteApplication extends Application {
    //This is the app state (tumor) file where all application data can be stored, until the app is terminated
    // all non-primitives must be thread-safe

    //Create a concurrent list of location callbacks
    //This list is thread-safe, and can be accessed and manipulated from any thread
    //This list comtains the subscriobers to location data, who should be updated when location changes
    private Queue<LocationRunnable> _locationChangedCallbacks = new ConcurrentLinkedQueue<LocationRunnable>();

    //create list of business runnables for batching yelp calls
    //arraylist is thread-safe on reads
    private List<Queue<BusinessRunnable>> _businessDataCallbacks = new ArrayList<Queue<BusinessRunnable>>(Arrays.asList(
            new ConcurrentLinkedQueue<BusinessRunnable>(),
            new ConcurrentLinkedQueue<BusinessRunnable>(),
            new ConcurrentLinkedQueue<BusinessRunnable>(),
            new ConcurrentLinkedQueue<BusinessRunnable>(),
            new ConcurrentLinkedQueue<BusinessRunnable>(),
            new ConcurrentLinkedQueue<BusinessRunnable>()
    ));

    private Object[] _lockObjects = new Object[]
            {new Object(), new Object(), new Object(), new Object(), new Object(), new Object()};

    private ConcurrentHashMap<String, BusinessData> _businessDataLookup = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer, BusinessData> _businessDataIndexLookup = new ConcurrentHashMap<>();


    public BusinessData getBusinessDataByTerm(String term)
    {
        return _businessDataLookup.get(term);
    }

    public boolean addBusinessDataCallback(BusinessRunnable callback, int index)
    {
        boolean retVal = false;

        synchronized (_lockObjects[index])
        {
            //TODO: SAM put a check to make sure the index is within bounds
            //check to see if indexed item is already back from the network
            BusinessData businessData = _businessDataIndexLookup.get(index);
            if (businessData != null)
            {
                callback.runWithBusiness(businessData);
            }
            retVal = _businessDataCallbacks.get(index).add(callback);
        }

        return retVal;
    }

    public boolean removeBusinessDataCallback(BusinessRunnable callback, int index)
    {
        //TODO: SAM put a check to make sure the index is within bounds
        return _businessDataCallbacks.get(index).remove(callback);
    }

    public void onBusinessDataReceived(BusinessData businessData, int index)
    {
        _businessDataLookup.put(businessData.term, businessData);


        synchronized (_lockObjects[index])
        {
            _businessDataIndexLookup.put(index, businessData);

            Queue<BusinessRunnable> callbacks = _businessDataCallbacks.get(index);

            for (BusinessRunnable runnable : callbacks)
            {
                runnable.runWithBusiness(businessData);
            }
        }

    }


    public boolean addLocationChangedCallback(LocationRunnable callback) {
        return _locationChangedCallbacks.add(callback);
    }

    public boolean removeLocationChangedCallback(LocationRunnable callback) {
        return _locationChangedCallbacks.remove(callback);
    }

    //On location change event, step through list of subscribers and update marker positions
    public void onLocationChange(double latitude, double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;

        for (LocationRunnable runnable : _locationChangedCallbacks) {
            runnable.runWithLocation(latitude, longitude);
        }

    }

    public Thread locationServicesThread;

    public double latitude;
    public double longitude;

    public int rouletteSelection = -1;
}



