package com.everyoo.smartgateway.everyoozwave.tronico.kuju;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KeyDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "key.db";
    public static final String TABLE_NAME = "key";
    public static final int DB_VERSION = 1;

    public KeyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME
                + "(id varchar(50) ,"
                + "key varchar(50))";
        db.execSQL(createSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS"+TABLE_NAME);
    }

    public void insert(String code) {
        ContentValues values = new ContentValues();
        values.put("id","2");
        values.put("key", code);
        SQLiteDatabase db = getWritableDatabase();
        db.replace(TABLE_NAME, null, values);
        db.close();
    }

    public void delData(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, " where id = "+"2" , null);
    }

    public String getKey() {
        String key=null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from key where id = ?", new String[] {"2"});
              if(cursor.moveToFirst()) {
                     key = cursor.getString(cursor.getColumnIndex("key"));
                   }else {
                  key = null;
                   }
        return key;
    }


}
