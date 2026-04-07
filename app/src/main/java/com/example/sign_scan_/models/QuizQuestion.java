package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class QuizQuestion implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("question_text")
    private String questionText;

    @SerializedName("options")
    private List<String> options;

    @SerializedName("category")
    private String category;

    public int getId() { return id; }
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public String getCategory() { return category; }
}
