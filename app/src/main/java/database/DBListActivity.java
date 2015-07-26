package database;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ozzca_000.myapplication.R;

/**
 * Created by george on 7/20/15.
 */
public class DBListActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_list);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Intent intent = getIntent();
        String gotName = intent.getStringExtra("gotName"); //if it's a string you stored.
        String gotEmail = intent.getStringExtra("gotEmail"); //if it's a string you stored.
        final TextView textViewToChange = (TextView) findViewById(R.id.textView2);
        textViewToChange.setText("Name: " + gotName
                + " Email: " + gotEmail);
    }
}
