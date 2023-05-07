package com.example.mbs.screens;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mbs.R;
import com.example.mbs.database.Announcements;
import com.example.mbs.database.Database;
import com.example.mbs.database.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;


public class AddAnnouncements extends AppCompatActivity {
    public static final int GALLERY_REQUEST_CODE = 103;
    public static final int GALLERY_PERM_CODE = 104;
    Database db = new Database();
    String author;
    Uri currentImgUri;
    ImageView imageView;
    ImageButton imageButton;
    EditText header, text;
    Button send;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ann);

        imageView = findViewById(R.id.iv_img);
        imageButton = findViewById(R.id.ib_img);
        header = findViewById(R.id.et_header);
        text = findViewById(R.id.et_ann);
        send = findViewById(R.id.btn_send);

        int id = getIntent().getIntExtra("annId", 0);

        db.getUserInfo(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        author = user.getFullName();
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Bir şeyler ters gitti.",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String str_header = header.getText().toString();
                    String str_text = text.getText().toString();
                    Announcements ann = new Announcements("announcements/", str_header, author, str_text);
                    ann.setId(String.valueOf(id));

                    if(currentImgUri != null)
                        db.addAnnouncement(ann, currentImgUri);
                    else
                        db.addAnnouncement(ann);

                    Toast.makeText(getApplicationContext(), "Kaydedildi.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                catch (Exception e_null){
                    Toast.makeText(AddAnnouncements.this, "Lütfen zorunlu bilgileri eksiksiz doldurun.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
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
            currentImgUri = data.getData();
            imageView.setImageURI(currentImgUri);
        }
        else
            Toast.makeText(this, "Bir şeyler ters gitti. Lütfen tekrar deneyiniz.",
                    Toast.LENGTH_SHORT).show();
    }
}
