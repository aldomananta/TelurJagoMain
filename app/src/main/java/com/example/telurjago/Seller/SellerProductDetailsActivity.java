package com.example.telurjago.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telurjago.Model.Products;
import com.example.telurjago.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SellerProductDetailsActivity extends AppCompatActivity {

    private Button confirmBtn, cancelBtn, editBtn;
    private String CategoryName, Description, Price, NumberofTrays, saveCurrentDate, saveCurrentTime, storeName;
    private EditText numofTraysInpt, priceInpt, descInpt;
    private TextView productName;
    private ImageView imageProduct;
    private Uri imageURI;
    private EditText storeNameDisplay;

    private String productRandomKey, downloadImageUrl, productSellerId;
    private StorageReference ProductImagesRef;
   // private DatabaseReference ProductsRef;

    private String productID = "";

    private ProgressDialog loadingbar;

    private FirebaseAuth mAuth;
    String sid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    Products currentProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_details);

        numofTraysInpt = (EditText) findViewById(R.id.details_productsAmnt);
        priceInpt = (EditText) findViewById(R.id.details_priceTxt);
        descInpt = (EditText) findViewById(R.id.details_productsDesc);
        confirmBtn = (Button) findViewById(R.id.details_addBtn);
        cancelBtn = (Button) findViewById(R.id.details_cancelBtn);
        editBtn = (Button) findViewById(R.id.details_editBtn);
        productName = (TextView) findViewById(R.id.details_prodName);
        imageProduct = (ImageView) findViewById(R.id.details_addImg);

        productID = getIntent().getStringExtra("pid");

        getProductDetails(productID);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editBtn.setVisibility(v.INVISIBLE);
                numofTraysInpt.setEnabled(true);
                priceInpt.setEnabled(true);
                descInpt.setEnabled(true);
                confirmBtn.setVisibility(v.VISIBLE);
                cancelBtn.setVisibility(v.VISIBLE);

            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateSellerProduct(productID);

                confirmBtn.setVisibility(v.INVISIBLE);
                numofTraysInpt.setEnabled(false);
                priceInpt.setEnabled(false);
                descInpt.setEnabled(false);
                cancelBtn.setVisibility(v.INVISIBLE);

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editBtn.setVisibility(v.VISIBLE);
                confirmBtn.setVisibility(v.INVISIBLE);
                cancelBtn.setVisibility(v.INVISIBLE);
                numofTraysInpt.setEnabled(false);
                priceInpt.setEnabled(false);
                descInpt.setEnabled(false);
            }
        });

    }

    private void updateSellerProduct(String productID) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("trays", numofTraysInpt.getText().toString());
        userMap.put("price", priceInpt.getText().toString());
        userMap.put("description", descInpt.getText().toString());

        ref.child(productID).updateChildren(userMap);

        Toast.makeText(SellerProductDetailsActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SellerProductDetailsActivity.this, SellerProductActivity.class));
        finish();

    }

    private void getProductDetails(String productID) {

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    currentProducts = dataSnapshot.getValue(Products.class);

                    productName.setText(currentProducts.getCategory());
                    descInpt.setText(currentProducts.getDescription());
                    numofTraysInpt.setText(currentProducts.getTrays());
                    priceInpt.setText(currentProducts.getPrice());
                    Picasso.get().load(currentProducts.getImage()).into(imageProduct);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
