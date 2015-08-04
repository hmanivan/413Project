package apitest.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ozzca_000.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import YelpData.Business;
import database.DbAbstractionLayer;
import revolverwheel.revolver.RevolverActivity;


public class Setting_Main extends ActionBarActivity {

    private static Toast address;
    private RelativeLayout RL;
    private Button buttonCategory;
    private static SeekBar seekbar_radius;
    private static TextView text_radius;
    private SharedPreferences prefs, prefs2;
    private String prefName = "spinner_value";
    private String YelpRating;
    private int id=0,currentProgress, newProgress, idtemp;
    private float SeekRadValue;
    private double progressValue;
    final private List<String> list=new ArrayList<String>();

    private Business[] bizzlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting__main);
        createSpinner();
        createSeekbar();
        seekbarRadius();

        Button back = (Button) findViewById(R.id.backtowheel);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //back button, goes back to revolver wheel
                Intent intent = new Intent(Setting_Main.this, RevolverActivity.class);
                startActivity(intent);
            }
        });
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_setting__main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void getSeekPref() {
        prefs2 = PreferenceManager.getDefaultSharedPreferences(Setting_Main.this);
        currentProgress = prefs2.getInt("SEEKPROG", 20);
    }

    public void createSeekbar(){
        getSeekPref();
        seekbar_radius = (SeekBar) findViewById(R.id.seekbar_radius);
        seekbar_radius.setProgress(currentProgress);

    }

    public float getRadius() {
        prefs2 = getSharedPreferences( "SEEKPROG", Context.MODE_PRIVATE );
        currentProgress = prefs2.getInt("SEEKPROG", 20);
        SeekRadValue = ((float)currentProgress/10);
        return SeekRadValue;
    }

    public void seekbarRadius(){
        text_radius = (TextView) findViewById(R.id.text_radius);
        text_radius.setText("Search Radius (mi.): " + ((double) seekbar_radius.getProgress() / 10));

        seekbar_radius.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {


                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        progressValue = ((double) progress / 10);
                        text_radius.setText("Search Radius (mi.): " + progressValue);


                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        newProgress = seekbar_radius.getProgress();
                        currentProgress = newProgress;
                        text_radius.setText("Search Radius (mi.): " + progressValue);
                        SharedPreferences.Editor editor = prefs2.edit();
                        editor.putInt("SEEKPROG", newProgress);
                        editor.commit();


                    }
                }

        );

    }

    public void onHintRadius(View view){
        if (address != null)
            address.cancel();
        address = Toast.makeText(getBaseContext(),"Set Search Radius (mi.) Centered Around Your Location", Toast.LENGTH_LONG);

        address.setGravity(Gravity.CENTER, 0, 450);
        address.show();
    }


    public void onHintSpinner(View view){
        if (address != null)
            address.cancel();
        address = Toast.makeText(getBaseContext(),"Select Minimum Yelp Stars Rating", Toast.LENGTH_LONG);

        address.setGravity(Gravity.CENTER, 0, 450);
        address.show();
    }

    public String getYelpRating(){

        prefs = this.getSharedPreferences(prefName, MODE_PRIVATE);
        idtemp = prefs.getInt("last_val", 2);
        YelpRating = (String) list.get(idtemp);
        return YelpRating;
    }

    public void createSpinner(){

        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");

        final Spinner sp=(Spinner) findViewById(R.id.rating);
        ArrayAdapter<String> adp= new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,list);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sp.setAdapter(adp);

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        id=prefs.getInt("last_val", 2);
        sp.setSelection(id);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
// TODO Auto-generated method stub

                prefs = getSharedPreferences(prefName, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
//---save the values in the EditText view to preferences---
                editor.putInt("last_val", pos);

//---saves the values---
                editor.commit();


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

            }
        });



    }

    public void goToDownVote(View view) {
        Intent goToDownVoteScreen = new Intent(this, ViewBadList.class);
        startActivity(goToDownVoteScreen);
    }
}