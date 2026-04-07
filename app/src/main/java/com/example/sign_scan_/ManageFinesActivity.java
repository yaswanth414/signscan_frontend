package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.widget.Toast;
import com.example.sign_scan_.models.TrafficFine;
import com.example.sign_scan_.network.ApiService;
import com.example.sign_scan_.network.RetrofitClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageFinesActivity extends AppCompatActivity {

    private static final int ADD_FINE_REQUEST_CODE = 101;
    private static final int DELETE_FINE_REQUEST_CODE = 102;
    private LinearLayout finesContainer;
    private EditText etSearchFines;
    private List<TrafficFine> allFines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_fines);

        finesContainer = findViewById(R.id.finesContainer);
        etSearchFines = findViewById(R.id.etSearchFines);
        ImageView btnSearch = findViewById(R.id.btnSearch);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        fetchFines();

        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> priorityFilter(etSearchFines.getText().toString().trim()));
        }

        if (etSearchFines != null) {
            etSearchFines.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    priorityFilter(s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(ManageFinesActivity.this, AddFineActivity.class);
                startActivityForResult(intent, ADD_FINE_REQUEST_CODE);
            });
        }
    }

    private void fetchFines() {
        RetrofitClient.getApiService().getAdminTrafficFines().enqueue(new Callback<List<TrafficFine>>() {
            @Override
            public void onResponse(Call<List<TrafficFine>> call, Response<List<TrafficFine>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allFines = response.body();
                    renderList(allFines);
                } else {
                    Toast.makeText(ManageFinesActivity.this, "Failed to load fines", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TrafficFine>> call, Throwable t) {
                Toast.makeText(ManageFinesActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderList(List<TrafficFine> fines) {
        finesContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < fines.size(); i++) {
            TrafficFine fine = fines.get(i);
            View cardView = inflater.inflate(R.layout.item_fine_card, finesContainer, false);

            TextView tvTitle = cardView.findViewById(R.id.tvTitle);
            TextView tvDesc = cardView.findViewById(R.id.tvDesc);
            TextView tvAmount = cardView.findViewById(R.id.tvAmount);
            TextView tvReference = cardView.findViewById(R.id.tvReference);

            tvTitle.setText((i + 1) + ". " + fine.getViolationName());
            tvDesc.setText(fine.getDescription());
            tvAmount.setText("Fine: ₹" + fine.getFineAmount());
            tvReference.setText("Legal Reference: " + fine.getLegalReference());

            cardView.setOnClickListener(v -> openViewFine(fine));

            finesContainer.addView(cardView);
        }
    }

    private void openViewFine(TrafficFine fine) {
        Intent intent = new Intent(this, UserTrafficFineDetailsActivity.class);
        intent.putExtra("FINE_ID", fine.getId());
        intent.putExtra("IS_ADMIN", true);
        intent.putExtra("TITLE", fine.getViolationName());
        intent.putExtra("DESC", fine.getDescription());
        intent.putExtra("AMOUNT", "₹" + fine.getFineAmount());
        intent.putExtra("PENALTY", fine.getAdditionalPenalty());
        intent.putExtra("LEGAL", fine.getLegalReference());
        startActivityForResult(intent, DELETE_FINE_REQUEST_CODE);
    }

    private void priorityFilter(String query) {
        if (query.isEmpty()) {
            renderList(allFines);
            return;
        }

        String lowerQuery = query.toLowerCase();
        List<TrafficFine> matches = new ArrayList<>();
        List<TrafficFine> others = new ArrayList<>();

        for (TrafficFine f : allFines) {
            if (f.getViolationName().toLowerCase().contains(lowerQuery)) {
                matches.add(f);
            } else {
                others.add(f);
            }
        }

        List<TrafficFine> reordered = new ArrayList<>(matches);
        reordered.addAll(others); 
        renderList(reordered); 
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ADD_FINE_REQUEST_CODE || requestCode == DELETE_FINE_REQUEST_CODE) && resultCode == RESULT_OK) {
            fetchFines(); // Refresh list from server
        }
    }
}
