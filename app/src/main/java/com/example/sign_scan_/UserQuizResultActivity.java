package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import com.example.sign_scan_.models.QuizReviewItem;
import com.example.sign_scan_.network.ApiService;
import com.example.sign_scan_.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;

public class UserQuizResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_quiz_result);

        TextView tvScoreText = findViewById(R.id.tvScoreText);
        TextView tvScorePercent = findViewById(R.id.tvScorePercent);
        TextView tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers);
        TextView tvIncorrectAnswers = findViewById(R.id.tvIncorrectAnswers);
        TextView tvAccuracy = findViewById(R.id.tvAccuracy);
        LinearLayout reviewContainer = findViewById(R.id.reviewContainer);
        MaterialButton btnRetakeQuiz = findViewById(R.id.btnRetakeQuiz);
        ImageView btnClose = findViewById(R.id.btnClose);

        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 12);
        int attemptId = getIntent().getIntExtra("ATTEMPT_ID", -1);
        
        int incorrect = total - score;
        int percent = (total > 0) ? (int) (((float) score / total) * 100) : 0;

        tvScoreText.setText("You scored " + score + " out of " + total);
        tvScorePercent.setText(percent + " %");
        tvCorrectAnswers.setText(String.valueOf(score));
        tvIncorrectAnswers.setText(String.valueOf(incorrect));
        tvAccuracy.setText(percent + " %");

        if (attemptId != -1) {
            fetchAndSetupReview(reviewContainer, attemptId);
        }

        if (btnClose != null) btnClose.setOnClickListener(v -> finish());
        
        if (btnRetakeQuiz != null) {
            btnRetakeQuiz.setOnClickListener(v -> {
                Intent intent = new Intent(this, QuizQuestionsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    private void fetchAndSetupReview(LinearLayout container, int attemptId) {
        RetrofitClient.getApiService().getQuizReview(attemptId).enqueue(new Callback<List<QuizReviewItem>>() {
            @Override
            public void onResponse(Call<List<QuizReviewItem>> call, Response<List<QuizReviewItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupReviewList(container, response.body());
                } else {
                    Toast.makeText(UserQuizResultActivity.this, "Failed to load review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuizReviewItem>> call, Throwable t) {
                Toast.makeText(UserQuizResultActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupReviewList(LinearLayout container, List<QuizReviewItem> reviewItems) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < reviewItems.size(); i++) {
            QuizReviewItem item = reviewItems.get(i);

            // Only show wrong answers or show all? 
            // The user said: "review answers laa enakku endha answer laa thappoo andha question kaana naan kudutha answer uh correct answer uh venum da"
            // Translation: "In the review answers, I want the questions where I am wrong, my given answer, and the correct answer."
            
            if (!item.isCorrect()) {
                View reviewItem = inflater.inflate(R.layout.review_answer_item, container, false);
                
                TextView tvQuestion = reviewItem.findViewById(R.id.tvReviewQuestion);
                TextView tvUserAns = reviewItem.findViewById(R.id.tvUserAnswer);
                TextView tvCorrectAns = reviewItem.findViewById(R.id.tvCorrectAnswer);
                TextView tvExplain = reviewItem.findViewById(R.id.tvExplanation);

                tvQuestion.setText("Q: " + item.getQuestion());
                tvUserAns.setText("Your answer:  " + item.getYourAnswer());
                tvCorrectAns.setText("Correct answer:  " + item.getCorrectAnswer());
                tvExplain.setText(item.getExplanation());

                container.addView(reviewItem);
            }
        }
    }
}
