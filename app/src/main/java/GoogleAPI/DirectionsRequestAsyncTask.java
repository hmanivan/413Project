package GoogleAPI;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;

import java.io.InputStream;

import javax.security.auth.callback.Callback;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Sam on 8/4/2015.
 */
public class DirectionsRequestAsyncTask extends AsyncTask<LatLng, Void, Document>
{
    private DirectionsRunnable _callback;

    public DirectionsRequestAsyncTask(DirectionsRunnable callback)
    {
        _callback = callback;
    }

    @Override
    protected Document doInBackground(LatLng... params)
    {
        LatLng start = params[0];
        LatLng end = params[1];

        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=imperial&mode=driving";
        // + "&client=" + API_KEY;
        Log.d("url", url);
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(in);
            return doc;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Document result)
    {
        _callback.runWithDirections(result);
    }
}
