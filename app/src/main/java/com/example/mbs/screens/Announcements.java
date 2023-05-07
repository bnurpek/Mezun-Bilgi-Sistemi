package com.example.mbs.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbs.R;
import com.example.mbs.adapter.AnnouncementsAdapter;
import com.example.mbs.database.Database;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Announcements extends AppCompatActivity {
    Database db = new Database();
    int count;
    TextView title;
    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        title = findViewById(R.id.tv_title);
        title.setText("Haberler");

        add = findViewById(R.id.floating_button);

        db.getAllAnnouncements(new OnSuccessListener<ArrayList<com.example.mbs.database.Announcements>>() {
            @Override
            public void onSuccess(ArrayList<com.example.mbs.database.Announcements> announcements) {
                AnnouncementsAdapter adapter = new AnnouncementsAdapter(announcements, Announcements.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Announcements.this,
                        RecyclerView.VERTICAL, false);

                RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
                count = announcements.size();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(Announcements.this, "There is an error happened.", Toast.LENGTH_SHORT).show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddAnnouncements.class);
                intent.putExtra("annId", count);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.getAllAnnouncements(new OnSuccessListener<ArrayList<com.example.mbs.database.Announcements>>() {
            @Override
            public void onSuccess(ArrayList<com.example.mbs.database.Announcements> announcements) {
                AnnouncementsAdapter adapter = new AnnouncementsAdapter(announcements, Announcements.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Announcements.this,
                        RecyclerView.VERTICAL, false);

                RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
                count = announcements.size();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(Announcements.this, "There is an error happened.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
