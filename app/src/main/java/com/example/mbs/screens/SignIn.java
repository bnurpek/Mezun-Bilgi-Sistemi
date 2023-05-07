package com.example.mbs.screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mbs.R;
import com.example.mbs.database.Database;

public class SignIn extends AppCompatActivity {
    Database db = new Database();
    EditText email, password;
    TextView forget;
    Button login, sign_up;
    Switch show_hide;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        forget = findViewById(R.id.tv_pass_change);

        login = findViewById(R.id.btn_login);
        sign_up = findViewById(R.id.btn_sign);
        show_hide = findViewById(R.id.show_hide);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String entered_email = email.getText().toString();
                String entered_password = password.getText().toString();
                try{
                    entered_email = email.getText().toString();
                    entered_password = password.getText().toString();
                    db.emailPasswordAuthentication(entered_email, entered_password, SignIn.this);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Please fill all the blank.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

        show_hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                else
                    password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(intent);
            }
        });
    }

}
