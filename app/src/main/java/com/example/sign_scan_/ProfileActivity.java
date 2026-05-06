package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SessionManager sessionManager = new SessionManager(this);

        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvRole = findViewById(R.id.tvRole);
        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        // Set User Data
        tvUserName.setText(sessionManager.getFullName());
        tvEmail.setText(sessionManager.getEmail());
        tvRole.setText(sessionManager.getUserRole());

        // Back Button
        btnBack.setOnClickListener(v -> finish());

        // Logout Button
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(ProfileActivity.this, UserLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
