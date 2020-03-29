package com.sliit.sliitmadnew02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity
{

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;
    private Button TypeButton;

    private EditText PostName;
    private EditText PostCountry;
    private EditText PostLink;


    private static  final int Gallery_Pick = 1;
    private Uri ImageUri;
    private String Description , Name , Link , Country;
    private StorageReference PostsImagesReference;
    private DatabaseReference UsersRef  , PostsRef;
    private FirebaseAuth mAuth;
    private String saveCurrentDate , saveCurrentTime , postRandomName , downloadUrl , current_user_id;
    private long countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        PostsImagesReference = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users1");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        SelectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        UpdatePostButton = (Button) findViewById(R.id.update_post_button);
        PostDescription = (EditText) findViewById(R.id.post_description);
        TypeButton = (Button) findViewById(R.id.topic_catalogue);

        PostName = (EditText) findViewById(R.id.post_name);
        PostCountry = (EditText) findViewById(R.id.post_country);
        PostLink = (EditText) findViewById(R.id.post_link);



        loadingBar = new ProgressDialog(this);
        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update post");

        SelectPostImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OpenGallery();
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidatePostInfo();
            }
        });

        TypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToTopicActivity();
            }
        });

    }



    private void ValidatePostInfo()
    {
        Description = PostDescription.getText().toString();
        Name = PostName.getText().toString();
        Country = PostCountry.getText().toString();
        Link = PostLink.getText().toString();

        if (ImageUri == null)
        {
            Toast.makeText(this , "Please select post image" , Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Name))
        {
            Toast.makeText(this , "Please Include Item Name" , Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this , "Please Write Item Description" , Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Country))
        {
            Toast.makeText(this , "Please Include contacts" , Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Link))
        {
            Toast.makeText(this , "Please Include product type correctly as below" , Toast.LENGTH_LONG).show();
        }

        else
        {
            loadingBar.setTitle("Add new Post");
            loadingBar.setMessage("Please wait , while we are uploading your new item");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();
        }
    }

    private void StoringImageToFirebaseStorage()
    {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = PostsImagesReference.child("Post Images1").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    //Toast.makeText(PostActivity.this , "Image uploaded successfully to storage..." , Toast.LENGTH_SHORT).show();
                    SavePostInformationToDatabase();
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this , "Error Occurred :" + message , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SavePostInformationToDatabase()
    {
        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    countPosts = dataSnapshot.getChildrenCount();
                }
                else
                {
                    countPosts = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });

        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String userFullName = dataSnapshot.child("fullname1").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileimage1").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid" , current_user_id);
                    postsMap.put("data" , saveCurrentDate);
                    postsMap.put("time" , saveCurrentTime);
                    postsMap.put("name1" , Name);

                    postsMap.put("description1" , Description);
                    postsMap.put("country1" , Country);
                    postsMap.put("link1" , Link);

                    postsMap.put("postimage1" , downloadUrl);
                    postsMap.put("profileimage1" , userProfileImage);
                    postsMap.put("fullname1" , userFullName);

                    postsMap.put("counter" , countPosts);
                    PostsRef.child(current_user_id + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if (task.isSuccessful())
                                    {

                                        //Toast.makeText(PostActivity.this , "New post is updated successfully..." , Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        SendUserToMainActivity();
                                    }
                                    else
                                    {
                                        //Toast.makeText(PostActivity.this , "Error occurred while uploading your post" , Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent , Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null)
        {
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PostActivity.this , RequesterMainActivity.class);
        startActivity(mainIntent);
    }

    private void SendUserToTopicActivity()
    {
        Intent topicIntent = new Intent(PostActivity.this , PostActivity.class);
        startActivity(topicIntent);
    }
}
