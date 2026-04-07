package com.example.sign_scan_;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.widget.Toast;
import com.example.sign_scan_.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityLogsActivity extends AppCompatActivity {

    private LinearLayout logsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity_logs);

        logsContainer = findViewById(R.id.logsContainer);

        // Back Button Navigation
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        fetchLogs();
    }

    private void fetchLogs() {
        RetrofitClient.getAuthenticatedApiService(this).getAdminLoginActivityLogs().enqueue(new Callback<List<com.example.sign_scan_.models.LoginActivity>>() {
            @Override
            public void onResponse(Call<List<com.example.sign_scan_.models.LoginActivity>> call, Response<List<com.example.sign_scan_.models.LoginActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    renderLogs(response.body());
                } else {
                    Toast.makeText(LoginActivityLogsActivity.this, "Failed to load logs: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<com.example.sign_scan_.models.LoginActivity>> call, Throwable t) {
                Toast.makeText(LoginActivityLogsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderLogs(List<com.example.sign_scan_.models.LoginActivity> logs) {
        logsContainer.removeAllViews();
        for (com.example.sign_scan_.models.LoginActivity log : logs) {
            addLogCard(log);
        }
    }

    private void addLogCard(com.example.sign_scan_.models.LoginActivity log) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_login_log, logsContainer, false);
        
        TextView tvEmail = view.findViewById(R.id.tvUserEmail);
        TextView tvTime = view.findViewById(R.id.tvTimestamp);
        TextView tvRole = view.findViewById(R.id.tvRoleBadge);

        boolean isAdmin = "admin".equalsIgnoreCase(log.getRole());

        if (tvEmail != null) tvEmail.setText(log.getEmail());
        if (tvTime != null) {
            // "2023-12-23T10:30:00Z" -> simplistic cleanup if needed, or use as is
            String time = log.getLoginTime();
            if (time != null && time.contains("T")) {
                time = time.replace("T", " ").substring(0, 16);
            }
            tvTime.setText(time);
        }
        
        if (tvRole != null) {
            tvRole.setText(log.getRole().toUpperCase());
            tvRole.setBackgroundResource(isAdmin ? R.drawable.badge_bg_admin : R.drawable.badge_bg_user);
            tvRole.setTextColor(isAdmin ? 0xFFD32F2F : 0xFF757575);
        }

        logsContainer.addView(view);
    }
}
