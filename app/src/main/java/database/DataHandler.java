package database;

/**
 * Created by george on 7/20/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataHandler {
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String TABLE_NAME = "mytable";
    public static final String DATA_BASE_NAME = "mydatabase";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_CREATE = "create table mytable (name text not null, email not null);";

    DataBaseHelper dbhelper;
    Context ctx;
    SQLiteDatabase db;

    public DataHandler(Context ctx) {
        this.ctx = ctx;
        dbhelper = new DataBaseHelper(ctx);
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context ctx) {
            super(ctx, DATA_BASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(TABLE_CREATE);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS mytable");
            onCreate(db);
        }


    }

    public DataHandler open(){
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbhelper.close();
    }

    public long insertData(String name, String email){
        ContentValues content = new ContentValues();
        content.put(NAME, name);
        content.put(EMAIL, email);



        return db.insertOrThrow(TABLE_NAME, null, content);

    }

    public Cursor returnData(){

        return db.query(TABLE_NAME, new String[] {NAME,EMAIL}, null, null, null, null, null);
    }
}
