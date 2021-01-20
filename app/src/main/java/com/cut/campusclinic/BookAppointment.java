package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookAppointment extends AppCompatActivity implements BookAppointmentAdapter.ItemClicked {

    private List<Doctor> doctorList;
    private Query query;
    private DatabaseReference ref;
    private Patient patient;
    private BookAppointmentAdapter appointmentAdapter;
    private RecyclerView rv_book_dr_list;
    private LinearLayoutManager layoutManager;
    private Dialog dlg;
    private Calendar myCalender;
    private DatePickerDialog.OnDateSetListener date;
    private TextView selectedDate, tvLoad_make_appointment;
    private ProgressBar make_appointment_progress;
    private LinearLayout make_appointment_form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        doctorList = new ArrayList<>();
        tvLoad_make_appointment = findViewById(R.id.tvLoad_make_appointment);
        make_appointment_progress = findViewById(R.id.make_appointment_progress);
        make_appointment_form = findViewById(R.id.make_appointment_form);

        rv_book_dr_list = findViewById(R.id.rv_book_dr_list);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        rv_book_dr_list.setLayoutManager(layoutManager);
        myCalender = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myCalender.set(Calendar.YEAR,i );
                myCalender.set(Calendar.MONTH,i1);
                myCalender.set(Calendar.DAY_OF_MONTH,i2 );

                selectedDate.setText(myCalender.get(Calendar.DAY_OF_MONTH) + "/"+ (myCalender.get(Calendar.MONTH) + 1) + "/"+
                        myCalender.get(Calendar.YEAR));
            }
        };

        GetAllDoctors();
    }
    private void GetAllDoctors()
    {
       query = FirebaseDatabase.getInstance().getReference("Users")
               .orderByChild("userRole")
               .equalTo("Doctor");
       query.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for (DataSnapshot s : snapshot.getChildren())
               {
                   Doctor dr = s.getValue(Doctor.class);
                   doctorList.add(dr);
               }
               appointmentAdapter = new BookAppointmentAdapter(BookAppointment.this, doctorList);
               rv_book_dr_list.setAdapter(appointmentAdapter);
               getCurrentUserInfo();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               Toast.makeText(BookAppointment.this, "error "+ error.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });
    }
    private void getCurrentUserInfo()
    {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(AppClass.userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                patient = snapshot.getValue(Patient.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookAppointment.this, "error "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getDialog(final int index)
    {
        dlg = new Dialog(this);
        dlg.setContentView(R.layout.dialog_make_appointment);
        selectedDate = dlg.findViewById(R.id.tvGetDate);
        ImageView selectDate = dlg.findViewById(R.id.iv_SelectDate);
        Button cancel, confirm;
        cancel = dlg.findViewById(R.id.btn_Cancel_Appointment);
        confirm = dlg.findViewById(R.id.btn_confirmApp);

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(BookAppointment.this,date, myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH),
                        myCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();

            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = FirebaseDatabase.getInstance().getReference("Appointments").push().getKey();
                ref = FirebaseDatabase.getInstance().getReference("Appointments").child(key);

                Appointments appointments = new Appointments();
                appointments.setNames(patient.getUserFirstName() + " "+ patient.getUserLastName());
                appointments.setDate(selectedDate.getText().toString());
                appointments.setDrId(doctorList.get(index).getUserID());
                appointments.setEmail(patient.getEmail());
                appointments.setConfirm(false);
                appointments.setPatientId(patient.getUserID());
                appointments.setTime("n/a");
                appointments.setPushKey(key);

                long tsLong = System.currentTimeMillis();
                String timestamp = String.valueOf(tsLong);
                appointments.setCreated(timestamp);

                showProgress(true);
                ref.setValue(appointments).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {

                            Toast.makeText(BookAppointment.this, "Appointment made successfully, please wait for the Dr to confirm!!!", Toast.LENGTH_SHORT).show();
                            BookAppointment.this.finish();
                            showProgress(false);
                        }
                    }
                });
                dlg.dismiss();
            }
        });
        dlg.show();
    }
    private void makeAppointment()
    {

    }
    @Override
    public void onItemClicked(int position) {
        getDialog(position);
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

            make_appointment_form.setVisibility(show ? View.GONE : View.VISIBLE);
            make_appointment_form.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    make_appointment_form.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            make_appointment_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            make_appointment_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    make_appointment_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad_make_appointment.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad_make_appointment.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad_make_appointment.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            make_appointment_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad_make_appointment.setVisibility(show ? View.VISIBLE : View.GONE);
            make_appointment_form.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}