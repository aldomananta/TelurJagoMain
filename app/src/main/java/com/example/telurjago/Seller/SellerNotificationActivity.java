package com.example.telurjago.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.telurjago.Model.SellerRequest;
import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.R;
import com.example.telurjago.ViewHolder.SellerOrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class SellerNotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<SellerRequest, SellerOrderViewHolder> adapter;

    FirebaseDatabase db;
    DatabaseReference requests;
    FirebaseAuth firebaseAuth;
    String sid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    MaterialSpinner spinner;

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_notification);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setSelectedItemId(R.id.navigation_notifications);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.navigation_home :
                        startActivity(new Intent(getApplicationContext()
                                , SellerHomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_products:
                        startActivity(new Intent(getApplicationContext()
                                , SellerProductActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_notifications :
                        return true;
                    case R.id.navigation_user :
                        startActivity(new Intent(getApplicationContext()
                                , SellerAccountActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }
        });

        //Firebase
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Request");

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.seller_listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(); // Load all orders

    }

    private void loadOrders() {

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = db.getReference().child("Request");
        Query query = requests.orderByChild("products/0/sid").equalTo(sid); // Need to be specified the phone number like using equalsto function

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
                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showUpdateDialog(adapter.getRef(position).getKey(),
                                adapter.getItem(position));

                    }
                });

                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteOrder(adapter.getRef(position).getKey());

                    }
                });

                holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent orderDetail = new Intent(SellerNotificationActivity.this, SellerOrderDetailsActivity.class);
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

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals(Prevalent.UPDATE))
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        else if (item.getTitle().equals(Prevalent.DELETE))
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {

        requests.child(key).removeValue();

    }

    private void showUpdateDialog(String key, final SellerRequest item) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SellerNotificationActivity.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);

        spinner = (MaterialSpinner) view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed", "Shipping", "Shipped");

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                requests.child(localKey).setValue(item);
                adapter.notifyDataSetChanged(); // Add to update item size

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }
}
