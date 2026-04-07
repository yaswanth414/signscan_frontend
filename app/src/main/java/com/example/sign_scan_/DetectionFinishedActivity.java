package com.example.sign_scan_;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class DetectionFinishedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_finished);

        ImageView ivResultImage = findViewById(R.id.ivResultImage);
        TextView tvSignName = findViewById(R.id.tvResultSignName);
        TextView tvCategory = findViewById(R.id.tvResultCategory);
        TextView tvConfidence = findViewById(R.id.tvResultConfidenceValue);
        TextView tvMeaning = findViewById(R.id.tvResultMeaning);
        ProgressBar pbConfidence = findViewById(R.id.pbResultConfidence);
        ImageView btnClose = findViewById(R.id.btnClose);
        MaterialButton btnDetectAnother = findViewById(R.id.btnDetectAnother);

        // Get data from intent
        String imageUriString = getIntent().getStringExtra("image_uri");
        String signName = getIntent().getStringExtra("sign_name");
        String category = getIntent().getStringExtra("category");
        String confidence = getIntent().getStringExtra("confidence");
        String meaning = getIntent().getStringExtra("meaning");

        if (imageUriString != null) {
            ivResultImage.setImageURI(Uri.parse(imageUriString));
        }
        if (signName != null) tvSignName.setText(signName);
        if (category != null) tvCategory.setText(category);
        if (meaning != null) tvMeaning.setText(meaning);
        if (confidence != null) {
            tvConfidence.setText(confidence + " %");
            try {
                float conf = Float.parseFloat(confidence);
                pbConfidence.setProgress((int) conf);
            } catch (Exception e) {
                pbConfidence.setProgress(0);
            }
        }

        btnClose.setOnClickListener(v -> finish());
        
        btnDetectAnother.setOnClickListener(v -> {
            Intent intent = new Intent(DetectionFinishedActivity.this, ImageUploadActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
