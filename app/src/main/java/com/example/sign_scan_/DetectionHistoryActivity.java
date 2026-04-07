package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sign_scan_.models.Detection;
import com.example.sign_scan_.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetectionHistoryActivity extends AppCompatActivity {

    public static final int DELETE_REQUEST_CODE = 301;
    private LinearLayout historyContainer;
    private String currentCategory = "All";
    private List<Detection> allDetections = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_history);

        historyContainer = findViewById(R.id.historyContainer);

        ImageView btnClose = findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        setupFilters();
        fetchHistory();
    }

    private void fetchHistory() {
        RetrofitClient.getAuthenticatedApiService(this).getDetectionHistory().enqueue(new Callback<List<Detection>>() {
            @Override
            public void onResponse(Call<List<Detection>> call, Response<List<Detection>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allDetections = response.body();
                    renderList();
                    updateStatistics();
                } else {
                    Toast.makeText(DetectionHistoryActivity.this, "Failed to load history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Detection>> call, Throwable t) {
                Toast.makeText(DetectionHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void addItemView(Detection d) {
        View view = getLayoutInflater().inflate(R.layout.item_detection_history, historyContainer, false);
        
        TextView tvTitle = view.findViewById(R.id.tvSignTitle);
        TextView tvCat = view.findViewById(R.id.tvCategory);
        TextView tvConf = view.findViewById(R.id.tvConfidence);
        TextView tvD = view.findViewById(R.id.tvDate);
        TextView tvT = view.findViewById(R.id.tvTime);
        ImageView ivImage = view.findViewById(R.id.ivDetectionImage); // Placeholder

        if (tvTitle != null) tvTitle.setText(d.getSignName());
        if (tvCat != null) {
            tvCat.setText(d.getCategory());
            // Set style based on category
            if ("Warning".equalsIgnoreCase(d.getCategory())) {
                tvCat.setBackgroundResource(R.drawable.pill_bg_warning);
                tvCat.setTextColor(android.graphics.Color.parseColor("#FF8F00"));
            } else if ("Regulatory".equalsIgnoreCase(d.getCategory())) {
                tvCat.setBackgroundResource(R.drawable.pill_bg_red);
                tvCat.setTextColor(android.graphics.Color.parseColor("#D32F2F"));
            } else {
                tvCat.setBackgroundResource(R.drawable.pill_bg_blue);
                tvCat.setTextColor(android.graphics.Color.parseColor("#1976D2"));
            }
        }
        if (tvConf != null) tvConf.setText(d.getConfidence() + " %");
        
        // Split ISO date if possible
        String dt = d.getDetectedAt();
        String datePart = dt;
        String timePart = "";
        if (dt != null && dt.contains("T")) {
            String[] parts = dt.split("T");
            datePart = parts[0];
            if (parts.length > 1) timePart = parts[1].substring(0, 5); // HH:mm
        }
        
        if (tvD != null) tvD.setText(datePart);
        if (tvT != null) tvT.setText(timePart);

        if (ivImage != null && d.getImageUrl() != null) {
            loadImage(d.getImageUrl(), ivImage);
        }

        final String finalDatePart = datePart;
        final String finalTimePart = timePart;

        view.setOnClickListener(v -> {
            Intent intent = new Intent(DetectionHistoryActivity.this, UserDetectionDetailsActivity.class);
            intent.putExtra("SIGN_NAME", d.getSignName());
            intent.putExtra("CATEGORY", d.getCategory());
            intent.putExtra("CONFIDENCE", String.valueOf(d.getConfidence()));
            intent.putExtra("DATE", finalDatePart + " " + finalTimePart);
            intent.putExtra("ITEM_ID", d.getId());
            intent.putExtra("IMAGE_URL", d.getImageUrl());
            startActivityForResult(intent, DELETE_REQUEST_CODE);
        });

        historyContainer.addView(view);
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

    private void setupFilters() {
        setupFilterButton(R.id.filterAll, "All");
        setupFilterButton(R.id.filterWarning, "Warning");
        setupFilterButton(R.id.filterRegulatory, "Regulatory");
        setupFilterButton(R.id.filterInformational, "Informational");
    }

    private void setupFilterButton(int btnId, String category) {
        MaterialButton btn = findViewById(btnId);
        if (btn != null) {
            btn.setOnClickListener(v -> {
                currentCategory = category;
                updateFilterStyles(btnId);
                renderList(); 
            });
        }
    }

    private void updateFilterStyles(int selectedId) {
        int[] filters = {R.id.filterAll, R.id.filterWarning, R.id.filterRegulatory, R.id.filterInformational};
        for (int id : filters) {
            MaterialButton btn = findViewById(id);
            if (btn != null) {
                if (id == selectedId) {
                    btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF4511E));
                    btn.setTextColor(android.graphics.Color.WHITE);
                } else {
                    btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
                    btn.setTextColor(0xFF757575);
                }
            }
        }
    }

    private void updateStatistics() {
        int total = allDetections.size();
        int warning = 0, regulatory = 0, informational = 0;
        
        for (Detection d : allDetections) {
            String cat = d.getCategory();
            if ("Warning".equalsIgnoreCase(cat)) warning++;
            else if ("Regulatory".equalsIgnoreCase(cat)) regulatory++;
            else if ("Informational".equalsIgnoreCase(cat)) informational++;
        }

        TextView tvTotal = findViewById(R.id.tvStatTotal);
        TextView tvWarn = findViewById(R.id.tvStatWarning);
        TextView tvReg = findViewById(R.id.tvStatRegulatory);
        TextView tvInfo = findViewById(R.id.tvStatInformational);

        if (tvTotal != null) tvTotal.setText(String.valueOf(total));
        if (tvWarn != null) tvWarn.setText(String.valueOf(warning));
        if (tvReg != null) tvReg.setText(String.valueOf(regulatory));
        if (tvInfo != null) tvInfo.setText(String.valueOf(informational));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DELETE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("DELETED_ITEM_ID")) {
                int deletedId = data.getIntExtra("DELETED_ITEM_ID", -1);
                if (deletedId != -1) {
                    // Optimistic update: Remove from local list immediately
                    for (int i = 0; i < allDetections.size(); i++) {
                        if (allDetections.get(i).getId() == deletedId) {
                            allDetections.remove(i);
                            break;
                        }
                    }
                    // Refresh UI immediately
                    renderList();
                    updateStatistics();
                }
            }
            // Still fetch from server to ensure sync
            fetchHistory();
        }
    }

    private void renderList() {
        // Clear all previous items
        historyContainer.removeAllViews();
        
        // Add matching items
        for (Detection d : allDetections) {
            boolean matches = currentCategory.equals("All") || 
                              (d.getCategory() != null && d.getCategory().equalsIgnoreCase(currentCategory));
            
            if (matches) {
                addItemView(d);
            }
        }
    }
}
