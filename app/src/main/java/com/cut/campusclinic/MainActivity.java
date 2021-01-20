package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Button btnLogin, btnRegister;
    EditText ed_loginEmail, ed_loginPassword;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    ProgressBar progressBar;
    TextView tvAdd_Load;
    LinearLayout login_Form;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_Form = findViewById(R.id.login_form);
        tvAdd_Load = findViewById(R.id.tvLoad);
        progressBar = findViewById(R.id.add_progress);

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
                    showProgress(true);
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                firebaseUser = auth.getCurrentUser();
                                String userId = firebaseUser.getUid();
                                AppClass.userId = userId;
                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        AppClass.role = user.getUserRole();
                                        AppClass.names = user.getUserFirstName() + " "+ user.getUserLastName();
                                        AppClass.photoUrl = user.getPhotoUrl();
                                        if(user.getUserRole().equals("Admin"))
                                        {
                                            startActivity(new Intent(MainActivity.this, AdminHome.class));
                                            Toast.makeText(MainActivity.this, "Admin login successful", Toast.LENGTH_SHORT).show();
                                            MainActivity.this.finish();
                                        }
                                        else if(user.getUserRole().equals("Patient"))
                                        {
                                            startActivity(new Intent(MainActivity.this, PatientHome.class));
                                            Toast.makeText(MainActivity.this, "Patient login successful", Toast.LENGTH_SHORT).show();
                                            MainActivity.this.finish();
                                        }
                                        else if(user.getUserRole().equals("Doctor"))
                                        {
                                            startActivity(new Intent(MainActivity.this, DoctorHome.class));
                                            Toast.makeText(MainActivity.this, "Doctor login successful", Toast.LENGTH_SHORT).show();
                                            MainActivity.this.finish();
                                        }
                                        showProgress(false);


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        showProgress(false);
                                        Toast.makeText(MainActivity.this, "error "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                            else
                            {
                                showProgress(false);
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

//    @Override
//    protected void onStart() {
//        super.onStart();
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if(firebaseUser != null)
//        {
//            String userId = firebaseUser.getUid();
//
//            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
//            reference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    User user = dataSnapshot.getValue(User.class);
//
//                    if(user.getUserRole().equals("Admin"))
//                    {
//                        startActivity(new Intent(MainActivity.this, AdminHome.class));
//                        Toast.makeText(MainActivity.this, "Admin login successful", Toast.LENGTH_SHORT).show();
//                        MainActivity.this.finish();
//                    }
//                    else if(user.getUserRole().equals("Patient"))
//                    {
//                        startActivity(new Intent(MainActivity.this, PatientHome.class));
//                        Toast.makeText(MainActivity.this, "Patient login successful", Toast.LENGTH_SHORT).show();
//                        MainActivity.this.finish();
//                    }
//                    else if(user.getUserRole().equals("Doctor"))
//                    {
//                        startActivity(new Intent(MainActivity.this, DoctorHome.class));
//                        Toast.makeText(MainActivity.this, "Doctor login successful", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            login_Form.setVisibility(show ? View.GONE : View.VISIBLE);
            login_Form.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    login_Form.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvAdd_Load.setVisibility(show ? View.VISIBLE : View.GONE);
            tvAdd_Load.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvAdd_Load.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            tvAdd_Load.setVisibility(show ? View.VISIBLE : View.GONE);
            login_Form.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}