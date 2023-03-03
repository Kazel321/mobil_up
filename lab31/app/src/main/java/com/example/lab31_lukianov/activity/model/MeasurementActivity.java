package com.example.lab31_lukianov.activity.model;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lab31_lukianov.ApiHelper;
import com.example.lab31_lukianov.R;
import com.example.lab31_lukianov.activity.BaseActivity;
import com.example.lab31_lukianov.activity.MeasurementsActivity;
import com.example.lab31_lukianov.activity.RatesActivity;
import com.example.lab31_lukianov.g;
import com.example.lab31_lukianov.models.Counter;
import com.example.lab31_lukianov.models.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MeasurementActivity extends BaseActivity {

    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

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

    ImageView img;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        ctx = this;

        spnCounters = findViewById(R.id.spnOneMeasurementCounter);
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
                                            if (c.id == g.m.counter) itm = (int)cAdapter.getPosition(c);
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

        txtTs = findViewById(R.id.txtOneMeasurementTs);
        txtValue = findViewById(R.id.txtMeasurementValue);

        if (g.action == "edit") {
            txtTs.setText(dateFormat.format(g.m.ts));
            txtValue.setText(g.m.value.toString());
        }
        else
        {
            txtTs.setText(dateFormat.format(new Date()));
        }

        btnDel = findViewById(R.id.btnDelMeasurement);
        if (g.action == "add") btnDel.setVisibility(View.GONE);

        img = findViewById(R.id.imgMeasurements);

        try {
            String b64 = g.m.image;
            byte[] jpeg = Base64.decode(b64, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
            img.setImageBitmap(bmp);
        }
        catch (Exception e)
        {
            Toast.makeText(ctx, "Fail to parse image", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void onImage(View v)
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    Bitmap bmp;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && requestCode == 1) {
            try {
                InputStream is = getContentResolver().openInputStream(data.getData());

                bmp = BitmapFactory.decodeStream(is);

                img.setImageBitmap(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

        String image;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
            byte[] ba = os.toByteArray();
            image = Base64.encodeToString(ba, Base64.NO_WRAP);
        }
        catch (Exception e)
        {
            Toast.makeText(ctx, "Failed to parse image", Toast.LENGTH_SHORT).show();
            return;
        }

        String req = "";
        String body = "";
        switch (g.action)
        {
            case "add":
            {
                req = "/add_measurement";
                body = "{\"counter1\": " + counteriId + ", \"image1\": \"" + image +"\", \"key1\": \"" + g.key + "\", \"ts1\": \"" + date + "\", \"value1\": " + value + "}";
                break;
            }
            case "edit":
            {
                req = "/update_measurement";
                body = "{\"counter1\": " + counteriId + ", \"image1\": \"" + image +"\", \"key1\": \"" + g.key + "\", \"ts1\": \"" + date + "\", \"value1\": " + value + "}";
            }
        }

        if (g.action == "edit") return;

        apiHelper = new ApiHelper(ctx)
        {
            @Override
            public void on_ready(String res) {
                if (res.equals("true")) {
                    ctx.finish();
                    i = new Intent(ctx, MeasurementsActivity.class);
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
                    i = new Intent(ctx, MeasurementsActivity.class);
                    startActivity(i);
                }
            }
        };
        apiHelper.send("/delete_measurement", "{\"key1\": \"" + g.key + "\", \"measurement1\": " + g.m.id + "}");
    }
}