package com.example.sos_mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Accaunt2 extends AppCompatActivity {

    // UI элементы
    private ImageView ivAvatar;
    private TextInputEditText etLastName, etFirstName, etMiddleName, etPhone, etEmail;
    private TextInputLayout tilLastName, tilFirstName, tilMiddleName, tilPhone, tilEmail;
    private MaterialButton btnEdit, btnSave, btnCancel, btnMedicalCard, btnLogout;
    private ProgressBar progressBar;

    // Переменные для работы с фото
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_PERMISSIONS = 100;
    private String currentPhotoPath;

    // Режим редактирования
    private boolean isEditMode = false;

    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";

    // URL сервера
    private static final String PROFILE_URL = "https://your-server.com/api/profile";
    private static final String UPDATE_PROFILE_URL = "https://your-server.com/api/profile/update";
    private static final String UPLOAD_AVATAR_URL = "https://your-server.com/api/avatar/upload";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accaunt2);

        initViews();
        setupClickListeners();
        loadUserData();
    }

    private void initViews() {
        // Аватар и фото
        ivAvatar = findViewById(R.id.ivAvatar);

        // Поля ввода
        etLastName = findViewById(R.id.etLastName);
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);

        // Layout полей ввода
        tilLastName = findViewById(R.id.tilLastName);
        tilFirstName = findViewById(R.id.tilFirstName);
        tilMiddleName = findViewById(R.id.tilMiddleName);
        tilPhone = findViewById(R.id.tilPhone);
        tilEmail = findViewById(R.id.tilEmail);

        // Кнопки
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnMedicalCard = findViewById(R.id.btnMedicalCard);
        btnLogout = findViewById(R.id.btnLogout);

        // Progress bar
        progressBar = findViewById(R.id.progressBar);

        // Инициализация SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    private void setupClickListeners() {
        // Кнопка изменения аватарки
        findViewById(R.id.fabEditPhoto).setOnClickListener(v -> showImagePickerDialog());

        // Кнопка редактирования
        btnEdit.setOnClickListener(v -> enableEditMode());

        // Кнопка сохранения
        btnSave.setOnClickListener(v -> saveProfile());

        // Кнопка отмены
        btnCancel.setOnClickListener(v -> disableEditMode());

        // Кнопка медкарты
        btnMedicalCard.setOnClickListener(v -> openMedicalCard());

        // Кнопка выхода
        btnLogout.setOnClickListener(v -> logout());
    }

    // ========== РЕЖИМ РЕДАКТИРОВАНИЯ ==========
    private void enableEditMode() {
        isEditMode = true;
        setFieldsEnabled(true);

        btnEdit.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);

        // Показываем подсказки для редактирования
        showEditHints();
    }

    private void disableEditMode() {
        isEditMode = false;
        setFieldsEnabled(false);

        btnEdit.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);

        // Загружаем данные заново чтобы отменить изменения
        loadUserData();
        hideEditHints();
    }

    private void setFieldsEnabled(boolean enabled) {
        etLastName.setEnabled(enabled);
        etFirstName.setEnabled(enabled);
        etMiddleName.setEnabled(enabled);
        etPhone.setEnabled(enabled);
        etEmail.setEnabled(enabled);
    }

    private void showEditHints() {
        tilLastName.setHint("Фамилия (редактирование)");
        tilFirstName.setHint("Имя (редактирование)");
        tilMiddleName.setHint("Отчество (редактирование)");
        tilPhone.setHint("Телефон (редактирование)");
        tilEmail.setHint("Email (редактирование)");
    }

    private void hideEditHints() {
        tilLastName.setHint("Фамилия");
        tilFirstName.setHint("Имя");
        tilMiddleName.setHint("Отчество");
        tilPhone.setHint("Номер телефона");
        tilEmail.setHint("Электронная почта");
    }

    // ========== ЗАГРУЗКА ДАННЫХ ==========
    private void loadUserData() {
        // Для тестирования используем локальные данные
        loadUserDataLocal();

        // Для реального использования раскомментируйте строку ниже
        // loadUserDataFromServer();
    }

    private void loadUserDataLocal() {
        // Локальные данные для тестирования
        etLastName.setText("Иванов");
        etFirstName.setText("Иван");
        etMiddleName.setText("Иванович");
        etPhone.setText("+7 (999) 123-45-67");
        etEmail.setText("ivanov@example.com");

        // Загрузка аватарки если есть
        loadLocalAvatar();
    }

    private void loadUserDataFromServer() {
        showLoading(true);

        new Thread(() -> {
            String accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
            String result = makeHttpRequest(PROFILE_URL, "GET", null, accessToken);

            runOnUiThread(() -> {
                showLoading(false);
                handleProfileResponse(result);
            });
        }).start();
    }

    private void handleProfileResponse(String result) {
        if (result.startsWith("Error:")) {
            Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            // Показываем локальные данные в случае ошибки
            loadUserDataLocal();
        } else {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                if (jsonResponse.getBoolean("success")) {
                    JSONObject userData = jsonResponse.getJSONObject("data");

                    etLastName.setText(userData.getString("last_name"));
                    etFirstName.setText(userData.getString("first_name"));
                    etMiddleName.setText(userData.getString("middle_name"));
                    etPhone.setText(userData.getString("phone"));
                    etEmail.setText(userData.getString("email"));

                    // Загрузка аватарки
                    if (userData.has("avatar_url")) {
                        loadAvatarFromUrl(userData.getString("avatar_url"));
                    }

                } else {
                    Toast.makeText(this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ========== СОХРАНЕНИЕ ДАННЫХ ==========
    private void saveProfile() {
        if (validateForm()) {
            // Для тестирования используем локальное сохранение
            saveProfileLocal();

            // Для реального использования раскомментируйте строку ниже
            // saveProfileToServer();
        }
    }

    private void saveProfileLocal() {
        showLoading(true);

        new Handler().postDelayed(() -> {
            showLoading(false);

            // Сохраняем данные локально
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("last_name", etLastName.getText().toString());
            editor.putString("first_name", etFirstName.getText().toString());
            editor.putString("middle_name", etMiddleName.getText().toString());
            editor.putString("phone", etPhone.getText().toString());
            editor.putString("email", etEmail.getText().toString());
            editor.apply();

            Toast.makeText(this, "Данные сохранены! (локальный режим)", Toast.LENGTH_SHORT).show();
            disableEditMode();
        }, 1500);
    }

    private void saveProfileToServer() {
        showLoading(true);

        JSONObject profileData = new JSONObject();
        try {
            profileData.put("last_name", etLastName.getText().toString());
            profileData.put("first_name", etFirstName.getText().toString());
            profileData.put("middle_name", etMiddleName.getText().toString());
            profileData.put("phone", etPhone.getText().toString());
            profileData.put("email", etEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        new Thread(() -> {
            String accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
            String result = makeHttpRequest(UPDATE_PROFILE_URL, "POST", profileData.toString(), accessToken);

            runOnUiThread(() -> {
                showLoading(false);
                handleSaveResponse(result);
            });
        }).start();
    }

    private void handleSaveResponse(String result) {
        if (result.startsWith("Error:")) {
            Toast.makeText(this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                if (jsonResponse.getBoolean("success")) {
                    Toast.makeText(this, "Данные успешно сохранены!", Toast.LENGTH_SHORT).show();
                    disableEditMode();
                } else {
                    Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ========== РАБОТА С АВАТАРКОЙ ==========
    private void showImagePickerDialog() {
        CharSequence[] options = {"Сделать фото", "Выбрать из галереи", "Отмена"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите аватарку");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    checkCameraPermission();
                    break;
                case 1:
                    checkGalleryPermission();
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSIONS);
        } else {
            openCamera();
        }
    }

    private void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        } else {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permissions[0].equals(Manifest.permission.CAMERA)) {
                    openCamera();
                } else if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openGallery();
                }
            } else {
                Toast.makeText(this, "Разрешение необходимо для выбора фото", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.sos_mobile.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    handleCameraImage();
                    break;
                case REQUEST_GALLERY:
                    handleGalleryImage(data);
                    break;
            }
        }
    }

    private void handleCameraImage() {
        if (currentPhotoPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (bitmap != null) {
                ivAvatar.setImageBitmap(bitmap);
                uploadAvatar(bitmap);
            }
        }
    }

    private void handleGalleryImage(Intent data) {
        if (data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                ivAvatar.setImageBitmap(bitmap);
                uploadAvatar(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadAvatar(Bitmap bitmap) {
        // Для тестирования сохраняем локально
        saveAvatarLocally(bitmap);

        // Для реального использования раскомментируйте:
        // uploadAvatarToServer(bitmap);
    }

    private void saveAvatarLocally(Bitmap bitmap) {
        // Сохраняем bitmap в SharedPreferences как base64 (для простоты демонстрации)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("avatar", encoded);
        editor.apply();

        Toast.makeText(this, "Аватарка сохранена!", Toast.LENGTH_SHORT).show();
    }

    private void loadLocalAvatar() {
        String encodedAvatar = sharedPreferences.getString("avatar", null);
        if (encodedAvatar != null) {
            byte[] decodedString = Base64.decode(encodedAvatar, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (decodedByte != null) {
                ivAvatar.setImageBitmap(decodedByte);
            }
        }
    }

    private void uploadAvatarToServer(Bitmap bitmap) {
        showLoading(true);

        new Thread(() -> {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                JSONObject avatarData = new JSONObject();
                avatarData.put("avatar", encodedImage);

                String accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
                String result = makeHttpRequest(UPLOAD_AVATAR_URL, "POST", avatarData.toString(), accessToken);

                runOnUiThread(() -> {
                    showLoading(false);
                    if (!result.startsWith("Error:")) {
                        Toast.makeText(this, "Аватарка обновлена!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Ошибка загрузки аватарки", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Ошибка обработки изображения", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========
    private boolean validateForm() {
        String lastName = etLastName.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (lastName.isEmpty()) {
            etLastName.setError("Введите фамилию");
            return false;
        }

        if (firstName.isEmpty()) {
            etFirstName.setError("Введите имя");
            return false;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Введите номер телефона");
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Введите email");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Введите корректный email");
            return false;
        }

        return true;
    }

    private void openMedicalCard() {
        Toast.makeText(this, "Переход к медкарте", Toast.LENGTH_SHORT).show();
         Intent intent = new Intent(this, MedCard.class);
         startActivity(intent);
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да", (dialog, which) -> performLogout())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void performLogout() {
        // Очищаем SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.putBoolean("is_logged_in", false);
        editor.apply();

        // Переходим на экран входа
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnEdit.setEnabled(!show);
        btnSave.setEnabled(!show);
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

            connection.setDoOutput(requestBody != null);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            if (requestBody != null && method.equals("POST")) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

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
                return "Error:" + responseCode;
            }

        } catch (IOException e) {
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

    private void loadAvatarFromUrl(String avatarUrl) {
        // Реализация загрузки аватарки по URL
        // Можно использовать Picasso или Glide
        Toast.makeText(this, "Загрузка аватарки с URL: " + avatarUrl, Toast.LENGTH_SHORT).show();
    }
}