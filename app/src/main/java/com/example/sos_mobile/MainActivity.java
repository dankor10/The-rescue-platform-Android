package com.example.sos_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etLastName, etFirstName, etUsername, etPhone, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private static final String REGISTRATION_URL = "https://your-server.com/api/register"; // Замените на ваш URL

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов
        initViews();

        btnRegister.setOnClickListener(v -> {
            if (validateForm()) {
                // Регистрация пользователя
                registerUser();
            }
        });

        tvLogin.setOnClickListener(v -> {
            // Переход на экран входа
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        etLastName = findViewById(R.id.etLastName);
        etFirstName = findViewById(R.id.etFirstName);
        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private boolean validateForm() {
        String lastName = etLastName.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Сброс ошибок
        clearErrors();

        boolean isValid = true;

        if (lastName.isEmpty()) {
            showError(etLastName, "Введите фамилию");
            isValid = false;
        }

        if (firstName.isEmpty()) {
            showError(etFirstName, "Введите имя");
            isValid = false;
        }

        if (username.isEmpty()) {
            showError(etUsername, "Введите логин");
            isValid = false;
        } else if (username.length() < 3) {
            showError(etUsername, "Логин должен содержать минимум 3 символа");
            isValid = false;
        }

        if (phone.isEmpty()) {
            showError(etPhone, "Введите номер телефона");
            isValid = false;
        } else if (!isValidPhone(phone)) {
            showError(etPhone, "Введите корректный номер телефона");
            isValid = false;
        }

        if (password.isEmpty()) {
            showError(etPassword, "Введите пароль");
            isValid = false;
        } else if (password.length() < 6) {
            showError(etPassword, "Пароль должен содержать минимум 6 символов");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            showError(etConfirmPassword, "Подтвердите пароль");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            showError(etConfirmPassword, "Пароли не совпадают");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidPhone(String phone) {
        // Простая валидация номера телефона
        String phoneRegex = "^[+]?[0-9]{10,13}$";
        return phone.matches(phoneRegex);
    }

    private void clearErrors() {
        etLastName.setError(null);
        etFirstName.setError(null);
        etUsername.setError(null);
        etPhone.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
    }

    private void showError(TextInputEditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
    }

    private void registerUser() {
        String lastName = etLastName.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Создание JSON объекта с данными пользователя
        JSONObject userData = new JSONObject();
        try {
            userData.put("last_name", lastName);
            userData.put("first_name", firstName);
            userData.put("username", username);
            userData.put("phone", phone);
            userData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка формирования данных", Toast.LENGTH_SHORT).show();
            return;
        }

        // Запуск асинхронной задачи для регистрации
        new RegistrationTask().execute(userData.toString());
    }

    @SuppressLint("StaticFieldLeak")
    private class RegistrationTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonInputString = params[0];
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(REGISTRATION_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Отправка данных
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Получение ответа
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }

            } catch (IOException e) {
                Log.e("Registration", "Error: " + e.getMessage());
                return "Error: " + e.getMessage();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);

            if (result.startsWith("Error:")) {
                Toast.makeText(MainActivity.this, "Ошибка соединения: " + result, Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    if (jsonResponse.getBoolean("success")) {
                        // Успешная регистрация
                        Toast.makeText(MainActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();

                        // Сохранение данных пользователя (например, в SharedPreferences)
                        saveUserData(jsonResponse);

                        // Переход на главный экран
                        Intent intent = new Intent(MainActivity.this, sos.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = jsonResponse.getString("message");
                        Toast.makeText(MainActivity.this, "Ошибка: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveUserData(JSONObject userData) {
        // Сохранение данных пользователя в SharedPreferences
        try {
            String token = userData.getString("token");
            String username = userData.getString("username");

            // Пример сохранения в SharedPreferences
            getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("auth_token", token)
                    .putString("username", username)
                    .putBoolean("isLoggedIn", true)
                    .apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Временная заглушка для тестирования (без сервера)
    private void registerUserLocal() {
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Имитация задержки сети
        new android.os.Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);

            Toast.makeText(MainActivity.this, "Регистрация успешна! (локальный режим)", Toast.LENGTH_SHORT).show();

            // Сохранение данных локально
            getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("username", etUsername.getText().toString())
                    .putBoolean("isLoggedIn", true)
                    .apply();

            // Переход на главный экран
            Intent intent = new Intent(MainActivity.this, sos.class);
            startActivity(intent);
            finish();
        }, 2000);
    }


    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }
}