package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class QuizAttemptResponse {
    @SerializedName("attempt_id")
    private int attemptId;

    @SerializedName("questions")
    private List<QuizQuestion> questions;

    public int getAttemptId() { return attemptId; }
    public List<QuizQuestion> getQuestions() { return questions; }
}
