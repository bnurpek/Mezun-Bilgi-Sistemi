package com.example.mbs.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbs.R;
import com.example.mbs.adapter.ShowMediaAdapter;
import com.example.mbs.database.Database;
import com.example.mbs.database.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    Database db = new Database();
    ImageView profile;
    RecyclerView mRecyclerView;
    TextView fullname, notFound;
    TextView lisansStart, lisansGraduate;
    TextView yüksek, yüksekStart, yüksekGraduate;
    TextView doktora, doktoraStart, doktoraGraduate;
    TextView work, country, city;
    ImageButton phone, mail, more;
    String str_phone, str_mail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String userID = getIntent().getStringExtra("userID");

        phone = findViewById(R.id.ib_tel);
        mail = findViewById(R.id.ib_email);
        fullname = findViewById(R.id.tv_full_name);
        profile = findViewById(R.id.iv_profile);
        lisansStart = findViewById(R.id.tv_start_date);
        lisansGraduate = findViewById(R.id.tv_grad_date);
        yüksek = findViewById(R.id.tv_yüksek_uni);
        yüksekStart = findViewById(R.id.tv_yüksek_start);
        yüksekGraduate = findViewById(R.id.tv_yüksek_grad);
        doktora = findViewById(R.id.tv_doktora_uni);
        doktoraStart = findViewById(R.id.tv_doktora_start);
        doktoraGraduate = findViewById(R.id.tv_doktora_grad);
        work = findViewById(R.id.tv_comp_name);
        country = findViewById(R.id.tv_country);
        city = findViewById(R.id.tv_city);

        db.getUserInfo(userID, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                db.getImageUri(user.getProfilePhotoUrl(), new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profile);
                        str_mail = user.getEmail();
                        str_phone = user.getPhone();
                        fullname.setText(user.getFullName());
                        lisansStart.setText("Giriş Tarihi: " + user.getStart().toString());
                        lisansGraduate.setText("Mezuniyet Tarihi: " + user.getGraduate().toString());
                        yüksek.setText("Üniversite Adı: " + user.getYüksek());
                        if (user.getStartYüksek() != null)
                            yüksekStart.setText("Giriş Tarihi: " + user.getStartYüksek().toString());
                        if (user.getGradYüksek() != null)
                            yüksekGraduate.setText("Mezuniyet Tarihi: " + user.getGradYüksek().toString());
                        doktora.setText("Üniversite Adı: " + user.getDoktora());
                        if (user.getStartDoktora() != null)
                            doktoraStart.setText("Giriş Tarihi: " + user.getStartDoktora().toString());
                        if (user.getGradDoktora() != null)
                            doktoraGraduate.setText("Mezuniyet Tarihi: " + user.getGradDoktora().toString());
                        work.setText("Üniversite Adı: " + user.getComp());
                        country.setText("Ülke: " + user.getCountry());
                        city.setText("Şehir: " + user.getCity());

                        if(!user.getPhoneStatue()) phone.setVisibility(View.INVISIBLE);
                        if(!user.getMailStatue()) mail.setVisibility(View.INVISIBLE);

                        db.getUserMedia(userID, new OnSuccessListener<ArrayList<Uri>>() {
                            @Override
                            public void onSuccess(ArrayList<Uri> uris) {
                                if (uris.size() > 0) {
                                    ShowMediaAdapter adapter = new ShowMediaAdapter(
                                            new ArrayList<Uri>(uris.subList(0, Math.min(uris.size(),3))), Profile.this);
                                    LinearLayoutManager linearLayoutManager =
                                            new LinearLayoutManager(Profile.this,
                                                    RecyclerView.HORIZONTAL, false);

                                    mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                                    more = findViewById(R.id.ib_more);
                                    notFound = findViewById(R.id.tv_not);
                                    notFound.setVisibility(View.INVISIBLE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    more.setVisibility(View.VISIBLE);
                                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                    mRecyclerView.setLayoutManager(linearLayoutManager);
                                    mRecyclerView.setAdapter(adapter);

                                    more.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getApplicationContext(), Medias.class);
                                            intent.putExtra("dataset", uris);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Kullanıcı medyasına erişilemedi.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        phone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("sms:" + str_phone));
                                startActivity(Intent.createChooser(intent, "Bir uygulama seçiniz."));
                            }
                        });

                        mail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:" + str_mail));
                                startActivity(Intent.createChooser(intent, "Bir uygulama seçiniz."));
                            }
                        });

                    }
                });
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Kullanıcıya erişilemedi.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
