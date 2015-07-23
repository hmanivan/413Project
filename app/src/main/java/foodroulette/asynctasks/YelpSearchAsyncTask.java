package foodroulette.asynctasks;

import android.os.AsyncTask;

import com.google.gson.Gson;

import YelpAPI.YelpAPI;
import YelpData.BusinessData;
import foodroulette.callbacks.BusinessRunnable;

/**
 * Created by Sam on 6/27/2015.
 */
public class YelpSearchAsyncTask extends AsyncTask<String, Void, Object> {
    private BusinessRunnable _callback;

    public YelpSearchAsyncTask(BusinessRunnable callback) {
        _callback = callback;
    }


    @Override
    protected Object doInBackground(String... params) {
        //android.os.Debug.waitForDebugger();

        //disseminate the params to get the parameters for the yelp search

        String response;
        String term = params[0];
        //initialize new instance of YelpAPI class
        YelpAPI api = YelpAPI.YelpInit();

        if (params.length == 2)
        {
            //get response by location string data
            String location = params[1];
            response = api.searchForBusinessesByLocation(term, location);
        } else
        {
            //get response by Lat/Long location data
            String latitude = params[1];
            String longitude = params[2];
            response = api.searchForBusinessesByGPS(term, latitude, longitude);
        }

        BusinessData business;
        business = new Gson().fromJson(response, BusinessData.class);

        //store term used, this will be used to select which particular Business Data to use
        business.term = term;

        return business;
    }

    @Override
    protected void onPostExecute(Object result) {
        _callback.runWithBusiness((BusinessData) result);
    }

}
