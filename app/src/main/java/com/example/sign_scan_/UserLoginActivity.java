package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sign_scan_.models.LoginRequest;
import com.example.sign_scan_.models.LoginResponse;
import com.example.sign_scan_.network.RetrofitClient;

public class UserLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, tabLogin, tabSignUp;
    private TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tabLogin = findViewById(R.id.tabLogin);
        tabSignUp = findViewById(R.id.tabSignUp);
        android.widget.LinearLayout btnBackToRole = findViewById(R.id.btnBackToRole);

        if (btnBackToRole != null) {
            btnBackToRole.setOnClickListener(v -> {
                Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(UserLoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                return;
            }

            // API Login
            LoginRequest request = new LoginRequest(email, password);
            RetrofitClient.getApiService().login(request).enqueue(new retrofit2.Callback<LoginResponse>() {
                @Override
                public void onResponse(retrofit2.Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Save Session
                        SessionManager sessionManager = new SessionManager(UserLoginActivity.this);
                        sessionManager.saveSession(response.body());

                        Toast.makeText(UserLoginActivity.this, "Welcome " + response.body().getUser().getFullName(), Toast.LENGTH_SHORT).show();

                        // Navigate to User Dashboard
                        Intent intent = new Intent(UserLoginActivity.this, UserDashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UserLoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<LoginResponse> call, Throwable t) {
                    android.util.Log.e("UserLogin", "Login failed", t);
                    Toast.makeText(UserLoginActivity.this, "Login Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tabSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(UserLoginActivity.this, UserSignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
