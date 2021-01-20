package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {
    EditText ed_regFirstName, ed_regLastName, ed_regEmail, ed_contactNumber, ed_regPassword, ed_regConfirmPassword, ed_age;
    RadioButton rb_regAdmin, rb_regDoc, rb_regPat;
    Button btn_regSubmit;

    FirebaseAuth auth;
    DatabaseReference reference;
    CustomSpinnerAdapter spinnerAdapter;
    List<CustomItem> customItemList;
    Spinner sp_register_role;
    ProgressBar register_progress;
    TextView tvLoad_register;
    LinearLayout register_form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        register_progress = findViewById(R.id.register_progress);
        tvLoad_register = findViewById(R.id.tvLoad_register);
        register_form = findViewById(R.id.register_form);

        ed_regFirstName = findViewById(R.id.ed_regName);
        ed_regLastName = findViewById(R.id.ed_regLastName);
        ed_regEmail = findViewById(R.id.ed_email);
        ed_regPassword = findViewById(R.id.ed_regPassword);
        ed_regConfirmPassword = findViewById(R.id.ed_regConfirmPassword);
        ed_contactNumber = findViewById(R.id.ed_contactNumber);
        ed_age = findViewById(R.id.ed_age);

        sp_register_role = findViewById(R.id.sp_register_role);

        btn_regSubmit = findViewById(R.id.btn_regSubmit);

        customItemList = new ArrayList<>();

        String[] role = getResources().getStringArray(R.array.roles);
        for (String s : role)
        {
            CustomItem customItem = new CustomItem();
            customItem.setSpinnerItemName(s);
            customItem.setSpinnerImage(R.drawable.ic_outline_account);
            customItemList.add(customItem);
        }
        spinnerAdapter = new CustomSpinnerAdapter(this, customItemList);
        sp_register_role.setAdapter(spinnerAdapter);



        btn_regSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String firstname = ed_regFirstName.getText().toString().trim();
                final String lastname = ed_regLastName.getText().toString().trim();
                final String email = ed_regEmail.getText().toString().trim();
                String password = ed_regPassword.getText().toString().trim();
                String passwordConfirm = ed_regConfirmPassword.getText().toString();
                int selectedIndex = sp_register_role.getSelectedItemPosition();

                if(firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() ||
                        ed_contactNumber.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(Register.this, "Some fields missing text", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(passwordConfirm))
                {
                    Toast.makeText(Register.this, "Passwords do not match!!!", Toast.LENGTH_SHORT).show();
                }
                else if (selectedIndex == 0)
                {
                    Toast.makeText(Register.this, "Please select role", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    showProgress(true);
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String userId = firebaseUser.getUid();
                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                                Doctor doc;
                                Patient pat;
                                String defValue = "default";
                                CustomItem item = (CustomItem) sp_register_role.getSelectedItem();
                                if(item.getSpinnerItemName().equals("Admin"))
                                {

                                }
                                else if(item.getSpinnerItemName().equals("Doctor"))
                                {

                                    doc = new Doctor();
                                    doc.setUserFirstName(firstname);
                                    doc.setUserLastName(lastname);
                                    doc.setEmail(email);
                                    doc.setUserID(userId);
                                    doc.setContactNumber(ed_contactNumber.getText().toString().trim());
                                    doc.setUserRole(item.getSpinnerItemName());
                                    doc.setExperience(defValue);
                                    doc.setPhotoUrl(defValue);
                                    doc.setQualifications(defValue);
                                    doc.setSpecialty(defValue);
                                    doc.setAge(ed_age.getText().toString().trim());

                                    saveData(doc);
                                }
                                else if(item.getSpinnerItemName().equals("Patient"))
                                {
                                    pat = new Patient();
                                    pat.setUserFirstName(firstname);
                                    pat.setUserLastName(lastname);
                                    pat.setEmail(email);
                                    pat.setUserID(userId);
                                    pat.setContactNumber(ed_contactNumber.getText().toString().trim());
                                    pat.setUserRole(item.getSpinnerItemName());
                                    pat.setMedicalAidName(defValue);
                                    pat.setAge(ed_age.getText().toString().trim());
                                    pat.setMedicalAidNr(defValue);
                                    pat.setPre_existing_condition(defValue);
                                    pat.setPhotoUrl(defValue);
                                    saveData(pat);
                                }


                            }
                        }
                    });
                }
            }
        });
    }
    private void saveData(Object obj)
    {
        reference.setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(Register.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                    Register.this.finish();
                    showProgress(false);
                }
                else
                {
                    showProgress(false);
                    Toast.makeText(Register.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
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

            register_form.setVisibility(show ? View.GONE : View.VISIBLE);
            register_form.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    register_form.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            register_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            register_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    register_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad_register.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad_register.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad_register.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            register_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad_register.setVisibility(show ? View.VISIBLE : View.GONE);
            register_form.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}