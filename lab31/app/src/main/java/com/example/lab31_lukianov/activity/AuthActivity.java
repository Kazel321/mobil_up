package com.example.lab31_lukianov.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lab31_lukianov.ApiHelper;
import com.example.lab31_lukianov.DB;
import com.example.lab31_lukianov.R;
import com.example.lab31_lukianov.g;

public class AuthActivity extends AppCompatActivity {

    EditText login, passwd;
    CheckBox chkSave;
    String log, pass, session;
    String[] logPass;
    Intent i;

    Activity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ctx = this;
        login = findViewById(R.id.editTextAuthLogin);
        passwd = findViewById(R.id.editTextAuthPass);
        chkSave = findViewById(R.id.chkSave);

        g.db = new DB(this, "counters.db", null, 1);

        String token = "";
        if (g.db.getToken() != null)
            token = g.db.getToken();
        if (token != null && !token.equals(""))
        if (!token.equals("null")) {
            g.key = token;
            i = new Intent(this, MenuActivity.class);
            startActivity(i);
        }

        logPass = g.db.getSettings();
        if (logPass != null)
        {
            chkSave.setChecked(true);
            login.setText(logPass[0]);
            passwd.setText(logPass[1]);
        }
        else
        {
            chkSave.setChecked(false);
            login.setText("");
            passwd.setText("");
        }
    }

    public void on_sign_in_auth(View v)
    {
        if (login.getText().toString().isEmpty() || passwd.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Text field(s) is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        log = login.getText().toString();
        pass = passwd.getText().toString();
        if (chkSave.isChecked()) //If save needed
        {
            g.db = new DB(this, "counters.db", null, 1);
            g.db.saveSettings(log, pass);
        }
        ApiHelper apiHelper = new ApiHelper(this)
        {
            @Override
            public void on_ready(String res) {
                if (!res.equals("null")) {
                    g.key = res.substring(1, res.length() - 1);
                    g.db.saveToken(g.key);
                    i = new Intent(ctx, MenuActivity.class);
                    startActivity(i);
                    ctx.finish();
                }
                else
                {
                    Toast.makeText(ctx, "Invalid login or pass", Toast.LENGTH_SHORT).show();
                }
            }
        };
        apiHelper.send("/sign_in", "{\"name1\": \"" + log + "\", \"password1\": \"" + pass + "\"}");
    }

    public void on_sign_up_auth(View v)
    {
        i = new Intent(this, RegActivity.class);
        startActivity(i);
        finish();
    }
}