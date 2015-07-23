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

    String[] mResturantNames = {"one", "two", "three", "four", "five"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_voted_list);
        DownVotedList data = new DownVotedList();
        DownVotedListAdaptor adapter = new DownVotedListAdaptor(this, data);

        ListView listView = (ListView) findViewById(R.id.mResturantNames);
        listView.setAdapter(adapter);
    }
}
