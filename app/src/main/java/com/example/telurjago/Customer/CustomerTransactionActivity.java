package com.example.telurjago.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.telurjago.Model.Request;
import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.R;
import com.example.telurjago.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CustomerTransactionActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    // i wrote start and stop when the adapter cant reveal

    @Override
    protected void onStart() { //When app Start, adapter executed
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() { // adapter stopped
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_transaction);

        BottomNavigationView navView1 = findViewById(R.id.nav_view);

        navView1.setSelectedItemId(R.id.cust_navigation_transaction);

        navView1.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.cust_navigation_home:
                        startActivity(new Intent(getApplicationContext()
                                , HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.cust_navigation_transaction:
                        return true;
                    case R.id.cust_navigation_cart :
                        startActivity(new Intent(getApplicationContext()
                                , CustomerCartActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.cust_navigation_user :
                        startActivity(new Intent(getApplicationContext()
                                , CustomerAccountActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }
        });

        // Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");

        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // if we start Transaction activity from HomeActivity
        // we will not put any extra , so we just loadorders by phone from Prevalent
        if (getIntent() == null)
            loadOrders(Prevalent.currentOnlineUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("Userphone"));
        loadOrders(Prevalent.currentOnlineUser.getPhone());


    }

    private void loadOrders(String phone) {

        //Change this if errors come
        //Modified by me
        Query query = requests.orderByChild("phone")
                .equalTo(phone);

        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(query, Request.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final Request model) {

                viewHolder.txtOrderID.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Prevalent.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());

                if (model.getStatus().equals("1") || model.getStatus().equals("2")){

                    viewHolder.cancelBtn.setVisibility(View.INVISIBLE);

                }
                else if (model.getStatus().equals("0")){

                    viewHolder.cancelBtn.setVisibility(View.VISIBLE);

                }

                // New Event Button
                viewHolder.detailsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent orderDetail = new Intent(CustomerTransactionActivity.this, CustomerOrderDetailsActivity.class);
                        Prevalent.customerCurrentRequest = model;
                        orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(orderDetail);

                    }
                });

                viewHolder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteOrder(adapter.getRef(position).getKey());

                    }
                });

            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent, false);

                return new OrderViewHolder (view);
            }

        };

        recyclerView.setAdapter(adapter);

    }

    private void deleteOrder(String key) {

        requests.child(key).removeValue();

    }

//    private String convertCodeToStatus(String status) {
//
//        if (status.equals("0"))
//            return "Placed";
//        else if (status.equals("1"))
//            return "Shipping";
//        else
//            return "Shipped";
//
//    }
}
