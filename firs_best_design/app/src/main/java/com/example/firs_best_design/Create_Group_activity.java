package com.example.firs_best_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Create_Group_activity extends AppCompatActivity {
    private TextInputLayout groupName;
    private Button buttonCreateGroup;
    private Button back;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    ProgressDialog loadingBar;


    private AutoCompleteTextView auto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_create__group_activity);
        groupName=findViewById(R.id.text_group_name);
        buttonCreateGroup=findViewById(R.id.confirmButton);
        auto=findViewById(R.id.auto_text);
        back=findViewById(R.id.back_groups);
        loadingBar=new ProgressDialog(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Home_Activity.class);
                startActivity(intent);
            }
        });
        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                validateGroupName();
                if(TextUtils.isEmpty(groupName.getEditText().getText().toString())){

                }else{
                    createNewGroup(groupName.getEditText().getText().toString());
                    Intent intent=new Intent(getApplicationContext(),Home_Activity.class);
                    startActivity(intent);
                }


            }
        });
        final String []categoriesArray={"WebDev","Design"};
        final List<String> categories=new ArrayList<>(Arrays.asList(categoriesArray));
        ArrayAdapter<String> adapter_auto=new ArrayAdapter<>(this,R.layout.item_list,categories);
        auto.setAdapter(adapter_auto);
        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            boolean isExist=false;
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String val=auto.getText().toString();
                    boolean exist= categories.contains(val);
                    if(exist==false){
                        auto.setError("invalid");
                    }

                }
            }
        });


    }
    private boolean validateGroupName(){
        String name=groupName.getEditText().getText().toString();
        if(name.isEmpty()){
            groupName.setError("group name should not be empty");
            return false;
        }else{
            groupName.setError(null);
            return true;
        }
    }

    private void createNewGroup(final String s ) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("name",s);

        hashMap.put("created by",auth.getCurrentUser().getUid());
        hashMap.put("category",auto.getText().toString());
        loadingBar.setTitle("Creating group ");
        loadingBar.setMessage("wait..");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
       DatabaseReference refGroup=databaseReference.child("Groups");
       refGroup.child(s).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               loadingBar.dismiss();
               Toast.makeText(getApplicationContext(),"created succefuly",Toast.LENGTH_SHORT).show();
           }

       })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       loadingBar.dismiss();
                       Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                   }
               });

    }
}
