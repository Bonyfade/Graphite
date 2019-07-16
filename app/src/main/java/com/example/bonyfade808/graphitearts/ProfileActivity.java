package com.example.bonyfade808.graphitearts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends Activity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //storage
    StorageReference storageReference;
    //path profile pictures will be saved
    String storagePath = "Users_Profile_Imgs/";

    //xml
    ImageView avatar;
    TextView nameProf, bioProf, phoneProf, emailProf;
    FloatingActionButton fab, fab2;

    //progress dialog
    ProgressDialog progressDialog;

    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;

    Uri imageURI;

    //for checking profile picture

    String profilePhoto;


    public ProfileActivity(){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();


        avatar = findViewById(R.id.profile_pic);
        nameProf = findViewById(R.id.nameProf);
        bioProf = findViewById(R.id.bioProf);
        phoneProf = findViewById(R.id.phoneProf);
        emailProf = findViewById(R.id.emailProf);
        fab = findViewById(R.id.fab);
        fab2= findViewById(R.id.fab2);

        progressDialog = new ProgressDialog(this);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //check until required data get

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String name = ""+ ds.child("Name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String uid = ""+ ds.child("uid").getValue();
                    String phoneNumber = ""+ ds.child("Phone").getValue();
                    String image = ""+ ds.child("image").getValue();
                    String bio = ""+ ds.child("Bio").getValue();

                    //set data
                    nameProf.setText(name);
                    emailProf.setText(email);
                    phoneProf.setText(phoneNumber);
                    bioProf.setText(bio);
                    try {
                        //set image profile
                        Picasso.get().load(image).into(avatar);

                    }catch (Exception e){
                        //set default image
                        Picasso.get().load(R.drawable.ic_default_face).into(avatar);

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //floating button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomePage.class));
            }
        });

    }

    private void showEditProfileDialog() {
     //dialog options
     String options[] = {"Edit Name","Edit Profile Picture","Edit Bio","Edit PhoneNumber"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action...");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle clicks in the dialog

                if(which == 0){
                    //update Name
                    showNamePhoneBioUpdateDialog("Name");

                }else if ( which == 1) {
                    pickFromGallery();
                    progressDialog.setMessage("Updating...");
                    profilePhoto = "image";
                }else if ( which == 2) {
                    showNamePhoneBioUpdateDialog("Bio");
                    //update bio

                }else if ( which == 3) {
                    //update number
                    showNamePhoneBioUpdateDialog("Phone");
                }
            }
        });
        builder.create().show();

    }

    private void showNamePhoneBioUpdateDialog(final String key) {

        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit " + key);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        final EditText editText = new EditText(this);
        editText.setHint("Enter " + key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //button to update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input from edittext
                String value = editText.getText().toString().trim();
                //validate input
                if(!TextUtils.isEmpty(value)){

                    progressDialog.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }else
                    Toast.makeText(ProfileActivity.this,"Please Enter " + key, Toast.LENGTH_SHORT).show();
            }
        });
        //button to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //create and show dialog
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //method will be called after an image is picked

        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE){
                //get uri of image picked
                imageURI = data.getData();
                //set image
                avatar.setImageURI(imageURI);

                uploadProfilePicture(imageURI);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePicture(Uri imageURI) {
        progressDialog.show();

        //path and name of image to be stored
        //eg: Users_Profile_Imgs/image/e2323j44b4
        String filePathAndName = storagePath +""+ profilePhoto +"_"+user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(imageURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded to storage and gets url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        // check if image is uploaded
                        if (uriTask.isSuccessful()){
                            HashMap<String,Object> results = new HashMap<>();
                            results.put(profilePhoto, downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //url in database is added successfully
                                            progressDialog.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Picture Updated", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();




                                }
                            });

                        }else{

                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "An error Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_REQUEST_CODE);

    }
}
