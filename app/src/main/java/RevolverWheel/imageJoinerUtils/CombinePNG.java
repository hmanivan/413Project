package RevolverWheel.imageJoinerUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.example.ozzca_000.myapplication.R;

import static RevolverWheel.imagecropper.ImageCropperUtils.getCroppedBitmap;

/**
 * Created by Sam on 7/7/2015.
 */
public class CombinePNG
{
    public static Bitmap PNGCombiner(Context context, int cylinderEdgeLength)
    {
        //turn off bitmap scaling
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;

        Bitmap combinedImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.chamber, o);
        Bitmap imageToAdd = BitmapFactory.decodeResource(context.getResources(), R.drawable.mcdonaldslogo, o);

        Bitmap bitmapCreate = Bitmap.createBitmap(combinedImage.getWidth(), combinedImage.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(bitmapCreate);

        //resize logo
        imageToAdd = Bitmap.createScaledBitmap(imageToAdd, 244, 244, false);

        //crop the image
        imageToAdd = getCroppedBitmap(imageToAdd);

        //set the density to even out the scaling
        combinedImage.setDensity(Bitmap.DENSITY_NONE);
        imageToAdd.setDensity(Bitmap.DENSITY_NONE);
        comboImage.setDensity(Bitmap.DENSITY_NONE);

        //draw the cylinder
        comboImage.drawBitmap(combinedImage, 0f, 0f, null);

        // populate the cylinders, 1-6
        comboImage.drawBitmap(imageToAdd, 390f, 97f, null);
        comboImage.drawBitmap(imageToAdd, 642f, 250f, null);
        comboImage.drawBitmap(imageToAdd, 634f, 547f, null);
        comboImage.drawBitmap(imageToAdd, 373f, 689f, null);
        comboImage.drawBitmap(imageToAdd, 121f, 533f, null);
        comboImage.drawBitmap(imageToAdd, 131f, 238f, null);

        //try scaling image for test
        bitmapCreate = Bitmap.createScaledBitmap(bitmapCreate, cylinderEdgeLength, cylinderEdgeLength, false);


        return bitmapCreate;
    }
}
