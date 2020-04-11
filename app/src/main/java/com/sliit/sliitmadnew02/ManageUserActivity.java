package com.sliit.sliitmadnew02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sliit.sliitmadnew02.Model.Products;
import com.sliit.sliitmadnew02.Model.Users;
import com.sliit.sliitmadnew02.ViewHolder.ItemViewHolder;
import com.sliit.sliitmadnew02.ViewHolder.UserViewHolder;
import com.squareup.picasso.Picasso;

public class ManageUserActivity extends AppCompatActivity
{

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);


        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = findViewById(R.id.all_requester_viewer);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(userRef , Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users , UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final UserViewHolder holder, int position, @NonNull final Users model)
                    {

                        holder.txtUserName.setText(model.getName());
                        holder.txtUserPhone.setText(model.getPhone());
                        Picasso.with(ManageUserActivity.this).load(model.getImage()).placeholder(R.drawable.profile).into(holder.userImageView);



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                final String phone = model.getPhone();
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes" ,
                                                "No"

                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(ManageUserActivity.this);
                                builder.setTitle("Want to delete this Request...?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position)
                                    {
                                        if (position == 0)
                                        {
                                            deleteRequest(phone);
                                        }
                                        if (position == 1)
                                        {

                                        }
                                    }
                                });
                                builder.show();
                            }
                        });




                        /*
                        final Products itemClick = model;
                        holder.setItemClickListner(new ItemClickListner()
                        {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick)
                            {

                            }
                        });

                         */




                    }

                    @NonNull
                    @Override
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_user_display_layout , parent , false);
                        UserViewHolder holder = new UserViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    //This functionality allow any requester to delete any approved or non approved product
    //which they have uploaded


    private void deleteRequest(String phone)
    {
        userRef.child(phone).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(ManageUserActivity.this, "That user is removed by you...", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
