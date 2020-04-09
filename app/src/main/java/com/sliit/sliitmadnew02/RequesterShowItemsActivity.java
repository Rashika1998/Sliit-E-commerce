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
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sliit.sliitmadnew02.Model.Products;
import com.sliit.sliitmadnew02.ViewHolder.ItemViewHolder;
import com.sliit.sliitmadnew02.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class RequesterShowItemsActivity extends AppCompatActivity
{

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_show_items);


        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = findViewById(R.id.requester_all_items_viewer);
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
                        .setQuery(unverifiedProductsRef.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()) , Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products , ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ItemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ItemViewHolder holder, int position, @NonNull final Products model)
                    {

                        //new new
                        holder.txtRequesterProductStatus.setText(model.getProductStatus());
                        holder.txtRequesterName.setText(model.getuName());
                        holder.txtRequesterAddress.setText(model.getuAddress());
                        holder.txtRequesterPhone.setText(model.getuPhone());

                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("price:" + model.getPrice() + " $");
                        Picasso.with(RequesterShowItemsActivity.this).load(model.getImage()).placeholder(R.drawable.profile).into(holder.imageView);


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

                                AlertDialog.Builder builder = new AlertDialog.Builder(RequesterShowItemsActivity.this);
                                builder.setTitle("Want to delete this Request...?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position)
                                    {
                                        if (position == 0)
                                        {
                                            deleteRequest(productID);
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


    //This functionality allow any requester to delete any approved or non approved product
    //which they have uploaded
    private void deleteRequest(String productID)
    {
        unverifiedProductsRef.child(productID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(RequesterShowItemsActivity.this, "That request is deleted by you...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}