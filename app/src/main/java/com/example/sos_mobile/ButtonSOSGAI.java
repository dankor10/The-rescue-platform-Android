package com.example.sos_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ButtonSOSGAI extends AppCompatActivity {

    private MaterialCardView emergencyButton;
    private ImageView wave1, wave2, wave3;
    private TextView countdownText;
    private MaterialButton cancelButton;
    private Button ButtonPI;
    private Handler handler = new Handler();
    private boolean isListening = false;
    private boolean isCallActive = false;
    private Runnable callRunnable;
    private int countdownTime = 5;

    // Элементы меню
    private MaterialCardView menuDropdown;
    private boolean isMenuOpen = false;

    // Элементы модального окна фото
    private MaterialCardView photoModal;
    private View photoModalOverlay;
    private MaterialButton btnTakePhoto;
    private MaterialButton btnChooseFromGallery;
    private MaterialButton btnCancelPhoto;

    // Константы для запросов
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 201;

    // Переменная для хранения пути к фото
    private String currentPhotoPath;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_sosgai);

        // Инициализация основных элементов
        emergencyButton = findViewById(R.id.emergencyButton);
        wave1 = findViewById(R.id.wave1);
        wave2 = findViewById(R.id.wave2);
        wave3 = findViewById(R.id.wave3);
        countdownText = findViewById(R.id.countdownText);
        cancelButton = findViewById(R.id.cancelButton);

        // Инициализация меню
        menuDropdown = findViewById(R.id.menuDropdown);

        // Инициализация модального окна фото
        photoModal = findViewById(R.id.photoModal);
        photoModalOverlay = findViewById(R.id.photoModalOverlay);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnChooseFromGallery = findViewById(R.id.btnChooseFromGallery);
        btnCancelPhoto = findViewById(R.id.btnCancelPhoto);

        setupWaveAnimations();
        setupMenuListeners();
        setupPhotoModalListeners();
        setupButtonListeners();
    }

    private void setupPhotoModalListeners() {
        // Кнопка "Сделать фото нарушителя"
        ButtonPI = findViewById(R.id.ButtonPI);
        ButtonPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpen) {
                    closeMenu();
                }
                showPhotoModal();
            }
        });

        // Кнопка сделать фото
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission()) {
                    takePhoto();
                }
            }
        });

        // Кнопка выбрать из галереи
        btnChooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStoragePermission()) {
                    chooseFromGallery();
                }
            }
        });

        // Кнопка отмены
        btnCancelPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePhotoModal();
            }
        });

        // Закрытие по клику на затемнение
        photoModalOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePhotoModal();
            }
        });
    }

    // Проверка разрешения для камеры
    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    // Проверка разрешения для хранилища
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Для Android 13+ используем READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                        STORAGE_PERMISSION_REQUEST_CODE);
                return false;
            }
        } else {
            // Для старых версий используем READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    // Обработка результатов запросов разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "Разрешение на использование камеры отклонено", Toast.LENGTH_SHORT).show();
                }
                break;

            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseFromGallery();
                } else {
                    Toast.makeText(this, "Разрешение на доступ к галерее отклонено", Toast.LENGTH_SHORT).show();
                    // Показываем диалог с объяснением
                    showStoragePermissionExplanation();
                }
                break;
        }
    }

    private void showStoragePermissionExplanation() {
        Toast.makeText(this,
                "Для выбора фото из галереи необходимо разрешение на доступ к хранилищу. " +
                        "Вы можете предоставить разрешение в настройках приложения.",
                Toast.LENGTH_LONG).show();
    }

    // Создание файла для фото
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Используем публичную директорию Pictures
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Создаем директорию если не существует
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void takePhoto() {
        hidePhotoModal();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Ошибка при создании файла", Toast.LENGTH_SHORT).show();
                return;
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.sos_mobile.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        } else {
            Toast.makeText(this, "Камера не доступна", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseFromGallery() {
        hidePhotoModal();

        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
        pickPhotoIntent.setType("image/*");
        pickPhotoIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Для Android 11+ используем новый интент
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pickPhotoIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            pickPhotoIntent.setType("image/*");
        }

        // Проверяем, есть ли приложение для обработки этого интента
        if (pickPhotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPhotoIntent, GALLERY_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Галерея не доступна", Toast.LENGTH_SHORT).show();
        }
    }

    // Обработка результатов от камеры и галереи
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    // Фото сделано камерой
                    handleCameraResult();
                    break;

                case GALLERY_REQUEST_CODE:
                    // Фото выбрано из галереи
                    handleGalleryResult(data);
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            // Пользователь отменил действие
            Toast.makeText(this, "Действие отменено", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCameraResult() {
        // currentPhotoPath содержит путь к сделанному фото
        if (currentPhotoPath != null) {
            File photoFile = new File(currentPhotoPath);
            if (photoFile.exists()) {
                Toast.makeText(this, "Фото успешно сохранено", Toast.LENGTH_LONG).show();
                showPhotoSuccessNotification("Фото нарушителя сохранено");

                // Здесь можно добавить дополнительную обработку фото
                // - Отображение preview
                // - Загрузка на сервер
                // - Сохранение в базе данных

            } else {
                Toast.makeText(this, "Фото не найдено", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Если currentPhotoPath null, значит фото сохранено в виде миниатюры
            Toast.makeText(this, "Фото сделано успешно", Toast.LENGTH_LONG).show();
            showPhotoSuccessNotification("Фото нарушителя сохранено");
        }
    }

    private void handleGalleryResult(Intent data) {
        if (data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            // Здесь можно обработать выбранное фото из галереи
            Toast.makeText(this, "Фото выбрано из галереи", Toast.LENGTH_LONG).show();
            showPhotoSuccessNotification("Фото выбрано из галереи");

            // Дополнительная обработка URI фото
            // - Получение реального пути к файлу
            // - Копирование файла
            // - Загрузка на сервер и т.д.

        } else {
            Toast.makeText(this, "Не удалось выбрать фото", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPhotoSuccessNotification(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Показываем красивый Toast
                Toast.makeText(ButtonSOSGAI.this, "✅ " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPhotoModal() {
        photoModal.setVisibility(View.VISIBLE);
        photoModalOverlay.setVisibility(View.VISIBLE);

        // Анимация появления
        photoModal.setAlpha(0f);
        photoModal.setScaleX(0.8f);
        photoModal.setScaleY(0.8f);

        photoModal.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void hidePhotoModal() {
        photoModal.animate()
                .alpha(0f)
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        photoModal.setVisibility(View.GONE);
                        photoModalOverlay.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    // Остальные методы без изменений...
    private void setupMenuListeners() {
        // Кнопка меню
        LinearLayout menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
            }
        });

        // Пункты меню
        LinearLayout menuEmergency = findViewById(R.id.menuEmergency);
        LinearLayout menuTrafficPolice = findViewById(R.id.menuTrafficPolice);
        LinearLayout menuAmbulance = findViewById(R.id.menuAmbulance);
        LinearLayout menuChildren = findViewById(R.id.menuChildren);
        LinearLayout menuHandbook = findViewById(R.id.menuHandbook);
        LinearLayout menuAccount = findViewById(R.id.menuAccount);

        menuEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
                Toast.makeText(ButtonSOSGAI.this, "Переход на страницу МЧС", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSGAI.this, sos.class);
                startActivity(intent);
            }
        });

        menuTrafficPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
                Toast.makeText(ButtonSOSGAI.this, "Переход на страницу МЧС", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSGAI.this, MHS.class);
                startActivity(intent);
            }
        });

        menuAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
                Toast.makeText(ButtonSOSGAI.this, "Переход на страницу Скорой помощи", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSGAI.this, ButtonSOSMed.class);
                startActivity(intent);
            }
        });

        menuChildren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
                Toast.makeText(ButtonSOSGAI.this, "Переход на страницу Мои дети", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSGAI.this, Cids.class);
                startActivity(intent);
            }
        });

        menuHandbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
                Toast.makeText(ButtonSOSGAI.this, "Переход в Справочник", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSGAI.this, Phone.class);
                startActivity(intent);
            }
        });

        menuAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
                Toast.makeText(ButtonSOSGAI.this, "Переход в профиль", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSGAI.this, Accaunt2.class);
                startActivity(intent);
            }
        });

        // Кнопка профиля
        TextView profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ButtonSOSGAI.this, "Переход в профиль", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSGAI.this, Accaunt2.class);
                startActivity(intent);
            }
        });

        // Закрытие меню при клике на основную кнопку
        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpen) {
                    closeMenu();
                }
                if (photoModal.getVisibility() == View.VISIBLE) {
                    hidePhotoModal();
                }
            }
        });

        // Закрытие меню при клике на кнопку "Написать заявление"
        MaterialButton writeStatementButton = findViewById(R.id.writeStatementButton);
        writeStatementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpen) {
                    closeMenu();
                }
                Toast.makeText(ButtonSOSGAI.this, "Переход к написанию заявления", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSGAI.this, ProtokolGAI.class);
                startActivity(intent);
            }
        });

        // Закрытие меню при клике на предупреждение
        TextView warningText = findViewById(R.id.warningText);
        warningText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpen) {
                    closeMenu();
                }
            }
        });
    }

    private void toggleMenu() {
        if (isMenuOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    private void openMenu() {
        menuDropdown.setVisibility(View.VISIBLE);
        isMenuOpen = true;
    }

    private void closeMenu() {
        menuDropdown.setVisibility(View.GONE);
        isMenuOpen = false;
    }

    private void setupButtonListeners() {
        emergencyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isCallActive && !isMenuOpen) {
                            startListening();
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (!isCallActive) {
                            stopListening();
                        }
                        return true;
                }
                return false;
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCall();
            }
        });
    }

    private void setupWaveAnimations() {
        wave1.setScaleX(0.8f);
        wave1.setScaleY(0.8f);
        wave1.setAlpha(0f);

        wave2.setScaleX(0.8f);
        wave2.setScaleY(0.8f);
        wave2.setAlpha(0f);

        wave3.setScaleX(0.8f);
        wave3.setScaleY(0.8f);
        wave3.setAlpha(0f);

        countdownText.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.GONE);
    }

    private void startListening() {
        isListening = true;
        emergencyButton.setCardBackgroundColor(ContextCompat.getColor(this, R.color.shazam_medium_gray));

        countdownText.setVisibility(View.VISIBLE);
        countdownTime = 5;
        updateCountdownText();

        cancelButton.setVisibility(View.GONE);

        callRunnable = new Runnable() {
            @Override
            public void run() {
                if (isListening) {
                    activateCall();
                }
            }
        };
        handler.postDelayed(callRunnable, 5000);

        startCountdown();
    }

    private void activateCall() {
        isCallActive = true;

        startContinuousWaveAnimation();
        showSuccessMessage();
        vibrate();
        cancelButton.setVisibility(View.VISIBLE);
        countdownText.setVisibility(View.INVISIBLE);
    }

    private void startCountdown() {
        final Runnable countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (isListening && countdownTime > 0 && !isCallActive) {
                    countdownTime--;
                    updateCountdownText();
                    if (countdownTime > 0) {
                        handler.postDelayed(this, 1000);
                    } else {
                        countdownText.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };
        handler.postDelayed(countdownRunnable, 1000);
    }

    private void updateCountdownText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countdownText.setText(String.valueOf(countdownTime));
            }
        });
    }

    private void stopListening() {
        if (!isCallActive) {
            isListening = false;
            emergencyButton.setCardBackgroundColor(ContextCompat.getColor(this, R.color.shazam_dark_gray));
            countdownText.setVisibility(View.INVISIBLE);

            if (callRunnable != null) {
                handler.removeCallbacks(callRunnable);
            }
        }
    }

    private void startContinuousWaveAnimation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startWaveCycle();
            }
        });
    }

    private void startWaveCycle() {
        wave1.animate()
                .scaleX(1.5f)
                .scaleY(1.5f)
                .alpha(1f)
                .setDuration(800)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        wave1.setScaleX(0.8f);
                        wave1.setScaleY(0.8f);
                        wave1.setAlpha(0f);
                    }
                });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isCallActive) {
                    wave2.animate()
                            .scaleX(1.5f)
                            .scaleY(1.5f)
                            .alpha(0.7f)
                            .setDuration(800)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    wave2.setScaleX(0.8f);
                                    wave2.setScaleY(0.8f);
                                    wave2.setAlpha(0f);
                                }
                            });
                }
            }
        }, 266);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isCallActive) {
                    wave3.animate()
                            .scaleX(1.5f)
                            .scaleY(1.5f)
                            .alpha(0.4f)
                            .setDuration(800)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    wave3.setScaleX(0.8f);
                                    wave3.setScaleY(0.8f);
                                    wave3.setAlpha(0f);
                                    if (isCallActive) {
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startWaveCycle();
                                            }
                                        }, 200);
                                    }
                                }
                            });
                }
            }
        }, 533);
    }

    private void stopWaveAnimation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wave1.animate().cancel();
                wave2.animate().cancel();
                wave3.animate().cancel();

                wave1.setScaleX(0.8f);
                wave1.setScaleY(0.8f);
                wave1.setAlpha(0f);

                wave2.setScaleX(0.8f);
                wave2.setScaleY(0.8f);
                wave2.setAlpha(0f);

                wave3.setScaleX(0.8f);
                wave3.setScaleY(0.8f);
                wave3.setAlpha(0f);
            }
        });
    }

    private void cancelCall() {
        isCallActive = false;
        isListening = false;

        stopWaveAnimation();
        cancelButton.setVisibility(View.GONE);
        emergencyButton.setCardBackgroundColor(ContextCompat.getColor(this, R.color.shazam_dark_gray));
        Toast.makeText(ButtonSOSGAI.this, "Вызов отменен", Toast.LENGTH_SHORT).show();

        if (callRunnable != null) {
            handler.removeCallbacks(callRunnable);
        }
    }

    private void showSuccessMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ButtonSOSGAI.this, "Вызов был успешно отправлен!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
} 