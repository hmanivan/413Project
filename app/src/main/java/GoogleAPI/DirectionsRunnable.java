package GoogleAPI;

import org.w3c.dom.Document;

/**
 * Created by Sam on 8/4/2015.
 */
public abstract class DirectionsRunnable implements Runnable
{

        @Override
        public void run() {
        }


        public abstract void runWithDirections(Document document);

}
