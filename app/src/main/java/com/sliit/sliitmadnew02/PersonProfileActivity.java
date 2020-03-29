package com.sliit.sliitmadnew02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity
{

    private TextView userName , userProfName , userStatus , userCountry , userGender , userRelation , userDOB;
    private CircleImageView userProfileImage;
    private DatabaseReference UsersRef ;
    private FirebaseAuth mAuth;
    private String senderUserId , receiverUserId ;
    private Button PersonPostButton;

    private DatabaseReference profileUserRef  , PostsRef;
    private String currentUserId;
    private int  countPosts =0;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);


        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //new
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        PersonPostButton = (Button) findViewById(R.id.person_post_button);

        mToolbar = (Toolbar) findViewById(R.id.my_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Profile");
        IntializeFields();

        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationStatus = dataSnapshot.child("relationshipstatus").getValue().toString();

                    Picasso.with(PersonProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
                    userName.setText("@" + myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText("Active Hours :" + myDOB);
                    userCountry.setText("Country:" +  myCountry);
                    userGender.setText("Type:" + myGender);
                    userRelation.setText("Contacts:" + myRelationStatus);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });

        PersonPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToPersonPostActivity();
            }
        });

        PostsRef.orderByChild("uid")
                .startAt(receiverUserId).endAt(receiverUserId + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            countPosts = (int) dataSnapshot.getChildrenCount();
                            PersonPostButton.setText(Integer.toString(countPosts) + " Posts");
                        }
                        else
                        {
                            PersonPostButton.setText("No Posts");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                    }
                });
    }



    private void SendUserToPersonPostActivity()
    {
        //new new
        Intent i = new Intent (this, PersonPostsActivity.class);
        i.putExtra("puzzle", receiverUserId);
        startActivity(i);
    }


    private void IntializeFields()
    {
        userName = (TextView) findViewById(R.id.person_username);
        userProfName = (TextView) findViewById(R.id.person_full_name);
        userStatus = (TextView) findViewById(R.id.person_profile_status);
        userCountry = (TextView) findViewById(R.id.person_country);
        userGender = (TextView) findViewById(R.id.person_gender);
        userRelation = (TextView) findViewById(R.id.person_relationship_status);
        userDOB = (TextView) findViewById(R.id.person_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.person_profile_pic);


    }


}
