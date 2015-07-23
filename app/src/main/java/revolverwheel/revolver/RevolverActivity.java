package revolverwheel.revolver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.ozzca_000.myapplication.R;

import java.util.concurrent.LinkedBlockingQueue;

import static revolverwheel.imageJoinerUtils.CombinePNG.PNGCombiner;


public class RevolverActivity extends AppCompatActivity {
    private Bitmap mBitmap;
    private CanvasView RevolverCanvas;
    private Context mContext;
    private ImageView combinedRevolverImage;

    public static Vibrator myVib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revolver_wheel);
        mContext = getApplicationContext();
        RevolverCanvas = (CanvasView) findViewById(R.id.revolver_canvas);
//
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        //get current display size in pixels
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //decide which dimension is smaller, set edgelength for cylinder
        int cylinderEdgeLength;
        if (width <= height) {
            cylinderEdgeLength = width;
        } else {
            cylinderEdgeLength = height;
        }

        Bitmap bm = PNGCombiner(getApplicationContext(), cylinderEdgeLength);
        RevolverCanvas.setSamBitmap(bm);

        RevolverCanvas.startRotationThread();
    }

    //-------------------------------------------------------------------------------------------//
    //  This is for the settings fragment tab that we want to implement (following 2 methods)    //
    //-------------------------------------------------------------------------------------------//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        Intent myIntent = new Intent(MainActivity.this, apitest.SettingsActivity.class);
////        myIntent.putExtra("key", value); //Optional parameters
//        MainActivity.this.startActivity(myIntent);
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
            Intent myIntent = new Intent(RevolverActivity.this, apitest.settings.Setting_Main.class);
//        myIntent.putExtra("key", value); //Optional parameters
            RevolverActivity.this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//-------------------------------------------------------------------------------------------//
//-------------------------------------------------------------------------------------------//



}