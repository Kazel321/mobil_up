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
