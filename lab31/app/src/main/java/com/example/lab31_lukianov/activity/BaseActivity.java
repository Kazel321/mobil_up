package com.example.lab31_lukianov.activity;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab31_lukianov.ApiHelper;
import com.example.lab31_lukianov.R;
import com.example.lab31_lukianov.g;

public class BaseActivity extends AppCompatActivity
{
    AlertDialog alertDialogChangePass;
    Activity ctx;
    View dialogChangePassView;
    EditText changePass;
    Button btnChangePass;
    Intent i;
    ApiHelper apiHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
