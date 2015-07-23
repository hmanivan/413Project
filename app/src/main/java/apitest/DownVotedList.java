package apitest;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ozzca_000.myapplication.R;

import java.util.ArrayList;

import YelpData.Business;
import database.DbAbstractionLayer;

/**
 * Created by Binesh on 7/22/2015.
 */


public class DownVotedList extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_voted_list);
        Business[] downVotedRestaurants = DbAbstractionLayer.getDownVotedList(this);
        String[] restaurantNames = new String[downVotedRestaurants.length];

        for(int i = 0; i < downVotedRestaurants.length; i++){
            restaurantNames[i] = downVotedRestaurants[i].name;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, restaurantNames);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);



    }
}
