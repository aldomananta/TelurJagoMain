package com.example.telurjago.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.telurjago.Database.Database;
import com.example.telurjago.Model.Order;
import com.example.telurjago.Model.Products;
import com.example.telurjago.Model.Request;
import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice, productDescription, productName, productQuantity, productStoreName;
    private String productID = "";
    private String cartID = "";
    private String currentOrder = "";
    private Button orderBtn;

    private TextView currentOrderID;

    Products currentProducts;
    List<Order> cart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");

        productStoreName = (TextView) findViewById(R.id.product_Store_Details);
        productImage = (ImageView) findViewById(R.id.product_Image_Details);
        numberButton = (ElegantNumberButton) findViewById(R.id.numberBtn);
        productPrice = (TextView) findViewById(R.id.product_Price_Details);
        productDescription = (TextView) findViewById(R.id.product_Desc_Details);
        productName = (TextView) findViewById(R.id.product_Name_Details);
        productQuantity = (TextView) findViewById(R.id.product_Quantity_Details);
        orderBtn = (Button) findViewById(R.id.prodAdd_cart_button);

      //  currentOrderID = (TextView) findViewById(R.id.currentOrder);

        getProductDetails(productID);

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // fetch cart id from database (Sqli database)
                //checking the data from cart, because if data is empty there's an error
                cart = new Database(getBaseContext()).getCarts();

                if (cart.isEmpty()){

                    currentOrder = "";

                }
                else {

                    currentOrder = cart.get(0).getSid();

                }


                if (currentOrder == "" || cartID.equals(currentOrder) ){

                    new Database(getBaseContext()).addToCart(new Order(

                            productID,
                            currentProducts.getCategory(),
                            numberButton.getNumber(),
                            currentProducts.getPrice(),
                            currentProducts.getSid()

                    ));

                    updateTotalProduct(productID);

                    Toast.makeText(ProductDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();

                }
                else if (cartID != currentOrder){

                    showAlertDialog();

                }

            }
        });

    }

    private void updateTotalProduct(String productID) {

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        int total = (Integer.parseInt(currentProducts.getTrays())) - (Integer.parseInt(numberButton.getNumber()));

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("trays", String.valueOf(total));
        productsRef.child(productID).updateChildren(userMap);
    }

    private void showAlertDialog() {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductDetailsActivity.this);
            alertDialog.setTitle("Proceed to Start New Basket?");
            alertDialog.setMessage("Adding this item will clear your current basket. Proceed ?");
            alertDialog.setIcon(R.drawable.cart);

            alertDialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //Delete Cart
                    new Database(getBaseContext()).cleanCart();
                    finish();

                    //Create new basket
                    new Database(getBaseContext()).addToCart(new Order(

                            productID,
                            currentProducts.getCategory(),
                            numberButton.getNumber(),
                            currentProducts.getPrice(),
                            currentProducts.getSid()

                    ));

                    updateTotalProduct(productID);

                    Toast.makeText(ProductDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();



                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });

            alertDialog.show();

    }

    private void getProductDetails(String productID) {

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    currentProducts = dataSnapshot.getValue(Products.class);

                    productName.setText(currentProducts.getCategory());
                    productDescription.setText("Description : "+currentProducts.getDescription());
                    productQuantity.setText("Stock : "+currentProducts.getTrays());
                    productPrice.setText("Rp."+currentProducts.getPrice());
                    productStoreName.setText("Fresh From "+currentProducts.getName());
                    Picasso.get().load(currentProducts.getImage()).into(productImage);
                    cartID = currentProducts.getSid();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
