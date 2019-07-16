package com.example.bonyfade808.graphitearts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private EditText password;
    private EditText email;
    private Button button_register;
    private Button button_login;
    private ImageView imageView;
    private String currentUserID;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.emailET);
        password = findViewById(R.id.passwordET);
        button_register = findViewById(R.id.emailcreateAccountButton);
        button_login = findViewById(R.id.emailsignInButton);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");



        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    email.setError("Invalid Email");
                    email.setFocusable(true);

                }else if(Password.length()<6){
                    password.setError("Password must be at least 6 characters");
                    email.setFocusable(true);

                }else{
                    registerUser();
                }


            }
        });
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignIn.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }


        public final void registerUser(){

            String Email = email.getText().toString().trim();
            String Password = password.getText().toString().trim();

        progressDialog.show();

            if (TextUtils.isEmpty(Email)) {

                Toast.makeText(this, "A field is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(Password)) {

                Toast.makeText(this, "A field is empty", Toast.LENGTH_LONG).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    //User is successfully registered
                                    progressDialog.dismiss();


                                    String email = currentUser.getEmail();
                                    String uid = currentUser.getUid();

                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    hashMap.put("email", email);
                                    hashMap.put("uid", uid);
                                    hashMap.put("name", "");
                                    hashMap.put("image", "");
                                    hashMap.put("phoneNumber", "");
                                    hashMap.put("bio", "");



                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("Users");

                                    reference.child(uid).setValue(hashMap);

                                    Toast.makeText(MainActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    //finish();


                                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                    finish();
                                } else {
                                            //User cannot login
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Couldn't Register User, Try Again", Toast.LENGTH_SHORT).show();
                                }
                            }

                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                });
    }
}










