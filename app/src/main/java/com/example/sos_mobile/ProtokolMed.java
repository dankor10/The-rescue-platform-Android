package com.example.sos_mobile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProtokolMed extends AppCompatActivity {

    private Calendar selectedDateTime = Calendar.getInstance();
    private Button ChatButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protokol_med);

        initViews();
        setupListeners();

        ChatButton = findViewById(R.id.ChatButton);
        ChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProtokolMed.this, Chat.class);
                startActivity(intent);
            }
        });
    }
    
    private void initViews() {
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
        // Вызов врача на дом
        MaterialButton homeDoctorButton = findViewById(R.id.homeDoctorButton);
        homeDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHomeDoctorDialog();
            }
        });

        // Открытие больничного
        MaterialButton sickLeaveButton = findViewById(R.id.sickLeaveButton);
        sickLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSickLeaveDialog();
            }
        });

        // Онлайн-консультация
        MaterialButton onlineConsultButton = findViewById(R.id.onlineConsultButton);
        onlineConsultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnlineConsultDialog();
            }
        });

        // Клик на карточки - исправлено на MaterialCardView
        MaterialCardView homeDoctorCard = findViewById(R.id.homeDoctorCard);
        MaterialCardView sickLeaveCard = findViewById(R.id.sickLeaveCard);
        MaterialCardView onlineConsultCard = findViewById(R.id.onlineConsultCard);

        homeDoctorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHomeDoctorDialog();
            }
        });

        sickLeaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSickLeaveDialog();
            }
        });

        onlineConsultCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnlineConsultDialog();
            }
        });
    }

    private void showHomeDoctorDialog() {
        // Диалог выбора даты и времени
        showDateTimePicker(new DateTimeSelectionCallback() {
            @Override
            public void onDateTimeSelected(Calendar dateTime) {
                selectedDateTime = dateTime;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(dateTime.getTime());

                // Здесь должна быть отправка заявки на сервер
                String message = "Заявка на вызов врача на дом принята\nДата: " + formattedDate;

                Toast.makeText(ProtokolMed.this, message, Toast.LENGTH_LONG).show();

                // Показываем подтверждение
                showConfirmationDialog("Вызов врача на дом",
                        "Врач приедет " + formattedDate + "\n\nПодготовьте:\n• Паспорт\n• Полис ОМС\n• Температурный листок");
            }
        });
    }

    private void showSickLeaveDialog() {
        // Диалог для открытия больничного
        showDatePicker(new DateSelectionCallback() {
            @Override
            public void onDateSelected(Calendar date) {
                selectedDateTime = date;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(date.getTime());

                String message = "Заявка на открытие больничного с " + formattedDate + " принята";

                Toast.makeText(ProtokolMed.this, message, Toast.LENGTH_LONG).show();

                showConfirmationDialog("Оформление больничного",
                        "Больничный лист будет открыт с " + formattedDate +
                                "\n\nЭлектронный документ будет направлен вашему работодателю в течение 24 часов");
            }
        });
    }

    private void showOnlineConsultDialog() {
        // Диалог выбора времени для онлайн-консультации
        showDateTimePicker(new DateTimeSelectionCallback() {
            @Override
            public void onDateTimeSelected(Calendar dateTime) {
                selectedDateTime = dateTime;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(dateTime.getTime());

                String message = "Запись на онлайн-консультацию на " + formattedDate + " принята";

                Toast.makeText(ProtokolMed.this, message, Toast.LENGTH_LONG).show();

                showConfirmationDialog("Онлайн-консультация",
                        "Консультация запланирована на " + formattedDate +
                                "\n\nЗа 15 минут до начала вам придет ссылка для подключения к видеоконференции");
            }
        });
    }

    private void showDateTimePicker(final DateTimeSelectionCallback callback) {
        final Calendar currentDate = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // После выбора даты выбираем время
                TimePickerDialog timePicker = new TimePickerDialog(ProtokolMed.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);

                                callback.onDateTimeSelected(selectedDateTime);
                            }
                        },
                        currentDate.get(Calendar.HOUR_OF_DAY),
                        currentDate.get(Calendar.MINUTE),
                        true
                );
                timePicker.show();
            }
        },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );

        // Минимальная дата - сегодня
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    private void showDatePicker(final DateSelectionCallback callback) {
        final Calendar currentDate = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                callback.onDateSelected(selectedDateTime);
            }
        },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    private void showConfirmationDialog(String title, String message) {
        // Здесь можно показать красивый диалог подтверждения
        // Пока используем Toast
        Toast.makeText(this, title + "\n" + message, Toast.LENGTH_LONG).show();
    }

    // Интерфейсы для колбэков
    interface DateTimeSelectionCallback {
        void onDateTimeSelected(Calendar dateTime);
    }

    interface DateSelectionCallback {
        void onDateSelected(Calendar date);
    }
}