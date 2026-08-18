package com.example.sos_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrMobile extends AppCompatActivity {

    private TextView phoneText;
    private EditText verificationCodeEditText;
    private Button verifyButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registr_mobile);

        phoneText = findViewById(R.id.phone_text);
        verificationCodeEditText = findViewById(R.id.verification_code);
        verifyButton = findViewById(R.id.verify_button);

        String phone = getIntent().getStringExtra("phone");
        phoneText.setText("Код отправлен на номер " + phone);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = verificationCodeEditText.getText().toString();

                if (verificationCode.equals("1234")) {
                    Toast.makeText(RegistrMobile.this, "Телефон подтвержден", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistrMobile.this, "Неверный код подтверждения", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}