//package gunstuff;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;


//import appstate.FoodRouletteApplication;
//import callbacks.BusinessRunnable;

/**
 * Created by george on 7/7/15.
 */


//public class AsyncGun extends AsyncTask<String, Void, Object> implements OnClickListener {
//    Button btn;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.blankactiv);
//        btn = (Button) findViewById(R.id.boton);
//        // because we implement OnClickListener we only have to pass "this"
//        // (much easier)
//        btn.setOnClickListener(this);
//    }
//
//    public void onClick(View view) {
//        // detect the view that was "clicked"
//        switch (view.getId()) {
//            case R.id.boton:
//                new LongOperation().execute("");
//                break;
//        }
//    }
//
//    private class LongOperation extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            for (int i = 0; i < 5; i++) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    Thread.interrupted();
//                }
//            }
//            return "Executed";
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            TextView txt = (TextView) findViewById(R.id.boton);
//            txt.setText("Executed"); // txt.setText(result);
//            // might want to change "executed" for the returned string passed
//            // into onPostExecute() but that is upto you
//        }
//
//        @Override
//        protected void onPreExecute() {}
//
//        @Override
//        protected void onProgressUpdate(Void... values) {}
//    }
//
//
//
//
//
//    private BusinessRunnable _callback;
//
//    public YelpSearchAsyncTask(BusinessRunnable callback)
//    {
//        _callback = callback;
//    }
//
//
//    @Override
//    protected Object doInBackground(String... params)
//    {
//        //android.os.Debug.waitForDebugger();
//
//        //disseminate the params to get the parameters for the yelp search
//        String term = params[0];
//        String location = params[1];
//
//        //initialize new instance of YelpAPI class
//        YelpAPI api = YelpAPI.YelpInit();
//
//
//        String response = api.searchForBusinessesByLocation(term, location);
//        BusinessData business;
//        business = new Gson().fromJson(response, BusinessData.class);
//        return business;
//    }
//
//    @Override
//    protected void onPostExecute(Object result)
//    {
//        _callback.runWithBusiness((BusinessData)result);
//    }
//}