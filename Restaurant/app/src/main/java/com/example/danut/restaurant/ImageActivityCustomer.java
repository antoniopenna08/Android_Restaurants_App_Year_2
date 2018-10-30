package com.example.danut.restaurant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImageActivityCustomer extends AppCompatActivity {

    //DEclare variables
    private String TAG = "ImageActivity", MSG = "";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRefMenu;
    String restaurantID = "";

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    private List<Menu> mUploads;

    private TextView textViewRestNameCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_customer);

        //ckeck uf the restaurant list is empty
        if(getIntent().hasExtra("RESID")) {
            restaurantID = getIntent().getExtras().getString("RESID");
        }

        else{
            finish();
            startActivity(new Intent(this, AddNewMenu.class));
        }

        textViewRestNameCustomer = (TextView) findViewById(R.id.tvRestNameCustomer);
        textViewRestNameCustomer.setText(restaurantID);

        mUploads = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();

        //add menu into the menu list
        if(databaseRefMenu == null){
            databaseRefMenu = FirebaseDatabase.getInstance().getReference().child("Menu");
        }
        databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Menu menu = postSnapshot.getValue(Menu.class);
                    if(menu != null) {
                        if( menu.getRestaurantName().equals(restaurantID)) {
                            mUploads.add(menu);

                        }
                    }
                }

                imageAdapter = new ImageAdapter(ImageActivityCustomer.this, mUploads);
                recyclerView.setAdapter(imageAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ImageActivityCustomer.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}
