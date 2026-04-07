package com.example.sign_scan_;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_details);

        TextView tvSignName = findViewById(R.id.tvSignName);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvShape = findViewById(R.id.tvShape);
        TextView tvColor = findViewById(R.id.tvColor);
        TextView tvMeaning = findViewById(R.id.tvMeaning);
        ImageView ivSignImage = findViewById(R.id.ivSignImage);
        ImageView btnClose = findViewById(R.id.btnClose);

        // Get data from Intent
        String name = getIntent().getStringExtra("SIGN_NAME");
        String category = getIntent().getStringExtra("SIGN_CATEGORY");
        String shape = getIntent().getStringExtra("SIGN_SHAPE");
        String color = getIntent().getStringExtra("SIGN_COLOR");
        String meaning = getIntent().getStringExtra("SIGN_MEANING");
        String imageUrl = getIntent().getStringExtra("SIGN_IMAGE_URL");

        if (name != null) tvSignName.setText(name);
        if (category != null) tvCategory.setText(category);
        if (shape != null) tvShape.setText(shape);
        if (color != null) tvColor.setText(color);
        if (meaning != null) tvMeaning.setText(meaning);
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadImage(imageUrl, ivSignImage);
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
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
