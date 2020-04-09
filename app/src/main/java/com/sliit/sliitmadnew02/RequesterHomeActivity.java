package com.sliit.sliitmadnew02;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RequesterHomeActivity extends AppCompatActivity
{

    public TextView mTextMessage;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;

    private Button AllItemDisplayButton;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
        {
            switch (menuItem.getItemId())
            {
                case R.id.navigation_home :
                    return true;

                case R.id.navigation_add :
                    Intent intent2 = new Intent(RequesterHomeActivity.this , RequesterProductCategoryActivity.class);
                    startActivity(intent2);
                    return true;

                case R.id.navigation_logout :
                    final FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();

                    Intent intent3 = new Intent(RequesterHomeActivity.this , MainActivity.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent3);
                    finish();
                    return true;

            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_home);





        //Special

        //this is layout background for drawer bottom
        //android:background="?android:attr/windowBackground"






        /*
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
         */

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Requests");

        AllItemDisplayButton = (Button) findViewById(R.id.button_all_item_show);
        AllItemDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(RequesterHomeActivity.this , RequesterShowItemsActivity.class);
                startActivity(intent);
            }
        });


    }

    /*

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Requests> options =
                new FirebaseRecyclerOptions.Builder<Requests>()
                        .setQuery(unverifiedProductsRef.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()) , Requests.class)
                        .build();

        FirebaseRecyclerAdapter<Requests , RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull final Requests model)
                    {

                        holder.txtRequestProductStatus.setText(model.getRequesterProductStatus());
                        holder.txtRequesterName.setText(model.getRequesterName());
                        holder.txtRequesterAddress.setText(model.getRequesterAddress());
                        holder.txtRequesterPhone.setText(model.getRequesterPhone());

                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("price:" + model.getPrice() + " $");
                        Picasso.with(RequesterHomeActivity.this).load(model.getImage()).placeholder(R.drawable.profile).into(holder.imageView);


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

                                AlertDialog.Builder builder = new AlertDialog.Builder(RequesterHomeActivity.this);
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
*/



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

/*

                    }

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_requests_layout , parent , false);
                        RequestsViewHolder holder = new RequestsViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    private void deleteRequest(String productID)
    {
        unverifiedProductsRef.child(productID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(RequesterHomeActivity.this, "That request is deleted by you...", Toast.LENGTH_SHORT).show();
            }
        });
    }

 */
}
