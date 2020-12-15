package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.Toast;

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

public class AppointmentList extends AppCompatActivity implements AppointmentsAdapter.ItemClicked {

    FirebaseAuth auth;
    FirebaseUser fbUser;
    DatabaseReference reference;
    RecyclerView appointmentList;
    List<Appointments> lst_appointments;
    LinearLayoutManager layoutManager;
    AppointmentsAdapter adapter;
    Query query;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        lst_appointments = new ArrayList<>();
        appointmentList = findViewById(R.id.lst_patList);

        adapter = new AppointmentsAdapter(AppointmentList.this, lst_appointments);
        appointmentList.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(this);
        appointmentList.setLayoutManager(layoutManager);
        auth = FirebaseAuth.getInstance();
        fbUser = auth.getCurrentUser();
        assert fbUser != null;
        userId = fbUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Appointments");

        query = FirebaseDatabase.getInstance().getReference("Appointments")
        .orderByChild("drId")
        .equalTo(userId);

        String role = getIntent().getStringExtra("role");
        assert role != null;
        if(role.equals("Doctor"))
            GetDoctorAppointments();
        else
            GetAllAppointments();


    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            lst_appointments.clear();
            for (DataSnapshot snap : snapshot.getChildren())
            {
                Appointments appointments = snap.getValue(Appointments.class);
                lst_appointments.add(appointments);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    private void GetAllAppointments()
    {
        reference.addListenerForSingleValueEvent(valueEventListener);
    }
    private void GetDoctorAppointments()
    {
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onItemClicked(int position)
    {
        String role = getIntent().getStringExtra("role");
        assert role != null;
        if (role.equals("Doctor"))
        {
            Intent intent = new Intent(this, ApproveAppointment.class);
            intent.putExtra("id", lst_appointments.get(position).getCreated());
            startActivity(intent);
        }
    }
}