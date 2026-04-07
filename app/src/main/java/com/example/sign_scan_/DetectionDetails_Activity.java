package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import com.example.sign_scan_.network.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetectionDetails_Activity extends AppCompatActivity {

    private int detectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_details_);

        ImageView btnBack = findViewById(R.id.btnBackHeader);
        ImageView btnClose = findViewById(R.id.btnClose);
        TextView tvSignName = findViewById(R.id.tvSignName);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvConfidenceValue = findViewById(R.id.tvConfidenceValue);
        TextView tvDetectedOn = findViewById(R.id.tvDetectedOn);
        ProgressBar pbConfidence = findViewById(R.id.pbConfidence);
        MaterialButton btnDelete = findViewById(R.id.btnDeleteDetection);
        ImageView ivDetectedImage = findViewById(R.id.ivDetectedImage); 

        // Retrieve data from Intent
        detectionId = getIntent().getIntExtra("ITEM_ID", -1);
        String name = getIntent().getStringExtra("NAME");
        String category = getIntent().getStringExtra("CATEGORY");
        String confidence = getIntent().getStringExtra("CONFIDENCE");
        String date = getIntent().getStringExtra("DATE");
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

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

        if (imageUrl != null && ivDetectedImage != null) {
            loadImage(imageUrl, ivDetectedImage);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Detection")
                    .setMessage("Are you sure you want to delete this detection history?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteDetectionOnServer())
                    .setNegativeButton("Cancel", null)
                    .show();
            });
        }
    }

    private void deleteDetectionOnServer() {
        if (detectionId == -1) return;

        RetrofitClient.getAuthenticatedApiService(this).adminDeleteDetection(detectionId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetectionDetails_Activity.this, "Detection Deleted", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(DetectionDetails_Activity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DetectionDetails_Activity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
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
