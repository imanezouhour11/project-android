package com.example.firs_best_design;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatGroupActivity extends AppCompatActivity {
private Toolbar chatGroupToolbar;
private String nameGroup,iconGroup;
private CircleImageView iconGrouImage;
private TextView nameOfGroup;
private Button btn_send;
private DatabaseReference groupRef;
private EditText messageGroup;
private FirebaseAuth auth;
private RecyclerView recyclerViewGroup;
    List<ChatGrouModel> chatList;
    AdapterGroupChat adapterGroupChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        chatGroupToolbar=findViewById(R.id.chatGroup_toolbar);
        nameGroup=getIntent().getExtras().get("groupName").toString();
        iconGroup=getIntent().getExtras().get("iconGroup").toString();
        iconGrouImage=findViewById(R.id.imageChatGroup);
        nameOfGroup=findViewById(R.id.groupNameChat);
        btn_send=findViewById(R.id.button_send_group);
        messageGroup=findViewById(R.id.editTextGroup);
        recyclerViewGroup=findViewById(R.id.chatListGroupRceyclerv);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerViewGroup.setLayoutManager(layoutManager);
        groupRef=FirebaseDatabase.getInstance().getReference().child("Groups");
        auth=FirebaseAuth.getInstance();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_send));
                String textMessage=messageGroup.getText().toString();
                if(TextUtils.isEmpty(textMessage)){

                }else{
                    sendMessage(textMessage);
                }

            }
        });
        nameOfGroup.setText(nameGroup);
        Picasso.with(this).load(iconGroup).placeholder(R.drawable.images).into(iconGrouImage);
        setSupportActionBar(chatGroupToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        chatGroupToolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        readMessage();
    }

    private void sendMessage(String textMessage) {
        DatabaseReference ref=groupRef.child(nameGroup);
        String timStamp= String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",auth.getCurrentUser().getUid());
        hashMap.put("message",textMessage);
        hashMap.put("timestamp",timStamp);
        ref.child("Messages").child(timStamp).setValue(hashMap);
        //reset editText
        messageGroup.setText("");

    }
    private void readMessage() {
        chatList=new ArrayList<>();
        groupRef.child(nameGroup).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ChatGrouModel chat=ds.getValue(ChatGrouModel.class);
                        chatList.add(chat);

                }
                adapterGroupChat=new AdapterGroupChat(ChatGroupActivity.this,chatList);
                adapterGroupChat.notifyDataSetChanged();
                recyclerViewGroup.setAdapter(adapterGroupChat);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
