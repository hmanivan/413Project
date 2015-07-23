package foodroulette.callbacks;

import YelpData.BusinessData;

/**
 * Created by Sam on 6/27/2015.
 */
public abstract class BusinessRunnable implements Runnable {

    @Override
    public void run() {

    }


    public abstract void runWithBusiness(BusinessData business);

}
