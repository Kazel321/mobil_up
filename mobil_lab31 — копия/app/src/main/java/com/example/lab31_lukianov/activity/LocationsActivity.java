package com.example.lab31_lukianov.activity;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab31_lukianov.ApiHelper;
import com.example.lab31_lukianov.R;
import com.example.lab31_lukianov.activity.model.LocationActivity;
import com.example.lab31_lukianov.g;
import com.example.lab31_lukianov.models.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationsActivity extends AppCompatActivity {

    AlertDialog alertDialogChangePass;
    Activity ctx;
    View dialogChangePassView;
    EditText changePass;
    Button btnChangePass;
    Intent i;
    ApiHelper apiHelper;

    ListView lstctl;
    ArrayList<Location> lst = new ArrayList<>();
    ArrayAdapter<Location> adp;


    int selectedLocation = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        ctx = this;

        lstctl = findViewById(R.id.listLocations);
        lstctl.setOnItemClickListener((parent, view, position, id) ->
        {
            selectedLocation = (int) id + 1;
            g.action = "edit";
            i = new Intent(this, LocationActivity.class);
            i.putExtra("id", lst.get(selectedLocation-1).id);
            i.putExtra("name", lst.get(selectedLocation-1).name);
            startActivity(i);
        });
        adp = new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, lst);
        lstctl.setAdapter(adp);
        update();

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
                        String name = jsonObject.getString("name2");

                        Location location = new Location(id, name);
                        lst.add(location);
                    }
                    adp.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };
        apiHelper.send("/get_locations", "{\"key1\": \"" + g.key + "\"}");
    }

    public void onAdd(View v)
    {
        g.action = "add";
        i = new Intent(this, LocationActivity.class);
        startActivity(i);
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
                        g.db.saveToken("null");
                        i = new Intent(ctx, AuthActivity.class);
                        finish();
                        startActivity(i);
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