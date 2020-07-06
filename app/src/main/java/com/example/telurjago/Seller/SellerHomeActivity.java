package com.example.telurjago.Seller;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.telurjago.Customer.HomeActivity;
import com.example.telurjago.Model.SellerRequest;
import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.R;
import com.example.telurjago.Service.ListenOrder;
import com.example.telurjago.ViewHolder.SellerOrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SellerHomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<SellerRequest, SellerOrderViewHolder> adapter;

    FirebaseDatabase db;
    DatabaseReference requests;
    FirebaseAuth firebaseAuth;
    String sid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    TextView welcome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);


        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.navigation_home :
                        return true;
                    case R.id.navigation_products:
                        startActivity(new Intent(getApplicationContext()
                                , SellerProductActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_notifications :
                        startActivity(new Intent(getApplicationContext()
                                , SellerNotificationActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_user :
                        startActivity(new Intent(getApplicationContext()
                                ,SellerAccountActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }
        });

        //Firebase
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Sellers");
//
//        //Init
//        recyclerView = (RecyclerView) findViewById(R.id.seller_listOrders);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);

   //     loadHistory(); // Load all histories

        welcome = findViewById(R.id.Seller_welcome);


        loadData(welcome);


    }

    private void loadData(final TextView welcome) {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //authenticate user
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Sellers").child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("name").getValue().toString();

                    welcome.setText("Welcome to "+name+" Store");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadHistory() {

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
       // DatabaseReference databaseReference = db.getReference().child("Request");
        Query query = requests.orderByChild("products/0/sid").equalTo(sid);// Need to be specified the phone number like using equalsto function
        Query query2 = requests.orderByChild("status").equalTo("2");

        FirebaseRecyclerOptions<SellerRequest> options =
                new FirebaseRecyclerOptions.Builder<SellerRequest>()
                        .setQuery(query, SellerRequest.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<SellerRequest, SellerOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SellerOrderViewHolder holder, final int position, @NonNull final SellerRequest model) {

                holder.txtOrderID.setText(adapter.getRef(position).getKey());
                holder.txtOrderStatus.setText(Prevalent.convertCodeToStatus(model.getStatus()));
                holder.txtOrderAddress.setText(model.getAddress());
                holder.txtOrderPhone.setText(model.getPhone());

                // New event button

                holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent orderDetail = new Intent(SellerHomeActivity.this, SellerOrderDetailsActivity.class);
                        Prevalent.currentRequest = model;
                        orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(orderDetail);

                    }
                });

            }

            @NonNull
            @Override
            public SellerOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.seller_order_layout, parent, false);

                return new SellerOrderViewHolder (view);
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

}
