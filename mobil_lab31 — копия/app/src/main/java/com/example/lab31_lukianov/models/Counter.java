package com.example.lab31_lukianov.models;

import android.location.Location;

public class Counter
{
    public int id;
    public int icon;
    public Location location;
    public String name;
    public String unit;

    public Counter(int id, int icon, Location location, String name, String unit)
    {
        this.id = id;
        this.icon = icon;
        this.location = location;
        this.name = name;
        this.unit = unit;
    }

    public String toString()
    {
        return name;
    }

}
