package com.example.sos_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegistrCids extends AppCompatActivity {

    private EditText phoneNumberEditTextqwer;
    private Button registerButtonqwert;
    private static final String TAG = "MainActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registr_cids);

        phoneNumberEditTextqwer = findViewById(R.id.phoneNumberEditTextqwer);
        registerButtonqwert = findViewById(R.id.registerButtonqwert);

        registerButtonqwert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEditTextqwer.getText().toString().trim();
                if (!phoneNumber.isEmpty()) {
                    showSmsCodeDialog();
                } else {
                    Toast.makeText(RegistrCids.this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showSmsCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        builder.setTitle("Введите код из СМС");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String smsCode = input.getText().toString().trim();
                if (!smsCode.isEmpty()) {
                    sendSmsCodeToServer(smsCode);
                } else {
                    Toast.makeText(RegistrCids.this, "Введите код из СМС", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void sendSmsCodeToServer(String smsCode) {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.sos.lectoria.by/auth/api/auth/login");  //TODO поменяить URL
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                String data = "{\"smsCode\":\"" + smsCode + "\"}";
                try (OutputStream os = urlConnection.getOutputStream()) {
                    os.write(data.getBytes());
                    os.flush();
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "SMS Code sent successfully: " + data);
                } else {
                    Log.e(TAG, "Failed to send SMS Code: " + data);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error sending SMS Code: ", e);
                e.printStackTrace();
            }
        }).start();
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