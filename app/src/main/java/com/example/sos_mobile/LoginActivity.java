package com.example.sos_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class LoginActivity extends AppCompatActivity {

    // UI элементы
    private TextInputEditText etLogin, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private ProgressBar progressBar;

    // SharedPreferences и константы
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // URL сервера
    private static final String LOGIN_URL = "https://your-server.com/api/login";
    private static final String REFRESH_TOKEN_URL = "https://your-server.com/api/refresh-token";
    private Button button2232342;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        button2232342 = findViewById(R.id.button2232342);
        button2232342.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, sos.class);
                startActivity(intent);
            }
        });

        // Проверка авторизации
        if (isUserLoggedIn()) {
            startMainActivity();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            if (validateForm()) {
                // Для тестирования используем локальный вход
                // Для реального использования раскомментируйте строку ниже
                // loginUser();
                loginUserLocal();
            }
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Функция восстановления пароля", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateForm() {
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        clearErrors();

        boolean isValid = true;

        if (login.isEmpty()) {
            showError(etLogin, "Введите логин или телефон");
            isValid = false;
        }

        if (password.isEmpty()) {
            showError(etPassword, "Введите пароль");
            isValid = false;
        } else if (password.length() < 6) {
            showError(etPassword, "Пароль должен содержать минимум 6 символов");
            isValid = false;
        }

        return isValid;
    }

    private void clearErrors() {
        etLogin.setError(null);
        etPassword.setError(null);
    }

    private void showError(TextInputEditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
    }

    // ========== РЕАЛЬНЫЙ ВХОД НА СЕРВЕР ==========
    private void loginUser() {
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        JSONObject loginData = new JSONObject();
        try {
            loginData.put("login", login);
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка формирования данных", Toast.LENGTH_SHORT).show();
            return;
        }

        new LoginTask().execute(loginData.toString());
    }

    @SuppressLint("StaticFieldLeak")
    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading(true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonInputString = params[0];
            return makeHttpRequest(LOGIN_URL, "POST", jsonInputString, null);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showLoading(false);
            handleLoginResponse(result);
        }
    }

    private String makeHttpRequest(String urlString, String method, String requestBody, String authToken) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            if (authToken != null) {
                connection.setRequestProperty("Authorization", "Bearer " + authToken);
            }

            connection.setDoOutput(true);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            // Отправка данных для POST запросов
            if (requestBody != null && method.equals("POST")) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
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
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorResponse.append(line);
                }
                return "Error:" + responseCode + ":" + errorResponse.toString();
            }

        } catch (IOException e) {
            Log.e("HTTP", "Network error: " + e.getMessage());
            return "Error:Network:" + e.getMessage();
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

    private void handleLoginResponse(String result) {
        if (result.startsWith("Error:")) {
            handleLoginError(result);
        } else {
            handleLoginSuccess(result);
        }
    }

    private void handleLoginSuccess(String result) {
        try {
            JSONObject jsonResponse = new JSONObject(result);

            if (jsonResponse.getBoolean("success")) {
                JSONObject userData = jsonResponse.getJSONObject("data");
                String accessToken = userData.getString("access_token");
                String refreshToken = userData.getString("refresh_token");
                String username = userData.getString("username");
                String userId = userData.getString("user_id");

                saveUserData(accessToken, refreshToken, userId, username);
                Toast.makeText(this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();
                startMainActivity();
            } else {
                String errorMessage = jsonResponse.getString("message");
                Toast.makeText(this, "Ошибка: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e("Login", "JSON parsing error: " + e.getMessage());
            Toast.makeText(this, "Ошибка обработки ответа сервера", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLoginError(String error) {
        String[] errorParts = error.split(":", 3);
        if (errorParts.length >= 3) {
            String errorType = errorParts[1];
            String errorMessage = errorParts[2];

            switch (errorType) {
                case "401":
                    Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_LONG).show();
                    break;
                case "404":
                    Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_LONG).show();
                    break;
                case "Network":
                    Toast.makeText(this, "Ошибка сети: " + errorMessage, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(this, "Ошибка сервера: " + errorMessage, Toast.LENGTH_LONG).show();
                    break;
            }
        } else {
            Toast.makeText(this, "Неизвестная ошибка", Toast.LENGTH_LONG).show();
        }
    }

    // ========== ЛОКАЛЬНЫЙ ВХОД ДЛЯ ТЕСТИРОВАНИЯ ==========
    private void loginUserLocal() {
        showLoading(true);

        new Handler().postDelayed(() -> {
            showLoading(false);

            String login = etLogin.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Простая проверка для демонстрации
            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
                return;
            }

            // Имитация успешного входа
            saveUserData(
                    "local_access_token_" + System.currentTimeMillis(),
                    "local_refresh_token_" + System.currentTimeMillis(),
                    "user_" + login.hashCode(),
                    login
            );

            Toast.makeText(this, "Вход выполнен успешно! (локальный режим)", Toast.LENGTH_SHORT).show();
            startMainActivity();
        }, 2000);
    }

    // ========== УПРАВЛЕНИЕ ДАННЫМИ ПОЛЬЗОВАТЕЛЯ ==========
    private void saveUserData(String accessToken, String refreshToken, String userId, String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (24 * 60 * 60 * 1000)); // 24 часа
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        long expiryTime = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0);
        return isLoggedIn && System.currentTimeMillis() < expiryTime;
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public void refreshAccessToken() {
        String refreshToken = getRefreshToken();
        if (refreshToken == null) {
            logout();
            return;
        }

        new Thread(() -> {
            JSONObject refreshData = new JSONObject();
            try {
                refreshData.put("refresh_token", refreshToken);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            String result = makeHttpRequest(REFRESH_TOKEN_URL, "POST", refreshData.toString(), refreshToken);

            runOnUiThread(() -> {
                if (!result.startsWith("Error:")) {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        if (jsonResponse.getBoolean("success")) {
                            String newAccessToken = jsonResponse.getJSONObject("data").getString("access_token");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(KEY_ACCESS_TOKEN, newAccessToken);
                            editor.putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (24 * 60 * 60 * 1000));
                            editor.apply();
                            Log.d("Token", "Access token refreshed successfully");
                        } else {
                            logout();
                        }
                    } catch (JSONException e) {
                        logout();
                    }
                } else {
                    logout();
                }
            });
        }).start();
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_TOKEN_EXPIRY);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();

        // Переход на экран входа
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public String getAuthHeader() {
        String token = getAccessToken();
        return token != null ? "Bearer " + token : null;
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Метод для проверки токена (можно вызвать перед важными запросами)
    public boolean checkAndRefreshToken() {
        if (isTokenExpired()) {
            refreshAccessToken();
            return false;
        }
        return true;
    }

    private boolean isTokenExpired() {
        long expiryTime = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0);
        return System.currentTimeMillis() > expiryTime;
    }

    // Метод для получения информации о пользователе
    public String getCurrentUserInfo() {
        String username = sharedPreferences.getString(KEY_USERNAME, "Гость");
        String userId = sharedPreferences.getString(KEY_USER_ID, "");
        return username + " (" + userId + ")";
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