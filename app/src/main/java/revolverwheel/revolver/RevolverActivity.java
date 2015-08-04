package revolverwheel.revolver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ozzca_000.myapplication.R;

import java.util.concurrent.LinkedBlockingQueue;

import apitest.settings.Setting_Main;
import revolverwheel.revolvercategories.FoodCategory;

import static revolverwheel.imageJoinerUtils.CombinePNG.PNGCombiner;


public class RevolverActivity extends AppCompatActivity {
    private Bitmap mBitmap;
    private CanvasView RevolverCanvas;
    private Context mContext;
    private ImageView combinedRevolverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revolver_wheel);
        mContext = getApplicationContext();
        RevolverCanvas = (CanvasView) findViewById(R.id.revolver_canvas);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void onStart()
    {
        super.onStart();

        //get current display size in pixels
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //decide which dimension is smaller, set edgelength for cylinder
        final int cylinderEdgeLength = width <= height ? width : height;

        //create a background thread that will populate the wheel with categories over time
        final CanvasView hardRevolverCanvas = RevolverCanvas;
        final Context hardContext = mContext;
        new Thread(){
            public void run() {
                Bitmap bitmap;

                FoodCategory[] categories = new FoodCategory[]{FoodCategory.None, FoodCategory.None, FoodCategory.None, FoodCategory.None, FoodCategory.None, FoodCategory.None};

                bitmap = PNGCombiner(hardContext, cylinderEdgeLength, categories);
                hardRevolverCanvas.setSamBitmap(bitmap);

                categories[0] = FoodCategory.Indian;
                bitmap = PNGCombiner(hardContext, cylinderEdgeLength, categories);
                hardRevolverCanvas.setSamBitmap(bitmap);

                categories[1] = FoodCategory.American;
                bitmap = PNGCombiner(hardContext, cylinderEdgeLength, categories);
                hardRevolverCanvas.setSamBitmap(bitmap);

                categories[2] = FoodCategory.Chinese;
                bitmap = PNGCombiner(hardContext, cylinderEdgeLength, categories);
                hardRevolverCanvas.setSamBitmap(bitmap);

                categories[3] = FoodCategory.Italian;
                bitmap = PNGCombiner(hardContext, cylinderEdgeLength, categories);
                hardRevolverCanvas.setSamBitmap(bitmap);

                categories[4] = FoodCategory.Japanese;
                bitmap = PNGCombiner(hardContext, cylinderEdgeLength, categories);
                hardRevolverCanvas.setSamBitmap(bitmap);

                categories[5] = FoodCategory.Mexican;
                bitmap = PNGCombiner(hardContext, cylinderEdgeLength, categories);
                hardRevolverCanvas.setSamBitmap(bitmap);
            }
        }.start();

        RevolverCanvas.startRotationThread();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    public void onRestart()
    {
        super.onRestart();
    }

    public void onResume()
    {
        super.onResume();
    }

    public void onPause()
    {
        super.onPause();

        //terminate revolver canvas thread
        try
        {
            RevolverCanvas.dispose();
        }
        catch (Exception e)
        {
        }
    }

    public void onStop()
    {
        super.onStop();

        //terminate revolver canvas thread
        try
        {
            RevolverCanvas.dispose();
        }
        catch (Exception e)
        {
        }
    }

    public void onDestroy()
    {
        super.onDestroy();

        //terminate revolver canvas thread
        try
        {
            RevolverCanvas.dispose();
        }
        catch (Exception e)
        {
        }
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
    public void onPreference(View view) {
        Intent intent = new Intent(this, Setting_Main.class);
        startActivity(intent);

    }
}


   // public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
           // Intent myIntent = new Intent(RevolverActivity.this, apitest.settings.Setting_Main.class);
//        myIntent.putExtra("key", value); //Optional parameters
          //  RevolverActivity.this.startActivity(myIntent);
           // return true;
        //}

       // return super.onOptionsItemSelected(item);
    //}
//}
//-------------------------------------------------------------------------------------------//
//-------------------------------------------------------------------------------------------//



