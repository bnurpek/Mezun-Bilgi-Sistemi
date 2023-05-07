package com.example.mbs.screens;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.mbs.R;
import com.example.mbs.database.Database;
import com.example.mbs.database.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MyProfile extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String email = user.getEmail();
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 103;
    public static final int GALLERY_PERM_CODE = 104;
    String storagePhotoPath;
    String currentPhotoPath;
    Uri currentImgUri;
    Database db = new Database();
    ImageButton gallery, camera;
    ImageView profile;

    TextView fullname;

    EditText mail, phone, lisansStart, lisansGraduate;
    EditText yüksek, yüksekStart, yüksekGraduate;
    EditText doktora, doktoraStart, doktoraGraduate;
    EditText work, country, city;

    CheckBox telCheck, mailCheck;

    Button save;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        fullname = findViewById(R.id.tv_full_name);
        profile = findViewById(R.id.iv_profile);
        mail = findViewById(R.id.et_mail);
        phone = findViewById(R.id.et_tel);
        lisansStart = findViewById(R.id.et_start_date);
        lisansGraduate = findViewById(R.id.et_grad_date);
        yüksek = findViewById(R.id.et_yüksek_uni);
        yüksekStart = findViewById(R.id.et_yüksek_start);
        yüksekGraduate = findViewById(R.id.et_yüksek_grad);
        doktora = findViewById(R.id.et_doktora_uni);
        doktoraStart = findViewById(R.id.et_doktora_start);
        doktoraGraduate = findViewById(R.id.et_doktora_grad);
        work = findViewById(R.id.et_comp_name);
        country = findViewById(R.id.et_country);
        city = findViewById(R.id.et_city);
        telCheck = findViewById(R.id.cb_tel);
        mailCheck = findViewById(R.id.cb_mail);

        db.getUserInfo(user.getUid(), new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                storagePhotoPath = user.getProfilePhotoUrl();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                storageRef.child(storagePhotoPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profile);
                        currentImgUri = uri;
                        fullname.setText(user.getFullName());
                        mail.setText(email);
                        lisansStart.setText(user.getStart());
                        lisansGraduate.setText(user.getGraduate());
                        yüksek.setText(user.getYüksek());
                        doktora.setText(user.getDoktora());
                        work.setText(user.getComp());
                        country.setText(user.getCountry());
                        city.setText(user.getCity());
                        phone.setText(user.getPhone());
                        yüksekStart.setText(user.getStartYüksek());
                        yüksekGraduate.setText(user.getGradYüksek());
                        doktoraStart.setText(user.getStartDoktora());
                        doktoraGraduate.setText(user.getGradDoktora());
                        telCheck.setChecked(user.getPhoneStatue());
                        mailCheck.setChecked(user.getMailStatue());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Kullanıcı bilgilerine erişilemedi.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Kullanıcı bilgilerine erişilemedi.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        gallery = findViewById(R.id.ib_file);
        camera = findViewById(R.id.ib_camera);


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

        save = findViewById(R.id.btn_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String[] data = new String[14];
                    data[0] = mail.getText().toString().trim();
                    data[1] = phone.getText().toString().trim();
                    data[2] = lisansStart.getText().toString().trim();
                    data[3] = lisansGraduate.getText().toString().trim();
                    if(data[0].isEmpty() || data[1].isEmpty() || data[2].isEmpty()
                            || data[3].isEmpty() || currentImgUri == null){
                        Toast.makeText(MyProfile.this, "Lütfen zorunlu bilgileri eksiksiz doldurun.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        data[4] = yüksek.getText().toString().trim();
                        data[5] = yüksekStart.getText().toString().trim();
                        data[6] = yüksekGraduate.getText().toString().trim();

                        data[7] = doktora.getText().toString().trim();
                        data[8] = doktoraStart.getText().toString().trim();
                        data[9] = doktoraGraduate.getText().toString().trim();

                        data[10] = work.getText().toString().trim();
                        data[11] = country.getText().toString().trim();
                        data[12] = city.getText().toString().trim();
                        data[13] = fullname.getText().toString().trim();
                        boolean tel = telCheck.isChecked();
                        boolean mail = mailCheck.isChecked();
                        if(currentPhotoPath != null)
                            db.saveUserInfos(currentImgUri, data, tel, mail ,MyProfile.this);
                        else
                            db.saveUserInfos(data, tel, mail, MyProfile.this);
                    }
                }
                catch (NullPointerException e_null){
                    Toast.makeText(MyProfile.this, "Lütfen tüm bilgileri doldurun.", Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }
            else{
                Toast.makeText(this, "Dosya izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
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
            currentPhotoPath = data.getData().getPath();
            currentImgUri = data.getData();
            profile.setImageURI(currentImgUri);
        }
        else
            Toast.makeText(this, "Bir şeyler ters gitti. Lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
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
