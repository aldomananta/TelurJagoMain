package com.example.telurjago.Customer;

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

import com.example.telurjago.Model.Users;
import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class  CustLoginActivity extends AppCompatActivity {


    private TextView textView;
    private EditText InputPhone, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingbar;

    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_login);

        textView = (TextView) findViewById(R.id.register_here);
        LoginButton = (Button) findViewById(R.id.loginBtn);
        InputPhone = (EditText) findViewById(R.id.login_phoneInpt);
        InputPassword = (EditText) findViewById(R.id.login_passwordInpt);

        loadingbar = new ProgressDialog(this);
        Paper.init(this);



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustLoginActivity.this, CustRegActivity.class );
                startActivity(intent);
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginUser();

            }
        });

    }

    private void LoginUser() {

        String phone = InputPhone.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)){

            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();

        }
        else {

            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("Please wait, while we are checking the credentials.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            AllowAccesstoAccount (phone, password);

        }

    }

    private void AllowAccesstoAccount(final String phone, final String password) {

        Paper.book().write(Prevalent.UserPhoneKey, phone);
        Paper.book().write(Prevalent.UserPasswordKey, password);


        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(parentDbName).child(phone).exists()){

                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    usersData.setPhone(phone);

                    if (usersData.getPhone().equals(phone)){

                        if (usersData.getPassword().equals(password)){

                            Toast.makeText(CustLoginActivity.this, "Logged in successfully...", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CustLoginActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent) ;

                        }
                        else {

                            loadingbar.dismiss();
                            Toast.makeText(CustLoginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                            
                        }

                    }

                }
                else {

                    Toast.makeText(CustLoginActivity.this, "Account with phone number " +phone+ " don't exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
