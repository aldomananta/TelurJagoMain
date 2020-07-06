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
import android.widget.Toast;

import com.example.telurjago.MainActivity;
import com.example.telurjago.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CustRegActivity extends AppCompatActivity {

    private Button CreateAccButton;
    private EditText RegInputUsername, RegInputPhonenumb, RegInputPassword;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_reg);

        CreateAccButton = (Button) findViewById(R.id.createaccBtn);
        RegInputUsername = (EditText) findViewById(R.id.register_username_inpt);
        RegInputPhonenumb = (EditText) findViewById(R.id.register_phone_inpt);
        RegInputPassword = (EditText) findViewById(R.id.register_pass_inpt);
        loadingbar = new ProgressDialog(this);

        CreateAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAcc();

            }
        });

    }

    private void CreateAcc() {

        String name = RegInputUsername.getText().toString();
        String phone = RegInputPhonenumb.getText().toString();
        String password = RegInputPassword.getText().toString();

        if (TextUtils.isEmpty(name)){

            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(phone)){

            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();

        }
        else {

            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Please wait, while we are checking the credentials.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            ValidateUsername(name, phone, password);
        }


    }

    private void ValidateUsername(final String name, final String phone, final String password) {

        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.child("Users").child(phone).exists())){

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("name", name);
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);

                    Rootref.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(CustRegActivity.this, "Congratulations, your account has been created. ", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();

                                        Intent intent = new Intent(CustRegActivity.this, CustLoginActivity.class);
                                        startActivity(intent) ;
                                    }

                                    else {

                                        loadingbar.dismiss();
                                        Toast.makeText(CustRegActivity.this, "Network ERROR: Please try again after some time... ", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                }
                else {

                    Toast.makeText(CustRegActivity.this, "This"+name+"already exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(CustRegActivity.this, "Please try again with another username.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CustRegActivity.this, MainActivity.class);
                    startActivity(intent) ;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
