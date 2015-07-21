package database;

import YelpData.Business;

/**
 * Created by timbauer on 7/21/15.
 */
public class DbAbstractionLayer {

    private static DbAbstractionLayer dbAbstractionLayer = null;

    public static DbAbstractionLayer getDbAbstractionLayer(){
        if (dbAbstractionLayer == null){
            dbAbstractionLayer = new DbAbstractionLayer();
            return dbAbstractionLayer;
        }else{
            return dbAbstractionLayer;
        }
    }

    private DbAbstractionLayer(){}

    public boolean isRestaurantInBlockedList(){
        return false;
    }

    public Business[] getDownVotedList(){
        Business[] downVotedList = new Business[]{};
        return downVotedList;
    }

    public void addRestaurant(Business downVotedRestaurant){

    }

    public void removeRestaurant(Business restaurant){

    }

}
