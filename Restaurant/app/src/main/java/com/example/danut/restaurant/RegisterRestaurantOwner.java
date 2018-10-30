package com.example.danut.restaurant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterRestaurantOwner extends AppCompatActivity {

    //Declare variables
    private EditText nameOwnerReg, emailOwnerReg, passOwnerReg,confPassOwnerReg;
    private Button buttonOwnerReg, buttonCancalOwnReg;

    private FirebaseAuth firebaseAuthRest;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRefOwner;
    ValueEventListener eventListener;

    private TextView textViewLogInOwner;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_restaurant_owner);

        //initialise variables
        progressDialog = new ProgressDialog(RegisterRestaurantOwner.this);

        nameOwnerReg = (EditText) findViewById(R.id.etNameOwnerReg);
        emailOwnerReg = (EditText) findViewById(R.id.etEmailOwnerReg);
        passOwnerReg = (EditText) findViewById(R.id.etPasswordOwnerReg);
        confPassOwnerReg = (EditText) findViewById(R.id.etConfPassOwnerReg);

        firebaseAuthRest = FirebaseAuth.getInstance();
        databaseRefOwner = FirebaseDatabase.getInstance().getReference().child("Restaurant Owner");

        //Action button Cancel
        buttonCancalOwnReg = (Button) findViewById(R.id.btnCancelOwnerReg);
        buttonCancalOwnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //clear data
                nameOwnerReg.setText("");
                emailOwnerReg.setText("");
                passOwnerReg.setText("");
                confPassOwnerReg.setText("");
            }
        });

        //Action TextView Log In
        textViewLogInOwner=(TextView)findViewById(R.id.tvLogInOwner);
        textViewLogInOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent textLog =new Intent(RegisterRestaurantOwner.this,LoginRestaurant.class);
                startActivity(textLog);
            }
        });

        //Action Button Register
        buttonOwnerReg = (Button) findViewById(R.id.btnOwnerReg);
        buttonOwnerReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Add data to sting
                final String nameOwner_Reg =  nameOwnerReg.getText().toString();
                final String emailOwner_Reg =  emailOwnerReg.getText().toString();
                String passOwner_Reg = passOwnerReg.getText().toString();
                String confPassOwner_Reg = confPassOwnerReg.getText().toString();

                //Check if the input fields are empty
                if (nameOwner_Reg.isEmpty()) {
                    nameOwnerReg.setError("Enter Owner Name");
                    nameOwnerReg.requestFocus();
                }

                else if (emailOwner_Reg.isEmpty()) {
                    emailOwnerReg.setError("Enter Owner Email Address");
                    emailOwnerReg.requestFocus();
                }

                else if(!Patterns.EMAIL_ADDRESS.matcher(emailOwner_Reg).matches()){
                    Toast.makeText(RegisterRestaurantOwner.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    emailOwnerReg.setError("Enter a valid Email Address");
                    emailOwnerReg.requestFocus();
                }

                else if (passOwner_Reg.isEmpty()) {
                    passOwnerReg.setError("Enter your Password");
                    passOwnerReg.requestFocus();
                }

                else if (passOwner_Reg.length()>0 && passOwner_Reg.length()<6) {
                    passOwnerReg.setError("The password is too short, enter mimimum 6 character long");
                    Toast.makeText(RegisterRestaurantOwner.this, "The password is too short, enter mimimum 6 character long", Toast.LENGTH_SHORT).show();
                    passOwnerReg.requestFocus();
                }

                else if (!passOwner_Reg.equals(confPassOwner_Reg)) {
                    Toast.makeText(RegisterRestaurantOwner.this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
                    confPassOwnerReg.setError("Enter same Password");
                    confPassOwnerReg.requestFocus();
                }

                //Create new user
                else {
                    progressDialog.show();
                    firebaseAuthRest.createUserWithEmailAndPassword(emailOwner_Reg, passOwner_Reg).addOnCompleteListener(RegisterRestaurantOwner.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        currentUser  = firebaseAuthRest.getCurrentUser();
                                        addOwnerDetail(nameOwner_Reg, emailOwner_Reg, currentUser);
                                        Toast.makeText(RegisterRestaurantOwner.this, "Succesful Registered", Toast.LENGTH_SHORT).show();
                                    }

                                    else {
                                        Toast.makeText(RegisterRestaurantOwner.this, "Register did not work", Toast.LENGTH_LONG).show();
                                    }

                                    progressDialog.dismiss();
                                }
                            });

                }
            }
        });
    }

    //Dealing with Database push and read data
    boolean exist = false;
    void addOwnerDetail(String nameReg_Rest,String emailReg_Rest,  FirebaseUser owner){
        exist = false;
        final RestaurantOwner restOwner = new RestaurantOwner(owner.getUid(), nameReg_Rest,emailReg_Rest );
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    RestaurantOwner restOwner = child.getValue(RestaurantOwner.class);
                    if(restOwner.getOwner_ID().equals(currentUser.getUid())){
                        exist = true;
                        break;
                    }
                }
                if(exist){
                    Toast.makeText(RegisterRestaurantOwner.this, "User Already exist", Toast.LENGTH_LONG).show();
                }else{
                    databaseRefOwner.push().setValue(restOwner).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                finish();
                                startActivity(new Intent(RegisterRestaurantOwner.this, LoginRestaurant.class));
                            }else{
                                Toast.makeText(RegisterRestaurantOwner.this, "Could not add to database", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("RegisterActivity", "Failed to read value.", databaseError.toException());
            }
        };
        databaseRefOwner.addValueEventListener(listener);
        eventListener = listener;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(eventListener != null){
            databaseRefOwner.removeEventListener(eventListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuthRest.getCurrentUser();
    }
}
