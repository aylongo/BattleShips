package com.example.project_battleships;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnLogin, btnCancel;
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogin) {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            if (isValidLogin(username, password)) {
                if (login(username, password)) {
                    Intent intent = new Intent(this, LoggedMainActivity.class);
                    intent.putExtra("Username", username);
                    intent.putExtra("Request", 1);
                    Toast.makeText(this, "You have logged in successfully", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Login has failed", Toast.LENGTH_LONG).show();
                }
            }
        } else if (view == btnCancel) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean login(String username, String password) {
        JSONObject login = new JSONObject();
        try {
            login.put("request", "login");
            login.put("username", username);
            login.put("password", password);
            Client client = new Client(login);
            JSONObject received = client.execute().get();
            String response = received.getString("response");
            System.out.println(String.format("'%s' logged in: %s", username, response));
            return Boolean.parseBoolean(response);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidLogin(String username, String password) {
        /*
        The function gets the data that the user filled in the login form and returns true
        if the data is valid and false if it isn't valid.
        A valid username is a username that its length isn't 0.
        A valid password is a password that its length isn't 0.
        */
        int errorsCounter = 0;
        if (username.length() == 0) {
            etUsername.setError("Please fill the username field");
            errorsCounter++;
        } if (password.length() == 0) {
            etPassword.setError("Please fill the password field");
            errorsCounter++;
        }
        return (errorsCounter == 0);
    }
}
