package apitest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.ozzca_000.myapplication.R;

import foodroulette.appstate.FoodRouletteApplication;

/**
 * Created by Hari on 7/29/2015.
 *
 * YELP WEBSITE ACTIVITY HAPPENING WITHIN APP, SHOWS CURRENT BUSINESS DATA THROUGH YELPS WEBSITE
 */
public class YelpWebViewActivity extends  Activity{


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yelpwebviewactivity);
        Intent myIntent = getIntent();
        String url=myIntent.getStringExtra("firstKeyName");


        WebView mWebview  = new WebView(this);

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity =this;

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });

        mWebview .loadUrl(url);
        setContentView(mWebview);

    }




}
