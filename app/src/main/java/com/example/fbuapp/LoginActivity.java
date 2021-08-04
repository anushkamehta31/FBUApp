package com.example.fbuapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbuapp.databinding.ActivityLoginBinding;
import com.google.android.gms.maps.model.LatLng;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    TextView tvCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());

        // layout of activity is stored in root property
        View view = binding.getRoot();
        setContentView(view);

        // Change status bar color
        if (android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }

        // If the user is already logged in then we want to go to the main activity automatically
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // Get references more efficiently
        etUsername = binding.etUsername;
        etPassword = binding.etPassword;
        tvCreateAccount = binding.tvCreateAccount;
        btnLogin = binding.btnLogin;
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the create profile activity if user presses sign up
                goCreateAccountActivity();
            }
        });
    }

    public void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        // Navigate to main activity if user has properly signed in
        ParseUser.logInInBackground(username, password, new LogInCallback() { // Use background login to prevent bad user experience
            @Override
            public void done(ParseUser user, ParseException e) { // If the request succeeded, e will be null
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    // TODO: Change to snackbar
                    Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Navigate to main activity if the user has signed in properly
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goCreateAccountActivity() {
        Intent i = new Intent(this, CreateAccountActivity.class);
        startActivity(i);
        finish();
    }
}