package com.example.danut.restaurant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RestaurantPage extends AppCompatActivity {

    //declare variables
    private FirebaseAuth firebaseAuthRest;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabaseRest;
    private DatabaseReference databaseRefRest;
    ValueEventListener eventListener;
    List<Restaurants> restaurants;
    List<String> restNames;

    ListView listView;
    ArrayAdapter<String> adapter;
    Button buttonNewRestaurant, buttonShowMap;
    private TextView textViewNameR, textViewAddressR,textViewWelcomeR;
    String restaurantID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_page);

        //initialize variables
        textViewNameR =  findViewById(R.id.tvNameRestaurant);
        textViewAddressR= findViewById(R.id.tvAddressRestaurant);

        firebaseAuthRest= FirebaseAuth.getInstance();
        firebaseDatabaseRest = FirebaseDatabase.getInstance();

        if(getIntent().hasExtra("RESID")) {
            restaurantID = getIntent().getExtras().getString("RESID");
        }

        textViewNameR = (TextView) findViewById(R.id.tvRestName);
        textViewNameR.setText(restaurantID);

        //load the restaurant details from database
        databaseRefRest = FirebaseDatabase.getInstance().getReference("Restaurants");
        databaseRefRest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Restaurants rest = child.getValue(Restaurants.class);

                    if( rest.getRest_Name().equals(restaurantID)){
                        textViewNameR.setText(rest.getRest_Name());
                        textViewAddressR.setText(rest.getRest_Address());
                        textViewWelcomeR.setText("Welcome: "+rest.getRest_Name());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RestaurantPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
