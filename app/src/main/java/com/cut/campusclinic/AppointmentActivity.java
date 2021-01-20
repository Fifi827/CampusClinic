package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity {

    Button btn_submit;
    EditText ed_datePicker, ed_names, ed_email, ed_contact;
    Spinner sp_doctors;
    List<Doctor> userList;
    Calendar myCalender;
    DatePickerDialog.OnDateSetListener date;

    FirebaseAuth auth;
    DatabaseReference reference, allUsersReference, appointmentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        auth = FirebaseAuth.getInstance();

        ed_datePicker = findViewById(R.id.ed_patDatePicker);
        ed_names = findViewById(R.id.ed_patNames);
        ed_email = findViewById(R.id.ed_patEmail);
        ed_contact = findViewById(R.id.ed_patNumbers);
        userList = new ArrayList<>();

        myCalender = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myCalender.set(Calendar.YEAR,i );
                myCalender.set(Calendar.MONTH,i1);
                myCalender.set(Calendar.DAY_OF_MONTH,i2 );

                ed_datePicker.setText(myCalender.get(Calendar.DAY_OF_MONTH) + "/"+ (myCalender.get(Calendar.MONTH) + 1) + "/"+
                        myCalender.get(Calendar.YEAR));
            }
        };

        ed_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AppointmentActivity.this,date, myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH),
                        myCalender.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        sp_doctors = findViewById(R.id.sp_patListOfDocs);

        btn_submit = findViewById(R.id.btn_patSubmitAppointment);


        GetAllDoctors();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeAppointment();
            }
        });
    }
    private void GetUserInfo()
    {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String userId = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               User user = snapshot.getValue(User.class);
               Log.d("GetUserInfo() ", "Class successfully retrieved");
               if(user != null)
               {
                   String name = user.getUserFirstName() + " "+ user.getUserLastName();
                   ed_names.setText(name);
                   ed_email.setText(user.getEmail());
                   ed_contact.setText(user.getContactNumber());
                //   Toast.makeText(AppointmentActivity.this, user.getUserFirstName(), Toast.LENGTH_SHORT).show();
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
    private void GetAllDoctors()
    {
        allUsersReference = FirebaseDatabase.getInstance().getReference("Users");
        allUsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                userList.clear();
                List<String> drNames = new ArrayList<>();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Doctor doc = snapshot1.getValue(Doctor.class);
                  //  User user = snapshot1.getValue(User.class);
                    assert doc != null;
                    if(doc.getUserRole().equals("Doctor"))
                    {
                        userList.add(doc);
                        drNames.add(doc.getUserFirstName() + " " + doc.getUserLastName());
                    }
                }
                ArrayAdapter<String> spAdapter = new ArrayAdapter<>(AppointmentActivity.this,
                        android.R.layout.simple_list_item_1, drNames);
                sp_doctors.setAdapter(spAdapter);
                GetUserInfo();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void makeAppointment()
    {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String userId = firebaseUser.getUid();
        String key = FirebaseDatabase.getInstance().getReference("Appointments").push().getKey();
        appointmentRef = FirebaseDatabase.getInstance().getReference("Appointments").child(key);

        Appointments appointments = new Appointments();
        appointments.setNames(ed_names.getText().toString());
        appointments.setDate(ed_datePicker.getText().toString());
        appointments.setDrId(userList.get(sp_doctors.getSelectedItemPosition()).getUserID());
        appointments.setEmail(ed_email.getText().toString());
        appointments.setConfirm(false);
        appointments.setPatientId(userId);
        appointments.setTime("n/a");
        appointments.setPushKey(key);

        long tsLong = System.currentTimeMillis();
        String timestamp = String.valueOf(tsLong);
        appointments.setCreated(timestamp);

        appointmentRef.setValue(appointments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {

                    Toast.makeText(AppointmentActivity.this, "Appointment made successfully, please wait for the Dr to confirm!!!", Toast.LENGTH_SHORT).show();
                    AppointmentActivity.this.finish();
                }
            }
        });
    }
}