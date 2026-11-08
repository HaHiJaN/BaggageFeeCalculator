package com.example.baggagefeecalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etBaggageWeight;
    private RadioGroup rgTicketClass;
    private EditText etBaggageRate;
    private Button btnCalculate;
    private Button btnReset;
    private TextView tvExcessWeight;
    private TextView tvExcessCharge;
    private TextView tvTotalPayable;
    private double baggageWeight;
    private double baggageRate;
    private int baggageAllowance;
    RadioButton rbEconomy;
    RadioButton rbBusiness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Connect custom Toolbar from XML
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        // Set Toolbar as the Activity's ActionBar
        setSupportActionBar(toolbar);
        // Set application title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    R.string.app_name
            );
        }

        etBaggageWeight = findViewById(R.id.etBaggageWeight);
        rgTicketClass = findViewById(R.id.rgTicketClass);
        etBaggageRate = findViewById(R.id.etBaggageRate);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnReset = findViewById(R.id.btnReset);
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvExcessCharge = findViewById(R.id.tvExcessCharge);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);
        rbEconomy = findViewById(R.id.rbEconomy);
        rbBusiness = findViewById(R.id.rbBusiness);

        rbEconomy.setOnClickListener(this);
        rbBusiness.setOnClickListener(this);

        // Calculate Button
        btnCalculate.setOnClickListener(v -> {
            if (readAndValidateInputs()) {
                // Valid inputs: calculate new results
                calculateAndDisplayResults();
            } else {
                // Invalid inputs: clear previous results
                clearResults();
            }
        });

        // Activate Reset Button
        btnReset.setOnClickListener(v -> {
            // Step A: Clear input fields
            etBaggageWeight.setText("");
            etBaggageRate.setText("");
            // Step B: Reset ticket class to Economy
            rgTicketClass.check(R.id.rbEconomy);
            // Step C: Reset output values
            clearResults();
            // Step D: Clear input error messages
            etBaggageWeight.setError(null);
            etBaggageRate.setError(null);
            // Step E: Reset stored calculation values
            baggageWeight = 0;
            baggageRate = 0;
            baggageAllowance = 20;
            // Step F: Move focus to baggage weight
            etBaggageWeight.requestFocus();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View view) {
        // Check if Economy RadioButton is clicked
        if (view.getId() == R.id.rbEconomy) {
            Toast.makeText(
                    this,"Economy Class Selected", Toast.LENGTH_SHORT
            ).show();
        }
        // Check if Business RadioButton is clicked
        else if (view.getId() == R.id.rbBusiness) {
            Toast.makeText(
                    this,"Business Class Selected", Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Load menu items from main_menu.xml
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check if user selected Share App
        if (item.getItemId() == R.id.action_share) {
            // Get GitHub project URL from strings.xml
            String appUrl = getString(R.string.github_project_url);
            // Prepare the message to be shared
            String shareMessage =
                    "Check out Baggage Fee Calculator!\n" + appUrl;
            // Create an Intent to share text
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            // Specify that the content is plain text
            shareIntent.setType("text/plain");
            // Attach the message to the Intent
            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    shareMessage
            );
            // Open Android Sharesheet
            startActivity(
                    Intent.createChooser(shareIntent, "Share App Via")
            );
            return true;
        }

        // Check if user selected About
        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(
                    MainActivity.this,
                    AboutActivity.class
            );
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean readAndValidateInputs() {
        // Clear previous input errors
        etBaggageWeight.setError(null);
        etBaggageRate.setError(null);
        // Step A: Read input as text
        String weightText = etBaggageWeight.getText().toString().trim();
        String rateText = etBaggageRate.getText().toString().trim();
        // Step B: Check empty weight
        if (weightText.isEmpty()) {
            etBaggageWeight.setError("Please enter baggage weight");
            etBaggageWeight.requestFocus();
            return false;
        }
        // Step C: Check empty rate
        if (rateText.isEmpty()) {
            etBaggageRate.setError("Please enter baggage rate");
            etBaggageRate.requestFocus();
            return false;
        }
        // Step D: Convert text to numbers
        try {
            baggageWeight = Double.parseDouble(weightText);
            baggageRate = Double.parseDouble(rateText);
        } catch (NumberFormatException e) {
            etBaggageWeight.setError("Please enter valid numbers");
            etBaggageWeight.requestFocus();
            return false;
        }
        // Step E: Check invalid or negative values
        if (!Double.isFinite(baggageWeight) || baggageWeight < 0) {
            etBaggageWeight.setError("Weight cannot be negative or invalid");
            etBaggageWeight.requestFocus();
            return false;
        }
        if (!Double.isFinite(baggageRate) || baggageRate < 0) {
            etBaggageRate.setError("Rate cannot be negative or invalid");
            etBaggageRate.requestFocus();
            return false;
        }
        // Step F: Determine ticket class allowance
        int selectedClassId = rgTicketClass.getCheckedRadioButtonId();
        if (selectedClassId == R.id.rbEconomy) {
            baggageAllowance = 20;
        } else if (selectedClassId == R.id.rbBusiness) {
            baggageAllowance = 40;
        } else {
            return false;
        }
        // All inputs are valid
        return true;
    }

    // Calculate excess baggage weight
    private double calculateExcessWeight() {

        double excessWeight = baggageWeight - baggageAllowance;
        excessWeight = Math.max(0, excessWeight);
        return excessWeight;
    }

    // Calculate charges and display results
    private void calculateAndDisplayResults() {

        // Step A: Calculate excess baggage weight
        double excessWeight = calculateExcessWeight();
        // Step B: Calculate excess baggage charge
        double excessCharge = excessWeight * baggageRate;
        // Step C: Calculate total payable with 8% tax
        double totalPayable = excessCharge * 1.08;
        // Step D: Display formatted results
        tvExcessWeight.setText(
                String.format(Locale.US, "%.2f kg", excessWeight)
        );
        tvExcessCharge.setText(
                String.format(Locale.US, "RM %.2f", excessCharge)
        );
        tvTotalPayable.setText(
                String.format(Locale.US, "RM %.2f", totalPayable)
        );
    }

    // Clear previous calculation results
    private void clearResults() {

        tvExcessWeight.setText("0.00 kg");
        tvExcessCharge.setText("RM 0.00");
        tvTotalPayable.setText("RM 0.00");
    }
}