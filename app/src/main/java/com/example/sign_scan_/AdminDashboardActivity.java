package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Map icons and colors exactly as shown in the reference design
        setupDashboardItem(R.id.cardFines, "Manage Fines", R.color.bg_fines, R.color.icon_fines, R.drawable.ic_dollar);
        setupDashboardItem(R.id.cardUsers, "Manage Users", R.color.bg_users, R.color.icon_users, R.drawable.ic_users_dashboard);
        setupDashboardItem(R.id.cardDetections, "Monitor Detections", R.color.bg_detections, R.color.icon_detections, R.drawable.ic_monitor);
        setupDashboardItem(R.id.cardLoginActivity, "Login Activity", R.color.bg_login, R.color.icon_login, R.drawable.ic_login_activity);
        setupDashboardItem(R.id.cardQuizResults, "Quiz Results", R.color.bg_quiz, R.color.icon_quiz, R.drawable.ic_quiz_results);

        // Attach Navigation Actions
        findViewById(R.id.cardFines).setOnClickListener(v -> {
            startActivity(new Intent(this, ManageFinesActivity.class));
        });

        findViewById(R.id.cardUsers).setOnClickListener(v -> {
            startActivity(new Intent(this, ManageUsersActivity.class));
        });

        findViewById(R.id.cardDetections).setOnClickListener(v -> {
            startActivity(new Intent(this, MonitorDetectionsActivity.class));
        });

        findViewById(R.id.cardLoginActivity).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivityLogsActivity.class));
        });

        findViewById(R.id.cardQuizResults).setOnClickListener(v -> {
            startActivity(new Intent(this, QuizResultsActivity.class));
        });

        MaterialButton btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(v -> finish());
    }

    private void setupDashboardItem(int viewId, String title, int bgColor, int iconColor, int iconRes) {
        View view = findViewById(viewId);
        if (view != null) {
            TextView tvTitle = view.findViewById(R.id.itemTitle);
            ImageView ivIcon = view.findViewById(R.id.itemIcon);
            MaterialCardView iconContainer = view.findViewById(R.id.iconContainer);

            if (tvTitle != null) tvTitle.setText(title);
            if (ivIcon != null) {
                ivIcon.setImageResource(iconRes);
                ivIcon.setColorFilter(getColor(iconColor));
            }
            if (iconContainer != null) {
                iconContainer.setCardBackgroundColor(getColor(bgColor));
            }
        }
    }
}
