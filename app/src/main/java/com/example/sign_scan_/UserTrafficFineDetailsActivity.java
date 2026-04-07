package com.example.sign_scan_;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.sign_scan_.network.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserTrafficFineDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_traffic_fine_details);

        TextView tvTitle = findViewById(R.id.tvViolationTitle);
        TextView tvDesc = findViewById(R.id.tvDescriptionValue);
        TextView tvAmount = findViewById(R.id.tvFineAmountValue);
        TextView tvPenalty = findViewById(R.id.tvAdditionalPenaltyValue);
        TextView tvLegal = findViewById(R.id.tvLegalReferenceValue);
        MaterialButton btnClose = findViewById(R.id.btnClose);
        MaterialButton btnDelete = findViewById(R.id.btnDelete);
        ImageView btnBack = findViewById(R.id.btnBack);

        // Get data from intent
        int fineId = getIntent().getIntExtra("FINE_ID", -1);
        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        String title = getIntent().getStringExtra("TITLE");
        String desc = getIntent().getStringExtra("DESC");
        String amount = getIntent().getStringExtra("AMOUNT");
        String penalty = getIntent().getStringExtra("PENALTY");
        String legal = getIntent().getStringExtra("LEGAL");

        if (title != null) tvTitle.setText(title);
        if (desc != null) tvDesc.setText(desc);
        if (amount != null) tvAmount.setText(amount);
        if (penalty != null) tvPenalty.setText(penalty);
        if (legal != null && tvLegal != null) tvLegal.setText(legal);

        if (isAdmin) {
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnClose.setOnClickListener(v -> finish());
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> {
            if (fineId != -1) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Fine")
                    .setMessage("Are you sure you want to delete this traffic fine? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> deleteFine(fineId))
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });
    }

    private void deleteFine(int fineId) {
        // Debug toast
        Toast.makeText(this, "Deleting Fine ID: " + fineId, Toast.LENGTH_SHORT).show();

        RetrofitClient.getApiService().adminDeleteTrafficFine(fineId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserTrafficFineDetailsActivity.this, "Fine deleted successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String error = "Delete failed: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            error += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(UserTrafficFineDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UserTrafficFineDetailsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}
