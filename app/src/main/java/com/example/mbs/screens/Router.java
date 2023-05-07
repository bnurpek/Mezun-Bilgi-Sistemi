package com.example.mbs.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mbs.R;
import com.google.firebase.auth.FirebaseAuth;

public class Router extends AppCompatActivity {
    ImageButton profile, search, announcements, media;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router);

        profile = findViewById(R.id.iBtn_myProfile);
        search = findViewById(R.id.iBtn_search);
        announcements = findViewById(R.id.iBtn_announcements);
        media = findViewById(R.id.iBtn_media);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchResult.class);
                startActivity(intent);
            }
        });

        announcements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Announcements.class);
                startActivity(intent);
            }
        });

        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Medias.class);
                intent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
            }
        });

    }
}
