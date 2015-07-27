package apitest.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.ozzca_000.myapplication.R;

import YelpData.Business;
import apitest.MapsActivity;
import database.DbAbstractionLayer;

/**
 * Created by Hari on 7/25/2015.
 */
public class ViewBadList extends Activity{

    LinearLayout linearMain;
    CheckBox checkBox;
    Business [] badList = DbAbstractionLayer.getDownVotedList(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* creates a checkbox view of all the blocked restaurants
            Created by Hari 7/26/2015
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badlistview);
        linearMain = (LinearLayout) findViewById(R.id.blocked);



        String [] bizName = new String[badList.length];

        for(int i=0;i<badList.length;i++)
        {
            bizName[i]=badList[i].name;
        }

        ViewGroup checkboxContainer= (ViewGroup) findViewById(R.id.blocked);


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1,  bizName);

            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);





    }

    public void onRemove(View view)
    {
       if(badList.length!=0)
       {
           for (int i = 0; i < badList.length; i++)
               DbAbstractionLayer.removeRestaurant(badList[i], this);

       }
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
