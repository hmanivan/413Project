package com.example.nikolay.nickxml;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * Created by Nikolay on 7/2/2015.
 */
public class Hunchback4h extends Activity {

    public Button _viewButton;
    Boolean swap = false;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // The activity is being created.
            setContentView(R.layout.prettycolors);

            final View someView = findViewById(R.id.prettycolors);

            final Button button = (Button) findViewById(R.id.my_btn);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(swap)
                    {
                        someView.setBackgroundColor(Color.argb(255, 0, 0, 255));

                    }
                    else{
                    someView.setBackgroundColor(Color.argb(255, 255, 0, 0));}
                    swap=!swap;

                }
            });


        }
        @Override
        protected void onStart() {
            super.onStart();
            // The activity is about to become visible.
        }
        @Override
        protected void onResume() {
            super.onResume();
            // The activity has become visible (it is now "resumed").
        }
        @Override
        protected void onPause() {
            super.onPause();
            // Another activity is taking focus (this activity is about to be "paused").
        }
        @Override
        protected void onStop() {
            super.onStop();
            // The activity is no longer visible (it is now "stopped")
        }
        @Override
        protected void onDestroy() {
            super.onDestroy();
            // The activity is about to be destroyed.
        }
    }

