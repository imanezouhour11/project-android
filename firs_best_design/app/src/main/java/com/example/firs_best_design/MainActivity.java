package com.example.firs_best_design;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
public class MainActivity extends AppCompatActivity {
private FirebaseUser currentUser;
private FirebaseAuth auth;
private TextView username_nav;
private String currentUserID;
private DatabaseReference databaseReference;
private CircleImageView image_nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username_nav=findViewById(R.id.username_text_nav);
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        currentUserID = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        username_nav=findViewById(R.id.username_text_nav);
        image_nav=findViewById(R.id.profil_image);
        retrieveUserInfo();
    }
    private void retrieveUserInfo() {
        databaseReference.child("Users").child(currentUserID).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if( (dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && dataSnapshot.hasChild("image")){
                            String retrieveUsername=dataSnapshot.child("name").getValue().toString();
                            String retrieveImage=dataSnapshot.child("image").getValue().toString();
                            username_nav.setText(retrieveUsername);
                            Picasso.with(MainActivity.this).load(retrieveImage).into(image_nav);
                        }else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retrieveUsername=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                            username_nav.setText(retrieveUsername);

                        }else{
                            username_nav.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this,"please , Set ur account setting",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

}