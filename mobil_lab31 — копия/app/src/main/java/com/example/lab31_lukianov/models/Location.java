package com.example.lab31_lukianov.models;

public class Location
{
    public int id;
    public String name;

    public Location(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String toString()
    {
        return id + "\t|\t" + name;
    }
}
