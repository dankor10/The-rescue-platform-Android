package com.example.sos_mobile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProtokolGAI extends AppCompatActivity {

    private Calendar selectedDateTime = Calendar.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protokol_gai);

        initViews();
        setupListeners();
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
        // Регистрация ТС
        MaterialButton btnVehicleRegistration = findViewById(R.id.btnVehicleRegistration);
        btnVehicleRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVehicleRegistrationDialog();
            }
        });

        // Водительские удостоверения
        MaterialButton btnDriverLicense = findViewById(R.id.btnDriverLicense);
        btnDriverLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDriverLicenseDialog();
            }
        });

        // Технический осмотр
        MaterialButton btnTechInspection = findViewById(R.id.btnTechInspection);
        btnTechInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTechInspectionDialog();
            }
        });

        // Тренинги
        MaterialButton btnTraining = findViewById(R.id.btnTraining);
        btnTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrainingDialog();
            }
        });

        // Клик на карточки
        MaterialCardView cardVehicleRegistration = findViewById(R.id.cardVehicleRegistration);
        MaterialCardView cardDriverLicense = findViewById(R.id.cardDriverLicense);
        MaterialCardView cardTechInspection = findViewById(R.id.cardTechInspection);
        MaterialCardView cardTraining = findViewById(R.id.cardTraining);

        cardVehicleRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVehicleRegistrationDialog();
            }
        });

        cardDriverLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDriverLicenseDialog();
            }
        });

        cardTechInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTechInspectionDialog();
            }
        });

        cardTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrainingDialog();
            }
        });
    }

    private void showVehicleRegistrationDialog() {
        showDateTimePicker(new DateTimeSelectionCallback() {
            @Override
            public void onDateTimeSelected(Calendar dateTime) {
                selectedDateTime = dateTime;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(dateTime.getTime());

                String message = "Запись на регистрацию ТС принята\nДата: " + formattedDate;

                Toast.makeText(ProtokolGAI.this, message, Toast.LENGTH_LONG).show();

                showConfirmationDialog("Регистрация транспортного средства",
                        "Запись на " + formattedDate + "\n\nПри себе иметь:\n• Паспорт\n• ПТС\n• СТС\n• Полис ОСАГО\n• Договор купли-продажи");
            }
        });
    }

    private void showDriverLicenseDialog() {
        showDateTimePicker(new DateTimeSelectionCallback() {
            @Override
            public void onDateTimeSelected(Calendar dateTime) {
                selectedDateTime = dateTime;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(dateTime.getTime());

                String message = "Запись на получение водительского удостоверения принята\nДата: " + formattedDate;

                Toast.makeText(ProtokolGAI.this, message, Toast.LENGTH_LONG).show();

                showConfirmationDialog("Водительское удостоверение",
                        "Запись на " + formattedDate + "\n\nПри себе иметь:\n• Паспорт\n• Медицинскую справку\n• Квитанцию об оплате госпошлины\n• Старое водительское удостоверение (при замене)");
            }
        });
    }

    private void showTechInspectionDialog() {
        showDateTimePicker(new DateTimeSelectionCallback() {
            @Override
            public void onDateTimeSelected(Calendar dateTime) {
                selectedDateTime = dateTime;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(dateTime.getTime());

                String message = "Запись на технический осмотр принята\nДата: " + formattedDate;

                Toast.makeText(ProtokolGAI.this, message, Toast.LENGTH_LONG).show();

                showConfirmationDialog("Технический осмотр",
                        "Запись на " + formattedDate + "\n\nТребования:\n• Чистый автомобиль\n• Исправные системы\n• Документы: ПТС, СТС, водительское удостоверение\n• Аптечка, огнетушитель, знак аварийной остановки");
            }
        });
    }

    private void showTrainingDialog() {
        showDatePicker(new DateSelectionCallback() {
            @Override
            public void onDateSelected(Calendar date) {
                selectedDateTime = date;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(date.getTime());

                String message = "Запись на тренинг принята\nДата: " + formattedDate;

                Toast.makeText(ProtokolGAI.this, message, Toast.LENGTH_LONG).show();

                showConfirmationDialog("Запись на тренинг",
                        "Тренинг запланирован на " + formattedDate +
                                "\n\nДлительность: 4 часа\nМесто проведения: учебный класс ГАИ\nПри себе иметь: паспорт, водительское удостоверение");
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

                TimePickerDialog timePicker = new TimePickerDialog(ProtokolGAI.this,
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