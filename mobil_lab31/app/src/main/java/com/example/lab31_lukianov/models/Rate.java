package com.example.lab31_lukianov.models;

import java.security.Timestamp;

public class Rate
{
    public int id;
    public Counter counter;
    public Timestamp ts;
    public Float value;

    public Rate(int id, Counter counter, Timestamp ts, Float value)
    {
        this.id = id;
        this.counter = counter;
        this.ts = ts;
        this.value = value;
    }

    public String toString()
    {
        return counter.id + "\t|\t" + value.toString();
    }
}
