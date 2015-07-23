package apitest.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ozzca_000.myapplication.R;

import java.util.ArrayList;
import java.util.List;


public class Setting_Main extends ActionBarActivity {

    private static Toast address;
    private Button buttonCategory;
    private static SeekBar seekbar_radius;
    private static TextView text_radius, text_test;
    private SharedPreferences prefs, prefs2, pref;
    private String prefName = "spinner_value", YelpRating;
    private int id=0,currentProgress, newProgress, idtemp;
    private float SeekRadValue;
    private double progressValue;
    final private List<String> list=new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting__main);
        EditText priceRange = (EditText) findViewById(R.id.text_priceRange);
        buttonCategory = (Button) findViewById(R.id.button_Category);
        createPriceRange();
        createSpinner();
        createSeekbar();
        seekbarRadius();
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

    public void getSeekPref(){
        prefs2 = getSharedPreferences( "SEEKPROG", Context.MODE_PRIVATE );
        currentProgress = prefs2.getInt("SEEKPROG", 20);
    }

    public void createSeekbar(){
        getSeekPref();
        seekbar_radius = (SeekBar) findViewById(R.id.seekbar_radius);
        seekbar_radius.setProgress(currentProgress);

        //text_test = (TextView) findViewById(R.id.text_test);
        //text_test.setText("Value is: " + getRadius());
    }

    public float getRadius(){
        prefs2 = getSharedPreferences( "SEEKPROG", Context.MODE_PRIVATE );
        currentProgress = prefs2.getInt("SEEKPROG", 20);
        SeekRadValue = ((float)currentProgress/10);
        return SeekRadValue;
    }

    public void seekbarRadius(){
        text_radius = (TextView) findViewById(R.id.text_radius);
        text_radius.setText("Radius: " + ((double) seekbar_radius.getProgress() / 10));

        seekbar_radius.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {


                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        progressValue = ((double) progress / 10);
                        text_radius.setText("Radius: " + progressValue);


                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        newProgress = seekbar_radius.getProgress();
                        currentProgress = newProgress;
                        text_radius.setText("Radius: " + progressValue);
                        SharedPreferences.Editor editor = prefs2.edit();
                        editor.putInt("SEEKPROG", newProgress);
                        editor.commit();


                    }
                }

        );

    }

    public void onButton_Category(View view){
        Intent intent = new Intent(this, Setting_Category.class);
        startActivity(intent);    }



    public void onSave(View view) {

        final EditText editText = (EditText) findViewById(R.id.text_priceRange);
        setPriceRange(editText);
        Toast.makeText(getApplicationContext(), "Saving New Price Range: "+editText.getText().toString(),
                Toast.LENGTH_LONG).show();
    }

    public void onClear(View view) {
        prefs2.edit().remove("SEEKPROG").commit();
        prefs.edit().remove("last_val").commit();
        pref.edit().remove("MAX").commit();
        Toast.makeText(getApplicationContext(), "Preferences Cleared",
                Toast.LENGTH_LONG).show();
    }
    public void onHintRadius(View view){
        if (address != null)
            address.cancel();
        address = Toast.makeText(getBaseContext(),"Set Search Radius centered around your current location", Toast.LENGTH_LONG);
        address.setGravity(Gravity.CENTER, 0, 0);
        address.show();
    }

    public void onHintCategory(View view){
        if (address != null)
            address.cancel();
        address = Toast.makeText(getBaseContext(),"Select desired food categories to populate the roulette wheel", Toast.LENGTH_LONG);
        address.setGravity(Gravity.CENTER, 0, 0);
        address.show();
    }

    public void onHintSpinner(View view){
        if (address != null)
            address.cancel();
        address = Toast.makeText(getBaseContext(),"Select desired yelp rating", Toast.LENGTH_LONG);
        address.setGravity(Gravity.CENTER, 0, 0);
        address.show();
    }

    public void onHintRange(View view){
        if (address != null)
            address.cancel();
        address = Toast.makeText(getBaseContext(),"Input MAX amount of money you are willing to spend", Toast.LENGTH_LONG);
        address.setGravity(Gravity.CENTER, 0, 0);
        address.show();
    }

    public void createPriceRange(){
        String range = getPriceRange();
        ((TextView)findViewById(R.id.text_priceRange)).setText(range);

    }

    public String getPriceRange(){
        pref = getSharedPreferences("RANGE", MODE_PRIVATE);
        String text = pref.getString("MAX", "");
        return text;

    }

    public void setPriceRange(EditText priceRange){
        SharedPreferences pref = getSharedPreferences("RANGE",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("MAX",priceRange.getText().toString());
        editor.commit();
    }

    public String getYelpRating(){
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        idtemp = prefs.getInt("last_val", 2);
        YelpRating = (String) list.get(idtemp);
        return YelpRating;
    }

    public void createSpinner(){

        //final List<String> list=new ArrayList<String>();
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
        id=prefs.getInt("last_val",2);
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

                //text_test = (TextView) findViewById(R.id.text_test);
                //text_test.setText("Value is: " + getYelpRating());

                //Toast.makeText(getBaseContext(), sp.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

            }
        });



    }
}