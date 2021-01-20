package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DrApprovedAppointments extends AppCompatActivity implements PopupAdapter.ItemClicked {

    DatabaseReference reference;
    Query query;
    FirebaseAuth auth;
    List<Appointments> appointmentsList;
    List<Appointments> appDatesList;
    Date today;
    Calendar nextyear;
    List<Date> dateLists;
    CalendarPickerView datePicker;
    Dialog dg;
    RecyclerView appList;
    LinearLayoutManager linearLayoutManager;
    PopupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dr_approved_appointments);

       // String userId = getIntent().getStringExtra("userId");
        dg = new Dialog(this);
        dg.setContentView(R.layout.pop_view_layout);
        appList = dg.findViewById(R.id.pop_listView);
        linearLayoutManager = new LinearLayoutManager(this);
        appList.setLayoutManager(linearLayoutManager);
        appointmentsList = new ArrayList<>();
        dateLists = new ArrayList<>();
        appDatesList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        FirebaseUser userId = auth.getCurrentUser();
        query = FirebaseDatabase.getInstance().getReference("Appointments")
                .orderByChild("drId")
                .equalTo(userId.getUid());

        today = new Date();
        nextyear = Calendar.getInstance();
        nextyear.add(Calendar.YEAR, 1);

        dateLists.add(today);
        datePicker = findViewById(R.id.dr_calendar);
        datePicker.init(today, nextyear.getTime())
                .withSelectedDates(dateLists);

        datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.setTime(date);

                String selectedDate = "" + selectedCal.get(Calendar.DAY_OF_MONTH)
                        + "/"+ (selectedCal.get(Calendar.MONTH) + 1)
                        + "/"+ selectedCal.get(Calendar.YEAR);
                appDatesList.clear();
                for(int x = 0; x < appointmentsList.size(); x++)
                {
                    if(appointmentsList.get(x).getDate().equals(selectedDate))
                    {
                        appDatesList.add(appointmentsList.get(x));
                       // Toast.makeText(DrApprovedAppointments.this, "Hi " + appointmentsList.get(x).getNames(), Toast.LENGTH_SHORT).show();
                    }
                }
                showPopup();
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });


        getAppontments();
    }
    private void getAppontments()
    {
       query.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot snap : snapshot.getChildren())
               {

                   Appointments apps = snap.getValue(Appointments.class);
                   if(apps.isConfirm() == true)
                   {
                       appointmentsList.add(apps);
                   }

               }


               for(Appointments dr_apps : appointmentsList)
               {
                   Date app_dates = new Date();
                   String stringdate = dr_apps.getDate();
                   String[] dates = stringdate.split("/");

                   app_dates.setDate(Integer.parseInt(dates[0]));
                   app_dates.setMonth(Integer.parseInt(dates[1]));
                   app_dates.setYear(Integer.parseInt(dates[2]));

                   dateLists.add(app_dates);
               }
              // datePicker.highlightDates(dateLists);



           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
    private void showPopup()
    {
        adapter = new PopupAdapter(this, appDatesList);
        appList.setAdapter(adapter);
        dg.show();


    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(this, ApproveAppointment.class);
        intent.putExtra("appId", appDatesList.get(position).getCreated());
        intent.putExtra("pushKey", appDatesList.get(position).getPushKey());
        startActivity(intent);
        dg.dismiss();
    }
}