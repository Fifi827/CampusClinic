package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FullRecord extends AppCompatActivity {

    TextView tv_full_rec_dr, tv_full_rec_dia,tv_full_rec_pres,tv_full_rec_app_date,tv_full_rec_app_time,
            tv_full_rec_chronic,tv_desc_notes;

    DatabaseReference ref;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_record);

        tv_full_rec_dr = findViewById(R.id.tv_full_rec_dr);
        tv_full_rec_dia = findViewById(R.id.tv_full_rec_dia);
        tv_full_rec_pres = findViewById(R.id.tv_full_rec_pres);
        tv_full_rec_app_date = findViewById(R.id.tv_full_rec_app_date);
        tv_full_rec_app_time = findViewById(R.id.tv_full_rec_app_time);
        tv_full_rec_chronic = findViewById(R.id.tv_full_rec_chronic);
        tv_desc_notes = findViewById(R.id.tv_desc_notes);

        getRecord();

    }
    private void getRecord()
    {
        String pushKey = getIntent().getStringExtra("pushKey");

        ref = FirebaseDatabase.getInstance().getReference("Records").child(pushKey);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Record rec = snapshot.getValue(Record.class);
                tv_full_rec_dia.setText(rec.getDiagnosis());
                tv_full_rec_pres.setText(rec.getPrescription());
                tv_full_rec_app_date.setText(rec.getDate());
                tv_full_rec_app_time.setText(rec.getTime());
                tv_full_rec_chronic.setText(rec.getChronic());
                tv_desc_notes.setText(rec.getDesc());

                getDr(rec.getDrId());
                getPatient(rec.getUserId());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getDr(String id)
    {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Doctor doc = snapshot.getValue(Doctor.class);
                tv_full_rec_dr.setText(doc.getUserFirstName() + " " + doc.getUserLastName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getPatient(String id)
    {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}