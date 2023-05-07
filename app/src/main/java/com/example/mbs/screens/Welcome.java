package com.example.mbs.screens;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mbs.R;
import com.example.mbs.database.User;

public class Welcome extends AppCompatActivity {
    TextView message;
    String username;
    User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String username = getIntent().getStringExtra("username");

        message = findViewById(R.id.textView2);

    }
}
