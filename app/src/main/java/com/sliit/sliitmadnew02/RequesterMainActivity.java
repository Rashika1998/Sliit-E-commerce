package com.sliit.sliitmadnew02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequesterMainActivity extends AppCompatActivity
{

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef , PostsRef , LikesRef;
    private ImageButton AddNewPostButton;
    String currentUserID;
    Boolean LikeChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_main);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users1");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("RoWeb");
        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(RequesterMainActivity.this , drawerLayout , R.string.drawer_open , R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        View navView = navigationView.inflateHeaderView(R.layout.requester_navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.exists()))
                {
                    if (dataSnapshot.hasChild("fullname1"))
                    {
                        String fullname = dataSnapshot.child("fullname1").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }
                    if (dataSnapshot.hasChild("profileimage1"))
                    {
                        String image = dataSnapshot.child("profileimage1").getValue().toString();
                        Picasso.with(RequesterMainActivity.this).load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    }
                    else
                    {
                        Toast.makeText(RequesterMainActivity.this , "Profile name does not exist" , Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                UserMenuSelector(item);
                return false;
            }
        });

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToPostActivity();
            }
        });

        DisplayAllUsersPosts();
    }



    private void DisplayAllUsersPosts()
    {

        Query SortPostsInDecendingOrder = PostsRef.orderByChild("counter");


        FirebaseRecyclerAdapter<Posts , PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                        (
                                Posts.class ,
                                R.layout.all_posts_layout ,
                                PostsViewHolder.class ,
                                SortPostsInDecendingOrder

                        )
                {
                    @Override
                    protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position)
                    {
                        final String  PostKey = getRef(position).getKey();

                        viewHolder.setFullname(model.getFullname());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setData(model.getData());

                        viewHolder.setItemName(model.getItemName());

                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setCountry(model.getCountry());
                        viewHolder.setItemLink(model.getItemLink());

                        viewHolder.setProfileimage(getApplicationContext() , model.getProfileimage());
                        viewHolder.setPostimage(getApplicationContext() , model.getPostimage());

                        viewHolder.setLikeButtonStatus(PostKey);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent clickPostIntent = new Intent(RequesterMainActivity.this , ClickPostActivity.class);
                                clickPostIntent.putExtra("PostKey" , PostKey);
                                startActivity(clickPostIntent);
                            }
                        });


                        viewHolder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent commentsIntent = new Intent(RequesterMainActivity.this , CommentsActivity.class);
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
        postList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;

        ImageButton LikePostButton , CommentPostButton;
        TextView DisplayNoOfLikes;
        int countLikes;
        String currentUserId;
        DatabaseReference LikesRef;

        public PostsViewHolder(@NonNull View itemView)
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



        public void setFullname(String fullname1)
        {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname1);
        }

        public void setProfileimage(Context ctx , String profileimage1)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileimage1).into(image);
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

        public void setItemName(String itemName1)
        {
            TextView PostName = (TextView) mView.findViewById(R.id.post_name);
            PostName.setText(itemName1);
        }

        public void setDescription(String description1)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description1);
        }

        public void setCountry(String country1)
        {
            TextView PostCountry = (TextView) mView.findViewById(R.id.post_country);
            PostCountry.setText(country1);
        }

        public void setItemLink(String itemLink1)
        {
            TextView PostLink = (TextView) mView.findViewById(R.id.post_link);
            PostLink.setText(itemLink1);
        }

        public void setPostimage(Context ctx , String postimage1)
        {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(postimage1).into(PostImage);
        }

    }

    private void SendUserToPostActivity()
    {
        Intent addNewPostIntent = new Intent(RequesterMainActivity.this , PostActivity.class);
        startActivity(addNewPostIntent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence()
    {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.hasChild(current_user_id))
                {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(RequesterMainActivity.this , SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(RequesterMainActivity.this , LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item)
    {
        switch (item.getItemId())
        {
            case  R.id.nav_post:
                SendUserToPostActivity();
                break;
            case  R.id.nav_profile :
                SendUserToProfileActivity();
                Toast.makeText(this , "Profile" , Toast.LENGTH_SHORT).show();
                break;
            case  R.id.nav_home :
                Toast.makeText(this , "Home" , Toast.LENGTH_SHORT).show();
                break;
            case  R.id.nav_friends :
                SendUserToCatalogueActivity();
                Toast.makeText(this , "Catalogue" , Toast.LENGTH_SHORT).show();
                break;
            case  R.id.nav_find_friends :
                SendUserToFindfriendsActivity();
                Toast.makeText(this , "Search Anything" , Toast.LENGTH_SHORT).show();
                break;
            case  R.id.nav_settings :
                SendUserToSettingsActivity();
                Toast.makeText(this , "Settings" , Toast.LENGTH_SHORT).show();
                break;
            case  R.id.nav_aboutUs :
                SendUserToAboutUsActivity();
                Toast.makeText(this , "About Us" , Toast.LENGTH_SHORT).show();
                break;
            case  R.id.nav_Logout :
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
    }


    private void SendUserToAboutUsActivity()
    {
        Intent aboutUsIntent = new Intent(RequesterMainActivity.this , PostActivity.class);
        startActivity(aboutUsIntent);
    }

    private void SendUserToCatalogueActivity()
    {
        Intent catalogueIntent = new Intent(RequesterMainActivity.this , PostActivity.class);
        startActivity(catalogueIntent);
    }

    private void SendUserToSettingsActivity()
    {
        Intent loginIntent = new Intent(RequesterMainActivity.this , RequesterSettingsActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToFindfriendsActivity()
    {
        Intent loginIntent = new Intent(RequesterMainActivity.this , PostActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToProfileActivity()
    {
        Intent ProfileIntent = new Intent(RequesterMainActivity.this , ProfileActivity.class);
        startActivity(ProfileIntent);
    }


}
