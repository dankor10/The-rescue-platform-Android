package com.example.sos_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ButtonSOSMed extends AppCompatActivity {

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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_sosmed);

        // Инициализация основных элементов
        emergencyButton = findViewById(R.id.emergencyButton);
        wave1 = findViewById(R.id.wave1);
        wave2 = findViewById(R.id.wave2);
        wave3 = findViewById(R.id.wave3);
        countdownText = findViewById(R.id.countdownText);
        cancelButton = findViewById(R.id.cancelButton);

        // Инициализация меню - исправлено на MaterialCardView
        menuDropdown = findViewById(R.id.menuDropdown);

        setupWaveAnimations();
        setupMenuListeners();
        setupButtonListeners();
    }

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
                Toast.makeText(ButtonSOSMed.this, "Переход на страницу МЧС", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSMed.this, sos.class);
                startActivity(intent);
            }
        });

        menuTrafficPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
                Toast.makeText(ButtonSOSMed.this, "Переход на страницу МЧС", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSMed.this, MHS.class);
                startActivity(intent);
            }
        });

        menuAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
                Toast.makeText(ButtonSOSMed.this, "Переход на страницу ГАИ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSMed.this, ButtonSOSGAI.class);
                startActivity(intent);
            }
        });

        menuChildren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
                Toast.makeText(ButtonSOSMed.this, "Переход на страницу Мои дети", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSMed.this, Cids.class);
                startActivity(intent);
            }
        });

        menuHandbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
                Toast.makeText(ButtonSOSMed.this, "Переход в Справочник", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSMed.this, Phone.class);
                startActivity(intent);
            }
        });

        // Кнопка профиля
        TextView profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ButtonSOSMed.this, "Переход в профиль", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSMed.this, Accaunt2.class);
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
                Toast.makeText(ButtonSOSMed.this, "Переход к заполнению обращения", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ButtonSOSMed.this, ProtokolMed.class);
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
        Toast.makeText(ButtonSOSMed.this, "Вызов отменен", Toast.LENGTH_SHORT).show();

        if (callRunnable != null) {
            handler.removeCallbacks(callRunnable);
        }
    }

    private void showSuccessMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ButtonSOSMed.this, "Вызов был успешно отправлен!", Toast.LENGTH_LONG).show();
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