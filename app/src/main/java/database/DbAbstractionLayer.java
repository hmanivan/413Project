package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import YelpData.Business;

/**
 * Created by timbauer on 7/21/15.
 */
public class DbAbstractionLayer {

    private static DbAbstractionLayer dbAbstractionLayer = null;
    private static Context mContext;
    private static RestaurantDatabase restaurantDatabase;
    private static SQLiteDatabase restaurantDb;

    private static String[] tableColumns = new String[]{
            restaurantDatabase.id,
            restaurantDatabase.resaurantName,
            restaurantDatabase.displayPhone,
            restaurantDatabase.image_url,
            restaurantDatabase.mobile_url,
            restaurantDatabase.phone,
            restaurantDatabase.rating,
            restaurantDatabase.reviewCount,
    };

    public static DbAbstractionLayer getDbAbstractionLayer(){
        if (dbAbstractionLayer == null){
            dbAbstractionLayer = new DbAbstractionLayer();
            return dbAbstractionLayer;
        }else{
            return dbAbstractionLayer;
        }
    }

    private DbAbstractionLayer(){}

    public static boolean isRestaurantInBlockedList(String restaurantIdStr, Context context){

        int restaurantId = Integer.parseInt(restaurantIdStr);
        mContext = context;
        restaurantDatabase = RestaurantDatabase.getRestaurantDatabase(mContext);
        restaurantDb = restaurantDatabase.getWritableDatabase();

        Cursor restaurantData = restaurantDb.query(
                restaurantDatabase.dbResTable,
                tableColumns,
                restaurantDatabase.id + " = ?",
                new String[] {restaurantIdStr},
                null, null, null);
        if (restaurantData.getCount() > 0){
            return true;
        }else {
            return false;
        }
    }

    public static Business[] getDownVotedList(Context context){
        mContext = context;
        restaurantDatabase = RestaurantDatabase.getRestaurantDatabase(mContext);
        restaurantDb = restaurantDatabase.getWritableDatabase();
        

        Cursor restData =restaurantDb.rawQuery("SELECT * FROM " + restaurantDatabase.dbResTable, null);
        restData.moveToFirst();
        int numOfRestaurants = restData.getCount();
        
        int idColumn = restData.getColumnIndex(RestaurantDatabase.id);
        int restNameColumn = restData.getColumnIndex(RestaurantDatabase.resaurantName);
        int dispPhoneColumn = restData.getColumnIndex(RestaurantDatabase.displayPhone);
        int imgUrlColumn = restData.getColumnIndex(RestaurantDatabase.image_url);
        int mobUrlColumn = restData.getColumnIndex(RestaurantDatabase.mobile_url);
        int phoneColumn = restData.getColumnIndex(RestaurantDatabase.phone);
        int rateColumn = restData.getColumnIndex(RestaurantDatabase.rating);
        int revColumn = restData.getColumnIndex(RestaurantDatabase.reviewCount);
        
        
        Business[] downVotedList = new Business[numOfRestaurants];

        for (int i = 0; i < numOfRestaurants; i++){
            downVotedList[i] = new Business();
            downVotedList[i].id = restData.getString(idColumn);
            downVotedList[i].name = restData.getString(restNameColumn);
            downVotedList[i].display_phone = restData.getString(dispPhoneColumn);
            downVotedList[i].image_url = restData.getString(imgUrlColumn);
            downVotedList[i].mobile_url = restData.getString(mobUrlColumn);
            downVotedList[i].phone = restData.getString(phoneColumn);
            downVotedList[i].rating = restData.getFloat(rateColumn);
            downVotedList[i].review_count = restData.getInt(revColumn);

        }
        restData.close();
        return downVotedList;
    }

    public static void addRestaurant(Business downVotedRestaurant, Context context){
        mContext = context;
        restaurantDatabase = RestaurantDatabase.getRestaurantDatabase(mContext);
        restaurantDb = restaurantDatabase.getWritableDatabase();

        //removing string to int parsing, which causes program crash
        //TODO: Tim Bauer - please remove all unnessary type conversions
        //int restaurantId = Integer.parseInt(downVotedRestaurant.id);

        ContentValues newRestaurant = new ContentValues();

        newRestaurant.put(RestaurantDatabase.id, downVotedRestaurant.id);
        newRestaurant.put(RestaurantDatabase.resaurantName, downVotedRestaurant.name);
        newRestaurant.put(RestaurantDatabase.displayPhone, downVotedRestaurant.display_phone);
        newRestaurant.put(RestaurantDatabase.image_url, downVotedRestaurant.image_url);
        newRestaurant.put(RestaurantDatabase.mobile_url, downVotedRestaurant.mobile_url);
        newRestaurant.put(RestaurantDatabase.phone, downVotedRestaurant.phone);
        newRestaurant.put(RestaurantDatabase.rating, downVotedRestaurant.rating);
        newRestaurant.put(RestaurantDatabase.reviewCount, downVotedRestaurant.review_count);

        restaurantDb.insert(RestaurantDatabase.dbResTable, null, newRestaurant);

    }

    public static void removeRestaurant(Business restaurant, Context context){
        mContext = context;
        restaurantDatabase = RestaurantDatabase.getRestaurantDatabase(mContext);
        restaurantDb = restaurantDatabase.getWritableDatabase();

        //int restaurantId = Integer.parseInt(restaurant.id);

        restaurantDb.delete(RestaurantDatabase.dbResTable, RestaurantDatabase.id + " = ?", new String[] {restaurant.id} );

    }

}