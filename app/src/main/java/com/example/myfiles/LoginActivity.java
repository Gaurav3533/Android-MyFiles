package com.example.myfiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfiles.Fragments.HomeFragment;

public class LoginActivity extends AppCompatActivity {
EditText edtEmail, edtPassword;
Button loginButton;
DatabaseHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtEmail = findViewById(R.id.editTextLoginEmail);
        edtPassword = findViewById(R.id.editTextLoginPassword);
        loginButton = findViewById(R.id.cirLoginButton);
        myDb = new DatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String passwordLogin = edtPassword.getText().toString();

                if (email.equals("") || passwordLogin.equals("")){
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

                }else {
                    Boolean resultData = myDb.checkData(email,passwordLogin);
                    if (resultData == true){
                        //it means user is valid, then move to homeFragment
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Invalid user credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);

    }
}