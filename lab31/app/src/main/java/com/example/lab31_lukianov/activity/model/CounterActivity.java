package com.example.lab31_lukianov.activity.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lab31_lukianov.ApiHelper;
import com.example.lab31_lukianov.R;
import com.example.lab31_lukianov.activity.AuthActivity;
import com.example.lab31_lukianov.activity.CountersActivity;
import com.example.lab31_lukianov.activity.LocationsActivity;
import com.example.lab31_lukianov.activity.MenuActivity;
import com.example.lab31_lukianov.g;
import com.example.lab31_lukianov.models.Location;

import org.json.JSONArray;
import org.json.JSONObject;

public class CounterActivity extends AppCompatActivity {

    AlertDialog alertDialogChangePass;
    Activity ctx;
    View dialogChangePassView;
    EditText changePass;
    Button btnChangePass;
    Intent i;
    ApiHelper apiHelper;

    Spinner spnLocations;
    ArrayAdapter<Location> locationAdapter;
    EditText txtName;
    EditText txtUnit;

    String selLocation = "";

    int id;
    int locationId;

    Button btnDel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        ctx = this;

        btnDel = findViewById(R.id.btnDelCounter);

        txtName = findViewById(R.id.txtCounterName);
        txtUnit = findViewById(R.id.txtCounterUnit);

        spnLocations = findViewById(R.id.spnCounterLocations);
        locationAdapter = new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1);
        spnLocations.setAdapter(locationAdapter);

        i = getIntent();
        id = i.getIntExtra("id", -1);
        locationId = i.getIntExtra("locationId", -1);
        String name = i.getStringExtra("name");
        String unit = i.getStringExtra("unit");

        txtName.setText(name);
        txtUnit.setText(unit);

        apiHelper = new ApiHelper(ctx) {
            @Override
            public void on_ready(String res)
            {
                try {
                    JSONArray arr = new JSONArray(res);
                    int itm = 0;
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        Location l = new Location();
                        l.id = obj.getInt("id2");
                        if (l.id == locationId)
                            itm = i;
                        l.name = obj.getString("name2");
                        locationAdapter.add(l);
                    }
                    spnLocations.setSelection(itm);
                    locationAdapter.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        apiHelper.send("/get_locations", "{\"key1\": \"" + g.key + "\"}");

        spnLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selLocation = "" + locationAdapter.getItem(i).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (g.action == "add") btnDel.setVisibility(View.GONE);

        //dialog
        LayoutInflater dialogLayout = LayoutInflater.from(this);
        dialogChangePassView = dialogLayout.inflate(R.layout.pass_dialog, null);
        alertDialogChangePass = new AlertDialog.Builder(this).create();
        alertDialogChangePass.setView(dialogChangePassView);
        changePass = dialogChangePassView.findViewById(R.id.editTextNewPass);
        btnChangePass = dialogChangePassView.findViewById(R.id.btnChangePass);
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (changePass.getText().toString().isEmpty())
                {
                    Toast.makeText(dialogChangePassView.getContext(), "New password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String newPass = changePass.getText().toString();
                ApiHelper apiHelper = new ApiHelper(ctx)
                {
                    @Override
                    public void on_ready(String res)
                    {
                        g.db.saveToken("null");
                        i = new Intent(ctx, AuthActivity.class);
                        alertDialogChangePass.cancel();
                        ctx.finish();
                        startActivity(i);
                    }
                };
                apiHelper.send("/update_password", "{\"key1\": \"" + g.key + "\", \"password1\": \"" + newPass + "\"}");
            }
        });
    }

    public void onSave(View v)
    {
        String name = txtName.getText().toString();
        String unit = txtUnit.getText().toString();
        int icon = 0;
        int locationId = locationAdapter.getItem(spnLocations.getSelectedItemPosition()).id;

        String req = "";
        String body = "";
        switch (g.action)
        {
            case "add":
            {
                req = "/add_counter";
                body = "{\"icon1\": 0, \"key1\": \"" + g.key + "\", \"location1\": " + locationId + ", \"name1\": \"" + name + "\", \"unit1\": \"" + unit + "\"}";
                break;
            }
            case "edit":
            {
                req = "/update_counter";
                body = "{\"counter1\": " + id + ", \"icon1\": 0, \"key1\": \"" + g.key + "\", \"location1\": " + locationId + ", \"name1\": \"" + name + "\", \"unit1\": \"" + unit + "\"}";
            }
        }
        apiHelper = new ApiHelper(ctx)
        {
            @Override
            public void on_ready(String res) {
                if (res.equals("true")) {
                    ctx.finish();
                    i = new Intent(ctx, CountersActivity.class);
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
                    i = new Intent(ctx, CountersActivity.class);
                    startActivity(i);
                }
            }
        };
        apiHelper.send("/delete_counter", "{\"counter1\": " + id + ", \"key1\": \"" + g.key + "\"}");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case R.id.itm_menu:
            {
                i = new Intent(this, MenuActivity.class);
                finish();
                startActivity(i);
                break;
            }
            case R.id.itm_sign_out:
            {
                ApiHelper apiHelper = new ApiHelper(this)
                {
                    @Override
                    public void on_ready(String res) {
                        super.on_ready(res);
                        if (res.equals(true)) {
                            g.db.saveToken("null");
                            i = new Intent(ctx, AuthActivity.class);
                            finish();
                            startActivity(i);
                        }
                    }
                };
                apiHelper.send("/sign_out", "{\"key1\": \"" + g.key + "\"}");
                break;
            }
            case R.id.itm_change_pass:
            {
                alertDialogChangePass.show();
                break;
            }
            case R.id.itm_del_acc:
            {
                ApiHelper apiHelper = new ApiHelper(ctx)
                {
                    @Override
                    public void on_ready(String res)
                    {
                        if (res.equals("true")) {
                            g.db.saveToken("null");
                            i = new Intent(ctx, AuthActivity.class);
                            finish();
                            startActivity(i);
                        }
                    }
                };
                apiHelper.send("/delete_account", "{\"key1\": \"" + g.key + "\"}");
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}