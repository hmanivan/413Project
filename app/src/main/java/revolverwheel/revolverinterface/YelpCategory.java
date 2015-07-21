package revolverwheel.revolverinterface;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by Sam on 7/21/2015.
 */

//this class will manage our categories for yelp
public class YelpCategory
{
    public String label;
    public String searchTerm;
    public Bitmap categoryImage;

    //this constructor will produce a category with associated resources
    public YelpCategory(String label, String term, Bitmap bitmap)
    {
        this.label = label;
        this.searchTerm = term;
        this.categoryImage = bitmap;
    }
}
