package com.example.nikolay.settings;

import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;


public class Setting_Category extends ActionBarActivity {
    private static CheckBox checkAmerican, checkItalian, checkJapanese, checkChinese,
            checkVeitnamese, checkMexican;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting__category);
        checkAmerican = (CheckBox) findViewById(R.id.checkbox_american);
        checkChinese = (CheckBox) findViewById(R.id.checkbox_chinese);
        checkItalian = (CheckBox) findViewById(R.id.checkbox_italian);
        checkJapanese = (CheckBox) findViewById(R.id.checkbox_japanense);
        checkMexican = (CheckBox) findViewById(R.id.checkbox_mexican);
        checkVeitnamese = (CheckBox) findViewById(R.id.checkbox_veitnamese);
        checkAmerican.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("A", false));
        checkChinese.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("C", false));
        checkItalian.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("I", false));
        checkJapanese.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("J", false));
        checkMexican.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("M", false));
        checkVeitnamese.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("V", false));

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        //SharedPreferences preferences = getApplicationContext().getSharedPreferences("checkbox", android.content.Context.MODE_PRIVATE);
        // SharedPreferences.Editor editor = preferences.edit();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkbox_american:
                if (checked) {
                    checkAmerican.setChecked(true);
                    // PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "burger").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("A", true).commit();

                }
                // Put some meat on the sandwich
                else {
                    checkAmerican.setChecked(false);
                    //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "NoBURGERS").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("A", false).commit();

                }
                break;
            case R.id.checkbox_chinese:
                if (checked) {
                    checkChinese.setChecked(true);
                    // PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "burger").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("C", true).commit();

                }
                // Put some meat on the sandwich
                else {
                    checkChinese.setChecked(false);
                    //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "NoBURGERS").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("C", false).commit();

                }
                break;
            case R.id.checkbox_italian:
                if (checked) {
                    checkItalian.setChecked(true);
                    // PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "burger").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("I", true).commit();

                }
                // Put some meat on the sandwich
                else {
                    checkItalian.setChecked(false);
                    //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "NoBURGERS").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("I", false).commit();

                }
                break;
            case R.id.checkbox_mexican:
                if (checked) {
                    checkMexican.setChecked(true);
                    // PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "burger").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("M", true).commit();

                }
                // Put some meat on the sandwich
                else {
                    checkMexican.setChecked(false);
                    //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "NoBURGERS").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("M", false).commit();

                }
                break;
            case R.id.checkbox_japanense:
                if (checked) {
                    checkJapanese.setChecked(true);
                    // PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "burger").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("J", true).commit();

                }
                // Put some meat on the sandwich
                else {
                    checkJapanese.setChecked(false);
                    //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "NoBURGERS").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("J", false).commit();

                }
                break;
            case R.id.checkbox_veitnamese:
                if (checked) {
                    checkVeitnamese.setChecked(true);
                    // PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "burger").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("V", true).commit();

                }
                // Put some meat on the sandwich
                else {
                    checkVeitnamese.setChecked(false);
                    //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "NoBURGERS").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("V", false).commit();

                }
                break;

        }
    }
}
