package apitest;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.ozzca_000.myapplication.R;

/**
 * Created by George on 7/29/2015.
 *
 * UBER WEBSITE ACTIVITY HAPPENING WITHIN APP, SHOWS UBER WEBSITE
 */
public class UberWebViewActivity extends  Activity{


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uberwebviewactivity);
        Intent myIntent = getIntent();
        String url=myIntent.getStringExtra("secondKeyName");


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
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }




}
