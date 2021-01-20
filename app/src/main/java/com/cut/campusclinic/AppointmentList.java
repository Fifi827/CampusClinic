package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    List<Appointments> allApsList;
    LinearLayoutManager layoutManager;
    AppointmentsAdapter adapter;
    Query query;
    String userId;
    Dialog dlg;
    Spinner pop_spinner;
    Button btnCancel, btnApprove;
    Appointments appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        allApsList = new ArrayList<>();
        lst_appointments = new ArrayList<>();
        appointmentList = findViewById(R.id.lst_patList);
        dlg = new Dialog(this);
        dlg.setContentView(R.layout.custom_approve_popup);
        pop_spinner = dlg.findViewById(R.id.pop_spinner);
        btnApprove = dlg.findViewById(R.id.pop_ApproveAppointment);
        btnCancel = dlg.findViewById(R.id.pop_cancelAppointment);
      // String[] t = getResources().getStringArray(R.array.times);

        ArrayAdapter adpt = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.times));
        pop_spinner.setAdapter(adpt);

        adapter = new AppointmentsAdapter(AppointmentList.this, lst_appointments);
        appointmentList.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(this);
        appointmentList.setLayoutManager(layoutManager);
        auth = FirebaseAuth.getInstance();
        userId = AppClass.userId;
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
                allApsList.add(appointments);
                if(appointments.isConfirm() == false)
                lst_appointments.add(appointments);
            }
            adapter = new AppointmentsAdapter(AppointmentList.this, lst_appointments);
            appointmentList.setAdapter(adapter);
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
    public void onItemClicked(final int position)
    {
        appointments = lst_appointments.get(position);
        appointments.setTime(pop_spinner.getSelectedItem().toString());
        appointments.setConfirm(true);
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String selectedTime = pop_spinner.getSelectedItem().toString();
                boolean isPresent = true;

                for(Appointments ap : allApsList)
                {
                    if(ap.getDate().equals(appointments.getDate()) && ap.getTime().equals(selectedTime))
                    {
                        isPresent = true;
                    }
                    else
                    {
                        isPresent = false;
                    }

                }

                if(isPresent)
                {
                    Toast.makeText(AppointmentList.this, "Please select a different time slot", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Query q = FirebaseDatabase.getInstance().getReference("Appointments")
                            .orderByChild("created")
                            .equalTo(appointments.getCreated());
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String s = snapshot.getRef().child("Appointment").child(lst_appointments.get(position).getPushKey()).getKey();
                            //Toast.makeText(AppointmentList.this, s, Toast.LENGTH_SHORT).show();
                            snapshot.getRef().child(s).child("confirm").setValue(appointments.isConfirm());
                            snapshot.getRef().child(s).child("time").setValue(appointments.getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        GetDoctorAppointments();
                                        Toast.makeText(AppointmentList.this, "" +
                                                "Appointment confirmed", Toast.LENGTH_SHORT).show();
                                        dlg.dismiss();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dlg.dismiss();
                        }
                    });

                }


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
        dlg.show();

    }
    private void showPop()
    {

    }
}