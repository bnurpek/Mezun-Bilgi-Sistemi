package com.example.mbs.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mbs.R;
import com.example.mbs.database.Database;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class Media extends AppCompatActivity {
    Database db = new Database();
    ImageView imageView;
    FloatingActionButton button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        imageView = findViewById(R.id.iv_img);
        button = findViewById(R.id.floating_button);
        Uri uri = getIntent().getData();
        int visibility = getIntent().getIntExtra("visibility", 4);
        Picasso.get().load(uri).into(imageView);

        button.setVisibility(visibility);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Media.this)
                        .setTitle("Silmeyi onaylıyor musunuz?")
                        .setMessage("Silme işlemi geri alınamaz.")
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteImageFromStorage(uri.getLastPathSegment(), new OnSuccessListener<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        if (aBoolean){
                                            Toast.makeText(getApplicationContext(),
                                                    "Medya başarıyla silindi.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                        else
                                            Toast.makeText(getApplicationContext(), "Medya silinemedi.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("Hayır", null)
                        .show();
            }
        });

    }
}
