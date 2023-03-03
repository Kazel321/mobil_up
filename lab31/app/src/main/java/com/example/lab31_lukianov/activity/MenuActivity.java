package com.example.lab31_lukianov.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lab31_lukianov.ApiHelper;
import com.example.lab31_lukianov.R;
import com.example.lab31_lukianov.g;

public class MenuActivity extends AppCompatActivity {

    AlertDialog alertDialogChangePass;
    Intent i;
    Activity ctx;
    View dialogChangePassView;
    EditText changePass;
    Button btnChangePass;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ctx = this;

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

    public void onSelectActivity(View v)
    {
        switch (v.getId())
        {
            case R.id.btnLocations:
            {
                i = new Intent(this, LocationsActivity.class);
                break;
            }
            case R.id.btnCounters:
            {
                i = new Intent(this, CountersActivity.class);
                break;
            }
            case R.id.btnRates:
            {
                i = new Intent(this, RatesActivity.class);
                break;
            }
            case R.id.btnMeasurements:
            {
                i = new Intent(this, CountersActivity.class);
                break;
            }
        }
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