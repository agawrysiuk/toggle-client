package com.example.companytoggleclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

public class MainActivity extends AppCompatActivity{
    public static final String EXTRA_MESSAGE = "com.example.companytoggleclient.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @SuppressLint("SetTextI18n")
    public void logIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        EditText loginField = findViewById(R.id.loginText);
        EditText passwordField = findViewById(R.id.passwordText);
        String returnedMessage = sendMessage("LOGIN:" + loginField.getText() + ":" + passwordField.getText());
        TextView errorTextView = findViewById(R.id.errorTextView);
        if (returnedMessage == null) {
            errorTextView.setText("Network error while trying to log in.");
            return;
        }
        errorTextView.setText(returnedMessage.split(":")[0]);
        if (returnedMessage.contains("Successfully logged in")) {
            String message = loginField.getText().toString();
            intent.putExtra(EXTRA_MESSAGE, message + ":" + returnedMessage.split(":")[1]);
            startActivity(intent);
        }
    }

    public static String sendMessage(String message) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("10.0.2.2", 5777), 5000);
            PrintWriter clientSender = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader clientReceiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientSender.println(message + ":" + new Random().nextInt());
            String returnedString = clientReceiver.readLine();
            socket.close();
            clientReceiver.close();
            clientSender.close();
            return returnedString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
