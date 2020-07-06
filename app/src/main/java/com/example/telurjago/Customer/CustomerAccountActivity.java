package com.example.telurjago.Customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telurjago.MainActivity;
import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.R;
import com.example.telurjago.Seller.SellerAccountActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class CustomerAccountActivity extends AppCompatActivity {

    private CircleImageView profileImgView;
    private EditText userNameEditTxt, userPhoneEditTxt, userPasswordTxt;
    private TextView updateButton, profileChangeTxt, editButton;

    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePicRef;
    private StorageTask uploadTask;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_account);

        Paper.init(this);

        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        BottomNavigationView navView1 = findViewById(R.id.nav_view);
        Button logoutBtn = findViewById(R.id.cust_logoutBtn);
        profileImgView = findViewById(R.id.cust_profile_image);
        userNameEditTxt = (EditText) findViewById(R.id.cust_username);
        userPhoneEditTxt = (EditText) findViewById(R.id.cust_phone);
        userPasswordTxt = (EditText) findViewById(R.id.cust_password);
        profileChangeTxt = (TextView) findViewById(R.id.cust_profile_change);
        updateButton = (TextView) findViewById(R.id.update_accountBtn);
        editButton = (TextView) findViewById(R.id.edit_accountBtn);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editButton.setVisibility(View.INVISIBLE);
                updateButton.setVisibility(View.VISIBLE);
                userNameEditTxt.setEnabled(true);
                userPhoneEditTxt.setEnabled(true);
                userPasswordTxt.setEnabled(true);
                profileChangeTxt.setEnabled(true);

            }
        });

        // Method for display user information
        userInfoDisplay(profileImgView, userNameEditTxt, userPhoneEditTxt, userPasswordTxt);
        // Getting username info from prevalent
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImgView);

        navView1.setSelectedItemId(R.id.cust_navigation_user);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Paper.book().destroy();

                Intent intent = new Intent(CustomerAccountActivity.this, MainActivity.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });

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
                        startActivity(new Intent(getApplicationContext()
                                , CustomerCartActivity.class));
                        overridePendingTransition(0,0);

                        return true;
                    case R.id.cust_navigation_user :

                        return true;
                }

                return false;
            }
        });


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editButton.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.INVISIBLE);
                userNameEditTxt.setEnabled(false);
                userPhoneEditTxt.setEnabled(false);
                userPasswordTxt.setEnabled(false);
                profileChangeTxt.setEnabled(false);


                if (checker.equals("clicked")){

                    userInfoSaved();

                }
                else {

                    updateOnlyUserInfo();

                }

            }
        });

        profileChangeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(CustomerAccountActivity.this);

            }
        });

    }

    private void updateOnlyUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", userNameEditTxt.getText().toString());
        userMap.put("phoneOrder", userPhoneEditTxt.getText().toString());
        userMap.put("password", userPasswordTxt.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(CustomerAccountActivity.this, CustomerAccountActivity.class));
        Toast.makeText(CustomerAccountActivity.this, "Profile info updated successfully", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImgView.setImageURI(imageUri);

        }
        else {

            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CustomerAccountActivity.this, CustomerAccountActivity.class));
            finish();
            
        }

    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(userNameEditTxt.getText().toString())) {

            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(userPhoneEditTxt.getText().toString())) {

            Toast.makeText(this, "Phone is mandatory", Toast.LENGTH_SHORT).show();

        } else if (checker.equals("clicked")) {

            uploadImage();

        }

    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null){


            final StorageReference fileRef = storageProfilePicRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){

                        throw task.getException();

                    }

                    return fileRef.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){

                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", userNameEditTxt.getText().toString());
                        userMap.put("phoneOrder", userPhoneEditTxt.getText().toString());
                        userMap.put("password", userPasswordTxt.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(CustomerAccountActivity.this, CustomerAccountActivity.class));
                        Toast.makeText(CustomerAccountActivity.this, "Profile info updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {

                        progressDialog.dismiss();
                        Toast.makeText(CustomerAccountActivity.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        
                    }

                }
            });

        }
        else {

            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();

        }

    }

    private void userInfoDisplay(final CircleImageView profileImgView, final EditText userNameEditTxt, final EditText userPhoneEditTxt, final EditText userPasswordTxt) {

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    if (dataSnapshot.child("image").exists()){

                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String pass = dataSnapshot.child("password").getValue().toString();

                        Picasso.get().load(image).into(profileImgView);
                        userNameEditTxt.setText(name);
                        userPhoneEditTxt.setText(phone);
                        userPasswordTxt.setText(pass);

                    }
                    else if (dataSnapshot.child("image").exists() == false){

                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String pass = dataSnapshot.child("password").getValue().toString();

                        userNameEditTxt.setText(name);
                        userPhoneEditTxt.setText(phone);
                        userPasswordTxt.setText(pass);

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
