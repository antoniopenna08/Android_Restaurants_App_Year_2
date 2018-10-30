package com.example.danut.restaurant;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener{

    //Declare variables
    private String TAG = "ImageActivity", MSG = "";

    private FirebaseAuth firebaseAuth;

    private FirebaseStorage menuStorage;
    private DatabaseReference databaseRefMenu;
    private ValueEventListener menuDBEventListener;

    String restaurantID = "";

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    private List<Menu> mUploads;

    private Button buttonNewMenu, buttonBackRestaurant;
    private TextView textViewRestName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //initialize variables
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LoginRestaurant.class));
        }

        if(getIntent().hasExtra("RESID")) {
            restaurantID = getIntent().getExtras().getString("RESID");
        }

        else{
            finish();
            startActivity(new Intent(this, AddNewMenu.class));
        }

        //Set textview
        textViewRestName = (TextView) findViewById(R.id.tvRestName);
        textViewRestName.setText(restaurantID);

        mUploads = new ArrayList<Menu>();

        //mUploads.add("No Menu Found!");

        //Action button back to restaurant
        buttonBackRestaurant = (Button)findViewById(R.id.btnBackRestaurant);
        buttonBackRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImageActivity.this, RestaurantActivity.class));
            }
        });

        //Action button new Menu
        buttonNewMenu = (Button) findViewById(R.id.btnNewMenu);
        buttonNewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ImageActivity.this, AddNewMenu.class);
                i.putExtra("RESID",restaurantID);
                startActivity(i);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //check if the menu list is empty and add a new menu
        if(databaseRefMenu == null){
            menuStorage = FirebaseStorage.getInstance();
            databaseRefMenu = FirebaseDatabase.getInstance().getReference().child("Menu");
        }
        menuDBEventListener = databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Menu menu = postSnapshot.getValue(Menu.class);
                    if(menu != null) {
                        if( menu.getRestaurantName().equals(restaurantID)) {
                            menu.setMenuKey(postSnapshot.getKey());
                            mUploads.add(menu);

                        }
                    }
                }

                imageAdapter = new ImageAdapter(ImageActivity.this, mUploads);
                recyclerView.setAdapter(imageAdapter);
                imageAdapter.setOnItmClickListener(ImageActivity.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ImageActivity.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more action: ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateClick(int position) {
        Toast.makeText(this, "Update click at position: ",Toast.LENGTH_SHORT).show();
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(ImageActivity.this);
        builderAlert.setMessage("Are sure to delete this item?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Menu selectedMenu = mUploads.get(position);
                        final String selectedKey = selectedMenu.getMenuKey();
                        StorageReference imageReference = menuStorage.getReferenceFromUrl(selectedMenu.getItemImage());
                        imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseRefMenu.child(selectedKey).removeValue();
                                Toast.makeText(ImageActivity.this, "The item has been deleted successfully ",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        builderAlert.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builderAlert.create();
        alert11.show();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        databaseRefMenu.removeEventListener(menuDBEventListener);
    }
}
