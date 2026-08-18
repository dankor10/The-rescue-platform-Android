package com.example.sos_mobile;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ButtonSOS extends AppCompatActivity {

    private static final String TAG = "GeoLocationApp";
    private static final String BASE_URL = "https://api.sos.lectoria.by/auth/api/auth/login";
    static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private ImageView imageViewMenu, imageViewBackSoS ,imageViewMenuBack , StarImage;
    private Button stopButton , zayava;
    private TextView textViewMHS , textViewAccount , textViewPhone , textViewCids , textViewAccaunt,
            textViewZaivlenie , textViewSOSGAI , textViewSOSMED;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_sos);

//        imageViewMenu = findViewById(R.id.imageViewMenu);
//        imageViewBackSoS = findViewById(R.id.imageViewBackSoS);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //StarImage = findViewById(R.id.StarImage);
        //stopButton = findViewById(R.id.stop_button);
//        imageViewMenuBack = findViewById(R.id.imageViewMenuBack);
//        textViewMHS = findViewById(R.id.textViewMHS);
//        textViewAccount = findViewById(R.id.textViewAccount);
//        textViewPhone = findViewById(R.id.textViewPhone);
//        textViewCids = findViewById(R.id.textViewCids);
//        zayava = findViewById(R.id.zayava);
//        textViewSOSGAI = findViewById(R.id.textViewSOSGAI);
//        textViewSOSMED = findViewById(R.id.textViewSOSMED);

        StarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ButtonSOS.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ButtonSOS.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    startLocationUpdates();
                    stopButton.setVisibility(View.VISIBLE);
                    zayava.setVisibility(View.INVISIBLE);
                }
            }
        });

        imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewMenuBack.setVisibility(View.VISIBLE);
                textViewMHS.setVisibility(View.VISIBLE);
                textViewAccount.setVisibility(View.VISIBLE);
                textViewPhone.setVisibility(View.VISIBLE);
                textViewCids.setVisibility(View.VISIBLE);
                imageViewBackSoS.setVisibility(View.VISIBLE);
                textViewSOSGAI.setVisibility(View.VISIBLE);
                textViewSOSMED.setVisibility(View.VISIBLE);
            }
        });

        imageViewBackSoS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewMenuBack.setVisibility(View.INVISIBLE);
                textViewMHS.setVisibility(View.INVISIBLE);
                textViewAccount.setVisibility(View.INVISIBLE);
                textViewPhone.setVisibility(View.INVISIBLE);
                textViewCids.setVisibility(View.INVISIBLE);
                imageViewBackSoS.setVisibility(View.INVISIBLE);
                textViewSOSGAI.setVisibility(View.INVISIBLE);
                textViewSOSMED.setVisibility(View.INVISIBLE);
            }
        });

        textViewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonSOS.this, Accaunt.class);
                startActivity(intent);
            }
        });

        textViewCids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonSOS.this, Cids.class);
                startActivity(intent);
            }
        });

        textViewMHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonSOS.this, MHS.class);
                startActivity(intent);
            }
        });

        textViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonSOS.this, Phone.class);
                startActivity(intent);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();
                stopButton.setVisibility(View.INVISIBLE);
                zayava.setVisibility(View.VISIBLE);
            }
        });

        zayava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonSOS.this, Zaivlenie.class);
                startActivity(intent);
            }
        });

        textViewSOSGAI.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonSOS.this,ButtonSOSGAI.class);
                startActivity(intent);
            }
        });

        textViewSOSMED.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonSOS.this,ButtonSOSMed.class);
                startActivity(intent);
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.d(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
                        sendLocationToServer(location);
                    }
                }
             }
        };
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            Toast.makeText(this, "Вызов отправлен", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Toast.makeText(this, "Вызов отменен", Toast.LENGTH_SHORT).show();
    }

    private void sendLocationToServer(Location location) {
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", location.getLatitude());
            jsonObject.put("longitude", location.getLongitude());

            Log.d(TAG, "Request JSON: " + jsonObject.toString());

            RequestBody body = RequestBody.create(
                    jsonObject.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "Location request failed", e);
                    runOnUiThread(() -> Toast.makeText(ButtonSOS.this, "Failed to send location: " +
                            e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String responseData = response.body().string();
                    int responseCode = response.code();
                    Log.d(TAG, "Response Code: " + responseCode);
                    Log.d(TAG, "Response Data: " + responseData);

                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(ButtonSOS.this, "Успешно: " +
                                    responseData, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ButtonSOS.this, "Геолокация записана успешно ", Toast.LENGTH_SHORT).show();
                                    //  responseCode + " - " + responseData, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error creating location request", e);
            runOnUiThread(() -> Toast.makeText(ButtonSOS.this, "Error creating request: " +
                    e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
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