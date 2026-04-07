package com.example.sign_scan_;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sign_scan_.models.Detection;
import com.example.sign_scan_.models.DetectionResponse;
import com.example.sign_scan_.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageDetectionActivity extends AppCompatActivity {

    private String imageUriString;
    private ProgressBar progressBar;
    private MaterialButton btnDetectSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detection);

        ImageView ivSelectedImage = findViewById(R.id.ivSelectedImage);
        ImageView btnClose = findViewById(R.id.btnClose);
        MaterialButton btnReset = findViewById(R.id.btnReset);
        btnDetectSign = findViewById(R.id.btnDetectSign);
        
        // Add a progress bar to your layout XML if it doesn't exist, 
        // or just use a loading dialog. For now we assume one might not be there 
        // so we'll just disable the button.

        imageUriString = getIntent().getStringExtra("image_uri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            if (ivSelectedImage != null) {
                ivSelectedImage.setImageURI(imageUri);
            }
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        if (btnReset != null) {
            btnReset.setOnClickListener(v -> finish());
        }

        if (btnDetectSign != null) {
            btnDetectSign.setOnClickListener(v -> uploadImage());
        }
    }

    private void uploadImage() {
        if (imageUriString == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse(imageUriString);
        File file = null;
        try {
            file = getFileFromUri(uri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (file == null) return;

        // Show loading state
        btnDetectSign.setEnabled(false);
        btnDetectSign.setText("Detecting...");

        // Prepare RequestBody
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // API Call
        RetrofitClient.getAuthenticatedApiService(this).detectSign(body).enqueue(new Callback<DetectionResponse>() {
            @Override
            public void onResponse(Call<DetectionResponse> call, Response<DetectionResponse> response) {
                btnDetectSign.setEnabled(true);
                btnDetectSign.setText("Detect Sign");

                if (response.isSuccessful() && response.body() != null) {
                    DetectionResponse res = response.body();
                    if (res.isSuccess() || res.getSignName() != null) {
                        // Create a temporary Detection object or pass fields directly
                        Detection detection = new Detection(); 
                        // Note: Detection.java uses reflection/Gson, so existing fields are private.
                        // We will add a constructor or simple logic to pass data.
                        
                        Intent intent = new Intent(ImageDetectionActivity.this, DetectionFinishedActivity.class);
                        intent.putExtra("image_uri", imageUriString);
                        intent.putExtra("sign_name", res.getSignName());
                        intent.putExtra("category", res.getCategory());
                        intent.putExtra("confidence", String.valueOf(res.getConfidence()));
                        intent.putExtra("meaning", res.getMeaning());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ImageDetectionActivity.this, "Detection returned no results", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Detection Failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            org.json.JSONObject obj = new org.json.JSONObject(errorJson);
                            errorMessage = obj.optString("error", "Detection Failed");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ImageDetectionActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DetectionResponse> call, Throwable t) {
                btnDetectSign.setEnabled(true);
                btnDetectSign.setText("Detect Sign");
                Log.e("Detection", "Error", t);
                Toast.makeText(ImageDetectionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private File getFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        String fileName = getFileName(uri);
        File file = new File(getCacheDir(), fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        
        byte[] buffer = new byte[1024]; // 1KB buffer
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        
        outputStream.close();
        inputStream.close();
        return file;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if(index != -1) {
                         result = cursor.getString(index);
                    }
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
