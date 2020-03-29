package com.sliit.sliitmadnew02;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequesterSettingsActivity extends AppCompatActivity
{

    private Toolbar mToolbar;
    private EditText userName , userProfName , userStatus , userCountry , userGender , userRelation , userDOB;
    private Button UpdateAccountSettingsButton;
    private CircleImageView userProfImage;
    private ProgressDialog loadingBar;
    private DatabaseReference SettingsUserRef;
    private FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;
    private String currentUserId;
    final static int Gallery_Pick = 1;
    private Button TopicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users1").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images1");
        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.settings_username);
        userProfName = (EditText) findViewById(R.id.settings_profile_full_name);
        userStatus = (EditText) findViewById(R.id.settings_status);
        userCountry = (EditText) findViewById(R.id.settings_country);
        userGender = (EditText) findViewById(R.id.settings_gender);
        userRelation = (EditText) findViewById(R.id.settings_relationship_status);
        userDOB = (EditText) findViewById(R.id.settings_dob);
        userProfImage = (CircleImageView) findViewById(R.id.settings_profile_image);
        UpdateAccountSettingsButton = (Button) findViewById(R.id.update_account_settings_buttons);
        TopicButton =(Button) findViewById(R.id.settings_topic_catalogue);
        loadingBar = new ProgressDialog(this);

        SettingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String myProfileImage = dataSnapshot.child("profileimage1").getValue().toString();
                    String myUserName = dataSnapshot.child("username1").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname1").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status1").getValue().toString();
                    String myDOB = dataSnapshot.child("dob1").getValue().toString();
                    String myCountry = dataSnapshot.child("country1").getValue().toString();
                    String myGender = dataSnapshot.child("gender1").getValue().toString();
                    String myRelationStatus = dataSnapshot.child("relationshipstatus1").getValue().toString();

                    Picasso.with(RequesterSettingsActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    userName.setText(myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText(myDOB);
                    userCountry.setText(myCountry);
                    userGender.setText(myGender);
                    userRelation.setText(myRelationStatus);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });

        UpdateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidateAccountInfo();
            }
        });

        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , Gallery_Pick);
            }
        });

        TopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToTopicActivity();
            }
        });



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1 , 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Profile  Image");
                loadingBar.setMessage("Please wait , while we are updating your profile image");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                Uri resultUri = result.getUri();
                StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(RequesterSettingsActivity.this , "Profile image uploaded successfully..." , Toast.LENGTH_LONG).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            SettingsUserRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(RequesterSettingsActivity.this , RequesterSettingsActivity.class);
                                                startActivity(selfIntent);
                                                Toast.makeText(RequesterSettingsActivity.this , "Profile image stored successfully." , Toast.LENGTH_LONG).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(RequesterSettingsActivity.this , "Error occurred :" + message , Toast.LENGTH_LONG).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this , "Error Occurred : Image can not be cropped , Try again. " , Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        }
    }

    private void ValidateAccountInfo()
    {
        String username1 = userName.getText().toString();
        String profilename1 = userProfName.getText().toString();
        String status1 = userStatus.getText().toString();
        String dob1 = userDOB.getText().toString();
        String country1 = userCountry.getText().toString();
        String gender1 = userGender.getText().toString();
        String relation1 = userRelation.getText().toString();

        if (TextUtils.isEmpty(username1))
        {
            Toast.makeText(this , "Please write your Short name..." , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(profilename1))
        {
            Toast.makeText(this , "Please write your profile full name..." , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(status1))
        {
            Toast.makeText(this , "Please write your status or company status..." , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(dob1))
        {
            Toast.makeText(this , "Please write your active hours..." , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(country1))
        {
            Toast.makeText(this , "Please write your country..." , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(gender1))
        {
            Toast.makeText(this , "Please write customer or company type..." , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(relation1))
        {
            Toast.makeText(this , "Please write your all contacts..." , Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Profile  Image");
            loadingBar.setMessage("Please wait , while we are updating your profile image");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            UpdateAccountInfo(username1 , profilename1 , status1 , dob1 , country1 , gender1 , relation1);
        }
    }

    private void UpdateAccountInfo(String username1, String profilename1, String status1, String dob1, String country1, String gender1, String relation1)
    {
        HashMap userMap = new HashMap();
        userMap.put("username1" , username1);
        userMap.put("fullname1" , profilename1);
        userMap.put("status1" , status1);
        userMap.put("dob1" , dob1);
        userMap.put("country1" , country1);
        userMap.put("gender1" , gender1);
        userMap.put("relationshipstatus1" , relation1);

        SettingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    SendUserToMainActivity();
                    Toast.makeText(RequesterSettingsActivity.this, "Account settings Updated Successfully..." , Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else
                {
                    Toast.makeText(RequesterSettingsActivity.this , "Error occurred, while updating account settings..." , Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(RequesterSettingsActivity.this , RequesterMainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToTopicActivity()
    {
        Intent topicIntent = new Intent(RequesterSettingsActivity.this , PostActivity.class);
        startActivity(topicIntent);
    }
}
