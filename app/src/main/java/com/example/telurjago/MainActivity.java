package com.example.telurjago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.telurjago.Customer.CustLoginActivity;
import com.example.telurjago.Customer.HomeActivity;
import com.example.telurjago.Model.Users;
import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.Seller.SellerHomeActivity;
import com.example.telurjago.Seller.SellerLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button customer_pageBtn, seller_pageBtn;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customer_pageBtn = (Button) findViewById(R.id.customerBtn);
        seller_pageBtn = (Button) findViewById(R.id.sellerBtn);
        loadingbar = new ProgressDialog(this);

        Paper.init(this);

        customer_pageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, CustLoginActivity.class);
                startActivity(intent) ;

            }
        });

        seller_pageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SellerLoginActivity.class);
                startActivity(intent) ;

            }
        });



        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        // Customer login check

        if (UserPhoneKey != "" && UserPasswordKey != ""){

            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){

                AllowAccess (UserPhoneKey, UserPasswordKey);

                loadingbar.setTitle("Already Logged in");
                loadingbar.setMessage("Please wait......");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

            }

        }

    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){

            Intent intent = new Intent(MainActivity.this, SellerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent) ;
            finish();

        }

    }

    private void AllowAccess(final String phone, final String password) {

        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Users").child(phone).exists()){

                    Users usersData = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone)){

                        if (usersData.getPassword().equals(password)){

                            Toast.makeText(MainActivity.this, "Logged in successfully...", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent) ;

                        }
                        else {

                            loadingbar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();

                        }

                    }

                }
                else {

                    Toast.makeText(MainActivity.this, "Account with username " +phone+ " don't exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
