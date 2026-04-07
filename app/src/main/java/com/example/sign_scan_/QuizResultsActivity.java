package com.example.sign_scan_;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.widget.Toast;
import com.example.sign_scan_.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizResultsActivity extends AppCompatActivity {

    private LinearLayout resultsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);

        resultsContainer = findViewById(R.id.resultsContainer);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        fetchQuizResults();
    }

    private void fetchQuizResults() {
        RetrofitClient.getAuthenticatedApiService(this).getAdminQuizResults().enqueue(new Callback<List<com.example.sign_scan_.models.AdminQuizResult>>() {
            @Override
            public void onResponse(Call<List<com.example.sign_scan_.models.AdminQuizResult>> call, Response<List<com.example.sign_scan_.models.AdminQuizResult>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    renderResults(response.body());
                } else {
                    Toast.makeText(QuizResultsActivity.this, "Failed to load results: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<com.example.sign_scan_.models.AdminQuizResult>> call, Throwable t) {
                Toast.makeText(QuizResultsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderResults(List<com.example.sign_scan_.models.AdminQuizResult> results) {
        resultsContainer.removeAllViews();
        for (com.example.sign_scan_.models.AdminQuizResult res : results) {
            addResultCard(res);
        }
    }

    private void addResultCard(com.example.sign_scan_.models.AdminQuizResult res) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_admin_quiz_result, resultsContainer, false);
        
        TextView tvEmail = view.findViewById(R.id.tvUserEmail);
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvScore = view.findViewById(R.id.tvScore);

        if (tvEmail != null) tvEmail.setText(res.getEmail());
        if (tvDate != null) tvDate.setText(res.getDate());
        if (tvScore != null) tvScore.setText(res.getScoreText());

        resultsContainer.addView(view);
    }
}
