package foodroulette.callbacks;

/**
 * Created by Sam on 6/27/2015.
 */
public abstract class LocationRunnable implements Runnable {


    @Override
    public void run() {

    }


    public abstract void runWithLocation(double latitude, double longitude);

}
