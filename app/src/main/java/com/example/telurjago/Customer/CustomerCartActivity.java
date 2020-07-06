package com.example.telurjago.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telurjago.Database.Database;
import com.example.telurjago.Model.Order;
import com.example.telurjago.Model.Products;
import com.example.telurjago.Model.Request;
import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.R;
import com.example.telurjago.Seller.SellerAccountActivity;
import com.example.telurjago.Seller.SellerNotificationActivity;
import com.example.telurjago.Seller.SellerProductActivity;
import com.example.telurjago.ViewHolder.CartAdapter;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerCartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    Button btnPlaceOrder, deleteOrder;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);

        BottomNavigationView navView1 = findViewById(R.id.nav_view);

        navView1.setSelectedItemId(R.id.cust_navigation_cart);

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
                        startActivity(new Intent(getApplicationContext()
                                , CustomerTransactionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.cust_navigation_cart :

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


        //Firebase

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");

        //Init

        recyclerView = (RecyclerView) findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlaceOrder = (Button) findViewById(R.id.placeOrderBtn);
        deleteOrder = (Button) findViewById(R.id.deleteOrderBtn);

        LoadListFood(); // load food on cart

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowAlertDialog();

            }
        });

        deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Delete Cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(CustomerCartActivity.this, "Cart Empty", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

    }

    private void ShowAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CustomerCartActivity.this);
        alertDialog.setTitle("One More Step");
        alertDialog.setMessage("Enter your address : ");

        final EditText edtAddress = new EditText(CustomerCartActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress); // add edit text to alert dialog
        alertDialog.setIcon(R.drawable.cart);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Create new request
                Request request = new Request(

                        //Database format design for Request(Database column)
                        Prevalent.currentOnlineUser.getPhone(),
                        Prevalent.currentOnlineUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart

                );

                //Submit data to firebase
                //We will using System.CurrentMilli to key
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);
                //Delete Cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(CustomerCartActivity.this, "Thank you, order placed", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        alertDialog.show();

    }

    private void LoadListFood() {

        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        recyclerView.setAdapter(adapter);

        int total = 0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("id", "ID");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);


        txtTotalPrice.setText(fmt.format(total));

    }

}
