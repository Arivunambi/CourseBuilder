package com.arivunambi.coursebuilder;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;

/**
 * Created by arivu on 4/7/2017.
 */
public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "CourseBuilder";
    // Contacts table name
    private static final String TABLE_NAME = "URLCache";
    private static final String TABLE2_NAME = "BlobCache";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "url";
    private static final String KEY_SH_ADDR = "response";
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_URLCACHE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
        + KEY_SH_ADDR + " TEXT" + ")";
        db.execSQL(CREATE_URLCACHE_TABLE);
        String CREATE_BLOBCACHE_TABLE = "CREATE TABLE " + TABLE2_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_SH_ADDR + " BLOB" + ")";
        db.execSQL(CREATE_BLOBCACHE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
// Creating tables again
        onCreate(db);
    }

    public void setURLCache(String url, String res) {
        String isPresent = getURLCache(url);
        if (isPresent != null){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_SH_ADDR, res);
// updating row
            db.update(TABLE_NAME, values, KEY_NAME + " = ?", new String[]{url});
            db.close(); // Closing database connection
        }else{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, url); // Shop Name
            values.put(KEY_SH_ADDR, res); // Shop Phone Number
            db.insert(TABLE_NAME, null, values);
            db.close(); // Closing database connection
        }
    }

    public String getURLCache(String url) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
                        KEY_NAME, KEY_SH_ADDR }, KEY_NAME + "=?",
                new String[] { String.valueOf(url) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            if (cursor.getCount()>0){
                String resp = cursor.getString(2);
                db.close(); // Closing database connection
                return resp;
            }else {
                db.close(); // Closing database connection
                return null;
            }
        }
        else{
            db.close(); // Closing database connection
            return null;
        }

    }

    public void setBlobCache(String url, byte[] res) {
        String isPresent = getURLCache(url);
        if (isPresent != null){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_SH_ADDR, res);
// updating row
            db.update(TABLE2_NAME, values, KEY_NAME + " = ?", new String[]{url});
            db.close(); // Closing database connection
        }else{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, url); // Shop Name
            values.put(KEY_SH_ADDR, res); // Shop Phone Number
            db.insert(TABLE2_NAME, null, values);
            db.close(); // Closing database connection
        }
    }

    public byte[] getBlobCache(String url) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE2_NAME, new String[] { KEY_ID,
                        KEY_NAME, KEY_SH_ADDR }, KEY_NAME + "=?",
                new String[] { String.valueOf(url) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            if (cursor.getCount()>0){
                byte[] resp = cursor.getBlob(2);
                db.close(); // Closing database connection
                return resp;
            }else {
                db.close(); // Closing database connection
                return null;
            }
        }
        else{
            db.close(); // Closing database connection
            return null;
        }

    }
}