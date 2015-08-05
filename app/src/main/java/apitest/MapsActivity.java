package apitest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.ozzca_000.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import GoogleAPI.DirectionsRunnable;
import GoogleAPI.GoogleMapDirections;
import SoundUtils.SoundPlayer;
import UberAPI.UberData;
import YelpData.Business;
import YelpData.BusinessData;
import database.DbAbstractionLayer;
import foodroulette.appstate.FoodRouletteApplication;
import foodroulette.callbacks.BusinessRunnable;
import foodroulette.callbacks.LocationRunnable;
import foodroulette.locationutils.LocationTools;

import com.android.volley.*;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;

import org.json.JSONObject;
//import org.scribe.model.Response;


public class MapsActivity extends ActionBarActivity
{
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private Marker mMarker;
    private Marker businessMarker;

    private GoogleMapDirections mapDirections;
    private Polyline navLine;


    private static RequestQueue mQueue;

    //list of businesses to sort
    private List<Business> yelpResults = new ArrayList<>();
    private Business currentBusiness;
    private int businessIndex = 0;

    private TextView businessName;

    //store reference to global appstate, access application-wide data here
    private FoodRouletteApplication _appState;
    private Bitmap businessIcon;
    private Bitmap userIcon;
    private  final static String uberUri= "https://api.uber.com/v1/estimates/time";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mQueue = Volley.newRequestQueue(this);
        // setting the reference to global appstate
        _appState = ((FoodRouletteApplication) getApplicationContext());

        // linking maps activity with the UI layout
        setContentView(R.layout.activity_maps);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        populateUBERData();
    }

    //-------------------------------------------------------------------------------------------//
//  This is for the settings fragment tab that we want to implement (following 2 methods)    //
//-------------------------------------------------------------------------------------------//
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        //initialize business name textview
        businessName = (TextView)findViewById(R.id.business_name);
        businessName.setTextSize(25);

        setupIcons();
        setUpMapIfNeeded();

//        Button back = (Button) findViewById(R.id.back);
//
//        back.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                //back button, goes back to revolver wheel
//                finish();
//            }
//        });
        if (yelpResults.size() != 0)
        {
            setTitle(yelpResults.get(businessIndex).name);
            businessName.setText(yelpResults.get(businessIndex).name);

            currentBusiness = yelpResults.get(businessIndex);

            //  DISPLAYING rating
            ImageView img = (ImageView) findViewById(R.id.rating);
            new ImageLoadTask(yelpResults.get(businessIndex).rating_img_url_large, img).execute();
            //img.setImageBitmap(getBitmapFromURL(yelpResults.get(businessIndex).rating_img_url_large));

            Button skip = (Button) findViewById(R.id.button);  //skip button

            skip.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //play gunshot sound
                    SoundPlayer.playGunshot(_appState);

                    //skip button, goes to next business when pressed
                    nextBusiness();
                }

            });

            Button yelpButton = (Button) findViewById(R.id.yelpButton);

            //WHEN YELPLOGO IS CLICKED, YelpWebViewActivity opens showing the businness's Yelp website within the app
            yelpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //skip button, goes to next business when pressed

//
                    Intent myIntent = new Intent(MapsActivity.this, YelpWebViewActivity.class);
                    myIntent.putExtra("firstKeyName", yelpResults.get(businessIndex).url);
                    startActivity(myIntent);

                }
            });

            Button uberButton = (Button) findViewById(R.id.uberButton);

            //WHEN UBERLOGO IS CLICKED, UberWebViewActivity opens showing Uber's website within the app
            uberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//
                    Intent myIntent = new Intent(MapsActivity.this, UberWebViewActivity.class);
                    myIntent.putExtra("secondKeyName", "https://login.uber.com/login");
                    startActivity(myIntent);
                }
            });

            Button blacklist = (Button) findViewById(R.id.blacklistbutton); //blacklist button
            blacklist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //code to run when click is on blacklist button
                    downVoteBusiness();

                    //play gunshot effect
                    SoundPlayer.playGunshot(_appState);

                }
            });
        }
        else
        {
            Context context = MapsActivity.this;
            CharSequence text = "RESET SETTINGS OR CLEAR BLACKLIST, NO BUSINESSES FOUND WITH SPECIFIED SETTINGS";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);

//            Intent intent = new Intent(context, Setting_Main.class);
//            startActivity(intent);
        }
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
        //if (id == R.id.action_settings)
        //{
           // Intent myIntent = new Intent(MapsActivity.this, Setting_Main.class);
            //MapsActivity.this.startActivity(myIntent);
            //return true;
        //}

        return super.onOptionsItemSelected(item);
    }

