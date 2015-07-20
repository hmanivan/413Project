package com.example.nikolay.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class Setting_Main extends ActionBarActivity {

    private Button buttonCategory;
    private SeekBar mSeekBarVolume;
    private SharedPreferences prefs;
    private String prefName = "spinner_value";
    int id=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting__main);
        EditText priceRange = (EditText) findViewById(R.id.text_priceRange);
        buttonCategory = (Button) findViewById(R.id.button_Category);
        createPriceRange();
        createSpinner();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting__main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onButton_Category(View view){
        Intent intent = new Intent(this, Setting_Category.class);
        startActivity(intent);
    }

    public void onSave(View view) {

        final EditText editText = (EditText) findViewById(R.id.text_priceRange);
        setPriceRange(editText);
        Toast.makeText(getApplicationContext(), "Saving New Price Range: "+editText.getText().toString(),
                Toast.LENGTH_LONG).show();
    }

    public void createPriceRange(){
        String range = getPriceRange();
        ((TextView)findViewById(R.id.text_priceRange)).setText(range);

    }

    public String getPriceRange(){
        SharedPreferences pref = getSharedPreferences("RANGE",MODE_PRIVATE);
        String text = pref.getString("MAX","Enter $$$");
        return text;

    }

    public void setPriceRange(EditText priceRange){
        SharedPreferences pref = getSharedPreferences("RANGE",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("MAX",priceRange.getText().toString());
        editor.commit();
    }

    public void createSpinner(){

        final List<String> list=new ArrayList<String>();
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
        id=prefs.getInt("last_val",0);
        sp.setSelection(id);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int pos, long arg3) {
// TODO Auto-generated method stub

                prefs = getSharedPreferences(prefName, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
//---save the values in the EditText view to preferences---
                editor.putInt("last_val", pos);

//---saves the values---
                editor.commit();

                Toast.makeText(getBaseContext(), sp.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

            }
        });



    }
}
