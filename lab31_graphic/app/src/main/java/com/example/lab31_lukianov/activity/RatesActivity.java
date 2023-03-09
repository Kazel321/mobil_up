package com.example.lab31_lukianov.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.lab31_lukianov.ApiHelper;
import com.example.lab31_lukianov.R;
import com.example.lab31_lukianov.activity.model.CounterActivity;
import com.example.lab31_lukianov.activity.model.RateActivity;
import com.example.lab31_lukianov.g;
import com.example.lab31_lukianov.models.Counter;
import com.example.lab31_lukianov.models.Location;
import com.example.lab31_lukianov.models.Rate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RatesActivity extends BaseActivity {

    Intent i;
    ApiHelper apiHelper;

    ListView lstctl;
    ArrayList<Rate> lst = new ArrayList<>();
    ArrayAdapter<Rate> adp;

    Spinner spnCounters;
    ArrayAdapter<Counter> cAdapter;

    int selectedRate = -1;
    String selCounter = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);

        ctx = this;

        spnCounters = findViewById(R.id.spnRateCounter);
        cAdapter = new ArrayAdapter<Counter>(this, android.R.layout.simple_list_item_1);
        spnCounters.setAdapter(cAdapter);

        lstctl = findViewById(R.id.listRates);
        lstctl.setOnItemClickListener((parent, view, position, id) ->
        {
            selectedRate = (int) id + 1;
            g.action = "edit";
            i = new Intent(this, RateActivity.class);
            g.r = adp.getItem((int)id);
            startActivity(i);
        });

        adp = new ArrayAdapter<Rate>(this, android.R.layout.simple_list_item_1, lst);
        lstctl.setAdapter(adp);
        update();

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
                                    }
                                    cAdapter.notifyDataSetChanged();
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

        spnCounters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selCounter = "" + cAdapter.getItem(i).id;
                update();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public void update()
    {
        lst.clear();
        apiHelper = new ApiHelper(this)
        {
            @Override
            public void on_ready(String res) {
                try
                {
                    JSONArray arr = new JSONArray(res);
                    for (int j = 0; j < arr.length(); j++)
                    {
                        JSONObject jsonObject = arr.getJSONObject(j);
                        int id = jsonObject.getInt("id2");
                        Date date = parseFormat.parse(jsonObject.getString("ts2").toString());
                        Float value = Float.parseFloat(jsonObject.get("value2").toString());

                        Rate r = new Rate(id, Integer.parseInt(selCounter), date, value);
                        lst.add(r);
                    }
                    adp.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };
        apiHelper.send("/get_rates", "{\"counter1\": " + selCounter + ", \"key1\": \"" + g.key + "\"}");
    }

    public void onAdd(View v)
    {
        g.action = "add";
        i = new Intent(this, RateActivity.class);
        startActivity(i);
    }
}