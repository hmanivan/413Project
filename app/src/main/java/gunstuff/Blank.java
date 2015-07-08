package gunstuff;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.media.MediaPlayer;

/**
 * Created by george on 7/2/15.
 */
public class Blank extends Activity {
    public Button _partybutton;
    Boolean partyOn = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The activity is being created.

        //links java code to xml code
        setContentView(R.layout.blankactiv);

        _partybutton = (Button) findViewById(R.id.partybutton);
        _partybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                partyOn = !partyOn;
                Button mButton = (Button) findViewById(R.id.partybutton);
                if (partyOn == false) {
                    mButton.setText("OFF");
                } else {
                    mButton.setText("ON");
                }

            }
        });
//        letsParty();

        Button boton = (Button) findViewById(R.id.boton);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(Blank.this, R.raw.single_shot);
                mp.start();
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

    public void letsParty() {
        Button mButton = (Button) findViewById(R.id.partybutton);
        mButton.setText("On");
    }
}
