package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.example.sign_scan_.models.Detection;
import com.example.sign_scan_.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonitorDetectionsActivity extends AppCompatActivity {

    public static final int DELETE_REQUEST_CODE = 201;
    private LinearLayout detectionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_detections);

        detectionsContainer = findViewById(R.id.detectionsContainer);

        // Back Button Navigation
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        fetchDetections();
    }

    private void fetchDetections() {
        RetrofitClient.getAuthenticatedApiService(this).adminGetDetections().enqueue(new Callback<List<Detection>>() {
            @Override
            public void onResponse(Call<List<Detection>> call, Response<List<Detection>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    renderDetections(response.body());
                } else {
                    String error = "Failed: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            error += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(MonitorDetectionsActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Detection>> call, Throwable t) {
                Toast.makeText(MonitorDetectionsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderDetections(List<Detection> detections) {
        detectionsContainer.removeAllViews();
        for (Detection d : detections) {
            addDetectionCard(d);
        }
    }

    private void addDetectionCard(Detection d) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_detection_card, detectionsContainer, false);

        TextView tvName = cardView.findViewById(R.id.tvDetectionName);
        TextView tvDetectedBy = cardView.findViewById(R.id.tvDetectedBy);
        TextView tvDateTime = cardView.findViewById(R.id.tvDateTime);
        View btnView = cardView.findViewById(R.id.btnView);

        tvName.setText(d.getSignName());
        tvDetectedBy.setText("Detected by: " + (d.getUserEmail() != null ? d.getUserEmail() : "Guest User"));
        tvDateTime.setText(d.getDetectedAt());

        btnView.setOnClickListener(v -> navigateToDetails(d));

        detectionsContainer.addView(cardView);
    }

    private void navigateToDetails(Detection d) {
        Intent intent = new Intent(this, DetectionDetails_Activity.class);
        intent.putExtra("ITEM_ID", d.getId());
        intent.putExtra("NAME", d.getSignName());
        intent.putExtra("CATEGORY", d.getCategory());
        intent.putExtra("CONFIDENCE", String.valueOf(d.getConfidence()));
        intent.putExtra("DATE", d.getDetectedAt());
        intent.putExtra("IMAGE_URL", d.getImageUrl());
        intent.putExtra("IS_ADMIN", true);
        startActivityForResult(intent, DELETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DELETE_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchDetections(); // Refresh list after deletion
        }
    }
}
