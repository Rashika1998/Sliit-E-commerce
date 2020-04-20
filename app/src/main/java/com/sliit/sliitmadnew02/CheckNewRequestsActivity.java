package com.sliit.sliitmadnew02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sliit.sliitmadnew02.Interface.ItemClickListner;
import com.sliit.sliitmadnew02.Model.Products;
import com.sliit.sliitmadnew02.ViewHolder.ItemViewHolder;
import com.sliit.sliitmadnew02.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class CheckNewRequestsActivity extends AppCompatActivity
{

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_new_requests);


        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView = findViewById(R.id.admin_requests_checklist);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductsRef.orderByChild("type").equalTo("request") , Products.class)
                .build();

        FirebaseRecyclerAdapter<Products , ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ItemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ItemViewHolder holder, int position, @NonNull final Products model)
                    {

                        holder.txtRequesterProductStatus.setText(model.getProductStatus());
                        holder.txtRequesterName.setText("Requester Name : " + model.getuName());
                        holder.txtRequesterAddress.setText("Requester Address : " + model.getuAddress());
                        holder.txtRequesterPhone.setText("Requester Phone : " + model.getuPhone());

                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("price: " + model.getPrice() + " $");
                        Picasso.with(CheckNewRequestsActivity.this).load(model.getImage()).placeholder(R.drawable.profile).into(holder.imageView);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                final String productID = model.getPid();
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes" ,
                                                "No"

                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(CheckNewRequestsActivity.this);
                                builder.setTitle("Can You Approve this Request...?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position)
                                    {
                                        if (position == 0)
                                        {
                                            ChangeProductStatus(productID);
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
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_requests_layout , parent , false);
                        ItemViewHolder holder = new ItemViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void ChangeProductStatus(String productID)
    {
        unverifiedProductsRef.child(productID).child("productStatus").setValue("Approved This Request").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(CheckNewRequestsActivity.this, "That request is approved by you...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
