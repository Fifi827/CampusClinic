package com.cut.campusclinic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AppointmentChoice extends AppCompatActivity {
    Button btnList, btnMakeApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_choice);

        btnList = findViewById(R.id.btn_appMade);
        btnMakeApp = findViewById(R.id.btn_appMake);

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentChoice.this, AppointmentList.class);
                intent.putExtra("role", "Patient");
                startActivity(intent);
            }
        });
        btnMakeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentChoice.this, AppointmentActivity.class);
                intent.putExtra("role", "Patient");
                startActivity(intent);
            }
        });
    }
}