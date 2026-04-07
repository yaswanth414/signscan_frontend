package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;
import com.example.sign_scan_.models.TrafficFine;
import com.example.sign_scan_.network.ApiService;
import com.example.sign_scan_.network.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFineActivity extends AppCompatActivity {

    private EditText etViolationName, etDescription, etFineAmount, etLegalReference, etPenalty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fine);

        etViolationName = findViewById(R.id.etViolationName);
        etDescription = findViewById(R.id.etDescription);
        etFineAmount = findViewById(R.id.etFineAmount);
        etLegalReference = findViewById(R.id.etLegalReference);
        etPenalty = findViewById(R.id.etPenalty);

        ImageView btnClose = findViewById(R.id.btnClose);
        if (btnClose != null) btnClose.setOnClickListener(v -> finish());

        View btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) btnCancel.setOnClickListener(v -> finish());

        Button btnAddFine = findViewById(R.id.btnAddFine);
        if (btnAddFine != null) {
            btnAddFine.setOnClickListener(v -> saveFineToApi());
        }
    }

    private void saveFineToApi() {
        String name = etViolationName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String amountStr = etFineAmount.getText().toString().trim();
        String reference = etLegalReference.getText().toString().trim();
        String penalty = etPenalty.getText().toString().trim();

        if (name.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Name and Amount are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        TrafficFine newFine = new TrafficFine(name, desc, amount, reference, penalty);

        RetrofitClient.getApiService().adminAddTrafficFine(newFine).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddFineActivity.this, "Fine added successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddFineActivity.this, "Failed to add fine", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AddFineActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
