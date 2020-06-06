package com.example.firs_best_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class registration extends AppCompatActivity {
private Button button_register;
private TextInputLayout username,password;
private TextInputEditText usernameText,passwordText;
private FirebaseAuth auth;
private DatabaseReference databaseReference;
private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        initializeFields();
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                createNewAccount();

            }
        });
    }

    public void initializeFields(){
        button_register=(Button)findViewById(R.id.register_btn);
        username=findViewById(R.id.username_register);
        usernameText=findViewById(R.id.username_register_text);
        password=findViewById(R.id.password_register);
        passwordText=findViewById(R.id.password_register_text);
        loadingBar=new ProgressDialog(this);

    }
    private void createNewAccount() {
        String email = usernameText.getText().toString();
        String passwrod = passwordText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please, enter your email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(passwrod)) {
            Toast.makeText(this, "please, enter password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("please wait, while we arre creating new account for u");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            auth.createUserWithEmailAndPassword(email, passwrod)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String deviceToken= FirebaseInstanceId.getInstance().getToken();
                                String currentUserId=auth.getCurrentUser().getUid();
                                databaseReference.child("Users").child(currentUserId).setValue("");
                                databaseReference.child("Users").child(currentUserId).child("device_token")
                                        .setValue(deviceToken);
                                databaseReference.child("Users").child(currentUserId).child("TypingTo").setValue("noOne");
                                Toast.makeText(registration.this, "account created succefully", Toast.LENGTH_SHORT).show();
                                sendUserToHome();
                                loadingBar.dismiss();
                            }else{
                                String message=task.getException().toString();
                                Toast.makeText(registration.this, "Error"+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
    public void sendUserToHome(){
        Intent intent=new Intent(this,Login_Activity.class);
        startActivity(intent);
    }
}
