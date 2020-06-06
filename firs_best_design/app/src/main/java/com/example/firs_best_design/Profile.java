package com.example.firs_best_design;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextInputLayout textInputLayout_username;
    private TextInputLayout textInputLayout_status;
    private TextInputEditText textInputEditText_username;
    private TextInputEditText textInputEditText_status;
    private Button btn_update_profil;
    private CircleImageView profil_image;
    private static final int GalleryPick = 1;
    private String currentUserID;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference UserProfileImagesRef;
    private ProgressDialog loadingBar;
    private ImageView backHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile");
        textInputLayout_username =findViewById(R.id.username_layout_input);
        textInputLayout_status = findViewById(R.id.status_layout_input);
        textInputEditText_username = findViewById(R.id.edit_layout_username);
        textInputEditText_status = findViewById(R.id.edit_layout_status);
        btn_update_profil = findViewById(R.id.profill_update_btn);
        profil_image = findViewById(R.id.profil_image);
        loadingBar=new ProgressDialog(Profile.this);
        backHome=findViewById(R.id.back_image);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                Intent intent=new Intent(getApplicationContext(),Home_Activity.class);
                startActivity(intent);
            }
        });
        profil_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
        btn_update_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameText = textInputEditText_username.getText().toString();
                String userStatusText = textInputEditText_status.getText().toString();
                v.startAnimation(AnimationUtils.loadAnimation(Profile.this, R.anim.button_anim));
                if (TextUtils.isEmpty(usernameText)) {
                    textInputLayout_username.setError(" Field should not be Empty..");
                    Toast.makeText(Profile.this, "username is null", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(userStatusText)) {
                    textInputLayout_status.setError(" Field should not be Empty..");
                }
                HashMap<String, Object> infoProfiles = new HashMap<>();
                infoProfiles.put("uid", currentUserID);
                infoProfiles.put("name", usernameText);
                infoProfiles.put("status", userStatusText);
                databaseReference.child("Users").child(currentUserID).updateChildren(infoProfiles).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Profile.this, "profile unpdated", Toast.LENGTH_SHORT).show();
                                    sendUserToHome();
                                } else {
                                    String exception = task.getException().toString();
                                    Toast.makeText(Profile.this, exception, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );

            }


        });
        retrieveUserInfo();
    }
    private void sendUserToHome() {
        Intent intent=new Intent(Profile.this,Home_Activity.class);
        startActivity(intent);
    }
    private void retrieveUserInfo() {
        databaseReference.child("Users").child(currentUserID).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if( (dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && dataSnapshot.hasChild("image")){
                            String retrieveUsername=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                            String retrieveImage=dataSnapshot.child("image").getValue().toString();
                            textInputEditText_username.setText(retrieveUsername);
                            textInputEditText_status.setText(retrieveStatus);
                            Picasso.with(Profile.this).load(retrieveImage).into(profil_image);

                        }else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retrieveUsername=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                            textInputEditText_username.setText(retrieveUsername);
                            textInputEditText_status.setText(retrieveStatus);
                        }else{
                            textInputEditText_username.setVisibility(View.VISIBLE);
                            Toast.makeText(Profile.this,"please , Set ur account setting",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode== Activity.RESULT_OK  &&  data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == Activity.RESULT_OK)
            {                              Toast.makeText(Profile.this, "ppp...", Toast.LENGTH_SHORT).show();

                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();


                StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(Profile.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                            final String downloaedUrl = task.getResult().getDownloadUrl().toString();

                            databaseReference.child("Users").child(currentUserID).child("image")
                                    .setValue(downloaedUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(Profile.this, "Image save in Database, Successfully...", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                Toast.makeText(Profile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(Profile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }
    }




}
