package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sign_scan_.models.SignupRequest;
import com.example.sign_scan_.models.SignupResponse;
import com.example.sign_scan_.network.RetrofitClient;

public class UserSignUpActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword;
    private Button btnCreateAccount, tabLogin, tabSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tabLogin = findViewById(R.id.tabLogin);
        tabSignUp = findViewById(R.id.tabSignUp);

        tabLogin.setOnClickListener(v -> {
            Intent intent = new Intent(UserSignUpActivity.this, UserLoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnCreateAccount.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(UserSignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // API Call
            SignupRequest request = new SignupRequest(name, email, password);
            RetrofitClient.getApiService().signup(request).enqueue(new retrofit2.Callback<SignupResponse>() {
                @Override
                public void onResponse(retrofit2.Call<SignupResponse> call, retrofit2.Response<SignupResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UserSignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        // Navigate to Login page
                        Intent intent = new Intent(UserSignUpActivity.this, UserLoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                         Toast.makeText(UserSignUpActivity.this, "Signup Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<SignupResponse> call, Throwable t) {
                    Toast.makeText(UserSignUpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
