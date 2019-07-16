package com.example.bonyfade808.graphitearts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends Activity {


    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button button_sign_in;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page);

        email = findViewById(R.id.login_emailET);
        password = findViewById(R.id.login_passwordET);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        button_sign_in = findViewById(R.id.sign_in_btn);

        button_sign_in.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if (v == button_sign_in){
                    LoginUser();
                }
            }
        });

        }


        public void LoginUser(){

        String Email = email.getText().toString().trim();
        String Password = password.getText().toString();

            if (TextUtils.isEmpty(Email)) {

                Toast.makeText(this, "A field is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(Password)) {

                Toast.makeText(this, "A field s empty", Toast.LENGTH_LONG).show();
                return;
            }


        mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    currentUser = mAuth.getCurrentUser();
                    finish();
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                }else {

                    Toast.makeText(SignIn.this, "Couldn't Login", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Toast.makeText(SignIn.this, "Login Successful", Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignIn.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}

