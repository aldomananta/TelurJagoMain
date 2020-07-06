package com.example.telurjago.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telurjago.Model.Seller;
import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class SellerLoginActivity extends AppCompatActivity {

    private TextView registerhere;
    private Button loginSellerBtn;
    private EditText usernameInpt, passwordInpt;
    private ProgressDialog loadingbar;

    private FirebaseAuth mAuth;

    private String parentDbName = "Sellers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        registerhere=  findViewById(R.id.seller_register_here);
        loginSellerBtn =  findViewById(R.id.seller_loginBtn);
        usernameInpt =  findViewById(R.id.seller_login_usernameInpt);
        passwordInpt =  findViewById(R.id.seller_login_passwordInpt);

        mAuth = FirebaseAuth.getInstance();

        loadingbar = new ProgressDialog(this);
        Paper.init(this);


        registerhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerLoginActivity.this, SellerRegActivity.class );
                startActivity(intent);
            }
        });

        loginSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginSeller();

            }
        });

    }

    private void LoginSeller() {

        String name = usernameInpt.getText().toString();
        String password = passwordInpt .getText().toString();


        if (!name.equals("") && !password.equals("")){

            loadingbar.setTitle("Seller Account Login ");
            loadingbar.setMessage("Please wait, while we are checking the credentials.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            mAuth.signInWithEmailAndPassword(name, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }

                }
            });

        }

        else {

            Toast.makeText(SellerLoginActivity .this, "Please complete the login form", Toast.LENGTH_SHORT).show();

        }

    }

    private void AllowAccesstoAccount(final String name, final String password) {

        Paper.book().write(Prevalent.SellerUNameKey, name);
        Paper.book().write(Prevalent.SellerPasswordKey, password);

        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(parentDbName).child(name).exists()){

                    Seller SellerData = dataSnapshot.child(parentDbName).child(name).getValue(Seller.class);

                    if (SellerData.getName().equals(name)){

                        if (SellerData.getPassword().equals(password)){

                            Toast.makeText(SellerLoginActivity.this, "Logged in successfully...", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                            startActivity(intent) ;

                        }
                        else {

                            loadingbar.dismiss();
                            Toast.makeText(SellerLoginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();

                        }

                    }

                }
                else {

                    Toast.makeText(SellerLoginActivity.this, "Account with username " +name+ " don't exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
