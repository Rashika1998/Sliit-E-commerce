package com.sliit.sliitmadnew02;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity
{

    private Toolbar mToolbar;
    private ImageView PostImage;
    private TextView PostDescription , PostName , PostLink , PostCountry;
    private Button DeletePostButton , EditPostButton;
    private DatabaseReference ClickPostRef;
    private FirebaseAuth mAuth;
    private String PostKey , currentUserID , databaseUserID , description , name , link, image , country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);


        mToolbar = (Toolbar) findViewById(R.id.click_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("See Post");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        PostImage = (ImageView) findViewById(R.id.click_post_image);
        PostDescription = (TextView) findViewById(R.id.click_post_description);

        PostName = (TextView) findViewById(R.id.click_post_name);
        PostCountry = (TextView) findViewById(R.id.click_post_country);
        PostLink = (TextView) findViewById(R.id.click_post_link);


        DeletePostButton = (Button) findViewById(R.id.delete_post_button);
        EditPostButton = (Button) findViewById(R.id.edit_post_button);

        DeletePostButton.setVisibility(View.INVISIBLE);
        EditPostButton.setVisibility(View.INVISIBLE);


        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    description = dataSnapshot.child("description1").getValue().toString();
                    name = dataSnapshot.child("name1").getValue().toString();
                    country = dataSnapshot.child("country1").getValue().toString();
                    link = dataSnapshot.child("link1").getValue().toString();
                    image = dataSnapshot.child("postimage1").getValue().toString();

                    databaseUserID = dataSnapshot.child("uid").getValue().toString();

                    PostDescription.setText(description);
                    PostName.setText(name);
                    PostCountry.setText(country);
                    PostLink.setText(link);

                    Picasso.with(ClickPostActivity.this).load(image).into(PostImage);

                    if (currentUserID.equals(databaseUserID)) {
                        DeletePostButton.setVisibility(View.VISIBLE);
                        EditPostButton.setVisibility(View.VISIBLE);
                    }

                    EditPostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditCurrentPost(description);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteCurrentPost();
            }
        });


    }

        private void EditCurrentPost(String description)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this );
            builder.setTitle("Edit Product Description ");

            final EditText inputField = new EditText(ClickPostActivity.this);
            inputField.setText(description);
            builder.setView(inputField);

            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    ClickPostRef.child("description1").setValue(inputField.getText().toString());
                    Toast.makeText(ClickPostActivity.this, "Post Updated successfully...", Toast.LENGTH_SHORT).show();

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });

            Dialog dialog = builder.create();
            dialog.show();
            //dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);
            dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimaryDark);

        }

        private void DeleteCurrentPost()
        {
            ClickPostRef.removeValue();
            SendUserToMainActivity();
            Toast.makeText(this , "Post has been deleted..." , Toast.LENGTH_SHORT).show();
        }

        private void SendUserToMainActivity()
        {
            Intent mainIntent = new Intent(ClickPostActivity.this , RequesterMainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }


}

