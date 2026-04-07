package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class UserDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Display User Name
        SessionManager sessionManager = new SessionManager(this);
        TextView tvWelcomeName = findViewById(R.id.tvWelcomeName);
        if (tvWelcomeName != null) {
            String fullName = sessionManager.getFullName();
            tvWelcomeName.setText("Welcome back, " + fullName + " ! 👋");
        }

        // Setup Dashboard Items
        setupDashboardItem(R.id.cardImageUpload, "Image Upload", "Detect signs from gallery images", "#A855F7", R.drawable.ic_upload_dashboard, ImageUploadActivity.class);
        setupDashboardItem(R.id.cardSignLibrary, "Sign Library", "Browse all Indian traffic signs", "#10B981", R.drawable.ic_library_dashboard, TrafficSignLibraryActivity.class);
        setupDashboardItem(R.id.cardDetectionHistory, "Detection History", "View your past detections", "#F97316", R.drawable.ic_history_dashboard, DetectionHistoryActivity.class);
        setupDashboardItem(R.id.cardTrafficFine, "Indian Traffic Fine", "View traffic violation fines and penalties", "#EF4444", R.drawable.ic_fine_dashboard, UserTrafficFinesActivity.class);
        setupDashboardItem(R.id.cardQuizLearning, "Quiz & Learning", "Test your traffic sign knowledge", "#EC4899", R.drawable.ic_quiz_dashboard, QuizLearningActivity.class);

        // Subscribe Button - Navigate to Subscription Page
        MaterialButton btnSubscribe = findViewById(R.id.btnSubscribe);
        if (btnSubscribe != null) {
            btnSubscribe.setOnClickListener(v -> {
                Intent intent = new Intent(UserDashboardActivity.this, SubscriptionActivity.class);
                startActivity(intent);
            });
        }

        MaterialButton btnSignOut = findViewById(R.id.btnSignOut);
        if (btnSignOut != null) {
            btnSignOut.setOnClickListener(v -> {
                sessionManager.logout();
                Intent intent = new Intent(UserDashboardActivity.this, UserLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupDashboardItem(int viewId, String title, String subtitle, String colorHex, int iconRes, final Class<?> targetActivity) {
        View view = findViewById(viewId);
        if (view != null) {
            TextView tvTitle = view.findViewById(R.id.itemTitle);
            TextView tvSubtitle = view.findViewById(R.id.itemSubtitle);
            ImageView ivIcon = view.findViewById(R.id.itemIcon);
            MaterialCardView iconContainer = view.findViewById(R.id.iconContainer);

            tvTitle.setText(title);
            tvSubtitle.setText(subtitle);
            ivIcon.setImageResource(iconRes);
            ivIcon.setColorFilter(android.graphics.Color.WHITE);
            iconContainer.setCardBackgroundColor(android.graphics.Color.parseColor(colorHex));
            
            view.setOnClickListener(v -> {
                if (targetActivity != null) {
                    Intent intent = new Intent(UserDashboardActivity.this, targetActivity);
                    startActivity(intent);
                }
            });
        }
    }
}
