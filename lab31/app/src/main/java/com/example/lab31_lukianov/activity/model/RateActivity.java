package com.example.lab31_lukianov.activity.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.lab31_lukianov.ApiHelper;
import com.example.lab31_lukianov.R;
import com.example.lab31_lukianov.activity.BaseActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class RateActivity extends BaseActivity
{

    DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");

    Intent i;
    ApiHelper apiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
    }
}