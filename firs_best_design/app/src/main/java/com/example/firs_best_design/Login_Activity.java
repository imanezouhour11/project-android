package com.example.firs_best_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login_Activity extends AppCompatActivity {
    private Button go;
    private FirebaseUser userCurrent;
    private FirebaseAuth auth;
    private ImageView imageBack;
    private TextInputEditText usernameText, passwordText;
    private ProgressDialog progressLoad;
    private DatabaseReference databaseReferenceUsers;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        initializeFileds();
        auth = FirebaseAuth.getInstance();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = auth.getCurrentUser();
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                allowUserToLogin();

            }
        });
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                Intent intent = new Intent(getApplicationContext(), Login_act.class);
                startActivity(intent);
            }
        });
    }

    public void initializeFileds() {
        go = findViewById(R.id.register_btn);
        imageBack = findViewById(R.id.back);
        usernameText = findViewById(R.id.username_login);
        passwordText = findViewById(R.id.password_login);
        progressLoad = new ProgressDialog(this);

    }

    private void allowUserToLogin() {
        String email = usernameText.getText().toString();
        String passwrod = passwordText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please, enter your email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(passwrod)) {
            Toast.makeText(this, "please, enter password", Toast.LENGTH_SHORT).show();
        } else {
            progressLoad.setTitle("sign in ");
            progressLoad.setMessage("wait..");
            progressLoad.setCanceledOnTouchOutside(true);
            progressLoad.show();
            auth.signInWithEmailAndPassword(email, passwrod)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userID = auth.getCurrentUser().getUid();
                                databaseReferenceUsers.child(userID).child("TypingTo").setValue("noOne");
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                databaseReferenceUsers.child(userID).child("device_token")
                                        .setValue(deviceToken).addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    sendUserToHome();
                                                    Toast.makeText(Login_Activity.this, "login successful", Toast.LENGTH_SHORT).show();
                                                    progressLoad.dismiss();
                                                }
                                            }
                                        }
                                );

                            } else {
                                String exception = task.getException().toString();
                                Toast.makeText(Login_Activity.this, exception, Toast.LENGTH_SHORT).show();
                                progressLoad.dismiss();
                            }
                        }
                    });
        }
    }
public void  sendUserToHome(){
      /*  profilFragment fragment=new profilFragment();
    FragmentManager fragmentManager=getSupportFragmentManager();
    FragmentTransaction ft=fragmentManager.beginTransaction();
    ft.replace(R.id.fragment_contain,fragment);
    ft.commit();*/
      Intent intent=new Intent(this,Home_Activity.class);
      startActivity(intent);
}
}
