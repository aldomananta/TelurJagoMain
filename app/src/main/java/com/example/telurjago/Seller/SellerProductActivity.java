package com.example.telurjago.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.telurjago.R;
import com.example.telurjago.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SellerProductActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        TabLayout tabLayout = findViewById(R.id.tLayout);
        ViewPager viewPager = findViewById(R.id.vPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // Adding Fragments
        adapter.AddFragment(new MyProductsFragment(), "My Products");
        adapter.AddFragment(new ManageProductsFragment(), "Manage My Products");
        // Adapter Setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // Spinner Setup
       /* List<String> list = new ArrayList<>();
        list.add("ex1");
        list.add("ex2");
        list.add("ex3");
        list.add("ex4");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenu.setAdapter(adapter1);

        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String itemvalue = parent.getItemAtPosition(position).toString();

                Toast.makeText(SellerProductActivity.this, "Selected : "+itemvalue, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/



        navView.setSelectedItemId(R.id.navigation_products);

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
                        return true;
                    case R.id.navigation_notifications :
                        startActivity(new Intent(getApplicationContext()
                                , SellerNotificationActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_user :
                        startActivity(new Intent(getApplicationContext()
                                , SellerAccountActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }
        });


    }
}
