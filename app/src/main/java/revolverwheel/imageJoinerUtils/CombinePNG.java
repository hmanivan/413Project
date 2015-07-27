package revolverwheel.imageJoinerUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.example.ozzca_000.myapplication.R;

import revolverwheel.revolvercategories.FoodCategory;

import static java.lang.Integer.parseInt;
import static revolverwheel.imagecropper.ImageCropperUtils.getCroppedBitmap;

/**
 * Created by Sam on 7/7/2015.
 */
public class CombinePNG
{
    public static Bitmap PNGCombiner(Context context, int cylinderEdgeLength, FoodCategory[] categories)
    {

        //turn off bitmap scaling
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;

        Bitmap combinedImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.chamber, o);
        Bitmap bitmapCreate = Bitmap.createBitmap(combinedImage.getWidth(), combinedImage.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(bitmapCreate);
        //set the density to even out the scaling
        combinedImage.setDensity(Bitmap.DENSITY_NONE);
        comboImage.setDensity(Bitmap.DENSITY_NONE);
        //draw the cylinder
        comboImage.drawBitmap(combinedImage, 0f, 0f, null);

        Bitmap[] imageToAdd = {null, null, null, null, null, null};

        for (int i = 0; i < 6; i++)
        {
            int categoryImageResource = categories[i].getDrawableResource();

            imageToAdd[i] = categoryImageResource != -1 ?
                    BitmapFactory.decodeResource(context.getResources(), categoryImageResource, o) :
                    null;
        }

        for (int i = 0; i < 6; i++)
        {
            if (imageToAdd[i] != null)
            {
                imageToAdd[i] = Bitmap.createScaledBitmap(imageToAdd[i], 244, 244, false);
                imageToAdd[i] = getCroppedBitmap(imageToAdd[i]);
                imageToAdd[i].setDensity(Bitmap.DENSITY_NONE);
            }
        }
        // populate the cylinders, 1-6
        if (imageToAdd[0] != null)
        {
            comboImage.drawBitmap(imageToAdd[0], 390f, 97f, null);
        }
        if (imageToAdd[1] != null)
        {
            comboImage.drawBitmap(imageToAdd[1], 642f, 250f, null);
        }
        if (imageToAdd[2] != null)
        {
            comboImage.drawBitmap(imageToAdd[2], 634f, 547f, null);
        }
        if (imageToAdd[3] != null)
        {
            comboImage.drawBitmap(imageToAdd[3], 373f, 689f, null);
        }
        if (imageToAdd[4] != null)
        {
            comboImage.drawBitmap(imageToAdd[4], 121f, 533f, null);
        }
        if (imageToAdd[5] != null)
        {
            comboImage.drawBitmap(imageToAdd[5], 131f, 238f, null);
        }

        //try scaling image for test
        bitmapCreate = Bitmap.createScaledBitmap(bitmapCreate, cylinderEdgeLength, cylinderEdgeLength, false);


        return bitmapCreate;
    }
}
