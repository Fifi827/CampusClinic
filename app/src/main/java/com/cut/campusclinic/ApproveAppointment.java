package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ApproveAppointment extends AppCompatActivity {
    TextView tv_rec_viewNames, tv_rec_viewAge, tv_rec_viewCell, tv_rec_viewDr, tv_rec_viewTime,
            tv_rec_view_Date, tv_rec_viewEx, tv_rec_viewMed;
    EditText et_rec_desc,et_rec_diagnosis,et_rec_pres;
    Button btn_cancel_app, btn_save_rec, btn_rec_history;
    RadioButton rbtn_yes_for_chronic, rbtn_no_for_chronic;
    Query query;
    DatabaseReference ref;
    Patient pat;
    Doctor dr;
    Appointments apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_appointment);

        tv_rec_view_Date = findViewById(R.id.tv_rec_viewDate);
        tv_rec_viewNames = findViewById(R.id.tv_rec_viewNames);
        tv_rec_viewAge = findViewById(R.id.tv_rec_viewAge);
        tv_rec_viewCell = findViewById(R.id.tv_rec_viewCell);
        tv_rec_viewDr = findViewById(R.id.tv_rec_viewDr);
        tv_rec_viewEx = findViewById(R.id.tv_rec_viewEx);
        tv_rec_viewMed = findViewById(R.id.tv_rec_viewMed);
        tv_rec_viewTime = findViewById(R.id.tv_rec_viewTime);

        et_rec_desc = findViewById(R.id.et_rec_desc);
        et_rec_diagnosis = findViewById(R.id.et_rec_diagnosis);
        et_rec_pres = findViewById(R.id.et_rec_pres);

        rbtn_yes_for_chronic = findViewById(R.id.rbtn_yes_for_chronic);
        rbtn_no_for_chronic = findViewById(R.id.rbtn_no_for_chronic);
        rbtn_no_for_chronic.setChecked(true);

        btn_cancel_app = findViewById(R.id.btn_cancel_app);
        btn_save_rec = findViewById(R.id.btn_save_rec);
        btn_rec_history = findViewById(R.id.btn_rec_history);

        btn_rec_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ApproveAppointment.this, RecordList.class);
                intent.putExtra("id", pat.getUserID());
                intent.putExtra("names", pat.getUserFirstName() + " "+ pat.getUserLastName());
                startActivity(intent);
            }
        });

        btn_save_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = et_rec_desc.getText().toString();
                String diag = et_rec_diagnosis.getText().toString();
                String presc = et_rec_pres.getText().toString();
                if(desc.isEmpty() || diag.isEmpty() || presc.isEmpty())
                {
                    Toast.makeText(ApproveAppointment.this, "Please fill in missing data", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    String pushKey = FirebaseDatabase.getInstance().getReference("Records").push().getKey();
                    ref = FirebaseDatabase.getInstance().getReference("Records").child(pushKey);
                    Record rec = new Record();
                    rec.setUserId(pat.getUserID());
                    rec.setDrId(dr.getUserID());
                    rec.setTime(apps.getTime());
                    rec.setDate(apps.getDate());
                    rec.setDiagnosis(diag);
                    rec.setDesc(desc);
                    rec.setPrescription(presc);
                    long tsLong = System.currentTimeMillis();
                    String timestamp = String.valueOf(tsLong);
                    rec.setRecId(timestamp);
                    rec.setRecPushKey(pushKey);
                    if(rbtn_no_for_chronic.isChecked())
                        rec.setChronic("No");
                    else
                        rec.setChronic("Yes");

                    ref.setValue(rec).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                ApproveAppointment.this.finish();
                                Toast.makeText(ApproveAppointment.this, "Record saved successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });


        getAppointment();

    }
    private void getPatientData(String patientId)
    {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(patientId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pat = snapshot.getValue(Patient.class);
                tv_rec_viewNames.setText(pat.getUserFirstName() + " " + pat.getUserLastName());
                tv_rec_viewAge.setText(pat.getAge());
                tv_rec_viewCell.setText(pat.getContactNumber());
                String ex = pat.getPre_existing_condition();
                if(ex == null)
                    tv_rec_viewEx.setText("None");
                else
                    tv_rec_viewEx.setText(ex);

                getDrInfo(apps.getDrId());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getAppointment()
    {
        String appId = getIntent().getStringExtra("appId");
        String pushKey = getIntent().getStringExtra("pushKey");
      //  Toast.makeText(this, appId + " " + pushKey, Toast.LENGTH_SHORT).show();

        ref = FirebaseDatabase.getInstance().getReference("Appointments").child(pushKey);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                apps = snapshot.getValue(Appointments.class);
                tv_rec_view_Date.setText(apps.getDate());
                tv_rec_viewTime.setText(apps.getTime());
                getPatientData(apps.getPatientId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ApproveAppointment.this, "error "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void getDrInfo(String drId)
    {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(drId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dr = snapshot.getValue(Doctor.class);
                tv_rec_viewDr.setText(dr.getUserFirstName() + " " + dr.getUserLastName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ApproveAppointment.this, "error "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}