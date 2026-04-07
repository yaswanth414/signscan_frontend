package com.example.sign_scan_;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class QuizLearningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_learning);

        ImageView btnClose = findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        MaterialButton btnStartQuiz = findViewById(R.id.btnStartQuiz);
        if (btnStartQuiz != null) {
            btnStartQuiz.setOnClickListener(v -> {
                Intent intent = new Intent(QuizLearningActivity.this, QuizQuestionsActivity.class);
                startActivity(intent);
            });
        }
    }
}
