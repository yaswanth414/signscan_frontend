package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;

public class TrafficFine {

    @SerializedName("id")
    private int id;

    @SerializedName("violation_name")
    private String violationName;

    @SerializedName("description")
    private String description;

    @SerializedName("fine_amount")
    private int fineAmount;

    @SerializedName("legal_reference")
    private String legalReference;

    @SerializedName("additional_penalty")
    private String additionalPenalty;

    public TrafficFine(String violationName, String description, int fineAmount, String legalReference, String additionalPenalty) {
        this.violationName = violationName;
        this.description = description;
        this.fineAmount = fineAmount;
        this.legalReference = legalReference;
        this.additionalPenalty = additionalPenalty;
    }

    public int getId() { return id; }
    public String getViolationName() { return violationName; }
    public String getDescription() { return description; }
    public int getFineAmount() { return fineAmount; }
    public String getLegalReference() { return legalReference; }
    public String getAdditionalPenalty() { return additionalPenalty; }

    public void setId(int id) { this.id = id; }
    public void setViolationName(String violationName) { this.violationName = violationName; }
    public void setDescription(String description) { this.description = description; }
    public void setFineAmount(int fineAmount) { this.fineAmount = fineAmount; }
    public void setLegalReference(String legalReference) { this.legalReference = legalReference; }
    public void setAdditionalPenalty(String additionalPenalty) { this.additionalPenalty = additionalPenalty; }
}
