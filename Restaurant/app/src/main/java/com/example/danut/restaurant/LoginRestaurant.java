package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginRestaurant extends AppCompatActivity {

    //declare variables
    String TAG = "LoginActivity";

    private EditText nameLogRest, emailLogRest, passLogRest;
    private TextView textViewInfoRest, textViewForgotPassRest;
    private Button buttonSignUpRest, buttonLogInRest, buttonCancelLogRest;
    private int counter = 5;

    private FirebaseAuth firebaseAuthRest;
    private FirebaseUser currentUser;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_restaurant);

        //initialize variables
        nameLogRest = (EditText) findViewById(R.id.etNameLogRest);
        emailLogRest = (EditText) findViewById(R.id.etEmailLogRest);
        passLogRest = (EditText) findViewById(R.id.etPassLogRest);
        textViewInfoRest = (TextView) findViewById(R.id.tvInfoRest);
        buttonLogInRest = (Button) findViewById(R.id.btnLogInRest);

        textViewInfoRest.setText("No of attempts remaining: " + counter);

        progressDialog = new ProgressDialog(this);

        firebaseAuthRest = FirebaseAuth.getInstance();

        if(firebaseAuthRest.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(LoginRestaurant.this, RestaurantActivity.class));
        }

        //Action TextView Forgotten Password
        textViewForgotPassRest = (TextView)findViewById(R.id.tvForgotPasswordRest);
        textViewForgotPassRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fPasword = new Intent(LoginRestaurant.this, ResetPassword.class);
                startActivity(fPasword);
            }
        });

        //Action Button Cancel
        buttonCancelLogRest = (Button)findViewById(R.id.btnCancelLogRest);
        buttonCancelLogRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameLogRest.setText("");
                emailLogRest.setText("");
                passLogRest.setText("");
            }
        });

        //Action button SignUp
        buttonSignUpRest = (Button) findViewById(R.id.btnSignUpRest);
        buttonSignUpRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = new Intent(LoginRestaurant.this, RegisterRestaurantOwner.class);
                startActivity(sign);
            }
        });

        //Action button LogIn
        buttonLogInRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    //validate input data into the editText
    public void validate() {
        String nameLog_Rest = nameLogRest.getText().toString();
        final String emailLog_Rest = emailLogRest.getText().toString();
        String passLog_Rest = passLogRest.getText().toString();

        if(nameLog_Rest.isEmpty()){
            nameLogRest.setError("Enter Restaurant Name");
            nameLogRest.requestFocus();
        }

        if (emailLog_Rest.isEmpty()) {
            emailLogRest.setError("Enter your Email Address");
            Toast.makeText(this, "Please enter your Email Address", Toast.LENGTH_SHORT).show();
            emailLogRest.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(emailLog_Rest).matches()){
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            emailLogRest.setError("Enter a valid Email Address");
        }

        else if (passLog_Rest.isEmpty()){
            passLogRest.setError("Enter your Password");
            Toast.makeText(this, "Please enter your Password",Toast.LENGTH_SHORT).show();
        }

        //sign in as a new user
        else {
            progressDialog.setMessage("Welcome to restaurant");
            progressDialog.show();
            firebaseAuthRest.signInWithEmailAndPassword(emailLog_Rest, passLog_Rest).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginRestaurant.this,RestaurantActivity.class));

                        //clear data
                        emailLogRest.setText("");
                        passLogRest.setText("");
                         //checkEmailVerification();
                    }

                    else {
                        Toast.makeText(LoginRestaurant.this, "Log In failed, you entered a wrong email or password", Toast.LENGTH_SHORT).show();
                        counter--;
                        progressDialog.dismiss();
                        textViewInfoRest.setText("No of attempts remaining: " + counter);

                        if (counter == 0) {
                            textViewInfoRest.setText("No more attempts remaining, please press Forgoten Password");
                            buttonLogInRest.setEnabled(false);
                            buttonLogInRest.setBackgroundColor(Color.parseColor("#cc3333"));
                            buttonLogInRest.setText("Stop");
                        }
                    }
                }
            });
        }
    }

    /*@Override
    protected void onResume(){
        super.onResume();
        currentUser = firebaseAuthRest.getCurrentUser();
        if (currentUser!=null){
            finish();
            startActivity(new Intent(LoginRestaurant.this,RegisterRestaurantOwner.class));
        }
    }*/

    /*
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Boolean emailFlag = firebaseUser.isEmailVerified();
        if(emailFlag){
            progressDialog.dismiss();
            Toast.makeText(LoginRestaurant.this, "Log In succesful", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(LoginRestaurant.this, AddMenu.class);
            startActivity(i);
            finish();
        }

        else{
            progressDialog.dismiss();
            Toast.makeText(this, "Please verify your Email first", Toast.LENGTH_SHORT).show();
            firebaseAuthRest.signOut();
        }
    }*/
}
