package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientHome extends AppCompatActivity {

    CardView btn_Appointment,btn_updateDetails,btn_Records, btn_consult;
    TextView tvPatHomeNames,tvPatHomeRole,tvPatHomeAppointements;
    Query dbRef;
    CircleImageView iv_userProfile;
    List<Appointments> appointmentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);

        btn_Appointment = findViewById(R.id.btn_patAppointment);
        btn_Records = findViewById(R.id.btn_patMedicalHistory);
        btn_updateDetails = findViewById(R.id.btn_petPersonalDetails);
        btn_consult = findViewById(R.id.btn_patConsult);
        iv_userProfile = findViewById(R.id.iv_userProfile);
        appointmentsList = new ArrayList<>();

        tvPatHomeNames = findViewById(R.id.tvPatHomeNames);
        tvPatHomeRole = findViewById(R.id.tvPatHomeRole);
        tvPatHomeNames.setText(AppClass.names);
        tvPatHomeRole.setText(AppClass.role);
        if(!AppClass.photoUrl.equals("default"))
            Picasso.with(this).load(AppClass.photoUrl).into(iv_userProfile);

        btn_Appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientHome.this, AppointmentChoice.class));
            }
        });
        btn_updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientHome.this,  UpdateProfile.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        if(!AppClass.photoUrl.equals("default"))
            Picasso.with(this).load(AppClass.photoUrl).into(iv_userProfile);
    }
}