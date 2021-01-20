package com.cut.campusclinic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity  {

    List<CustomItem> customItemListQualifications, customItemListMedicalAid, customItemListSpecialty;
    DatabaseReference ref;
    Patient patient;
    Doctor dr;
    User user;
    EditText tvUpdateFirstName,tvUpdateLastName,tvUpdateAge,tvUpdateCellNumber,tvUpdateExperience,
            tvMedicalAidName,tvMedicalAidNumber;
    Spinner spUpdateMedicalAid,spUpdateSpecialization,sp_qualifications;
    Button btn_Update,btn_update_add_edu;
    CircleImageView ivProfilePic;
    private StorageReference mStorageRef;
    private StorageTask uploadTask;
    private Uri uri;
    private List<String> eduList;
    private TextView tv_qualification_hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        sp_qualifications = findViewById(R.id.sp_qualifications);
        customItemListQualifications = new ArrayList<>();
        customItemListMedicalAid = new ArrayList<>();

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        btn_Update = findViewById(R.id.btn_Update);
        tv_qualification_hint = findViewById(R.id.tv_qualification_hint);
        tvUpdateFirstName = findViewById(R.id.tvUpdateFirstName);
        tvUpdateLastName = findViewById(R.id.tvUpdateLastName);
        tvUpdateAge = findViewById(R.id.tvUpdateAge);
        tvUpdateCellNumber = findViewById(R.id.tvUpdateCellNumber);
        tvUpdateExperience = findViewById(R.id.tvUpdateExperience);
        tvMedicalAidName = findViewById(R.id.tvMedicalAidName);
        tvMedicalAidNumber = findViewById(R.id.tvMedicalAidNumber);
        spUpdateMedicalAid = findViewById(R.id.spUpdateMedicalAid);
        spUpdateSpecialization = findViewById(R.id.spUpdateSpecialization);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        btn_update_add_edu = findViewById(R.id.btn_update_add_edu);

        customItemListSpecialty = new ArrayList<>();
        eduList = new ArrayList<>();


        btn_update_add_edu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp_qualifications.getSelectedItemPosition() > 0)
                {
                    CustomItem item = (CustomItem) sp_qualifications.getSelectedItem();

                    if(eduList.size() == 0)
                    {
                        eduList.add(item.getSpinnerItemName());
                        Toast.makeText(UpdateProfile.this, "Added", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        for(String x : eduList)
                        {
                            if(!x.equals(item.getSpinnerItemName()))
                            {

                                eduList.add(item.getSpinnerItemName());
                                Toast.makeText(UpdateProfile.this, "Qualification added", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(UpdateProfile.this, "Qualification already added", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }
                else
                {
                    Toast.makeText(UpdateProfile.this, "Please select qualification", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

        btn_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(uploadTask != null && uploadTask.isInProgress())
                        Toast.makeText(UpdateProfile.this, "Uploading...", Toast.LENGTH_SHORT).show();
                    else
                        uploadPicture();

            }
        });
        if(AppClass.role.equals("Patient"))
        {
            tvUpdateExperience.setVisibility(View.GONE);
            sp_qualifications.setVisibility(View.GONE);
            spUpdateSpecialization.setVisibility(View.GONE);
            tvMedicalAidName.setVisibility(View.GONE);
            tvMedicalAidNumber.setVisibility(View.GONE);
            tv_qualification_hint.setVisibility(View.GONE);
            btn_update_add_edu.setVisibility(View.GONE);
        }
        else
        {
            tvMedicalAidName.setVisibility(View.GONE);
            tvMedicalAidNumber.setVisibility(View.GONE);
            spUpdateMedicalAid.setVisibility(View.GONE);
        }

        fillSpinner();
        getUserInfo();
    }
    private void fillSpinner()
    {
        String[] degree = getResources().getStringArray(R.array.degrees);
        for(String s : degree)
        {
            CustomItem item = new CustomItem();
            item.setSpinnerItemName(s);
            item.setSpinnerImage(R.drawable.ic_books);
            customItemListQualifications.add(item);
        }
        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(this, customItemListQualifications);
        sp_qualifications.setAdapter(spinnerAdapter);
        sp_qualifications.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CustomItem itemSelected = (CustomItem) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        String[] medicalAid = getResources().getStringArray(R.array.medaid);
        int count = 0;
        for(String s : medicalAid)
        {
            CustomItem item = new CustomItem();

            if(count != 0)
            {
                item.setSpinnerItemName(s);
                item.setSpinnerImage(R.drawable.ic_outline_local_hospital);
            }
            item.setSpinnerItemName(s);
            customItemListMedicalAid.add(item);
            count++;
        }
        CustomSpinnerAdapter spinnerAdapter2 = new CustomSpinnerAdapter(this, customItemListMedicalAid);
        spUpdateMedicalAid.setAdapter(spinnerAdapter2);
        spUpdateMedicalAid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CustomItem itemSelected = (CustomItem) adapterView.getSelectedItem();
                if(itemSelected.getSpinnerItemName().equals("Yes"))
                {
                    tvMedicalAidName.setVisibility(View.VISIBLE);
                    tvMedicalAidNumber.setVisibility(View.VISIBLE);
                }
                else if(itemSelected.getSpinnerItemName().equals("No"))
                {
                    tvMedicalAidName.setVisibility(View.GONE);
                    tvMedicalAidNumber.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        String[] specialty = getResources().getStringArray(R.array.specialty);
        for (String sp : specialty)
        {
            CustomItem customItem = new CustomItem();
            customItem.setSpinnerImage(R.drawable.ic_books);
            customItem.setSpinnerItemName(sp);
            customItemListSpecialty.add(customItem);
        }
        CustomSpinnerAdapter customSpinnerAdapter3 = new CustomSpinnerAdapter(this, customItemListSpecialty);
        spUpdateSpecialization.setAdapter(customSpinnerAdapter3);


    }
    private void getUserInfo()
    {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(AppClass.userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(AppClass.role.equals("Doctor"))
                {
                    dr = snapshot.getValue(Doctor.class);
                    user = snapshot.getValue(User.class);
//                    if(!dr.getPhotoUrl().equals("default")) {
//                        Picasso.with(UpdateProfile.this).load(dr.getPhotoUrl()).into(ivProfilePic);
//                    }
                    tvUpdateFirstName.setText(dr.getUserFirstName());
                    tvUpdateLastName.setText(dr.getUserLastName());
                    tvUpdateAge.setText(dr.getAge());
                    tvUpdateCellNumber.setText(dr.getContactNumber());
                    if(!dr.getExperience().equals("default"))
                        tvUpdateExperience.setText(dr.getExperience());
                    String paper = dr.getQualifications();
                    if(!paper.equals("") || !paper.equals("default"))
                    {
                        int count = 0;
                        for(CustomItem item : customItemListQualifications)
                        {
                            if(item.getSpinnerItemName().equals(paper))
                            {
                                sp_qualifications.setSelection(count);
                            }
                            count++;
                        }
                    }
                }
                else if(AppClass.role.equals("Patient"))
                {
                    patient = snapshot.getValue(Patient.class);
                    user = snapshot.getValue(User.class);
                    if(!patient.getPhotoUrl().equals("default"))
                        Picasso.with(UpdateProfile.this).load(patient.getPhotoUrl()).into(ivProfilePic);
                    tvUpdateFirstName.setText(patient.getUserFirstName());
                    tvUpdateLastName.setText(patient.getUserLastName());
                    tvUpdateAge.setText(patient.getAge());
                    tvUpdateCellNumber.setText(patient.getContactNumber());
                    if(!patient.getMedicalAidName().equals("default"))
                    {
                        tvMedicalAidName.setVisibility(View.VISIBLE);
                        tvMedicalAidName.setText(patient.getMedicalAidName());
                    }

                    if(!patient.getMedicalAidNr().equals("default"))
                    {
                        tvMedicalAidNumber.setVisibility(View.VISIBLE);
                        tvMedicalAidNumber.setText(patient.getMedicalAidNr());
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updatePatient()
    {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(AppClass.userId);
        ref.setValue(patient).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(UpdateProfile.this, "Updated", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(UpdateProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updateDetails(Object obj)
    {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(AppClass.userId);
        ref.setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(UpdateProfile.this, "Successfully saved", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(UpdateProfile.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void choosePhoto()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 202);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 202 && resultCode == RESULT_OK && data != null)
        {
            uri = data.getData();
            ivProfilePic.setImageURI(uri);
        }
    }
    private String getFileExtention(Uri uri1)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri1));

    }
    private void uploadPicture()
    {
      //  String randomKey = UUID.randomUUID().toString();

        if(uri != null)
        {
            String imageName = System.currentTimeMillis() + "."+ getFileExtention(uri);
            if(!user.getPhotoUrl().equals("default"))
            {
                imageName = user.getPhotoUrl();
            }
            final StorageReference fileRef = mStorageRef.child(imageName);
            uploadTask = fileRef.putFile(uri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri dwnLink = task.getResult();
                        String realLink = dwnLink.toString();
                        AppClass.photoUrl = realLink;
                        saveUpdatedData(realLink, true);

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            saveUpdatedData("no url needed", false);
           // Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendData()
    {
        if(AppClass.role.equals("Patient"))
        {
            String firstName = tvUpdateFirstName.getText().toString().trim();
            String lastName = tvUpdateLastName.getText().toString().trim();
            String age = tvUpdateAge.getText().toString().trim();
            String medAidName = tvMedicalAidName.getText().toString().trim();
            String medAidNo = tvMedicalAidNumber.getText().toString().trim();
            String contact = tvUpdateCellNumber.getText().toString().trim();
            CustomItem item = (CustomItem) spUpdateMedicalAid.getSelectedItem();

            if(firstName.isEmpty() || lastName.isEmpty() || age.isEmpty() || contact.isEmpty())
            {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            }
            else
            {
//                if(item.getSpinnerItemName().equals("Yes"))
//                {
//                    if(medAidName.isEmpty() || medAidNo.isEmpty())
//                    {
//                        Toast.makeText(this, "Please enter medical aid details", Toast.LENGTH_SHORT).show();
//                    }
//                    else
//                    {
//
//                    }
//
//                }

                if(medAidName.isEmpty() || medAidNo.isEmpty())
                {
                    if(item.getSpinnerItemName().equals("Yes"))
                    {
                        Toast.makeText(this, "Please enter medical info", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        patient.setUserFirstName(firstName);
                        patient.setUserLastName(lastName);
                        patient.setAge(age);
                        patient.setContactNumber(contact);
                       // updateDetails(patient);
                    }
                }
                else
                {
                    if(item.getSpinnerItemName().equals("No"))
                    {
                        tvMedicalAidName.setText("");
                        tvMedicalAidNumber.setText("");
                        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        patient.setUserFirstName(firstName);
                        patient.setUserLastName(lastName);
                        patient.setAge(age);
                        patient.setContactNumber(contact);
                        patient.setMedicalAidName(medAidName);
                        patient.setMedicalAidNr(medAidNo);
                    }
                }
                updateDetails(patient);

            }


        }
        else if(AppClass.role.equals("Doctor"))
        {
            String firstName = tvUpdateFirstName.getText().toString().trim();
            String lastName = tvUpdateLastName.getText().toString().trim();
            String age = tvUpdateAge.getText().toString().trim();
            String contact = tvUpdateCellNumber.getText().toString().trim();
            String experience = tvUpdateExperience.getText().toString().trim();
            String qualifications = "";
            int count = 0;
           // CustomItem itemSelected = (CustomItem) sp_qualifications.getSelectedItem();

            if(firstName.isEmpty() || lastName.isEmpty() || age.isEmpty() || contact.isEmpty() || experience.isEmpty())
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
            else if(eduList.size() == 0 && dr.getQualifications().equals("default"))
            {
                Toast.makeText(this, "Please select qualifications", Toast.LENGTH_SHORT).show();
            }
            else
            {
                dr.setUserFirstName(tvUpdateFirstName.getText().toString());
                dr.setUserLastName(tvUpdateLastName.getText().toString());
                dr.setAge(tvUpdateAge.getText().toString());
                dr.setContactNumber(tvUpdateCellNumber.getText().toString());
                dr.setExperience(tvUpdateExperience.getText().toString());

                if(eduList.size() != 0)
                {
                    for (String s : eduList)
                    {
                        if(count == 0)
                        {
                            qualifications = s;
                        }
                        else
                        {
                            qualifications = qualifications +" | "+  s;
                        }
                    }
                    dr.setQualifications(qualifications);
                }
                if(spUpdateSpecialization.getSelectedItemPosition() > 0)
                {
                    CustomItem item = (CustomItem) spUpdateSpecialization.getSelectedItem();
                    dr.setSpecialty(item.getSpinnerItemName());
                }
                updateDetails(dr);
            }

        }
    }
    private void saveUpdatedData(String urlLink, boolean flag)
    {
        if(flag)
        {
            if(AppClass.role.equals("Patient"))
            {
                patient.setPhotoUrl(urlLink);
                sendData();

            }
            else
            {
                dr.setPhotoUrl(urlLink);
                sendData();
            }


        }
        else
        {
            sendData();
        }

    }
}