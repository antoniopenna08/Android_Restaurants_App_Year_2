package com.example.danut.restaurant;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddNewMenu extends AppCompatActivity {

    //Declare Variables
    private String TAG = "AddNewMenu", MSG = "";

    private ImageView imageView;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri=null;
    private List<Restaurants> restaurantNameList;

    private EditText editTextItemName, editTextItemDescription, editTextItemPrice;
    private Button buttonInsert, buttonBackMenu;

    private StorageReference storageReference;
    private DatabaseReference restaurantReference;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuthMenu;

    private ProgressDialog progressDialog;

    String restaurantID = "";

    private TextView textViewRestaurantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_menu);

        //initialize variables
        firebaseAuthMenu = FirebaseAuth.getInstance();
        if(firebaseAuthMenu.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginRestaurant.class));
        }

        if(getIntent().hasExtra("RESID")) {
            restaurantID = getIntent().getExtras().getString("RESID");
        }else{
            finish();
            startActivity(new Intent(this, RestaurantActivity.class));
        }

        textViewRestaurantName = (TextView) findViewById(R.id.tvMenuRestName);
        textViewRestaurantName.setText(restaurantID);

        restaurantNameList = new ArrayList<>();

        editTextItemName = (EditText) findViewById(R.id.etItemName);
        editTextItemDescription = (EditText) findViewById(R.id.etItemDescription);
        editTextItemPrice = (EditText) findViewById(R.id.etItemPrice);

        storageReference = FirebaseStorage.getInstance().getReference("Menu");
        databaseReference = FirebaseDatabase.getInstance().getReference("Menu");
        restaurantReference = FirebaseDatabase.getInstance().getReference("Restaurants");

        //create a new restaurant
        restaurantReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    Restaurants r = child.getValue(Restaurants.class);
                    if(r!= null){
                        restaurantNameList.add(r);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        progressDialog = new ProgressDialog(AddNewMenu.this);

        imageView = (ImageView) findViewById(R.id.imgView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        //Action button back to restaurant
        buttonBackMenu = (Button)findViewById(R.id.btnBackMenu);
        buttonBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNewMenu.this, RestaurantActivity.class));
            }
        });

        //Action button insert
        buttonInsert = (Button) findViewById(R.id.btnInsert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show progress Dialog
                progressDialog.show();
                uploadFile();
            }
        });
    }

    //Insert a picture
    public void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //gallery.setType("Image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode ==RESULT_OK){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            //Picasso.with(this).load(imageView).into(imageView);
            Toast.makeText(AddNewMenu.this, "Image upload", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //Upload a new menu into the menu table
    public void uploadFile(){
        progressDialog.dismiss();
        final String editText_ItemName = editTextItemName.getText().toString();
        final String editText_ItemDescription = editTextItemDescription.getText().toString();
        final double editText_ItemPrice = Double.parseDouble(editTextItemPrice.getText().toString());
        //final String editText_ItemPrice= editTextItemPrice.getText().toString();


        if (imageUri == null)   {
            Toast.makeText(AddNewMenu.this, "Please add an image", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(editText_ItemName)){
            editTextItemName.setError("");
            Toast.makeText(AddNewMenu.this, "Please enter the item name", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(editText_ItemDescription)){
            editTextItemDescription.setError("");
            Toast.makeText(AddNewMenu.this, "Please enter the item description", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(new String(String.valueOf(editText_ItemPrice)))){
            editTextItemPrice.setError("");
            Toast.makeText(AddNewMenu.this, "Please enter the item price", Toast.LENGTH_SHORT).show();
        }

        //Create a new menu into the menu table
        else{
            progressDialog.show();
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Menu menu = new Menu(editText_ItemName, editText_ItemDescription, editText_ItemPrice,
                                    taskSnapshot.getDownloadUrl().toString(), restaurantID);
                            String id = databaseReference.push().getKey();
                            databaseReference.child(id).setValue(menu).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        editTextItemName.setText("");
                                        editTextItemDescription.setText("");
                                        editTextItemPrice.setText("");
                                        imageView.setImageResource(R.drawable.menu);

                                        Toast.makeText(AddNewMenu.this, "Upload successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AddNewMenu.this, ImageActivity.class));
                                    }else{

                                        Toast.makeText(AddNewMenu.this, "Failed to add menu!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddNewMenu.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //show upload Progress
                            double progress = 100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded "+ (int)progress+"%");
                            progressDialog.setProgress((int)progress);
                        }
                    });
        }
    }

    private void openImagesActivity(){
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
    }
}
