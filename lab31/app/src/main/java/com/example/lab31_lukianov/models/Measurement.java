package com.example.lab31_lukianov.models;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Measurement
{
    public int id;
    public int counter;
    public String image;
    public Date ts;
    public Float value;

    public Measurement(int id, int counter, String image, Date ts, Float value)
    {
        this.id = id;
        this.counter = counter;
        this.image = image;
        this.ts = ts;
        this.value = value;
    }

    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

    public String toString()
    {
        return id + "\t\t\t\t" + dateFormat.format(ts) + "\t\t\t\t" + value.toString();
    }

}
