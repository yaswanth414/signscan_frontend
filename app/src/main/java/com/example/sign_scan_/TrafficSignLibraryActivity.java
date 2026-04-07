package com.example.sign_scan_;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sign_scan_.models.TrafficSign;
import com.example.sign_scan_.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrafficSignLibraryActivity extends AppCompatActivity {

    private LinearLayout signsContainer;
    private EditText etSearchLibrary;
    private String currentCategory = "All";
    private List<TrafficSign> allSigns = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_sign_library);

        signsContainer = findViewById(R.id.signsContainer);
        etSearchLibrary = findViewById(R.id.etSearchLibrary);

        ImageView btnClose = findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        setupFilters();
        
        // Setup Search
        if (etSearchLibrary != null) {
            etSearchLibrary.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    renderList(s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        fetchSigns();
    }

    private void fetchSigns() {
        RetrofitClient.getAuthenticatedApiService(this).getTrafficSigns(null).enqueue(new Callback<List<TrafficSign>>() {
            @Override
            public void onResponse(Call<List<TrafficSign>> call, Response<List<TrafficSign>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allSigns = response.body();
                    renderList(etSearchLibrary.getText().toString().trim());
                } else {
                    Toast.makeText(TrafficSignLibraryActivity.this, "Failed to load signs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TrafficSign>> call, Throwable t) {
                Toast.makeText(TrafficSignLibraryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderList(String query) {
        signsContainer.removeAllViews();
        
        // Add footer info if needed (hardcoded in original XML as a child?)
        // Assuming we just want to show the list for now.

        query = query.toLowerCase();

        for (TrafficSign sign : allSigns) {
            boolean matchesCategory = currentCategory.equals("All") || 
                                      (sign.getCategory() != null && sign.getCategory().equalsIgnoreCase(currentCategory));
            boolean matchesQuery = sign.getTitle() != null && sign.getTitle().toLowerCase().contains(query);

            if (matchesCategory && matchesQuery) {
                addSignView(sign);
            }
        }
    }

    private void addSignView(TrafficSign sign) {
        // Inflate item_library_sign or similar. If it doesn't exist, we fallback or try to find it.
        // Assuming item_library_sign.xml is the card layout.
        View view = getLayoutInflater().inflate(R.layout.item_library_sign, signsContainer, false);
        
        TextView tvTitle = view.findViewById(R.id.tvSignTitle);
        TextView tvCat = view.findViewById(R.id.tvSignCategory);
        ImageView ivImage = view.findViewById(R.id.ivSignImage);

        if (tvTitle != null) tvTitle.setText(sign.getTitle());
        
        if (tvCat != null) {
            String cat = sign.getCategory();
            tvCat.setText(cat);
            
            // Set colors based on category
            if (cat != null) {
                int bgColor;
                int textColor;
                
                switch (cat.toLowerCase()) {
                    case "warning":
                        bgColor = 0xFFFFF9C4; // Light Yellow
                        textColor = 0xFFFBC02D; // Darker Yellow
                        break;
                    case "informational":
                        bgColor = 0xFFE3F2FD; // Light Blue
                        textColor = 0xFF1976D2; // Darker Blue
                        break;
                    case "regulatory":
                    default:
                        bgColor = 0xFFFFEBEE; // Light Red
                        textColor = 0xFFD32F2F; // Red
                        break;
                }
                tvCat.setBackgroundTintList(android.content.res.ColorStateList.valueOf(bgColor));
                tvCat.setTextColor(textColor);
            }
        }
        
        if (ivImage != null && sign.getImageUrl() != null) {
            loadImage(sign.getImageUrl(), ivImage);
        }

        view.setOnClickListener(v -> navigateToDetails(sign));
        signsContainer.addView(view);
    }

    private void navigateToDetails(TrafficSign sign) {
        Intent intent = new Intent(this, SignDetailsActivity.class);
        intent.putExtra("SIGN_NAME", sign.getTitle());
        intent.putExtra("SIGN_CATEGORY", sign.getCategory());
        intent.putExtra("SIGN_SHAPE", sign.getShape());
        intent.putExtra("SIGN_COLOR", sign.getColor());
        intent.putExtra("SIGN_MEANING", sign.getMeaning());
        intent.putExtra("SIGN_IMAGE_URL", sign.getImageUrl());
        startActivity(intent);
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
                updateButtonStyles(btnId);
                renderList(etSearchLibrary.getText().toString().trim());
            });
        }
    }

    private void updateButtonStyles(int selectedBtnId) {
        int[] btnIds = {R.id.filterAll, R.id.filterWarning, R.id.filterRegulatory, R.id.filterInformational};
        for (int id : btnIds) {
            MaterialButton btn = findViewById(id);
            if (btn != null) {
                if (id == selectedBtnId) {
                    btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
                    btn.setTextColor(android.graphics.Color.WHITE);
                } else {
                    btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF5F5F5));
                    btn.setTextColor(0xFF757575);
                }
            }
        }
    }

    private void loadImage(String url, ImageView imageView) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Bitmap image = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            final Bitmap finalImage = image;
            handler.post(() -> {
                if (finalImage != null) {
                    imageView.setImageBitmap(finalImage);
                }
            });
        });
    }
}
