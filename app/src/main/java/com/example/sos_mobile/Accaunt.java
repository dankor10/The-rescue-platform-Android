package com.example.sos_mobile;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Accaunt extends AppCompatActivity {

    private TextView textViewName, textViewPassword, textViewEmail, textViewLogin, textViewSurname;
    private static final String BASE_URL = "https://api.sos.lectoria.by/user-management/api/users/profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accaunt);

        textViewName = findViewById(R.id.textViewName);
        textViewSurname = findViewById(R.id.textViewSurname);
        textViewLogin = findViewById(R.id.textViewLogin);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPassword = findViewById(R.id.textViewPassword);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        fetchAccountData();
    }

    private void fetchAccountData() {
        String urlString = "https://api.sos.lectoria.by/user-management/api/users/profile";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { // OK
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                char[] buffer = new char[1024];
                int length;
                while ((length = reader.read(buffer)) != -1) {
                    response.append(buffer, 0, length);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                String name = jsonObject.getString("name");
                String surname = jsonObject.getString("surname");
                String login = jsonObject.getString("login");
                String email = jsonObject.getString("email");
                String password = jsonObject.getString("password");

                runOnUiThread(() -> {
                    textViewName.setText(name);
                    textViewSurname.setText(surname);
                    textViewLogin.setText(login);
                    textViewEmail.setText(email);
                    textViewPassword.setText(password);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

