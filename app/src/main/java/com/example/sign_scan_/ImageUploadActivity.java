package com.example.sign_scan_;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class ImageUploadActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        ImageView btnClose = findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        // Initialize the gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Navigate to ImageDetectionActivity with the selected image URI
                        Intent intent = new Intent(ImageUploadActivity.this, ImageDetectionActivity.class);
                        intent.putExtra("image_uri", uri.toString());
                        startActivity(intent);
                    }
                }
        );

        MaterialButton btnChooseImage = findViewById(R.id.btnChooseImage);
        if (btnChooseImage != null) {
            btnChooseImage.setOnClickListener(v -> {
                // Launch gallery to choose an image
                galleryLauncher.launch("image/*");
            });
        }
    }
}
