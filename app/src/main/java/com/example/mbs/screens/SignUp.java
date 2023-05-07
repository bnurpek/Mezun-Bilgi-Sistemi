package com.example.mbs.screens;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.mbs.R;
import com.example.mbs.database.Database;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SignUp extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 103;
    public static final int GALLERY_PERM_CODE = 104;
    String currentPhotoPath;
    Uri currentImgUri;
    Database db = new Database();
    Button sign_up;
    ImageButton gallery, camera;
    ImageView profile;
    EditText name, surname, mail, pass, start_date, graduate_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sign_up = findViewById(R.id.btn_sign);
        gallery = findViewById(R.id.ib_file);
        camera = findViewById(R.id.ib_camera);

        profile = findViewById(R.id.iv_profile);
        name = findViewById(R.id.et_name);
        surname = findViewById(R.id.et_surname);
        start_date = findViewById(R.id.et_start_date);
        graduate_date = findViewById(R.id.et_end_date);
        mail = findViewById(R.id.et_email);
        pass = findViewById(R.id.et_password);


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentDate = Calendar.getInstance();
                int year = currentDate.get(Calendar.YEAR);
                int month = currentDate.get(Calendar.MONTH);
                int day = currentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(SignUp.this,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String selectedDate = String.format(Locale.getDefault(),
                                "%02d/%02d/%d", day, month+1, year);
                        start_date.setText(selectedDate);
                    }
                }, year, month, day);
                datePicker.show();
            }
        });

        graduate_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentDate = Calendar.getInstance();
                int year = currentDate.get(Calendar.YEAR);
                int month = currentDate.get(Calendar.MONTH);
                int day = currentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(SignUp.this,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String selectedDate = String.format(Locale.getDefault(),
                                "%02d/%02d/%d", day, month+1, year);
                        graduate_date.setText(selectedDate);
                    }
                }, year, month, day);
                datePicker.show();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissions(CAMERA_REQUEST_CODE);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissions(GALLERY_REQUEST_CODE);
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String str_name = name.getText().toString();
                    String str_surname = surname.getText().toString();
                    String str_mail = mail.getText().toString();
                    String str_password = pass.getText().toString();
                    String str_start_date = start_date.getText().toString();
                    String str_end_date = graduate_date.getText().toString();

                    if(currentImgUri != null)
                        db.registerNewUser(currentImgUri, str_mail, str_password,
                                str_name+" "+str_surname, str_start_date, str_end_date,
                                SignUp.this);
                    else
                        Toast.makeText(SignUp.this, "Lütfen bir profil fotoğrafı seçin.",
                                Toast.LENGTH_SHORT).show();
                }
                catch (Exception e_null){
                    Toast.makeText(SignUp.this, "Lütfen tüm bilgileri eksiksiz doldurun.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void verifyPermissions(int request_code){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA};

        if(request_code == GALLERY_REQUEST_CODE){
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
        else if(request_code == CAMERA_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    permissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    permissions[1]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    permissions[2]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    permissions[3]) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }
            else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        CAMERA_PERM_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions, grantResults);
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }
            else{
                Toast.makeText(this, "Kamera izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == GALLERY_PERM_CODE){
            Toast.makeText(this, "Dosya izni reddedildi.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            File file = new File(currentPhotoPath);
            currentImgUri = Uri.fromFile(file);
            profile.setImageURI(currentImgUri);
        }
        else if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            currentImgUri = data.getData();
            profile.setImageURI(currentImgUri);
        }
        else
            Toast.makeText(this, "Bir şeyler ters gitti. Lütfen tekrar deneyiniz.",
                    Toast.LENGTH_SHORT).show();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MBSimage_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
}
