package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText ed_regFirstName, ed_regLastName, ed_regEmail, ed_contactNumber, ed_regPassword, ed_regConfirmPassword;
    RadioButton rb_regAdmin, rb_regDoc, rb_regPat;
    Button btn_regSubmit;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        ed_regFirstName = findViewById(R.id.ed_regName);
        ed_regLastName = findViewById(R.id.ed_regLastName);
        ed_regEmail = findViewById(R.id.ed_email);
        ed_regPassword = findViewById(R.id.ed_regPassword);
        ed_regConfirmPassword = findViewById(R.id.ed_regConfirmPassword);
        ed_contactNumber = findViewById(R.id.ed_contactNumber);

        rb_regAdmin = findViewById(R.id.rb_regAdmin);
        rb_regDoc = findViewById(R.id.rb_regDoctor);
        rb_regPat = findViewById(R.id.rb_regPatient);

        btn_regSubmit = findViewById(R.id.btn_regSubmit);

        btn_regSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String firstname = ed_regFirstName.getText().toString().trim();
                final String lastname = ed_regLastName.getText().toString().trim();
                final String email = ed_regEmail.getText().toString().trim();
                String password = ed_regPassword.getText().toString().trim();
                String passwordConfirm = ed_regConfirmPassword.getText().toString();

                if(firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() ||
                        ed_contactNumber.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(Register.this, "Some fields missing text", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(passwordConfirm))
                {
                    Toast.makeText(Register.this, "Passwords do not match!!!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String userId = firebaseUser.getUid();

                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                                User user = new User();
                                user.setUserFirstName(firstname);
                                user.setUserLastName(lastname);
                                user.setEmail(email);
                                user.setUserID(userId);
                                user.setContactNumber(ed_contactNumber.getText().toString().trim());

                                if(rb_regAdmin.isChecked())
                                {
                                    user.setUserRole("Admin");
                                }
                                else if(rb_regDoc.isChecked())
                                {
                                    user.setUserRole("Doctor");
                                }
                                else if(rb_regPat.isChecked())
                                {
                                    user.setUserRole("Patient");
                                }

                                reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(Register.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                                            Register.this.finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(Register.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}