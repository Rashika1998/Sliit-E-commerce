package com.sliit.sliitmadnew02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class FindProductsActivity extends AppCompatActivity
{

    private Toolbar mToolbar;

    private ImageButton SearchButton;
    private EditText SearchInputText;

    private RecyclerView searchResultList;

    private DatabaseReference allPostsDatabaseRef;

    private FirebaseAuth mAuth;
    private DatabaseReference PostsRef , UsersRef , LikesRef;
    private String currentUserID;
    Boolean LikeChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_products);

        allPostsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mToolbar = (Toolbar) findViewById(R.id.find_products_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find what you want");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        searchResultList = (RecyclerView) findViewById(R.id.search_products_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        SearchButton = (ImageButton) findViewById(R.id.search_products_button);
        SearchInputText = (EditText) findViewById(R.id.search_products_input);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String searchProductsInput = SearchInputText.getText().toString();

                if (TextUtils.isEmpty(searchProductsInput))
                {
                    Toast.makeText(FindProductsActivity.this, "Type Product's name", Toast.LENGTH_SHORT).show();
                }else {
                    SearchProducts(searchProductsInput);
                }

            }
        });

    }


    private void SearchProducts(String searchProductsInput)
    {

        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();

        Query searchProductsQuery = allPostsDatabaseRef.orderByChild("name")
                .startAt(searchProductsInput).endAt(searchProductsInput + "\uf8ff");

        FirebaseRecyclerAdapter<Posts , PostsViewHolder1> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Posts, PostsViewHolder1>
                (
                        Posts.class ,
                        R.layout.all_posts_layout ,
                        PostsViewHolder1.class ,
                        searchProductsQuery
                )
        {
            @Override
            protected void populateViewHolder(PostsViewHolder1 viewHolder, Posts model, int position)
            {
                final String  PostKey = getRef(position).getKey();

                viewHolder.setFullname(model.getFullname());
                viewHolder.setProfileimage(getApplicationContext() , model.getProfileimage());
                viewHolder.setTime(model.getTime());
                viewHolder.setData(model.getData());
                viewHolder.setItemName(model.getItemName());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setCountry(model.getCountry());
                viewHolder.setItemLink(model.getItemLink());
                viewHolder.setPostimage(getApplicationContext() , model.getPostimage());

                viewHolder.setLikeButtonStatus(PostKey);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent clickPostIntent = new Intent(FindProductsActivity.this , ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey" , PostKey);
                        startActivity(clickPostIntent);
                    }
                });

                viewHolder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent commentsIntent = new Intent(FindProductsActivity.this , CommentsActivity.class);
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

        searchResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostsViewHolder1 extends RecyclerView.ViewHolder
    {
        View mView;

        ImageButton LikePostButton , CommentPostButton;
        TextView DisplayNoOfLikes;
        int countLikes;
        String currentUserId;
        DatabaseReference LikesRef;

        public PostsViewHolder1(View itemView)
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

        //setters are here...
        public void setFullname(String fullname)
        {
            TextView username1 = (TextView) mView.findViewById(R.id.post_user_name);
            username1.setText(fullname);
        }

        public void setProfileimage(Context ctx , String profileimage)
        {
            CircleImageView image1 = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileimage).into(image1);
        }

        public void setTime(String time)
        {
            TextView PostTime1 = (TextView) mView.findViewById(R.id.post_time);
            PostTime1.setText("   " + time);
        }

        public void setData(String data)
        {
            TextView PostDate1 = (TextView) mView.findViewById(R.id.post_date);
            PostDate1.setText("   " + data);
        }

        public void setItemName(String itemName)
        {
            TextView PostName1 = (TextView) mView.findViewById(R.id.post_name);
            PostName1.setText(itemName);
        }

        public void setDescription(String description)
        {
            TextView PostDescription1 = (TextView) mView.findViewById(R.id.post_description);
            PostDescription1.setText(description);
        }

        public void setCountry(String country)
        {
            TextView PostCountry = (TextView) mView.findViewById(R.id.post_country);
            PostCountry.setText(country);
        }

        public void setItemLink(String itemLink)
        {
            TextView PostLink1 = (TextView) mView.findViewById(R.id.post_link);
            PostLink1.setText(itemLink);
        }

        public void setPostimage(Context ctx ,String postimage)
        {
            ImageView PostImage1 = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(postimage).into(PostImage1);
        }

    }
}
