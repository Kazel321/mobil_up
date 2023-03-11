package com.example.lab31_lukianov.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lab31_lukianov.ApiHelper;
import com.example.lab31_lukianov.R;
import com.example.lab31_lukianov.g;

public class RegActivity extends AppCompatActivity {

    EditText login, passwd, rePasswd;
    String log, pass;
    Intent i;

    Activity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        ctx = this;

        login = findViewById(R.id.editTextRegLogin);
        passwd = findViewById(R.id.editTextRegPass);
    }

    public void on_sign_up_reg(View v)
    {
        if (login.getText().toString().isEmpty() || passwd.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Text field(s) is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        log = login.getText().toString();
        pass = passwd.getText().toString();

        ApiHelper apiHelper = new ApiHelper(ctx)
        {
            @Override
            public void on_ready(String res) {
                if (res.equals("true"))
                {
                    ApiHelper signInApiHelper = new ApiHelper(ctx)
                    {
                        @Override
                        public void on_ready(String res)
                        {
                            g.key = res.substring(1, res.length()-1);
                            g.db.saveToken(g.key);
                            i = new Intent(ctx, MenuActivity.class);
                            startActivity(i);
                            ctx.finish();
                        }
                    };
                    signInApiHelper.send("/sign_in", "{\"name1\": \"" + log + "\", \"password1\": \"" + pass + "\"}");;
                }
                else
                {
                    Toast.makeText(RegActivity.this, "This login is exist", Toast.LENGTH_SHORT).show();
                }
            }
        };
        apiHelper.send("/register_account", "{\"name1\": \"" + log + "\", \"password1\": \"" + pass + "\"}");
    }

    public void on_sign_in_reg(View v)
    {
        i = new Intent(this, AuthActivity.class);
        startActivity(i);
        finish();
    }
}