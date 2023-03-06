package com.example.lab31_lukianov.models;

import android.location.Location;

public class Counter
{
    public int id;
    public int icon;
    public int location;
    public String name;
    public String unit;

    public Counter(int id, int icon, int location, String name, String unit)
    {
        this.id = id;
        this.icon = icon;
        this.location = location;
        this.name = name;
        this.unit = unit;
    }

    public String toString()
    {
        return id + "\t|\t" + name;
    }

}
