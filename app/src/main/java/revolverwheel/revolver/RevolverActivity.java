package revolverwheel.revolver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.ozzca_000.myapplication.R;

import apitest.MapsActivity;

import static revolverwheel.imageJoinerUtils.CombinePNG.PNGCombiner;


public class RevolverActivity extends AppCompatActivity
{
    private Bitmap mBitmap;
    private CanvasView RevolverCanvas;
    private Context mContext;
    private ImageView combinedRevolverImage;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revolver_wheel);
        mContext = getApplicationContext();
        RevolverCanvas = (CanvasView) findViewById(R.id.revolver_canvas);

        //get current display size in pixels
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //decide which dimension is smaller, set edgelength for cylinder
        int cylinderEdgeLength;
        if (width <= height)
        {
            cylinderEdgeLength = width;
        }
        else
        {
            cylinderEdgeLength = height;
        }

        Bitmap bm = PNGCombiner(getApplicationContext(), cylinderEdgeLength);
        RevolverCanvas.setSamBitmap(bm);

        RevolverCanvas.startRotationThread();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
