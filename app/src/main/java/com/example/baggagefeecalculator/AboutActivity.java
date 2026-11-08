package com.example.baggagefeecalculator;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ActivityNotFoundException;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);

        // Connect About Page Toolbar from XML
        Toolbar toolbarAbout = findViewById(R.id.toolbarAbout);
        // Set custom Toolbar as the Activity's ActionBar
        setSupportActionBar(toolbarAbout);
        // Configure Toolbar title and Back Arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("About");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView tvGithubLink = findViewById(R.id.tvGithubLink);

        tvGithubLink.setOnClickListener(v -> {
            // Get GitHub repository URL from strings.xml
            String githubUrl = getString(R.string.github_project_url);
            // Create an Intent to open the URL
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(githubUrl)
            );
            try {
                // Open GitHub using a browser or supported app
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Display a message if no app can open the URL
                Toast.makeText(
                        AboutActivity.this,
                        "Unable to open GitHub link",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}