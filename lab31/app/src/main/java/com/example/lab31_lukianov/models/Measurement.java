package com.example.lab31_lukianov.models;

import java.security.Timestamp;

public class Measurement
{
    public int id;
    public Counter counter;
    public String image;
    public Timestamp ts;
    public Float value;

    public Measurement(int id, Counter counter, String image, Timestamp ts, Float value)
    {
        this.id = id;
        this.counter = counter;
        this.image = image;
        this.ts = ts;
        this.value = value;
    }

    public String toString()
    {
        return counter.id + "\t|\t" + value.toString();
    }

}
