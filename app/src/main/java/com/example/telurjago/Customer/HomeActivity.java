package com.example.telurjago.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.telurjago.Model.Products;
import com.example.telurjago.Model.Request;
import com.example.telurjago.Prevalent.Prevalent;
import com.example.telurjago.R;
import com.example.telurjago.Service.ListenOrder;
import com.example.telurjago.ViewHolder.OrderViewHolder;
import com.example.telurjago.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String categoryId = "";

    FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter;

    //Search Functionality
    FirebaseRecyclerAdapter<Products, ProductViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navView1 = findViewById(R.id.nav_view);

        ProductsRef = FirebaseDatabase.getInstance().getReference("Products");
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        navView1.setSelectedItemId(R.id.cust_navigation_home);

        navView1.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.cust_navigation_home:

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
                        startActivity(new Intent(getApplicationContext()
                                , CustomerAccountActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }
        });

        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your product name");
        loadSuggest(); // Function to load suggest from Firebase
        materialSearchBar.setLastSuggestions(suggestList);
      //  materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //When user type their text, we will change suggest list

                List<String> suggest = new ArrayList<String>();
                for (String search:suggestList){ //Loop in suggest list


                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);

                }

                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when searchbar is close
                //restore original adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                // When search finish
                // Show result of search adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


        //Register Service
//        Intent service = new Intent(HomeActivity.this, ListenOrder.class);
//        startService(service);


    }

    private void startSearch(CharSequence text) {

        Query query = ProductsRef.orderByChild("category")
                .equalTo(text.toString()); // compare name (But need to be fixed)

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(query, Products.class)
                        .build();

        searchAdapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                holder.txtProductName.setText(model.getCategory());
                holder.txtProductPrice.setText("Rp."+ model.getPrice());
                holder.txtProductDesc.setText(model.getDescription());
                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("pid", model.getPid()); // Change to searchAdapter if search doesnt work (adapter -> searchAdapter)
                        startActivity(intent) ;

                    }
                });

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_item_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(searchAdapter); // Set adapter for recycler view is search result
        searchAdapter.startListening();

    }

    private void loadSuggest() {

        ProductsRef// change this if errors happen
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                            Products item = postSnapshot.getValue(Products.class);
                            suggestList.add(item.getCategory()); // Add name to food to suggest list

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef, Products.class)
                .build();

         adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options ) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                        holder.txtProductName.setText(model.getCategory());
                        holder.txtProductPrice.setText("Rp."+ model.getPrice());
                        holder.txtProductDesc.setText(model.getDescription());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent) ;

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_item_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;

                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
      //  searchAdapter.startListening();
    }
}
