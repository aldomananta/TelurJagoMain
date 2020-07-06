package com.example.telurjago.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.telurjago.Customer.CustLoginActivity;
import com.example.telurjago.Customer.CustRegActivity;
import com.example.telurjago.MainActivity;
import com.example.telurjago.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import javax.xml.namespace.QName;

public class SellerRegActivity extends AppCompatActivity {

    private Button CreateAccButton;
    private EditText RegInputUsername, RegInputPhonenumb, RegInputPassword, RegInputEmail;
    private ProgressDialog loadingbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_reg);

        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);

        CreateAccButton = findViewById(R.id.seller_createaccBtn);
        RegInputUsername =  findViewById(R.id.seller_register_username_inpt);
        RegInputPhonenumb =  findViewById(R.id.seller_register_phone_inpt);
        RegInputPassword = findViewById(R.id.seller_register_pass_inpt);
        RegInputEmail =  findViewById(R.id.seller_register_email_inpt);


        CreateAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = RegInputUsername.getText().toString();
                String email = RegInputEmail.getText().toString();
                String phone = RegInputPhonenumb.getText().toString();
                String password = RegInputPassword.getText().toString();

                SellerCreateAcc(name, email, phone, password);

            }

            private void SellerCreateAcc(final String name, final String email, final String phone, String password) {

                if (!name.equals("") && !email.equals("") && !phone.equals("") && !password.equals("")) {

                    loadingbar.setTitle("Creating Seller Account");
                    loadingbar.setMessage("Please wait, while we are checking the credentials.");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        final DatabaseReference rootRef;
                                        rootRef = FirebaseDatabase.getInstance().getReference();

                                        String sid = mAuth.getCurrentUser().getUid();

                                        HashMap<String, Object> sellerMap = new HashMap<>();
                                        sellerMap.put("sid", sid);
                                        sellerMap.put("name", name);
                                        sellerMap.put("email", email);
                                        sellerMap.put("phone", phone);

                                        rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        loadingbar.dismiss();
                                                        Toast.makeText(SellerRegActivity.this, "You are registered successfully", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(SellerRegActivity.this, SellerHomeActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                });

                                    } else {
                                        Toast.makeText(SellerRegActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                else {

                    Toast.makeText(SellerRegActivity.this, "Please complete the registration form", Toast.LENGTH_SHORT).show();
                    
                }

            }
        });

    }



   /* private void ValidateUsername(final String name, final String phone, final String password) {

        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.child("Seller").child(name).exists())){

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("name", name);
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);

                    Rootref.child("Seller").child(name).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(SellerRegActivity.this, "Congratulations, your account has been created. ", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();

                                        Intent intent = new Intent(SellerRegActivity.this, SellerLoginActivity.class);
                                        startActivity(intent) ;
                                    }

                                    else {

                                        loadingbar.dismiss();
                                        Toast.makeText(SellerRegActivity.this, "Network ERROR: Please try again after some time... ", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                }
                else {

                    Toast.makeText(SellerRegActivity.this, "This"+name+"already exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(SellerRegActivity.this, "Please try again with another username.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SellerRegActivity.this, MainActivity.class);
                    startActivity(intent) ;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    } */
}
