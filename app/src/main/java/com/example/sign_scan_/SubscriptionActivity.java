package com.example.sign_scan_;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class SubscriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnStartPremium = findViewById(R.id.btnStartPremium);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnStartPremium != null) {
            btnStartPremium.setOnClickListener(v -> {
                Toast.makeText(this, "Subscription process started!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
