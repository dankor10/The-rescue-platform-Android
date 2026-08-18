package com.example.sos_mobile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MedCard extends AppCompatActivity {

    private boolean isEditMode = false;
    private EditText patientName, patientAge, patientBloodType, patientPhone, patientEmail;
    private EditText chronicDiseases, allergies, medications;
    private EditText contact1Name, contact1Phone, contact1Relation;
    private EditText contact2Name, contact2Phone, contact2Relation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_card);

        initViews();
        setupListeners();
        loadMedicalData();
    }

    @SuppressLint("WrongViewCast")
    private void initViews() {
        // Инициализация всех EditText полей
        patientName = findViewById(R.id.patientName);
        patientAge = findViewById(R.id.patientAge);
        patientBloodType = findViewById(R.id.patientBloodType);
        patientPhone = findViewById(R.id.patientPhone);
        patientEmail = findViewById(R.id.patientEmail);
        chronicDiseases = findViewById(R.id.chronicDiseases);
        allergies = findViewById(R.id.allergies);
        medications = findViewById(R.id.medications);
        contact1Name = findViewById(R.id.contact1Name);
        contact1Phone = findViewById(R.id.contact1Phone);
        contact1Relation = findViewById(R.id.contact1Relation);
        contact2Name = findViewById(R.id.contact2Name);
        contact2Phone = findViewById(R.id.contact2Phone);
        contact2Relation = findViewById(R.id.contact2Relation);

        // Кнопка назад
        LinearLayout backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    // Если в режиме редактирования, спрашиваем подтверждение
                    showSaveConfirmation();
                } else {
                    finish();
                }
            }
        });
    }

    private void setupListeners() {
        // Кнопка редактирования
        TextView editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditMode();
            }
        });
    }

    private void loadMedicalData() {
        // Загрузка данных из SharedPreferences или базы данных
        // Пока используем демо-данные

        patientName.setText("Иван Иванов");
        patientAge.setText("35 лет");
        patientBloodType.setText("A(II) Rh+");
        patientPhone.setText("+7 (999) 123-45-67");
        patientEmail.setText("ivanov@mail.ru");
        chronicDiseases.setText("Гипертония, аллергия на пенициллин");
        allergies.setText("Пенициллин, пыльца");
        medications.setText("Лизиноприл 10мг - 1 раз в день");
        contact1Name.setText("Мария Иванова");
        contact1Phone.setText("+7 (999) 765-43-21");
        contact1Relation.setText("Супруга");
        contact2Name.setText("Петр Иванов");
        contact2Phone.setText("+7 (999) 555-44-33");
        contact2Relation.setText("Брат");
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;

        TextView editButton = findViewById(R.id.editButton);

        if (isEditMode) {
            // Включаем режим редактирования
            editButton.setText("💾");
            enableAllFields(true);
            Toast.makeText(this, "Режим редактирования включен", Toast.LENGTH_SHORT).show();
        } else {
            // Сохраняем и выключаем режим редактирования
            if (validateData()) {
                saveMedicalData();
                editButton.setText("✏️");
                enableAllFields(false);
                Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
            } else {
                // Если валидация не прошла, остаемся в режиме редактирования
                isEditMode = true;
                Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableAllFields(boolean enabled) {
        int color = enabled ? getResources().getColor(R.color.white) : getResources().getColor(R.color.white);

        patientName.setEnabled(enabled);
        patientAge.setEnabled(enabled);
        patientBloodType.setEnabled(enabled);
        patientPhone.setEnabled(enabled);
        patientEmail.setEnabled(enabled);
        chronicDiseases.setEnabled(enabled);
        allergies.setEnabled(enabled);
        medications.setEnabled(enabled);
        contact1Name.setEnabled(enabled);
        contact1Phone.setEnabled(enabled);
        contact1Relation.setEnabled(enabled);
        contact2Name.setEnabled(enabled);
        contact2Phone.setEnabled(enabled);
        contact2Relation.setEnabled(enabled);

        // Визуальные изменения для режима редактирования
        if (enabled) {
            patientName.setBackgroundResource(R.drawable.edit_text_background);
            patientAge.setBackgroundResource(R.drawable.edit_text_background);
            patientBloodType.setBackgroundResource(R.drawable.edit_text_background);
            patientPhone.setBackgroundResource(R.drawable.edit_text_background);
            patientEmail.setBackgroundResource(R.drawable.edit_text_background);
            chronicDiseases.setBackgroundResource(R.drawable.edit_text_background);
            allergies.setBackgroundResource(R.drawable.edit_text_background);
            medications.setBackgroundResource(R.drawable.edit_text_background);
            contact1Name.setBackgroundResource(R.drawable.edit_text_background);
            contact1Phone.setBackgroundResource(R.drawable.edit_text_background);
            contact1Relation.setBackgroundResource(R.drawable.edit_text_background);
            contact2Name.setBackgroundResource(R.drawable.edit_text_background);
            contact2Phone.setBackgroundResource(R.drawable.edit_text_background);
            contact2Relation.setBackgroundResource(R.drawable.edit_text_background);
        } else {
            patientName.setBackgroundResource(android.R.color.transparent);
            patientAge.setBackgroundResource(android.R.color.transparent);
            patientBloodType.setBackgroundResource(android.R.color.transparent);
            patientPhone.setBackgroundResource(android.R.color.transparent);
            patientEmail.setBackgroundResource(android.R.color.transparent);
            chronicDiseases.setBackgroundResource(android.R.color.transparent);
            allergies.setBackgroundResource(android.R.color.transparent);
            medications.setBackgroundResource(android.R.color.transparent);
            contact1Name.setBackgroundResource(android.R.color.transparent);
            contact1Phone.setBackgroundResource(android.R.color.transparent);
            contact1Relation.setBackgroundResource(android.R.color.transparent);
            contact2Name.setBackgroundResource(android.R.color.transparent);
            contact2Phone.setBackgroundResource(android.R.color.transparent);
            contact2Relation.setBackgroundResource(android.R.color.transparent);
        }
    }

    private boolean validateData() {
        // Проверка обязательных полей
        if (TextUtils.isEmpty(patientName.getText().toString().trim())) {
            patientName.setError("Введите ФИО");
            return false;
        }
        if (TextUtils.isEmpty(patientPhone.getText().toString().trim())) {
            patientPhone.setError("Введите телефон");
            return false;
        }
        if (TextUtils.isEmpty(contact1Name.getText().toString().trim()) ||
                TextUtils.isEmpty(contact1Phone.getText().toString().trim())) {
            Toast.makeText(this, "Заполните данные первого экстренного контакта", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveMedicalData() {
        // Сохранение данных в SharedPreferences или базу данных
        String name = patientName.getText().toString().trim();
        String age = patientAge.getText().toString().trim();
        String bloodType = patientBloodType.getText().toString().trim();
        String phone = patientPhone.getText().toString().trim();
        String email = patientEmail.getText().toString().trim();
        String diseases = chronicDiseases.getText().toString().trim();
        String allergiesText = allergies.getText().toString().trim();
        String medicationsText = medications.getText().toString().trim();
        String contact1NameText = contact1Name.getText().toString().trim();
        String contact1PhoneText = contact1Phone.getText().toString().trim();
        String contact1RelationText = contact1Relation.getText().toString().trim();
        String contact2NameText = contact2Name.getText().toString().trim();
        String contact2PhoneText = contact2Phone.getText().toString().trim();
        String contact2RelationText = contact2Relation.getText().toString().trim();

        // Здесь должен быть код сохранения в базу данных или SharedPreferences
        // Например:
        // SharedPreferences prefs = getSharedPreferences("medical_card", MODE_PRIVATE);
        // SharedPreferences.Editor editor = prefs.edit();
        // editor.putString("patient_name", name);
        // ... и так для всех полей
        // editor.apply();

        // Пока просто выводим в лог
        System.out.println("Медицинская карта сохранена:");
        System.out.println("ФИО: " + name);
        System.out.println("Телефон: " + phone);
    }

    private void showSaveConfirmation() {
        // Диалог подтверждения сохранения при выходе
        Toast.makeText(this, "Сначала сохраните изменения", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            showSaveConfirmation();
        } else {
            super.onBackPressed();
        }
    }
}