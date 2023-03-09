package com.example.lab31_lukianov.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.security.identity.CredentialDataResult;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.lab31_lukianov.ApiHelper;
import com.example.lab31_lukianov.R;
import com.example.lab31_lukianov.activity.BaseActivity;
import com.example.lab31_lukianov.activity.model.MeasurementActivity;
import com.example.lab31_lukianov.activity.model.RateActivity;
import com.example.lab31_lukianov.g;
import com.example.lab31_lukianov.models.Counter;
import com.example.lab31_lukianov.models.Location;
import com.example.lab31_lukianov.models.Measurement;
import com.example.lab31_lukianov.models.Rate;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MeasurementsActivity extends BaseActivity {

    Intent i;
    ApiHelper apiHelper;

    ListView lstctl;
    ArrayList<Measurement> lst = new ArrayList<>();
    ArrayAdapter<Measurement> adp;

    Spinner spnCounters;
    ArrayAdapter<Counter> cAdapter;

    int selectedMeasurement = -1;
    String selCounter = "";

    Activity ctx;

    SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    BarChart chart;
    ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
    ArrayList<String> xLabel = new ArrayList<>();

    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);

        ctx = this;

        chart = findViewById(R.id.chart);

        spnCounters = findViewById(R.id.spnMeasurementCounter);
        cAdapter = new ArrayAdapter<Counter>(this, android.R.layout.simple_list_item_1);
        spnCounters.setAdapter(cAdapter);

        lstctl = findViewById(R.id.listMeasurements);
        lstctl.setOnItemClickListener((parent, view, position, id) ->
        {
            selectedMeasurement = (int) id + 1;
            g.action = "edit";
            i = new Intent(this, MeasurementActivity.class);
            g.m = adp.getItem((int)id);
            startActivity(i);
        });

        adp = new ArrayAdapter<Measurement>(this, android.R.layout.simple_list_item_1, lst);
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

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(31);
        xAxis.setLabelCount(32, true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(xAxisFormatter);
    }

    public class DayAxisValueFormatter extends ValueFormatter {
        private final BarLineChartBase<?> chart;
        public DayAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }
        @Override
        public String getFormattedValue(float value) {
            return "" + (int)value;
        }
    }

    public void update()
    {
        entries.clear();
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
                        String image = jsonObject.getString("image2");

                        Measurement m = new Measurement(id, Integer.parseInt(selCounter), image, date, value);
                        lst.add(m);

                        Date nowDate = new Date();
                        if (date.getMonth() == nowDate.getMonth())
                        {
                            entries.add(new BarEntry((int)date.getDate()+1, m.value));
                        }
                    }
                    adp.notifyDataSetChanged();
                    BarDataSet dataset = new BarDataSet(entries, "Month measurements");

                    //dataset.set
                    BarData data = new BarData(dataset);

                    chart.setData(data);

                    Description desc = new Description();
                    desc.setText("day to measurement value");
                    chart.setDescription(desc);

                    chart.invalidate();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };
        apiHelper.send("/get_measurements", "{\"counter1\": " + selCounter + ", \"key1\": \"" + g.key + "\"}");
    }

    public void onAdd(View v)
    {
        g.action = "add";
        i = new Intent(this, MeasurementActivity.class);
        startActivity(i);
    }


/*
    public void updateChart()
    {

        apiHelper = new ApiHelper(ctx) {
            @Override
            public void on_ready(String res)
            {
                try {
                    JSONArray arrLocation = new JSONArray(res);
                    int i;
                    for (i = 0; i < arrLocation.length(); i++) {
                        JSONObject obj = arrLocation.getJSONObject(i);
                        Location l = new Location();
                        l.id = obj.getInt("id2");
                        int finalI = i;
                        ApiHelper lApi = new ApiHelper(ctx)
                        {
                            @Override
                            public void on_ready(String res)
                            {
                                try
                                {
                                    JSONArray arrCounter = new JSONArray(res);
                                    int j;
                                    for (j = 0; j < arrCounter.length(); j++)
                                    {
                                        JSONObject obj = arrCounter.getJSONObject(j);
                                        int id = obj.getInt("id2");
                                        Counter c = new Counter();
                                        c.id = id;
                                        int finalJ = j;
                                        ApiHelper mApi = new ApiHelper(ctx)
                                        {
                                            @Override
                                            public void on_ready(String res)
                                            {
                                                try
                                                {
                                                    JSONArray arr = new JSONArray(res);
                                                    for (int j = 0; j < arr.length(); j++)
                                                    {
                                                        JSONObject jsonObject = arr.getJSONObject(j);
                                                        int id = jsonObject.getInt("id2");
                                                        Date date = parseFormat.parse(jsonObject.getString("ts2").toString());
                                                        Float value = Float.parseFloat(jsonObject.get("value2").toString());
                                                        String image = jsonObject.getString("image2");

                                                        Measurement m = new Measurement(id, c.id, image, date, value);

                                                        Date nowDate = new Date();
                                                        if (date.getMonth() == nowDate.getMonth())
                                                        {

                                                        }
                                                    }
                                                }
                                                catch (Exception e)
                                                {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        mApi.send("/get_measurements", "{\"counter1\": " + c.id + ", \"key1\": \"" + g.key + "\"}");
                                    }
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
    }*/

}