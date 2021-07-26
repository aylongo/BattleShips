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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnRegister, btnCancel;
    EditText etUsername, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
    }

    @Override
    public void onClick(View view) {
        if (view == btnRegister) {
            etUsername.setError(null);
            etPassword.setError(null);
            etConfirmPassword.setError(null);
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            if (isValidRegister(username, password, confirmPassword)) {
                if (register(username, password)) {
                    Intent intent = new Intent(this, LoggedMainActivity.class);
                    intent.putExtra("Username", username);
                    intent.putExtra("Request", 2);
                    Toast.makeText(this, "You have registered successfully", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Register has failed", Toast.LENGTH_LONG).show();
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

    public boolean register(String username, String password) {
        JSONObject register = new JSONObject();
        try {
            register.put("request", "register");
            register.put("username", username);
            register.put("password", password);
            Client client = new Client(register);
            JSONObject received = client.execute().get();
            String response = received.getString("response");
            System.out.println(String.format("'%s' registered: %s", username, response));
            return Boolean.parseBoolean(response);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidRegister(String username, String password, String confirmPassword) {
        /*
        The function gets the data that the user filled in the registration form and returns true
        if the data is valid and false if it isn't valid.
        A valid username is a username that its length isn't 0.
        A valid password is a password that its length isn't 0. The passwords (the data from
        the password and the confirm password fields) must be equal.
        */
        int errorsCounter = 0;
        if (username.length() == 0) {
            etUsername.setError("Please fill the username field");
            errorsCounter++;
        } if (username.length() > 12) {
            etUsername.setError("Username contains 12 characters at most");
            errorsCounter++;
        } if (password.length() == 0) {
            etPassword.setError("Please fill the password field");
            errorsCounter++;
        } if (password.length() > 20) {
            etPassword.setError("Password contains 20 characters at most");
            errorsCounter++;
        } if (confirmPassword.length() == 0) {
            etConfirmPassword.setError("Please fill the confirm password field");
            errorsCounter++;
        } if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords must be equal");
            errorsCounter++;
        }
        return (errorsCounter == 0);
    }
}
