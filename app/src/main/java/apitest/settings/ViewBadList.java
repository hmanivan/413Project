package apitest.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ozzca_000.myapplication.R;

import YelpData.Business;
import apitest.MapsActivity;
import database.DbAbstractionLayer;

/**
 * Created by Hari on 7/25/2015.
 */
public class ViewBadList extends Activity{

   LinearLayout linearMain;
    CheckBox []checkBox;
    Business [] badList = DbAbstractionLayer.getDownVotedList(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* creates a checkbox view of all the blocked restaurants
            Created by Hari 7/26/2015
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badlistview);
        //displaying title on top of the page
        TextView blockTitle = (TextView) findViewById(R.id.blacklistTitle);
        blockTitle.setText("Blocked List");
       linearMain = (LinearLayout) findViewById(R.id.checkList);

    //getting downvoted list from database
        badList = DbAbstractionLayer.getDownVotedList(this);

        //getting business name and setting to bizName[]
        String [] bizName = new String[badList.length];

        for(int i=0;i<badList.length;i++)
        {
            bizName[i]=badList[i].name;

        }

        //creating checkbox for each blocked restraunt
        checkBox=new CheckBox[bizName.length];
        for(int i=0;i<bizName.length;i++)
        {
            checkBox[i]=new CheckBox(this);
            checkBox[i].setId(i);
            checkBox[i].setText(bizName[i]);
            checkBox[i].setTextSize(25);
            checkBox[i].setTextColor(Color.parseColor("#FFFFFF"));

            linearMain.addView(checkBox[i]);
        }


        Button selectall=(Button) findViewById(R.id.selectAll);
        Button remove=(Button) findViewById(R.id.remove);

        //button that selects all or deselects all
        selectall.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                for(int i=0;i<checkBox.length;i++)
                {
                    if(checkBox[i].isChecked()==false)
                    checkBox[i].setChecked(true);
                    else
                        checkBox[i].setChecked(false);
                }

            }
        });

        //remove button that removed selected checkboxes
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //skip button, goes to next business when pressed

                int id=0;
                for(int i=0;i<checkBox.length;i++)
                {
                    if(checkBox[i].isChecked()&&badList.length!=0)
                    {
                        id=i;
                        DbAbstractionLayer.removeRestaurant(badList[checkBox[id].getId()], getApplicationContext());

                    }
                }
                Intent intent = getIntent();
                finish();
                startActivity(intent);

//

            }

        });




    }



}
