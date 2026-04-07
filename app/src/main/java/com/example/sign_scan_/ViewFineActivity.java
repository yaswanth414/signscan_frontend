package com.example.sign_scan_;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewFineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fine);

        TextView tvTitle = findViewById(R.id.tvViolationTitle);
        TextView tvDesc = findViewById(R.id.tvDescription);
        TextView tvAmount = findViewById(R.id.tvFineAmount);
        TextView tvPenalty = findViewById(R.id.tvPenalty);
        Button btnClose = findViewById(R.id.btnClose);

        // Get data from intent
        String title = getIntent().getStringExtra("TITLE");
        String desc = getIntent().getStringExtra("DESC");
        String amount = getIntent().getStringExtra("AMOUNT");
        String penalty = getIntent().getStringExtra("PENALTY");

        if (title != null) tvTitle.setText(title);
        if (desc != null) tvDesc.setText(desc);
        if (amount != null) tvAmount.setText("₹" + amount);
        if (penalty != null) tvPenalty.setText(penalty);

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }
    }
}
