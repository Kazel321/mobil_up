package com.example.lab31_lukianov;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    String sql;

    @Override
    public void onCreate(SQLiteDatabase db) {
        sql = "CREATE TABLE Settings (username TEXT, password TEXT);";
        db.execSQL(sql);
        sql = "CREATE TABLE Session (sessionkey TEXT);";
        db.execSQL(sql);
        sql = "CREATE TABLE APIEndPoint (endpoint TEXT);";
        db.execSQL(sql);
        sql = "CREATE TABLE MeasImages (id TEXT, image TEXT);";
        db.execSQL(sql);
    }

    public int getCountImage()
    {
        SQLiteDatabase db = getReadableDatabase();
        sql = "SELECT COUNT(id) FROM MeasImages;";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst())
        {
            return cur.getInt(0);
        }
        return -1;
    }

    public void addImage(String id, String image)
    {
        SQLiteDatabase db = getWritableDatabase();
        sql = "INSERT INTO MeasImages VALUES ('" + id + "', '" + image + "');";
        db.execSQL(sql);
    }

    public void updateImage(String id, String newId, String image)
    {
        SQLiteDatabase db = getWritableDatabase();
        sql = "DELETE FROM MeasImages WHERE id = '" + id + "';";
        db.execSQL(sql);
        addImage(newId, image);
    }

    public String getImage(String id)
    {
        SQLiteDatabase db = getReadableDatabase();
        sql = "SELECT * FROM MeasImages WHERE id = '" + id + "';";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst())
        {
            return cur.getString(1);
        }
        return null;
    }

    public void delImage(String id)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM MeasImages WHERE id = '" + id + "';";
        db.execSQL(sql);
    }

    public void saveEndPoint(String endpoint)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM APIEndPoint;";
        db.execSQL(sql);
        sql = "INSERT INTO APIEndPoint VALUES ('" + endpoint + "');";
        db.execSQL(sql);
    }

    public String getEndPoint()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM APIEndPoint;";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst())
        {
            String endpoint = cur.getString(0);
            return endpoint;
        }
        else return null;
    }

    public void saveSettings(String username, String password)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM Settings;";
        db.execSQL(sql);
        sql = "INSERT INTO Settings VALUES ('" + username + "', '" + password + "');";
        db.execSQL(sql);
    }

    public String[] getSettings()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM Settings;";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst())
        {
            String[] settings = new String[2];
            settings[0] = cur.getString(0);
            settings[1]= cur.getString(1);
            return settings;
        }
        else return null;
    }

    public void onDeleteAll()
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM Settings;";
        db.execSQL(sql);
    }

    public void saveToken(String sessionkey)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM Session;";
        db.execSQL(sql);
        sql = "INSERT INTO Session VALUES ('" + sessionkey + "');";
        db.execSQL(sql);
    }

    public String getToken()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM Session;";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst())
        {
            String token = cur.getString(0);
            return token;
        }
        else return null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
