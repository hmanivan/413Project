package com.example.ozzca_000.myapplication;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    /*Initialize variables for buttons created in activity_main.xml file*/
    private Button button1, button2, button3, button4, button5, button6;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        //set local button variables to corresponding id's from activity_main
        button1 = (Button) findViewById(R.id.B1);
        button2 = (Button) findViewById(R.id.B2);
        button3 = (Button) findViewById(R.id.B3);
        button4 = (Button) findViewById(R.id.B4);
        button5 = (Button) findViewById(R.id.B5);
        button6 = (Button) findViewById(R.id.B6);
    }


    //-------------------------------------------------------------------------------------------//
    //  This is for the settings fragment tab that we want to implement (following 2 methods)    //
    //-------------------------------------------------------------------------------------------//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //-------------------------------------------------------------------------------------------//
    //-------------------------------------------------------------------------------------------//

    //On Button1 clicked or roulette game played
    public void onB1(View view) {
        //Set MediaPlayer object to gunshot sound
        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.single_shot);
        //play gunshot sound from mediaPlayer object
        mediaPlayer.start();
        //update view to activity_maps (for testing purposes)
        setContentView(R.layout.activity_maps);
    }

    //On Button2 Pressed
    public void onB2(View view) {
        //Set MediaPlayer object to gunshot sound
        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.single_shot);
        //play gunshot sound from mediaPlayer object
        mediaPlayer.start();
        //set to dummy next_activity screen ( to become a search )
        setContentView(R.layout.activity_maps);
    }

    //On Button3 Pressed
    public void onB3(View view) {
        //Set MediaPlayer object to gunshot sound
        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.single_shot);
        //play gunshot sound from mediaPlayer object
        mediaPlayer.start();
        //set to dummy next_activity screen ( to become a search )
        setContentView(R.layout.activity_maps);
    }

    //On Button4 Pressed
    public void onB4(View view) {
        //Set MediaPlayer object to gunshot sound
        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.single_shot);
        //play gunshot sound from mediaPlayer object
        mediaPlayer.start();
        //set to dummy next_activity screen ( to become a search )
        setContentView(R.layout.activity_maps);
    }

    //On Button5 Pressed
    public void onB5(View view) {
        //Set MediaPlayer object to gunshot sound
        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.single_shot);
        //play gunshot sound from mediaPlayer object
        mediaPlayer.start();
        //set to dummy next_activity screen ( to become a search )
        setContentView(R.layout.activity_maps);
    }

    //On Button6 Pressed
    public void onB6(View view) {
        //Set MediaPlayer object to gunshot sound
        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.single_shot);
        //play gunshot sound from mediaPlayer object
        mediaPlayer.start();
        //set to dummy next_activity screen ( to become a search )
        setContentView(R.layout.activity_maps);
    }

    // on back button pressed
    public void onBack(View view) {
        //Set MediaPlayer object to reload sound
        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.reload);
        //play gunshot sound from mediaPlayer object
        mediaPlayer.start();
        //Go back to Home Screen
        setContentView(R.layout.activity_main);
    }

    //On Settings button pressed
    public void onSet(View view) {
        // go to settings screen
        setContentView(R.layout.settings_activity);
    }
}
