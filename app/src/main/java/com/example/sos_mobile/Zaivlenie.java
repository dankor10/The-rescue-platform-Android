package com.example.sos_mobile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Zaivlenie extends AppCompatActivity {

    private EditText statementEditText;
    private CheckBox agreementCheckbox;
    private MaterialButton sendButton;
    private TextView charCountText;
    private static final int MAX_CHARS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zaivlenie);

        initViews();
        setupListeners();
        updateSendButtonState();
    }

    private void initViews() {
        statementEditText = findViewById(R.id.statementEditText);
        agreementCheckbox = findViewById(R.id.agreementCheckbox);
        sendButton = findViewById(R.id.sendButton);
        charCountText = findViewById(R.id.charCountText);

        // Кнопка назад
        LinearLayout backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupListeners() {
        // Слушатель изменения текста
        statementEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCharCount();
                updateSendButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Слушатель чекбокса
        agreementCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSendButtonState();
            }
        });

        // Клик на весь layout чекбокса
        LinearLayout checkboxLayout = findViewById(R.id.checkboxLayout);
        checkboxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreementCheckbox.setChecked(!agreementCheckbox.isChecked());
                updateSendButtonState();
            }
        });

        // Кнопка отправки
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendStatement();
            }
        });
    }

    private void updateCharCount() {
        int currentLength = statementEditText.getText().length();
        charCountText.setText(currentLength + "/" + MAX_CHARS);

        if (currentLength > MAX_CHARS) {
            charCountText.setTextColor(getResources().getColor(R.color.red));
        } else {
            charCountText.setTextColor(getResources().getColor(R.color.shazam_light_gray));
        }
    }

    private void updateSendButtonState() {
        String text = statementEditText.getText().toString().trim();
        boolean hasText = !text.isEmpty();
        boolean isChecked = agreementCheckbox.isChecked();
        boolean withinLimit = text.length() <= MAX_CHARS;

        sendButton.setEnabled(hasText && isChecked && withinLimit);

        if (sendButton.isEnabled()) {
            sendButton.setBackgroundColor(getResources().getColor(R.color.shazam_dark_gray));
            sendButton.setAlpha(1.0f);
        } else {
            sendButton.setBackgroundColor(getResources().getColor(R.color.shazam_medium_gray));
            sendButton.setAlpha(0.6f);
        }
    }

    private void sendStatement() {
        String statement = statementEditText.getText().toString().trim();

        if (statement.isEmpty()) {
            Toast.makeText(this, "Заявление не может быть пустым", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!agreementCheckbox.isChecked()) {
            Toast.makeText(this, "Подтвердите согласие с ответственностью", Toast.LENGTH_SHORT).show();
            return;
        }

        if (statement.length() > MAX_CHARS) {
            Toast.makeText(this, "Превышен лимит символов", Toast.LENGTH_SHORT).show();
            return;
        }

        // Здесь должна быть логика отправки на сервер
        // Покажем сообщение об успешной отправке
        Toast.makeText(this, "Заявление успешно отправлено!", Toast.LENGTH_LONG).show();

        // Очистим форму после отправки
        statementEditText.setText("");
        agreementCheckbox.setChecked(false);
        updateCharCount();
        updateSendButtonState();

        // Через 2 секунды вернемся назад
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        finish();
                    }
                },
                2000);
    }
}