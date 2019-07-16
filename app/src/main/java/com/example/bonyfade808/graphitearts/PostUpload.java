package com.example.bonyfade808.graphitearts;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class PostUpload extends AppCompatActivity {

    //firebase
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //storage
    StorageReference storageReference;

    ActionBar actionBar;


    //image pick constant
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //views
    EditText artTitle, artPrice;
    ImageView uploadImage;
    Button uploadArtBtn;

    //user info
    String name, email, uid, dp;


    //image to be saved in this uri
    Uri image_uri = null;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_upload);
        setSupportActionBar(toolbar);

        //action bar
        actionBar = getSupportActionBar();
        actionBar.setTitle("Add New Post");

        pd =new ProgressDialog(this);

        //back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        //user information to include in post
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    name = "" + ds.child("name").getValue();
                    email = "" + ds.child("email").getValue();
                    dp = "" + ds.child("image").getValue();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //initialize views
        artTitle = findViewById(R.id.art_title);
        uploadImage = findViewById(R.id.artChooser);
        uploadArtBtn = findViewById(R.id.uploadArtBtn);
        artPrice = findViewById(R.id.art_price);


        //get image
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        //upload button listener
        uploadArtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = artPrice.getText().toString().trim();
                String title = artTitle.getText().toString().trim();
                if (TextUtils.isEmpty(title)){
                    Toast.makeText(PostUpload.this,"Enter a Title...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(image_uri==null){
                    Toast.makeText(PostUpload.this,"Select an Image", Toast.LENGTH_SHORT).show();
                }else {
                    uploadData(title,price,String.valueOf(image_uri));
                }
            }
        });

    }

    private void uploadData(final String title,final String price,final String uri) {
        pd.setMessage("Uploading Art...");
        pd.show();

        final String timestamp = String.valueOf(System.currentTimeMillis());
        String filePathandName = "Posts/" + "post_" + timestamp;

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathandName);
        ref.putFile(Uri.parse(uri))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        String downloadUri = uriTask.getResult().toString();

                        if(uriTask.isSuccessful()){

                            HashMap<Object,String> hashmap = new HashMap<>();
                            //put post info
                            hashmap.put("uid",uid);
                            hashmap.put("uName",name);
                            hashmap.put("uEmail",email);
                            hashmap.put("uDp",dp);
                            hashmap.put("postId",timestamp);
                            hashmap.put("postTitle",title);
                            hashmap.put("postImage",downloadUri);
                            hashmap.put("postTime",timestamp);
                            hashmap.put("postPrice", price);

                            //path to store data
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

                            ref.child(timestamp).setValue(hashmap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(PostUpload.this,"Art Piece Uploaded", Toast.LENGTH_SHORT).show();
                                            // reset views
                                            artPrice.setText("");
                                            artTitle.setText("");
                                            uploadImage.setImageURI(null);
                                            image_uri = null;

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PostUpload.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }

                });
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!= null){
            //user is signed in to stay
            //set email of logged user
            email = user.getEmail();
            uid = user.getUid();


        }else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    private void pickFromGallery() {
        //Intent to choose image from the gallery

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // this method will be called after picking an image

        if(resultCode==RESULT_OK){

            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                //get uri
                image_uri = data.getData();
                //set to imageView
                uploadImage.setImageURI(image_uri);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}