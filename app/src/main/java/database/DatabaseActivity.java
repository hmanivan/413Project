package database;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ozzca_000.myapplication.R;

/**
 * Created by george on 7/20/15.
 */
public class DatabaseActivity extends Activity {
    Button save, load, toDBList;
    EditText name, email;
    DataHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        save = (Button) findViewById(R.id.save);
        load = (Button) findViewById(R.id.load);
        toDBList = (Button) findViewById(R.id.toDBList);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String getName = name.getText().toString();
                String getEmail = email.getText().toString();
                handler = new DataHandler(getBaseContext());
                handler.open();
                handler.insertData(getName, getEmail);
                Toast.makeText(getBaseContext(), "Data inserted", Toast.LENGTH_LONG).show();


                handler.close();
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getName, getEmail;
                getName = "";
                getEmail = "";
                handler = new DataHandler(getBaseContext());
                handler.open();
                Cursor C = handler.returnData();
                if (C.moveToFirst()) {
                    do {
                        getName = C.getString(0);
                        getEmail = C.getString(1);
                    } while (C.moveToNext());
                }

                handler.close();
                final TextView textViewToChange = (TextView) findViewById(R.id.textView2);
                textViewToChange.setText("Name: " + getName
                        + " Email: " + getEmail);
                Toast.makeText(getBaseContext(), "Name: " + getName
                        + " Email: " + getEmail, Toast.LENGTH_LONG).show();

            }
        });

        toDBList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getName, getEmail;
                getName = "";
                getEmail = "";
                handler = new DataHandler(getBaseContext());
                handler.open();
                Cursor C = handler.returnData();
                if (C.moveToFirst()) {
                    do {
                        getName = C.getString(0);
                        getEmail = C.getString(1);
                    } while (C.moveToNext());
                }

                handler.close();
                Intent myIntent = new Intent(DatabaseActivity.this, DBListActivity.class);
                myIntent.putExtra("gotName", getName); //Optional parameters
                myIntent.putExtra("gotEmail", getEmail); //Optional parameters
                DatabaseActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