//-------------------------------------------------------------------------------------------//
//-------------------------------------------------------------------------------------------//

    @Override
    protected void onResume()
    {
        super.onResume();
//        registerLocationChangeCallback();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onStart()
    {
        super.onStart();


    }

    @Override
    protected void onStop()
    {
        super.onStop();
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
                //fetchBusinessData();

                //display our location
                updateMarker(_appState.latitude, _appState.longitude);
                setupBusinessDataCallbacks();

                if (yelpResults.size() != 0)
                {
                    setMapCameraPosition(yelpResults.get(businessIndex).location.coordinate.latitude, yelpResults.get(businessIndex).location.coordinate.longitude);

                    setTitle(yelpResults.get(businessIndex).name);
                    businessName.setText(yelpResults.get(businessIndex).name);

                    //  DISPLAYING rating
                    ImageView img = (ImageView) findViewById(R.id.rating);
                    new ImageLoadTask(yelpResults.get(businessIndex).rating_img_url_large, img).execute();

                    //display nav
                    drawDirections();
                }
            }
        }
    }

    private void drawDirections()
    {
        mapDirections = new GoogleMapDirections();

        if(navLine != null)
        {
            navLine.remove();
        }

        //initialize start and end points
        LatLng start = new LatLng(_appState.latitude, _appState.longitude);
        double latitude = yelpResults.get(businessIndex).location.coordinate.latitude;
        double longitude = yelpResults.get(businessIndex).location.coordinate.longitude;
        LatLng end = new LatLng(latitude, longitude);

        DirectionsRunnable callback = new DirectionsRunnable()
        {
            @Override
            public void runWithDirections(Document doc)
            {
                //create point array
                ArrayList<LatLng> directionPoint = mapDirections.getDirection(doc);

                //set characteristics for navigation line
                PolylineOptions rectLine = new PolylineOptions().width(20).color(Color.argb(96, 255, 0, 0));

                //draw navline to screen
                int size = directionPoint.size();
                for(int i = 0; i < size; i++)
                {
                    rectLine.add(directionPoint.get(i));
                }
                navLine = mMap.addPolyline(rectLine);
            }
        };
        //get HTTP response
        mapDirections.getDocument(start, end, callback);
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


                //FILTERING OUT BUSINESS BY RATINGS IN SETTINGS, ONLY GOING TO DISPLAY BUSINESSES that are >= GIVEN SETTING
                SharedPreferences ratingPreferences = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
                float rating = (float) ratingPreferences.getInt("last_val", 2) + 1;
                //System.out.println("RATING==="+rating);

                //FILTERING OUT BUSINESS BY RADIUS IN SETTINGS, ONLY GOING TO DISPLAY BUSINESSES that are <= GIVEN SETTING
                SharedPreferences radiusPreferences = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
                float radius = (float) radiusPreferences.getInt("SEEKPROG", 20);
                //System.out.println("RATING==="+radius);


                int businessCount = businessData.businesses.size();


                for (int j = 0; j < businessCount; j++)
                {

                    Business business = businessData.businesses.get(j);


                    //get location of business
                    LatLng position = new LatLng(business.location.coordinate.latitude, business.location.coordinate.longitude);

                    //get distance from business to user
                    business.distanceToUser = offsetHypot(position.latitude, userLat, position.longitude, userLong);

                    //add business to array if business rating >= settings rating && business radius <= settings radius && Business isnt in blockedlist
                    if (business.rating >= rating && business.distanceToUser <= radius && !DbAbstractionLayer.isRestaurantInBlockedList(business.id, getApplicationContext()))
                    {

                        yelpResults.add(business);

                    }
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
                if (yelpResults.size() != 0)
                {
                    Collections.sort(yelpResults, new BusinessComparator());
                    currentBusiness = yelpResults.get(businessIndex);
                    //old code which displays all businesses in category
                    LatLng position = new LatLng(currentBusiness.location.coordinate.latitude, currentBusiness.location.coordinate.longitude);
                    businessMarker = mMap.addMarker(new MarkerOptions()
                            .title(currentBusiness.name)
                            .position(position)
                            .icon(BitmapDescriptorFactory.fromBitmap(businessIcon)));
                    businessMarker.isVisible();
                }
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

        if (businessIndex < yelpResults.size() - 1)
        {
            //update marker with information for next business
            businessIndex++;
            Business business = yelpResults.get(businessIndex);

            if (business != null && !DbAbstractionLayer.isRestaurantInBlockedList(business.id, getApplicationContext()))
            {
                LatLng position = new LatLng(business.location.coordinate.latitude, business.location.coordinate.longitude);
                businessMarker.setPosition(position);
                businessMarker.setTitle(business.name);

                setMapCameraPosition(position.latitude, position.longitude);

                setTitle(yelpResults.get(businessIndex).name);
                businessName.setText(yelpResults.get(businessIndex).name);
                drawDirections();

                //  DISPLAYING rating
                ImageView img = (ImageView) findViewById(R.id.rating);
                new ImageLoadTask(yelpResults.get(businessIndex).rating_img_url_large, img).execute();

            }
        }
        else
        {
            businessIndex = 0;
            Context context = MapsActivity.this;
            CharSequence text = "RESET SETTINGS OR CLEAR BLACKLIST, NO BUSINESSES FOUND WITH SPECIFIED SETTINGS";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
        }
    }

    public void downVoteBusiness()
    {
        //get current business
        Business business = yelpResults.get(businessIndex);


        if (business != null && !DbAbstractionLayer.isRestaurantInBlockedList(business.id, getApplicationContext()))
        {
            //add current business to blacklist
            DbAbstractionLayer.addRestaurant(business, this);

            if (businessIndex < yelpResults.size() - 1)
            {
                //update marker with information for next business
                businessIndex++;

                //get the next business out
                business = yelpResults.get(businessIndex);

                //get the position and title of the next business to update the marker
                LatLng position = new LatLng(business.location.coordinate.latitude, business.location.coordinate.longitude);
                businessMarker.setPosition(position);
                businessMarker.setTitle(business.name);

                setMapCameraPosition(position.latitude, position.longitude);
//                TextView businessTitleTextView = (TextView) findViewById(R.id.businessTitle);
//                businessTitleTextView.setText(business.name);
                setTitle(yelpResults.get(businessIndex).name);
                businessName.setText(yelpResults.get(businessIndex).name);
                drawDirections();

                //  DISPLAYING rating
                ImageView img = (ImageView) findViewById(R.id.rating);
                new ImageLoadTask(yelpResults.get(businessIndex).rating_img_url_large, img).execute();
            }
            else
            {
                businessIndex = 0;
                Context context = MapsActivity.this;
                CharSequence text = "RESET SETTINGS OR CLEAR BLACKLIST, NO BUSINESSES FOUND WITH SPECIFIED SETTINGS";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
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
            options.icon(BitmapDescriptorFactory.fromBitmap(userIcon));
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

    public void setupIcons()
    {
        //initialize bitmaps
        userIcon = BitmapFactory.decodeResource(_appState.getResources(), R.drawable.revolver);
        businessIcon = BitmapFactory.decodeResource(_appState.getResources(), R.drawable.redcrosshairs);

        //set up scale factors
        float scaleFactor = 1.25f;
        float mapWidth = findViewById(R.id.map).getWidth();
        float userIconScale = scaleFactor * (mapWidth / 17500);
        float businessIconScale = scaleFactor * (mapWidth / 4000);

        //set up scaling matrix
        Matrix scaleMatrix = new Matrix();

        //resize markers
        scaleMatrix.setScale(userIconScale, userIconScale);
        userIcon = Bitmap.createBitmap(userIcon, 0, 0, userIcon.getWidth(), userIcon.getHeight(), scaleMatrix, false);

        scaleMatrix.setScale(businessIconScale, businessIconScale);
        businessIcon = Bitmap.createBitmap(businessIcon, 0, 0, businessIcon.getWidth(), businessIcon.getHeight(), scaleMatrix, false);
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
        //establish parameters
        LatLngBounds mapBounds = getLatLngBounds(_appState.latitude, _appState.longitude, latitude, longitude);
        int width = ((findViewById(R.id.map).getWidth() * 2) / 5);
        int height = ((findViewById(R.id.map).getHeight() * 2) / 5);

        //move camera
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(
                mapBounds, width, height, 0));
    }

    //launches Google maps for selected restaurant
    public void getDirections(View view)
    {
        currentBusiness = yelpResults.get(businessIndex);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" +
                        Double.toString(currentBusiness.location.coordinate.latitude) + "," +
                        Double.toString(currentBusiness.location.coordinate.longitude)));
        startActivity(intent);
    }


    private void populateUBERData()
    {
        String currLat=Double.toString(_appState.latitude);
        String currLong=Double.toString(_appState.longitude);

        final String url1 ="https://api.uber.com/v1/estimates/time?start_latitude="+currLat+"&start_longitude="+currLong+"&server_token=VG3lVgGREzta1qdqqxR5dzHSRZPOFiZkYnBEpAXD";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url1, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        //DO something here with json

                        UberData uberData = new Gson().fromJson(response.toString(), UberData.class);

                        int time = uberData.times.get(0).estimate;

                        int timeMinutes=time/60;
                        TextView blockTitle = (TextView) findViewById(R.id.uber);
                        blockTitle.setText("Uber Pick Up Time:\n"+String.valueOf(timeMinutes)+" Min.");
                        //SOmeDopeTextView.setText(String.valueOf(time));
                        System.out.println("UBER TIME============"+timeMinutes);


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

            mQueue.add(jsObjRequest);

    }


}
