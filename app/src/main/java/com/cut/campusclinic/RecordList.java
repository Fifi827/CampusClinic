package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecordList extends AppCompatActivity implements RecordAdapter.ItemClicked {

    TextView tv_reclist_name;
    RecyclerView rc_recordList;
    Query query;
    List<Record> recordList;
    RecordAdapter recordAdapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        rc_recordList = findViewById(R.id.rc_recordList);
        tv_reclist_name = findViewById(R.id.tv_reclist_name);

        recordList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        rc_recordList.setLayoutManager(layoutManager);

        getSpecificPatientRecords();
    }
    private void getSpecificPatientRecords()
    {
        String patientId = getIntent().getStringExtra("id");
        String patientName = getIntent().getStringExtra("names");

        tv_reclist_name.setText(patientName + "'s Medical Record");
        query = FirebaseDatabase.getInstance().getReference("Records")
        .orderByChild("userId")
        .equalTo(patientId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null)
                {
                    for (DataSnapshot s : snapshot.getChildren())
                    {
                        Record rec = s.getValue(Record.class);
                        recordList.add(rec);
                    }
                    recordAdapter = new RecordAdapter(RecordList.this, recordList);
                    rc_recordList.setAdapter(recordAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(this, FullRecord.class);
        intent.putExtra("pushKey", recordList.get(position).getRecPushKey());
        startActivity(intent);
    }
}