package com.example.sign_scan_;

import com.example.sign_scan_.models.TrafficFine;
import com.example.sign_scan_.network.ApiService;
import com.example.sign_scan_.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UserTrafficFinesActivity extends AppCompatActivity {

    private List<TrafficFine> allFines = new ArrayList<>();
    private LinearLayout finesContainer;
    private EditText etSearchFines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_traffic_fines);

        finesContainer = findViewById(R.id.finesContainer);
        etSearchFines = findViewById(R.id.etSearchFines);

        ImageView btnClose = findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        setupSearch();
        fetchTrafficFines();
    }

    private void fetchTrafficFines() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<TrafficFine>> call = apiService.getTrafficFines();

        call.enqueue(new Callback<List<TrafficFine>>() {
            @Override
            public void onResponse(Call<List<TrafficFine>> call, Response<List<TrafficFine>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allFines = response.body();
                    renderFinesList(allFines);
                } else {
                    android.widget.Toast.makeText(UserTrafficFinesActivity.this, "Failed to load fines", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TrafficFine>> call, Throwable t) {
                android.widget.Toast.makeText(UserTrafficFinesActivity.this, "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderFinesList(List<TrafficFine> fines) {
        if (finesContainer == null) return;
        finesContainer.removeAllViews();

        for (int i = 0; i < fines.size(); i++) {
            TrafficFine fine = fines.get(i);
            
            View itemView = getLayoutInflater().inflate(R.layout.item_traffic_fine, finesContainer, false);
            
            TextView tvSerial = itemView.findViewById(R.id.tvSerialNumber);
            TextView tvName = itemView.findViewById(R.id.tvViolationName);

            if (tvSerial != null) tvSerial.setText(String.valueOf(i + 1));
            if (tvName != null) tvName.setText(fine.getViolationName());

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(UserTrafficFinesActivity.this, UserTrafficFineDetailsActivity.class);
                intent.putExtra("FINE_ID", fine.getId());
                intent.putExtra("TITLE", fine.getViolationName());
                intent.putExtra("DESC", fine.getDescription());
                intent.putExtra("AMOUNT", "₹" + fine.getFineAmount());
                intent.putExtra("PENALTY", fine.getAdditionalPenalty());
                intent.putExtra("LEGAL", fine.getLegalReference());
                startActivity(intent);
            });

            finesContainer.addView(itemView);
        }
    }

    private void setupSearch() {
        if (etSearchFines != null) {
            etSearchFines.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterFines(s.toString().toLowerCase());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void filterFines(String query) {
        List<TrafficFine> filtered = new ArrayList<>();
        for (TrafficFine fine : allFines) {
            if (fine.getViolationName().toLowerCase().contains(query)) {
                filtered.add(fine);
            }
        }
        renderFinesList(filtered);
    }
}
