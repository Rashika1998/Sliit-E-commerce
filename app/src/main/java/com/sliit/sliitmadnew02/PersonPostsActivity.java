package com.sliit.sliitmadnew02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonPostsActivity extends AppCompatActivity
{

    private Toolbar mToolbar;
    private RecyclerView PersonPostsList;
    private FirebaseAuth mAuth;
    private DatabaseReference PostsRef , UsersRef , LikesRef;
    private String currentUserID , receiverUserId , recid;
    Boolean LikeChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_posts);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        //receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        //new new
        Intent i = getIntent();
        receiverUserId = i.getStringExtra("puzzle");

        mToolbar = (Toolbar) findViewById(R.id.person_posts_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Uploaded Posts");


        PersonPostsList = (RecyclerView) findViewById(R.id.person_all_posts_list);
        PersonPostsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        PersonPostsList.setLayoutManager(linearLayoutManager);

        DisplayMyAllPosts();
    }


    private void DisplayMyAllPosts()
    {

        Query myPostsQuery = PostsRef.orderByChild("uid")
                .startAt(receiverUserId).endAt(receiverUserId + "\uf8ff");

        FirebaseRecyclerAdapter<Posts , MyPostsActivity.MyPostsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Posts, MyPostsActivity.MyPostsViewHolder>
                (
                        Posts.class ,
                        R.layout.all_posts_layout ,
                        MyPostsActivity.MyPostsViewHolder.class ,
                        myPostsQuery
                )
        {
            @Override
            protected void populateViewHolder(MyPostsActivity.MyPostsViewHolder viewHolder, Posts model, int position)
            {

                final String  PostKey = getRef(position).getKey();

                viewHolder.setFullname(model.getFullname());
                viewHolder.setTime(model.getTime());
                viewHolder.setData(model.getData());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setCountry(model.getCountry());
                viewHolder.setItemName(model.getItemName());
                viewHolder.setItemLink(model.getItemLink());

                viewHolder.setProfileimage(getApplicationContext() , model.getProfileimage());
                viewHolder.setPostimage(getApplicationContext() , model.getPostimage());

                viewHolder.setLikeButtonStatus(PostKey);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent clickPostIntent = new Intent(PersonPostsActivity.this , ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey" , PostKey);
                        startActivity(clickPostIntent);
                    }
                });


                viewHolder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent commentsIntent = new Intent(PersonPostsActivity.this , CommentsActivity.class);
                        commentsIntent.putExtra("PostKey" , PostKey);
                        startActivity(commentsIntent);
                    }
                });

                viewHolder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        LikeChecker = true;

                        LikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (LikeChecker.equals(true))
                                {
                                    if (dataSnapshot.child(PostKey).hasChild(currentUserID))
                                    {
                                        LikesRef.child(PostKey).child(currentUserID).removeValue();
                                        LikeChecker = false;
                                    }
                                    else
                                    {
                                        LikesRef.child(PostKey).child(currentUserID).setValue(true);
                                        LikeChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {
                            }
                        });
                    }
                });

            }
        };

        PersonPostsList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class MyPostsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;

        ImageButton LikePostButton , CommentPostButton;
        TextView DisplayNoOfLikes;
        int countLikes;
        String currentUserId ;
        DatabaseReference LikesRef;

        public MyPostsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView = itemView;

            LikePostButton = (ImageButton) mView.findViewById(R.id.like_button);
            CommentPostButton = (ImageButton) mView.findViewById(R.id.comment_button);
            DisplayNoOfLikes = (TextView) mView.findViewById(R.id.display_no_of_likes);
            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setLikeButtonStatus(final String PostKey)
        {
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child(PostKey).hasChild(currentUserId))
                    {
                        countLikes = (int ) dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.votedbutton);
                        DisplayNoOfLikes.setText((Integer.toString(countLikes) + (" Votes")));
                    }
                    else
                    {
                        countLikes = (int ) dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.votebutton);
                        DisplayNoOfLikes.setText(Integer.toString(countLikes) + (" Votes"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                }
            });
        }

        public void setFullname(String fullname)
        {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileimage(Context ctx , String profileimage)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileimage).into(image);
        }

        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("   " + time);
        }

        public void setData(String data)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("   " + data);
        }

        public void setItemName(String name)
        {
            TextView PostName = (TextView) mView.findViewById(R.id.post_name);
            PostName.setText(name);
        }

        public void setDescription(String description)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setCountry(String country)
        {
            TextView PostCountry = (TextView) mView.findViewById(R.id.post_country);
            PostCountry.setText(country);
        }

        public void setItemLink(String itemLink)
        {
            TextView PostLink = (TextView) mView.findViewById(R.id.post_link);
            PostLink.setText(itemLink);
        }

        public void setPostimage(Context ctx , String postimage)
        {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(postimage).into(PostImage);
        }
    }
}
