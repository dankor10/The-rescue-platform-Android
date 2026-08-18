package com.example.sos_mobile;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Chat extends AppCompatActivity {

    private LinearLayout chatContainer;
    private ScrollView chatScrollView;
    private EditText messageEditText;
    private TextView typingIndicator;
    private Handler typingHandler = new Handler();
    private boolean isDoctorTyping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        setupListeners();
        loadInitialMessages();
    }

    private void initViews() {
        chatContainer = findViewById(R.id.chatContainer);
        chatScrollView = findViewById(R.id.chatScrollView);
        messageEditText = findViewById(R.id.messageEditText);
        typingIndicator = findViewById(R.id.typingIndicator);

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
        // Кнопка отправки сообщения
        LinearLayout sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Кнопка видео звонка
        LinearLayout videoCallButton = findViewById(R.id.videoCallButton);
        videoCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideoCall();
            }
        });

        // Кнопка прикрепления файла
        LinearLayout attachButton = findViewById(R.id.attachButton);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAttachmentOptions();
            }
        });
    }

    private void loadInitialMessages() {
        // Добавляем приветственное сообщение от врача
        addDoctorMessage("Здравствуйте! Я доктор Иванов. Чем могу вам помочь?", getCurrentTime());

        // Добавляем системное сообщение
        addSystemMessage("Консультация начата");
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();

        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        // Добавляем сообщение пользователя
        addUserMessage(messageText, getCurrentTime());
        messageEditText.setText("");

        // Имитируем ответ врача через 2-5 секунд
        simulateDoctorResponse();
    }

    private void simulateDoctorResponse() {
        // Скрываем клавиатуру
        hideKeyboard();

        // Показываем индикатор набора текста
        showTypingIndicator();

        // Имитируем задержку ответа врача (2-5 секунд)
        int delay = new Random().nextInt(3000) + 2000;

        typingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideTypingIndicator();

                // Генерируем ответ врача на основе последнего сообщения пользователя
                String lastUserMessage = getLastUserMessage();
                String doctorResponse = generateDoctorResponse(lastUserMessage);

                addDoctorMessage(doctorResponse, getCurrentTime());
            }
        }, delay);
    }

    private String getLastUserMessage() {
        // Получаем последнее сообщение пользователя
        for (int i = chatContainer.getChildCount() - 1; i >= 0; i--) {
            View child = chatContainer.getChildAt(i);
            if (child.getTag() != null && child.getTag().equals("user")) {
                LinearLayout messageLayout = (LinearLayout) child;

                // Ищем MaterialCardView в layout сообщения
                MaterialCardView cardView = findMaterialCardView(messageLayout);
                if (cardView != null) {
                    // Ищем TextView с текстом сообщения внутри cardView
                    TextView messageText = findMessageTextView(cardView);
                    if (messageText != null) {
                        return messageText.getText().toString();
                    }
                }
            }
        }
        return "";
    }

    // Вспомогательный метод для поиска MaterialCardView
    private MaterialCardView findMaterialCardView(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof MaterialCardView) {
                return (MaterialCardView) child;
            } else if (child instanceof ViewGroup) {
                MaterialCardView result = findMaterialCardView((ViewGroup) child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    // Вспомогательный метод для поиска TextView с сообщением
    private TextView findMessageTextView(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                // Проверяем, что это не время сообщения (по размеру текста или другим признакам)
                if (textView.getTextSize() > 12) { // Основное сообщение имеет больший размер
                    return textView;
                }
            } else if (child instanceof ViewGroup) {
                TextView result = findMessageTextView((ViewGroup) child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private String generateDoctorResponse(String userMessage) {
        userMessage = userMessage.toLowerCase();

        if (userMessage.contains("голова") || userMessage.contains("головная")) {
            return "Опишите характер головной боли: постоянная или приступообразная, где именно болит?";
        } else if (userMessage.contains("температура") || userMessage.contains("жар")) {
            return "Какая у вас температура? Как давно она держится?";
        } else if (userMessage.contains("горло") || userMessage.contains("кашель")) {
            return "Есть ли боль при глотании? Кашель сухой или с мокротой?";
        } else if (userMessage.contains("живот") || userMessage.contains("болит живот")) {
            return "Опишите локализацию боли. Есть ли тошнота, рвота?";
        } else if (userMessage.contains("аллергия") || userMessage.contains("аллерги")) {
            return "На что именно аллергия? Какие симптомы проявляются?";
        } else if (userMessage.contains("давление") || userMessage.contains("гипертон")) {
            return "Какое у вас давление? Принимаете ли какие-то лекарства?";
        } else {
            String[] responses = {
                    "Понятно. Расскажите подробнее о ваших симптомах.",
                    "Сколько дней продолжаются эти симптомы?",
                    "Принимали ли вы какие-либо лекарства?",
                    "Есть ли у вас хронические заболевания?",
                    "Были ли подобные симптомы раньше?",
                    "Рекомендую измерить температуру и давление.",
                    "Опишите ваше самочувствие более подробно."
            };
            return responses[new Random().nextInt(responses.length)];
        }
    }

    private void addUserMessage(String message, String time) {
        // Создаем контейнер для сообщения пользователя
        LinearLayout messageLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 8, 0, 8);
        messageLayout.setLayoutParams(layoutParams);
        messageLayout.setOrientation(LinearLayout.HORIZONTAL);
        messageLayout.setGravity(Gravity.END);
        messageLayout.setTag("user");

        // Создаем карточку сообщения
        MaterialCardView cardView = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.weight = 0;
        cardParams.width = 0;
        cardParams.width = dpToPx(280); // ← ИСПРАВЛЕНИЕ ЗДЕСЬ
        cardView.setLayoutParams(cardParams);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.purple_medium));
        cardView.setRadius(32);
        cardView.setCardElevation(4);

        // Создаем контент сообщения
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

        // Текст сообщения
        TextView messageText = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        messageText.setLayoutParams(textParams);
        messageText.setText(message);
        messageText.setTextColor(ContextCompat.getColor(this, R.color.white));
        messageText.setTextSize(14);
        messageText.setMaxWidth(dpToPx(240)); // Ограничиваем ширину текста
        messageText.setSingleLine(false); // Разрешаем многострочный текст

        // Время сообщения
        TextView timeText = new TextView(this);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        timeParams.gravity = Gravity.END;
        timeText.setLayoutParams(timeParams);
        timeText.setText(time);
        timeText.setTextColor(ContextCompat.getColor(this, R.color.shazam_light_gray));
        timeText.setTextSize(10);
        timeText.setPadding(0, dpToPx(4), 0, 0);

        // Добавляем элементы
        contentLayout.addView(messageText);
        contentLayout.addView(timeText);
        cardView.addView(contentLayout);

        // Добавляем аватар пользователя
        TextView userAvatar = new TextView(this);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(36), dpToPx(36));
        avatarParams.setMargins(dpToPx(8), 0, 0, 0);
        userAvatar.setLayoutParams(avatarParams);
        userAvatar.setGravity(Gravity.CENTER);
        userAvatar.setText("👤");
        userAvatar.setTextSize(14);
        userAvatar.setBackgroundResource(R.drawable.circle_background);

        // Сначала карточка, потом аватар
        messageLayout.addView(cardView);
        messageLayout.addView(userAvatar);

        // Добавляем сообщение в чат
        chatContainer.addView(messageLayout);
        scrollToBottom();
    }

    private void addDoctorMessage(String message, String time) {
        // Создаем контейнер для сообщения врача
        LinearLayout messageLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 8, 0, 8);
        messageLayout.setLayoutParams(layoutParams);
        messageLayout.setOrientation(LinearLayout.HORIZONTAL);
        messageLayout.setGravity(Gravity.START);
        messageLayout.setTag("doctor");

        // Добавляем аватар врача
        TextView doctorAvatar = new TextView(this);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(36), dpToPx(36));
        avatarParams.setMargins(0, 0, dpToPx(8), 0);
        doctorAvatar.setLayoutParams(avatarParams);
        doctorAvatar.setGravity(Gravity.CENTER);
        doctorAvatar.setText("👨‍⚕️");
        doctorAvatar.setTextSize(14);
        doctorAvatar.setBackgroundResource(R.drawable.circle_background);
        messageLayout.addView(doctorAvatar);

        // Создаем карточку сообщения
        MaterialCardView cardView = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.weight = 0;
        cardParams.width = 0;
        cardParams.width = dpToPx(280); // ← ИСПРАВЛЕНИЕ ЗДЕСЬ
        cardView.setLayoutParams(cardParams);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.blue_medium));
        cardView.setRadius(32);
        cardView.setCardElevation(4);

        // Создаем контент сообщения
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

        // Текст сообщения
        TextView messageText = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        messageText.setLayoutParams(textParams);
        messageText.setText(message);
        messageText.setTextColor(ContextCompat.getColor(this, R.color.white));
        messageText.setTextSize(14);
        messageText.setMaxWidth(dpToPx(240)); // Ограничиваем ширину текста
        messageText.setSingleLine(false); // Разрешаем многострочный текст

        // Время сообщения
        TextView timeText = new TextView(this);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        timeParams.gravity = Gravity.START;
        timeText.setLayoutParams(timeParams);
        timeText.setText(time);
        timeText.setTextColor(ContextCompat.getColor(this, R.color.shazam_light_gray));
        timeText.setTextSize(10);
        timeText.setPadding(0, dpToPx(4), 0, 0);

        // Добавляем элементы
        contentLayout.addView(messageText);
        contentLayout.addView(timeText);
        cardView.addView(contentLayout);
        messageLayout.addView(cardView);

        // Добавляем сообщение в чат
        chatContainer.addView(messageLayout);
        scrollToBottom();
    }

    private void addSystemMessage(String message) {
        // Создаем контейнер для системного сообщения
        LinearLayout messageLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 8, 0, 8);
        messageLayout.setLayoutParams(layoutParams);
        messageLayout.setOrientation(LinearLayout.HORIZONTAL);
        messageLayout.setGravity(Gravity.CENTER);
        messageLayout.setTag("system");

        // Создаем текстовое поле для системного сообщения
        TextView systemText = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        systemText.setLayoutParams(textParams);
        systemText.setText(message);
        systemText.setTextColor(ContextCompat.getColor(this, R.color.shazam_light_gray));
        systemText.setTextSize(12);
        systemText.setBackgroundResource(R.drawable.system_message_background);
        systemText.setPadding(dpToPx(24), dpToPx(8), dpToPx(24), dpToPx(8));

        messageLayout.addView(systemText);
        chatContainer.addView(messageLayout);
        scrollToBottom();
    }

    private void showTypingIndicator() {
        isDoctorTyping = true;
        typingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideTypingIndicator() {
        isDoctorTyping = false;
        typingIndicator.setVisibility(View.GONE);
    }

    private void scrollToBottom() {
        chatScrollView.post(new Runnable() {
            @Override
            public void run() {
                chatScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void startVideoCall() {
        Toast.makeText(this, "Подключение к видео-консультации...", Toast.LENGTH_SHORT).show();
    }

    private void showAttachmentOptions() {
        Toast.makeText(this, "Прикрепление файлов", Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}