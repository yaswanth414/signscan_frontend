package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;

public class QuizReviewItem {
    @SerializedName("question")
    private String question;

    @SerializedName("your_answer")
    private String yourAnswer;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("is_correct")
    private boolean isCorrect;

    @SerializedName("explanation")
    private String explanation;

    public String getQuestion() { return question; }
    public String getYourAnswer() { return yourAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public boolean isCorrect() { return isCorrect; }
    public String getExplanation() { return explanation; }
}
