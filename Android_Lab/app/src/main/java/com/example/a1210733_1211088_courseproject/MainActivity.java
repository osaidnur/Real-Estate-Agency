package com.example.a1210733_1211088_courseproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a1210733_1211088_courseproject.api.PropertyApiClient;

public class MainActivity extends AppCompatActivity {

    private Button btnConnect;
    private PropertyApiClient propertyApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize PropertyApiClient
        propertyApiClient = new PropertyApiClient(this);

        // Set up connect button click listener
        btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show loading message
                Toast.makeText(MainActivity.this, "Connecting to API...", Toast.LENGTH_SHORT).show();

                // Disable button to prevent multiple clicks
                btnConnect.setEnabled(false);

                // Fetch properties from API
                propertyApiClient.fetchAndStoreProperties();

                // The PropertyApiClient will handle the success/failure toasts
                // Re-enable button after a delay (to prevent rapid clicking)
                btnConnect.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnConnect.setEnabled(true);
                    }
                }, 2000); // 2 seconds delay
            }
        });
    }
}

