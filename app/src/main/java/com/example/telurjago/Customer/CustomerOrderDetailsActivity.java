package com.example.telurjago.Customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.R;
import com.example.telurjago.ViewHolder.OrderDetailAdapter;

public class CustomerOrderDetailsActivity extends AppCompatActivity {

    TextView order_id, order_phone, order_address, order_total;
    String order_id_value = "";
    RecyclerView firstProducts;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_details);

        order_id = (TextView) findViewById(R.id.customer_order_id_details);
        order_phone = (TextView) findViewById(R.id.customer_order_phone);
        order_address = (TextView) findViewById(R.id.customer_order_address_details);
        order_total = (TextView) findViewById(R.id.customer_order_total_details);

        firstProducts = (RecyclerView) findViewById(R.id.customer_firstProducts);
        firstProducts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        firstProducts.setLayoutManager(layoutManager);

        if (getIntent() != null)

            order_id_value = getIntent().getStringExtra("OrderId");

        //Set Value
        order_id.setText(order_id_value);
        order_phone.setText(Prevalent.customerCurrentRequest.getPhone());
        order_address.setText(Prevalent.customerCurrentRequest.getAddress());
        order_total.setText(Prevalent.customerCurrentRequest.getTotal());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Prevalent.customerCurrentRequest.getProducts());
        adapter.notifyDataSetChanged();
        firstProducts.setAdapter(adapter);

    }
}
