package com.example.telurjago.Seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.telurjago.MainActivity;
import com.example.telurjago.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class ManageProductsFragment extends Fragment {

    private View view;
    private Spinner dropdownMenu;
    private Button confirmBtn;
    private String CategoryName, Description, Price, NumberofTrays, saveCurrentDate, saveCurrentTime, storeName;
    private EditText numofTraysInpt, priceInpt, descInpt;
    private ImageView imageProductInpt;
    private Uri imageURI;
    private EditText storeNameDisplay;

    private String productRandomKey, downloadImageUrl, productSellerId;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;

    private static final int GalleryPick = 1;

    private ProgressDialog loadingbar;

    private FirebaseAuth mAuth;
    String sid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


    public ManageProductsFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_products, container, false);

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        dropdownMenu = view.findViewById(R.id.Spinner_category);
        confirmBtn = view.findViewById(R.id.addBtn);
        numofTraysInpt = view.findViewById(R.id.productsAmnt);
        priceInpt = view.findViewById(R.id.priceTxt);
        descInpt = view.findViewById(R.id.productsDesc);
        imageProductInpt = view.findViewById(R.id.addImg);
        storeNameDisplay = view.findViewById(R.id.product_Store_Details);

        loadingbar = new ProgressDialog(getContext());

        storeNameDisplay(storeNameDisplay);

        imageProductInpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGalery();

            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateProductData();

            }
        });


        // Spinner Setup
        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                String itemvalue = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getContext(), "Selected : "+itemvalue, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;

    }

    private void storeNameDisplay(final EditText storeNameDisplay) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Sellers").child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                storeNameDisplay.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ValidateProductData() {

        Description = descInpt.getText().toString();
        Price = priceInpt.getText().toString();
        NumberofTrays = numofTraysInpt.getText().toString();
        storeName = storeNameDisplay.getText().toString();
        CategoryName = dropdownMenu.getSelectedItem().toString();

        if (imageURI == null){

            Toast.makeText(getContext(), "Product image is mandatory", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Description)){

            Toast.makeText(getContext(), "Please write product description", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Price)){

            Toast.makeText(getContext(), "Please write product price", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(NumberofTrays)){

            Toast.makeText(getContext(), "Please write number of trays", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(CategoryName)){

            Toast.makeText(getContext(), "Please insert product category", Toast.LENGTH_SHORT).show();

        }
        else {

            StoreProductInformation();

        }

    }

    private void openGalery() {

        Intent galleryintent = new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, GalleryPick);

    }

    private void StoreProductInformation() {

        loadingbar.setTitle("Add New Product");
        loadingbar.setMessage("Please wait, while we adding the new product.");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate +saveCurrentTime;

        final StorageReference filepath = ProductImagesRef.child(imageURI.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filepath.putFile(imageURI);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(getContext(), "Error : "+message, Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getContext(), "Product image uploaded successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()){

                            throw task.getException();

                        }

                        downloadImageUrl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(getContext(), "Got the product image successfully...", Toast.LENGTH_SHORT).show();

                            SaveProductInfotoDatabase();

                        }

                    }
                });

            }
        });


    }

    private void SaveProductInfotoDatabase() {

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("trays", NumberofTrays);
        productMap.put("sid", sid);
        productMap.put("name", storeName);

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            loadingbar.dismiss();
                            Toast.makeText(getContext(), "Product is added successfully...", Toast.LENGTH_SHORT).show();

                        }
                        else {

                            loadingbar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(getContext(), "Error : "+message, Toast.LENGTH_SHORT).show();

                        }

                    }
                });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null){

            imageURI = data.getData();
            imageProductInpt.setImageURI(imageURI);

        }

    }
}
