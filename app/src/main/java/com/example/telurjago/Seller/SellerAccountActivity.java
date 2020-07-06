package com.example.telurjago.Seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telurjago.Customer.CustomerAccountActivity;
import com.example.telurjago.MainActivity;
import com.example.telurjago.Model.Seller;
import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class SellerAccountActivity extends AppCompatActivity {

    private Button logoutBtn, requestBtn;
    private ActionBar toolbar;
    private EditText sellerUserName, sellerPhone, sellerEmail, sellerPassword;
    private TextView updateBtn, editBtn, profileChangeTxt;
    private CircleImageView profileImgView;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePicRef;
    private StorageTask uploadTask;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_account);

        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("Seller profile pictures");

        logoutBtn = findViewById(R.id.Seller_logoutBtn);
        profileChangeTxt = findViewById(R.id.Seller_profile_change);
        profileImgView = findViewById(R.id.Seller_profile_image);
        sellerUserName = findViewById(R.id.Seller_usernameAccount);
        sellerPhone = findViewById(R.id.Seller_phoneAccount);
        sellerEmail = findViewById(R.id.Seller_emailAccount);
        sellerPassword = findViewById(R.id.Seller_passwordAccount);
        updateBtn = findViewById(R.id.Seller_update_accountBtn);
        editBtn = findViewById(R.id.Seller_edit_accountBtn);
        requestBtn = findViewById(R.id.Seller_changePassword);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_user);

        // Method for displaying user info
        userInfoDisplay(sellerUserName, sellerEmail, sellerPhone, sellerPassword, profileImgView);


//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                Seller seller = dataSnapshot.getValue(Seller.class);
//
//                sellerUserName.setText(seller.getName());
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        sellerUserName.setText(Prevalent.currentOnlineSeller.getName());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                Intent intent = new Intent(SellerAccountActivity.this, MainActivity.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.navigation_home :
                        startActivity(new Intent(getApplicationContext()
                                , SellerHomeActivity.class));
                        overridePendingTransition(0,0);

                        return true;
                    case R.id.navigation_products:
                        startActivity(new Intent(getApplicationContext()
                                , SellerProductActivity.class));
                        overridePendingTransition(0,0);

                        return true;
                    case R.id.navigation_notifications :
                        startActivity(new Intent(getApplicationContext()
                                , SellerNotificationActivity.class));
                        overridePendingTransition(0,0);

                        return true;
                    case R.id.navigation_user :

                        return true;
                }

                return false;
            }
        });

        profileChangeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SellerAccountActivity.this);

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editBtn.setVisibility(View.INVISIBLE);
                updateBtn.setVisibility(View.VISIBLE);
                sellerUserName.setEnabled(true);
                sellerPhone.setEnabled(true);
                profileChangeTxt.setEnabled(true);
                requestBtn.setVisibility(View.VISIBLE);
                logoutBtn.setVisibility(View.INVISIBLE);
 //               sellerPassword.setEnabled(true);

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editBtn.setVisibility(View.VISIBLE);
                updateBtn.setVisibility(View.INVISIBLE);
                sellerUserName.setEnabled(false);
                sellerPhone.setEnabled(false);
                sellerPassword.setEnabled(false);
                profileChangeTxt.setEnabled(false);
                requestBtn.setVisibility(View.INVISIBLE);
                logoutBtn.setVisibility(View.VISIBLE);


                if (checker.equals("clicked")){

                    userInfoSaved();

                }
                else {

                    updateOnlyUserInfo();

                }

            }
        });

        final FirebaseAuth auth = FirebaseAuth.getInstance();
       final String email = auth.getCurrentUser().getEmail();

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SellerAccountActivity.this, ""+email, Toast.LENGTH_SHORT).show();

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SellerAccountActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(SellerAccountActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

    }

    private void userInfoDisplay(final EditText sellerUserName, final EditText sellerEmail, final EditText sellerPhone, final EditText sellerPassword, final CircleImageView profileImgView) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
       // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //authenticate user
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Sellers").child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("image").exists()) {

                    String image = dataSnapshot.child("image").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    //               String password = dataSnapshot.child("password").getValue().toString();


                    Picasso.get().load(image).into(SellerAccountActivity.this.profileImgView);
                    sellerUserName.setText(name);
                    sellerEmail.setText(email);
                    sellerPhone.setText(phone);
                    //               sellerPassword.setText(password);

                }
                else if (dataSnapshot.child("image").exists() == false){

                    String name = dataSnapshot.child("name").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    //               String password = dataSnapshot.child("password").getValue().toString();

                    sellerUserName.setText(name);
                    sellerEmail.setText(email);
                    sellerPhone.setText(phone);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateOnlyUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Sellers");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", sellerUserName.getText().toString());
        userMap.put("phone", sellerPhone.getText().toString());
        userMap.put("email", sellerEmail.getText().toString());
        userMap.put("password", sellerPassword.getText().toString());

        ref.child(firebaseAuth.getUid()).updateChildren(userMap);

        startActivity(new Intent(SellerAccountActivity.this, SellerAccountActivity.class));
        Toast.makeText(SellerAccountActivity.this, "Profile info updated successfully", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(SellerAccountActivity.this, SellerAccountActivity.class));
            finish();

        }

    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(sellerUserName .getText().toString())) {

            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(sellerEmail.getText().toString())) {

            Toast.makeText(this, "Phone is mandatory", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(sellerPhone.getText().toString())) {

            Toast.makeText(this, "Phone is mandatory", Toast.LENGTH_SHORT).show();
        }else if (checker.equals("clicked")) {

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
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();


            final StorageReference fileRef = storageProfilePicRef
                    .child(firebaseAuth.getUid()+ ".jpg");

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

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Sellers");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("name", sellerUserName.getText().toString());
                                userMap.put("phone", sellerPhone.getText().toString());
                                userMap.put("email", sellerEmail.getText().toString());
                                userMap.put("password", sellerPassword.getText().toString());
                                userMap.put("image", myUrl);
                                ref.child(firebaseAuth.getUid()).updateChildren(userMap);

                                progressDialog.dismiss();
                                startActivity(new Intent(SellerAccountActivity.this, SellerAccountActivity.class));
                                Toast.makeText(SellerAccountActivity.this, "Profile info updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {

                                progressDialog.dismiss();
                                Toast.makeText(SellerAccountActivity.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }
        else {

            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();

        }

    }
}
