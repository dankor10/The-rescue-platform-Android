package com.example.sos_mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ButtonChildSOS extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private TextView locationTextView;
    private ImageView helpButton;
    private static final String TAG = "MainActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_child_sos);

        locationTextView = findViewById(R.id.locationTextView);
        helpButton = findViewById(R.id.helpButton);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Инициализируйте locationCallback сразу
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocationUI(location);
                    sendLocationToServer(location);
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHelpMessageToServer();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000); // 1 минута
        locationRequest.setFastestInterval(30000); // 30 секунд
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Убедитесь, что locationCallback инициализирован
        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        updateLocationUI(location);
                        sendLocationToServer(location);
                    }
                }
            };
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, new Handler().getLooper());
    }

    private void updateLocationUI(Location location) {
        runOnUiThread(() -> locationTextView.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude()));
    }

    private void sendLocationToServer(Location location) {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.sos.lectoria.by/auth/api/auth/login"); //TODO поменять URL
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                String locationData = "{\"latitude\":\"" + location.getLatitude() + "\",\"longitude\":\"" + location.getLongitude() + "\"}";
                try (OutputStream os = urlConnection.getOutputStream()) {
                    os.write(locationData.getBytes());
                    os.flush();
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Location sent successfully: " + locationData);
                } else {
                    Log.e(TAG, "Failed to send location: " + locationData);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error sending location: ", e);
                e.printStackTrace();
            }
        }).start();
    }

    private void sendHelpMessageToServer() {
        new Thread(() -> {
            try {
                URL url = new URL("https://example.com/help");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                String helpMessage = "{\"message\":\"Мне нужна помощь\"}";
                try (OutputStream os = urlConnection.getOutputStream()) {
                    os.write(helpMessage.getBytes());
                    os.flush();
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Help message sent successfully: " + helpMessage);
                } else {
                    Log.e(TAG, "Failed to send help message: " + helpMessage);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error sending help message: ", e);
                e.printStackTrace();
            }
        }).start();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                // Разрешение не предоставлено
            }
        }
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