package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import com.example.sign_scan_.models.QuizAttemptResponse;
import com.example.sign_scan_.models.QuizQuestion;
import com.example.sign_scan_.network.ApiService;
import com.example.sign_scan_.network.RetrofitClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizQuestionsActivity extends AppCompatActivity {

    private TextView tvQuestionCount, tvProgressPercent, tvQuestionText;
    private ProgressBar quizProgressBar;
    private RadioButton rb1, rb2, rb3, rb4;
    private MaterialCardView card1, card2, card3, card4;
    private MaterialButton btnAction;

    private int currentQuestionIndex = 0;
    private int selectedOptionIndex = -1;
    private boolean isLocked = false;
    private int attemptId = -1;
    private List<QuizQuestion> quizQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_questions);

        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        quizProgressBar = findViewById(R.id.quizProgressBar);
        rb1 = findViewById(R.id.rbOption1);
        rb2 = findViewById(R.id.rbOption2);
        rb3 = findViewById(R.id.rbOption3);
        rb4 = findViewById(R.id.rbOption4);
        card1 = findViewById(R.id.cardOption1);
        card2 = findViewById(R.id.cardOption2);
        card3 = findViewById(R.id.cardOption3);
        card4 = findViewById(R.id.cardOption4);
        btnAction = findViewById(R.id.btnNextQuestion);

        findViewById(R.id.btnClose).setOnClickListener(v -> finish());

        setupManualSelection();
        startNewQuiz();

        btnAction.setOnClickListener(v -> handleActionButtonClick());
    }

    private void startNewQuiz() {
        ApiService apiService = RetrofitClient.getAuthenticatedApiService(this);
        apiService.startQuiz().enqueue(new Callback<QuizAttemptResponse>() {
            @Override
            public void onResponse(Call<QuizAttemptResponse> call, Response<QuizAttemptResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    attemptId = response.body().getAttemptId();
                    quizQuestions = response.body().getQuestions();
                    loadQuestion();
                } else {
                    Toast.makeText(QuizQuestionsActivity.this, "Failed to start quiz", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<QuizAttemptResponse> call, Throwable t) {
                Toast.makeText(QuizQuestionsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupManualSelection() {
        card1.setOnClickListener(v -> selectOption(1));
        rb1.setOnClickListener(v -> selectOption(1));
        card2.setOnClickListener(v -> selectOption(2));
        rb2.setOnClickListener(v -> selectOption(2));
        card3.setOnClickListener(v -> selectOption(3));
        rb3.setOnClickListener(v -> selectOption(3));
        card4.setOnClickListener(v -> selectOption(4));
        rb4.setOnClickListener(v -> selectOption(4));
    }

    private void selectOption(int index) {
        if (isLocked) return;
        selectedOptionIndex = index;
        
        rb1.setChecked(index == 1);
        rb2.setChecked(index == 2);
        rb3.setChecked(index == 3);
        rb4.setChecked(index == 4);
        
        updateHighlighting(index);
    }

    private void updateHighlighting(int index) {
        resetCardStyle(card1, rb1);
        resetCardStyle(card2, rb2);
        resetCardStyle(card3, rb3);
        resetCardStyle(card4, rb4);

        if (index == 1) setCardSelected(card1, rb1);
        else if (index == 2) setCardSelected(card2, rb2);
        else if (index == 3) setCardSelected(card3, rb3);
        else if (index == 4) setCardSelected(card4, rb4);
    }

    private void resetCardStyle(MaterialCardView card, RadioButton rb) {
        card.setCardBackgroundColor(android.graphics.Color.WHITE);
        card.setStrokeColor(0xFFEEEEEE);
        rb.setTextColor(0xFF5F6368);
    }

    private void setCardSelected(MaterialCardView card, RadioButton rb) {
        card.setCardBackgroundColor(0xFFF3E5F5);
        card.setStrokeColor(0xFF9C27B0);
        rb.setTextColor(0xFF1A1C1E);
    }

    private void handleActionButtonClick() {
        if (!isLocked) {
            if (selectedOptionIndex == -1) {
                Toast.makeText(this, "Please select an answer first", Toast.LENGTH_SHORT).show();
                return;
            }

            submitAnswerToServer();
        } else {
            if (currentQuestionIndex == quizQuestions.size() - 1) {
                finishQuizOnServer();
            } else {
                currentQuestionIndex++;
                isLocked = false;
                selectedOptionIndex = -1;
                loadQuestion();
            }
        }
    }

    private void submitAnswerToServer() {
        isLocked = true;
        lockOptions(true);

        Map<String, Object> body = new HashMap<>();
        body.put("attempt_id", attemptId);
        body.put("question_id", quizQuestions.get(currentQuestionIndex).getId());
        body.put("selected_option", selectedOptionIndex);

        RetrofitClient.getAuthenticatedApiService(this).submitAnswer(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (currentQuestionIndex == quizQuestions.size() - 1) {
                    btnAction.setText("Finish Test");
                } else {
                    btnAction.setText("Next Question");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(QuizQuestionsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                isLocked = false;
                lockOptions(false);
            }
        });
    }

    private void finishQuizOnServer() {
        Map<String, Object> body = new HashMap<>();
        body.put("attempt_id", attemptId);

        RetrofitClient.getAuthenticatedApiService(this).finishQuiz(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    org.json.JSONObject obj = new org.json.JSONObject(json);
                    int score = obj.getInt("score");
                    int total = obj.getInt("total_questions");

                    Intent intent = new Intent(QuizQuestionsActivity.this, UserQuizResultActivity.class);
                    intent.putExtra("SCORE", score);
                    intent.putExtra("TOTAL", total);
                    intent.putExtra("ATTEMPT_ID", attemptId);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(QuizQuestionsActivity.this, "Error parsing results", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(QuizQuestionsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestion() {
        if (quizQuestions.isEmpty()) return;

        lockOptions(false);
        btnAction.setText("Submit Answer");

        QuizQuestion q = quizQuestions.get(currentQuestionIndex);
        tvQuestionCount.setText("Question " + (currentQuestionIndex + 1) + " of " + quizQuestions.size());
        tvQuestionText.setText(q.getQuestionText());
        
        List<String> options = q.getOptions();
        if (options != null && options.size() >= 4) {
            rb1.setText(options.get(0));
            rb2.setText(options.get(1));
            rb3.setText(options.get(2));
            rb4.setText(options.get(3));
        }

        rb1.setChecked(false);
        rb2.setChecked(false);
        rb3.setChecked(false);
        rb4.setChecked(false);
        updateHighlighting(-1);

        int progress = (int) (((float) (currentQuestionIndex + 1) / quizQuestions.size()) * 100);
        quizProgressBar.setProgress(progress);
        tvProgressPercent.setText(progress + "%");
    }

    private void lockOptions(boolean lock) {
        card1.setClickable(!lock);
        card2.setClickable(!lock);
        card3.setClickable(!lock);
        card4.setClickable(!lock);
        rb1.setClickable(!lock);
        rb2.setClickable(!lock);
        rb3.setClickable(!lock);
        rb4.setClickable(!lock);
    }
}
