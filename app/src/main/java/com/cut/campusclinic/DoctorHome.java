package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorHome extends AppCompatActivity {
    private CardView btn_appointments, btn_DrPersonalDetails,btn_DrMedicalHistory,btn_DrConsult;
    private TextView tvDrHomeNames, tvDrHomeRole;
    private CircleImageView ivDrProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);

        btn_DrPersonalDetails = findViewById(R.id.btn_DrPersonalDetails);
        btn_DrMedicalHistory = findViewById(R.id.btn_DrMedicalHistory);
        btn_DrConsult = findViewById(R.id.btn_DrConsult);
        tvDrHomeNames = findViewById(R.id.tvDrHomeNames);
        tvDrHomeRole = findViewById(R.id.tvDrHomeRole);
        ivDrProfile = findViewById(R.id.iv_DrProfile);

        tvDrHomeNames.setText(AppClass.names);
        tvDrHomeRole.setText(AppClass.role);
        if(!AppClass.photoUrl.equals("default"))
            Picasso.with(this).load(AppClass.photoUrl).into(ivDrProfile);
        btn_appointments = findViewById(R.id.btn_drAppointment);
        btn_appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorHome.this, DoctorAppointment.class));
            }
        });
        btn_DrConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_DrMedicalHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_DrConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_DrPersonalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorHome.this, UpdateProfile.class));
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}