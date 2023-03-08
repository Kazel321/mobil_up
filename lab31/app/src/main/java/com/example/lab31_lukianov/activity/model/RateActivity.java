package com.example.lab31_lukianov.activity.model;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lab31_lukianov.ApiHelper;
import com.example.lab31_lukianov.R;
import com.example.lab31_lukianov.activity.BaseActivity;
import com.example.lab31_lukianov.activity.CountersActivity;
import com.example.lab31_lukianov.activity.RatesActivity;
import com.example.lab31_lukianov.g;
import com.example.lab31_lukianov.models.Counter;
import com.example.lab31_lukianov.models.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RateActivity extends BaseActivity
{

    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    Intent i;
    ApiHelper apiHelper;

    Spinner spnCounters;
    ArrayAdapter<Counter> cAdapter;

    String selCounter = "";
    int itm = 0;

    Activity ctx;

    EditText txtTs;
    EditText txtValue;

    Button btnDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        ctx = this;

        spnCounters = findViewById(R.id.spnOneRateCounter);
        cAdapter = new ArrayAdapter<Counter>(this, android.R.layout.simple_list_item_1);
        spnCounters.setAdapter(cAdapter);

        apiHelper = new ApiHelper(ctx) {
            @Override
            public void on_ready(String res)
            {
                try {
                    JSONArray arr = new JSONArray(res);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        Location l = new Location();
                        l.id = obj.getInt("id2");
                        ApiHelper lApi = new ApiHelper(ctx)
                        {
                            @SuppressLint("SuspiciousIndentation")
                            @Override
                            public void on_ready(String res)
                            {
                                try
                                {
                                    JSONArray arr = new JSONArray(res);
                                    for (int j = 0; j < arr.length(); j++)
                                    {
                                        JSONObject obj = arr.getJSONObject(j);
                                        int id = obj.getInt("id2");
                                        String name = obj.getString("name2");
                                        String unit = obj.getString("unit2");
                                        int icon = obj.getInt("icon2");
                                        int locaiton = obj.getInt("location2");

                                        Counter c = new Counter(id, icon, locaiton, name, unit);
                                        cAdapter.add(c);
                                        if (g.action == "edit")
                                        if (c.id == g.r.counter) itm = (int)cAdapter.getPosition(c);
                                    }
                                    cAdapter.notifyDataSetChanged();
                                    if (g.action == "edit")
                                    spnCounters.setSelection((int)itm);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        };
                        lApi.send("/get_counters", "{\"key1\": \"" + g.key + "\", \"location1\": " + l.id + "}");
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        apiHelper.send("/get_locations", "{\"key1\": \"" + g.key + "\"}");

        txtTs = findViewById(R.id.txtOneRateTs);
        txtValue = findViewById(R.id.txtRateValue);

        if (g.action == "edit") {
            txtTs.setText(dateFormat.format(g.r.ts));
            txtValue.setText(g.r.value.toString());
        }
        else
        {
            txtTs.setText(dateFormat.format(new Date()));
        }

        btnDel = findViewById(R.id.btnDelRate);
        if (g.action == "add") btnDel.setVisibility(View.GONE);
    }

    public void onSave(View v)
    {
        String date = txtTs.getText().toString();
        String value = txtValue.getText().toString();
        Date ts;
        try {
            ts = dateFormat.parse(date);
        }
        catch (Exception e)
        {
            Toast.makeText(ctx, "Failed to parse ts", Toast.LENGTH_SHORT).show();
            return;
        }
        int counteriId = cAdapter.getItem(spnCounters.getSelectedItemPosition()).id;

        String req = "";
        String body = "";
        switch (g.action)
        {
            case "add":
            {
                req = "/add_rate";
                body = "{\"counter1\": " + counteriId + ", \"key1\": \"" + g.key + "\", \"ts1\": \"" + date + "\", \"value1\": " + value + "}";
                break;
            }
            case "edit":
            {
                req = "/update_rate";
                body = "{\"counter1\": " + counteriId + ", \"key1\": \"" + g.key + "\", \"rate1\": " + g.r.id + ", \"ts1\": \"" + date + "\", \"value1\": " + value + "}";
            }
        }

        apiHelper = new ApiHelper(ctx)
        {
            @Override
            public void on_ready(String res) {
                if (res.equals("true")) {
                    ctx.finish();
                    i = new Intent(ctx, RatesActivity.class);
                    startActivity(i);
                }
            }
        };
        apiHelper.send(req, body);
    }

    public void onDelete(View v)
    {
        apiHelper = new ApiHelper(ctx)
        {
            @Override
            public void on_ready(String res) {
                if (res.equals("true")) {
                    ctx.finish();
                    i = new Intent(ctx, RatesActivity.class);
                    startActivity(i);
                }
            }
        };
        apiHelper.send("/delete_rate", "{\"key1\": \"" + g.key + "\", \"rate1\": " + g.r.id + "}");
    }
}