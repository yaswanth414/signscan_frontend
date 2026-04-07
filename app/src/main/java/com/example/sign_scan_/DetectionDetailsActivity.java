package com.example.sign_scan_;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class DetectionDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_details);

        ImageView btnBack = findViewById(R.id.btnBackHeader);
        ImageView btnClose = findViewById(R.id.btnClose);
        TextView tvSignName = findViewById(R.id.tvSignName);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvConfidenceValue = findViewById(R.id.tvConfidenceValue);
        TextView tvDetectedOn = findViewById(R.id.tvDetectedOn);
        ProgressBar pbConfidence = findViewById(R.id.pbConfidence);
        MaterialButton btnDelete = findViewById(R.id.btnDeleteDetection);

        // Retrieve data from Intent
        String name = getIntent().getStringExtra("NAME");
        String category = getIntent().getStringExtra("CATEGORY");
        String confidence = getIntent().getStringExtra("CONFIDENCE");
        String date = getIntent().getStringExtra("DATE");

        if (name != null) tvSignName.setText(name);
        if (category != null) tvCategory.setText(category);
        if (date != null) tvDetectedOn.setText(date);
        
        if (confidence != null) {
            tvConfidenceValue.setText(confidence + "%");
            try {
                float conf = Float.parseFloat(confidence);
                pbConfidence.setProgress((int) conf);
            } catch (Exception e) {
                pbConfidence.setProgress(0);
            }
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                Toast.makeText(this, "Detection Deleted", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}
