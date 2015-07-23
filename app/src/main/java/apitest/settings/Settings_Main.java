package apitest.settings;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.example.ozzca_000.myapplication.R;


public class Settings_Main extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings__main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void onButton_Category(View view){
        Intent intent = new Intent(this, apitest.CheckBoxActivity.class);
        startActivity(intent);

    }

}
