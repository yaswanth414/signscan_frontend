package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;
import com.example.sign_scan_.models.User;
import com.example.sign_scan_.network.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etRole, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etRole = findViewById(R.id.etRole);
        etPassword = findViewById(R.id.etPassword);

        ImageView btnClose = findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        Button btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }

        Button btnAddUser = findViewById(R.id.btnAddUser);
        if (btnAddUser != null) {
            btnAddUser.setOnClickListener(v -> saveUserToApi());
        }
    }

    private void saveUserToApi() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String role = etRole.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(name, email, role, password);

        RetrofitClient.getApiService().adminAddUser(newUser).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddUserActivity.this, "User added successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddUserActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AddUserActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
