package com.example.bonyfade808.graphitearts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;



public class HomePage extends AppCompatActivity {

    //firebase
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPosts adapterPosts;


    //storage
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //recycler
        recyclerView = findViewById(R.id.postRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);


        //init posts
        postList = new ArrayList<>();


        loadPosts();


    }

    private void loadPosts() {
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    postList.add(modelPost);

                    //adapter
                    adapterPosts = new AdapterPosts(HomePage.this, postList);

                    recyclerView.setAdapter(adapterPosts);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //in case of error // add a toast here databaerror.getmessage


            }
        });

    }

    private void searchPosts(final String searchQuery){
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    if(modelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase())) {
                        postList.add(modelPost);
                    }

                    //adapter
                    adapterPosts = new AdapterPosts(HomePage.this, postList);

                    recyclerView.setAdapter(adapterPosts);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //in case of error // add a toast here databaeerror.getmessage


            }
        });



    }

    //toolbar inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);


        super.onCreateOptionsMenu(menu);

        return true;
    }

    private void checkUserStatus(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){


        }else {

            startActivity(new Intent(getApplicationContext(), SignIn.class));
        }



    }




            //toolbar options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.add_post){
            startActivity(new Intent((getApplicationContext()), PostUpload.class));

        }else if (id == R.id.profile_toolbar){
            startActivity(new Intent((getApplicationContext()), ProfileActivity.class));

        }else if (id == R.id.logout){
            mAuth.signOut();
            checkUserStatus();

        }else if (id == R.id.search){

            Toast.makeText(this, "You clicked Search", Toast.LENGTH_SHORT).show();

        }
        return true;
    }

}