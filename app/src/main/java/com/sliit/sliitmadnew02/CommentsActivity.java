package com.sliit.sliitmadnew02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity
{

    private RecyclerView CommentsList;
    private ImageButton PostCommentButton;
    private EditText CommentsInputText;

    private DatabaseReference UsersRef , PostsRef;
    private FirebaseAuth mAuth;

    private String Post_Key , current_user_id;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        Post_Key = getIntent().getExtras().get("PostKey").toString();

        mToolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Let's talk");

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users1");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");

        CommentsList = (RecyclerView) findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        PostCommentButton = (ImageButton) findViewById(R.id.post_comment_btn);
        CommentsInputText = (EditText) findViewById(R.id.comment_input);

        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userFullName = dataSnapshot.child("fullname1").getValue().toString();
                            //new new
                            String commentImage = dataSnapshot.child("profileimage1").getValue().toString();
                            //ValidateComment(userName , commentImage);
                            ValidateComment(userFullName, commentImage);
                            CommentsInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();


        Query myPostsQuery = PostsRef.orderByChild("time");

        FirebaseRecyclerAdapter<Comments , CommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>
                (
                        Comments.class ,
                        R.layout.all_comments_layout ,
                        CommentsViewHolder.class ,
                        myPostsQuery

                )
        {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comments model, int position)
            {

                //new new
                viewHolder.setProfileimage(getApplicationContext() , model.getProfileimage());


                viewHolder.setFullname(model.getFullname());
                //viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setData(model.getData());
                viewHolder.setTime(model.getTime());
            }
        };

        CommentsList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public CommentsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mView = itemView;

        }

        //new new

        public void setFullname(String fullname1)
        {
            TextView myUserName = (TextView) mView.findViewById(R.id.comment_username);
            myUserName.setText(""+fullname1 + "  ");
        }

        public void setProfileimage(Context ctx , String profileimage1)
        {
            CircleImageView ommentImage = (CircleImageView) mView.findViewById(R.id.comment_profile_image);
            Picasso.with(ctx).load(profileimage1).into(ommentImage);
        }

        /*
        public void setUsername(String username)
        {
            TextView myUserName = (TextView) mView.findViewById(R.id.comment_username);
            myUserName.setText(" "+username + "  ");
        }
         */
        public void setComment(String comment)
        {
            TextView myComment = (TextView) mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }
        public void setData(String data)
        {
            TextView myDate = (TextView) mView.findViewById(R.id.comment_date);
            myDate.setText("  Date: "+data);
        }
        public void setTime(String time)
        {
            TextView myTime = (TextView) mView.findViewById(R.id.comment_time);
            myTime.setText("  Time: "+time);
        }


    }

    private void ValidateComment(String userFullName , String commentImage)
    {
        String commentText = CommentsInputText.getText().toString();



        if (TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this , "Please Comment" , Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());
            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            final String saveCurrentTime = currentTime.format(calFordTime.getTime());
            final String RandomKey = current_user_id + saveCurrentDate + saveCurrentTime;


            HashMap commentsMap = new HashMap();
            commentsMap.put("uid" , current_user_id);
            commentsMap.put("comment" , commentText);
            commentsMap.put("date" , saveCurrentDate);
            commentsMap.put("time" , saveCurrentTime);
            //commentsMap.put("username" , userName);
            commentsMap.put("fullname1" , userFullName);
            commentsMap.put("profileimage1" , commentImage);

            PostsRef.child(RandomKey).updateChildren(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(CommentsActivity.this, "Commented", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(CommentsActivity.this, "Error occurred , try again...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }
}

