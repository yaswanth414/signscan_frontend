package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class UserDetectionDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detection_details);

        TextView tvSignName = findViewById(R.id.tvSignName);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvConfidenceValue = findViewById(R.id.tvConfidenceValue);
        TextView tvDetectedOn = findViewById(R.id.tvDetectedOn);
        ProgressBar pbConfidence = findViewById(R.id.pbConfidence);
        ImageView btnClose = findViewById(R.id.btnClose);
        MaterialButton btnDelete = findViewById(R.id.btnDelete);

        // Retrieve data from Intent
        String name = getIntent().getStringExtra("SIGN_NAME");
        String category = getIntent().getStringExtra("CATEGORY");
        String confidence = getIntent().getStringExtra("CONFIDENCE");
        String date = getIntent().getStringExtra("DATE");
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        int itemId = getIntent().getIntExtra("ITEM_ID", -1);

        if (name != null) tvSignName.setText(name);
        if (category != null) tvCategory.setText(category);
        if (date != null) tvDetectedOn.setText(date);
        
        ImageView ivDetectionImage = findViewById(R.id.ivDetectionImage);
        if (imageUrl != null && ivDetectionImage != null) {
            loadImage(imageUrl, ivDetectionImage);
        }
        
        if (confidence != null) {
            tvConfidenceValue.setText(confidence);
            try {
                String cleanConfidence = confidence.replace("%", "").trim();
                float conf = Float.parseFloat(cleanConfidence);
                pbConfidence.setProgress((int) conf);
            } catch (Exception e) {
                pbConfidence.setProgress(0);
            }
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                if (itemId == -1) {
                    Toast.makeText(this, "Invalid detection ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call Delete API
                com.example.sign_scan_.network.RetrofitClient.getAuthenticatedApiService(UserDetectionDetailsActivity.this).deleteDetection(itemId)
                        .enqueue(new retrofit2.Callback<Void>() {
                            @Override
                            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(UserDetectionDetailsActivity.this, "Detection Deleted", Toast.LENGTH_SHORT).show();
                                    // Send signal back to refresh history
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("DELETED_ITEM_ID", itemId);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                } else {
                                    try {
                                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                        Toast.makeText(UserDetectionDetailsActivity.this, "Failed: " + response.code() + " " + errorBody, Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(UserDetectionDetailsActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                                Toast.makeText(UserDetectionDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }
    }
    
    private void loadImage(String url, ImageView imageView) {
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

        executor.execute(() -> {
            android.graphics.Bitmap image = null;
            try {
                java.io.InputStream in = new java.net.URL(url).openStream();
                image = android.graphics.BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            final android.graphics.Bitmap finalImage = image;
            handler.post(() -> {
                if (finalImage != null) {
                    imageView.setImageBitmap(finalImage);
                }
            });
        });
    }
}
