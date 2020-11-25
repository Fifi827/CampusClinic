package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button btnLogin, btnRegister;
    EditText ed_loginEmail, ed_loginPassword;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed_loginEmail = findViewById(R.id.ed_loginEmail);
        ed_loginPassword = findViewById(R.id.ed_loginPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ed_loginEmail.getText().toString().trim();
                String pass = ed_loginPassword.getText().toString().trim();

                if(email.isEmpty() || pass.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please enter login details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                startActivity(new Intent(MainActivity.this, AdminHome.class));
                                Toast.makeText(MainActivity.this, "login successful", Toast.LENGTH_SHORT).show();
                                MainActivity.this.finish();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });
    }
}