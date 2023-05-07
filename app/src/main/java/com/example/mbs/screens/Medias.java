package com.example.mbs.screens;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbs.R;
import com.example.mbs.adapter.ShowMediaAdapter;
import com.example.mbs.database.Database;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Medias extends AppCompatActivity {
    public static final int GALLERY_REQUEST_CODE = 103;
    public static final int GALLERY_PERM_CODE = 104;
    Database db = new Database();
    String authID, userID;
    FloatingActionButton add;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medias);
        add = findViewById(R.id.floating_button);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        userID = getIntent().getStringExtra("userID");

        if(userID != null){
            add.setVisibility(View.VISIBLE);
            db.getUserMedia(userID, new OnSuccessListener<ArrayList<Uri>>() {
                @Override
                public void onSuccess(ArrayList<Uri> uris) {
                    if (uris.size() > 0){
                        ShowMediaAdapter adapter = new ShowMediaAdapter(uris, Medias.this);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(Medias.this,
                                3, GridLayoutManager.VERTICAL, false);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mRecyclerView.setLayoutManager(gridLayoutManager);
                        mRecyclerView.setAdapter(adapter);
                        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                            @Override
                            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                                View item = rv.findChildViewUnder(e.getX(), e.getY());
                                if(item != null) {
                                    Uri uri = uris.get(rv.getChildAdapterPosition(item));
                                    if(uri != null){
                                        Intent intent = new Intent(getApplicationContext(), Media.class);
                                        intent.setData(uri);
                                        intent.putExtra("visibility", add.getVisibility());
                                        intent.putExtra("userID", userID);
                                        startActivity(intent);
                                    };
                                }
                                return true;
                            }

                            @Override
                            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

                            @Override
                            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
                        });}}
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Medias.this, "Medya bulunamadı.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            ArrayList<Uri> uris = getIntent().getParcelableArrayListExtra("dataset");
            if (uris.size() > 0){
                ShowMediaAdapter adapter = new ShowMediaAdapter(uris, Medias.this);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(Medias.this,
                        3, GridLayoutManager.VERTICAL, false);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setLayoutManager(gridLayoutManager);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                        View item = rv.findChildViewUnder(e.getX(), e.getY());
                        if(item != null) {
                            Uri uri = uris.get(rv.getChildAdapterPosition(item));
                            if(uri != null){
                                Intent intent = new Intent(getApplicationContext(), Media.class);
                                intent.setData(uri);
                                intent.putExtra("visibility", add.getVisibility());
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                            };
                        }
                        return true;
                    }
                    @Override
                    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}
                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
                });
            }
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissions(GALLERY_REQUEST_CODE);
            }
        });
    }

    private void verifyPermissions(int request_code){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED){
            Intent imageGallery = new Intent(Intent.ACTION_PICK);
            imageGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(imageGallery, GALLERY_REQUEST_CODE);
        }
        else{
            ActivityCompat.requestPermissions(this, permissions, GALLERY_PERM_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions, grantResults);
        if(requestCode == GALLERY_PERM_CODE){
            Toast.makeText(this, "Dosya izni reddedildi.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri currentImgUri = data.getData();
            db.addUserMedia(userID, currentImgUri, new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean b) {
                    if(b.equals(Boolean.TRUE))
                        recreate();
                    else
                        Toast.makeText(Medias.this, "Bir şeyler ters gitti. Lütfen tekrar deneyiniz.",
                                Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
            Toast.makeText(this, "Bir şeyler ters gitti. Lütfen tekrar deneyiniz.",
                    Toast.LENGTH_SHORT).show();
    }
}
