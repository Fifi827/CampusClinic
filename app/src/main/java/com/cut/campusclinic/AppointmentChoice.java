package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AppointmentChoice extends AppCompatActivity implements AppointmentsAdapter.ItemClicked {
    CardView btnMakeApp;
    RecyclerView pat_appList;
    List<Appointments> lst_appointments;
    LinearLayoutManager layoutManager;
    AppointmentsAdapter adapter;
    FirebaseUser fbUser;
    DatabaseReference reference;
    Query query;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_choice);

        auth = FirebaseAuth.getInstance();
        fbUser = auth.getCurrentUser();

        btnMakeApp = findViewById(R.id.btn_appMake);
        pat_appList = findViewById(R.id.pat_appList);
        lst_appointments = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        pat_appList.setLayoutManager(layoutManager);
        getData();
        btnMakeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(AppointmentChoice.this, AppointmentActivity.class);
//                intent.putExtra("role", "Patient");
//                startActivity(intent);
                startActivity(new Intent(AppointmentChoice.this, BookAppointment.class));
            }
        });
    }
    private void getData()
    {
        lst_appointments.clear();
        String userId = AppClass.userId;
        query = FirebaseDatabase.getInstance().getReference("Appointments")
                .orderByChild("patientId")
                .equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren())
                {
                    Appointments apps = s.getValue(Appointments.class);
                    lst_appointments.add(apps);
                }
                adapter = new AppointmentsAdapter(AppointmentChoice.this, lst_appointments);
                pat_appList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClicked(int position) {

    }
    @Override
    protected void onResume() {
        super.onResume();

        getData();
    }
}