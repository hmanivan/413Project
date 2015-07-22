package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by timbauer on 7/21/15.
 */
public class RestaurantDatabase extends SQLiteOpenHelper {

    public final static int dbVersion = 1;
    public final static String dbName = "RestaurantDatabase.db";
    public final static String dbResTable = "downvoted_restaurants";

    static Context mContext;

    private static RestaurantDatabase restaurantDatabase = null;

    public final static String displayPhone = "display_phone";
    public final static String id = "id";
    public final static String image_url = "image_url";
    public final static String mobile_url = "mobile_url";
    public final static String resaurantName = "restaurant_name";
    public final static String phone = "phone";
    public final static String rating = "rating";
    public final static String reviewCount = "review_count";


    @Override
    public void onCreate(SQLiteDatabase restaurantDb) {
        String CREATE_CHAMP_TABLE = "CREATE TABLE " + dbResTable + "("
                + id + " INT PRIMARY KEY,"
                + resaurantName + " TEXT,"
                + displayPhone + " TEXT,"
                + phone + " TEXT,"
                + image_url + " TEXT,"
                + mobile_url + " TEXT,"
                + rating + " REAL,"
                + reviewCount + " INT," + ")";
        restaurantDb.execSQL(CREATE_CHAMP_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static RestaurantDatabase getRestaurantDatabase(Context context){
        mContext = context;
        if(restaurantDatabase == null){
            restaurantDatabase = new RestaurantDatabase(mContext);
        }

        return restaurantDatabase;

    }

    private RestaurantDatabase(Context context) {
        super(context, dbName, null, dbVersion);
    }
}
