package apitest;

import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;

import com.example.ozzca_000.myapplication.R;


public class CheckBoxActivity extends ActionBarActivity {
    private static CheckBox C1, C2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_box);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        C1 = (CheckBox) findViewById(R.id.checkBoxCustomized1);
        C2 = (CheckBox) findViewById(R.id.checkbox_burger);
        C2.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("KEY", false));
        //Log.i("SHARED", String.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("KEY", false)));
    }
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        //SharedPreferences preferences = getApplicationContext().getSharedPreferences("checkbox", android.content.Context.MODE_PRIVATE);
        // SharedPreferences.Editor editor = preferences.edit();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_burger:
                if (checked){

                    // PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "burger").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("KEY",true).commit();

                }
                // Put some meat on the sandwich
                else{

                    //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("KEY", "NoBURGERS").commit();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("KEY", false).commit();

                }
                // Remove the meat
                break;
            case R.id.checkBoxCustomized1:
                if (checked){

                }
                // Cheese me
                else{

                }
                // I'm lactose intolerant
                break;
            // TODO: Veggie sandwich
        }

    }


}
