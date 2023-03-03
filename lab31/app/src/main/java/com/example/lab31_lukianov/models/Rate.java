package com.example.lab31_lukianov.models;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Rate
{
    public int id;
    public int counter;
    public Date ts;
    public Float value;

    public Rate(int id, int counter, Date ts, Float value)
    {
        this.id = id;
        this.counter = counter;
        this.ts = ts;
        this.value = value;
    }

    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public String toString()
    {
        return id + "\t\t\t\t" + dateFormat.format(ts) + "\t\t\t\t" + value.toString();
    }
}
