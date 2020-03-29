package com.sliit.sliitmadnew02;

import androidx.appcompat.app.AppCompatActivity;

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

public class ProfileActivity extends AppCompatActivity
{

    private TextView userName , userProfName , userStatus , userCountry , userGender , userRelation , userDOB;
    private CircleImageView userProfileImage;

    private DatabaseReference profileUserRef  , PostsRef;
    private FirebaseAuth mAuth;
    private Button MyPosts ;
    private String currentUserId;

    private int  countPosts =0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users1").child(currentUserId);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        userName = (TextView) findViewById(R.id.my_username);
        userProfName = (TextView) findViewById(R.id.my_profile_full_name);
        userStatus = (TextView) findViewById(R.id.my_profile_status);
        userCountry = (TextView) findViewById(R.id.my_country);
        userGender = (TextView) findViewById(R.id.my_gender);
        userRelation = (TextView) findViewById(R.id.my_relationship_status);
        userDOB = (TextView) findViewById(R.id.my_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.my_profile_pic);
        MyPosts = (Button) findViewById(R.id.my_post_button);



        MyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToMyPostsActivity();
            }
        });

        PostsRef.orderByChild("uid")
                .startAt(currentUserId).endAt(currentUserId + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            countPosts = (int) dataSnapshot.getChildrenCount();
                            MyPosts.setText(Integer.toString(countPosts) + " Posts");
                        }
                        else
                        {
                            MyPosts.setText("No Posts");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                    }
                });



        profileUserRef.addValueEventListener(new ValueEventListener() {
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

                    Picasso.with(ProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
                    userName.setText("@" + myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText("Active Hours:" + myDOB);
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
    }

    private void SendUserToMyPostsActivity()
    {
        Intent friendsIntent = new Intent(ProfileActivity.this , MyPostsActivity.class);
        startActivity(friendsIntent);
    }

}
