package com.example.sos_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Phone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button backButton2222 = (Button) findViewById(R.id.backButton2222);
        backButton2222.setOnClickListener(v -> startActivity(new Intent(Phone.this, sos.class)));
    }
}