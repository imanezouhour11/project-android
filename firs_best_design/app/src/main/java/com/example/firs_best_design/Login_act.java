package com.example.firs_best_design;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Login_act extends AppCompatActivity {
private Button register;
private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_act);
        register=findViewById(R.id.buttonRegister);
        login=findViewById(R.id.btn_login);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callHomeActivity();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_anim));
                callLoginScreen();

            }
        });

    }
    public void callLoginScreen(){
        Intent intent=new Intent(getApplicationContext(),Login_Activity.class);
        Pair[] pairs=new Pair[1];
        pairs[0]=new Pair<View,String>(findViewById(R.id.btn_login),"transition_login_button");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(this,pairs);
            startActivity(intent,options.toBundle());
        }else{
            startActivity(intent);

        }

    }
    public void callHomeActivity(){
        Intent intent =new Intent(this,registration.class);
        startActivity(intent);
    }


}
