package com.example.firs_best_design;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
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


public class Chat_Room extends AppCompatActivity {
private Toolbar chatToolbar;
private Button button_send;
private EditText edit;
private String messageReceiverID,messageSenderID,messageReceiverName,imageReceiver;
private TextView userText;
private CircleImageView imageProfileReceiver;
private DatabaseReference chatsRefBase,userRef;
private FirebaseAuth auth;
ValueEventListener seenLisnter;
RecyclerView recyclerViewChat;
DatabaseReference databaseReferenceForSeen;
List<ModelChat> chatList;
AdapterChat adapterChat;
TextView typeStatus;
ScrollView scrollview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__room);
        chatToolbar=findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);
        auth=FirebaseAuth.getInstance();
        messageReceiverID=getIntent().getExtras().get("viti_name").toString();
        messageReceiverName=getIntent().getExtras().get("username").toString();
        messageSenderID=auth.getCurrentUser().getUid();
        imageReceiver=getIntent().getExtras().get("image").toString();
        userText=findViewById(R.id.usernameChat);
        imageProfileReceiver=findViewById(R.id.imageChat);
        typeStatus=findViewById(R.id.statusType);
        scrollview=findViewById(R.id.scrollview_chat);
        recyclerViewChat=findViewById(R.id.chatListRceyclerv);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.setLayoutManager(layoutManager);
        Picasso.with(this).load(imageReceiver).placeholder(R.drawable.images).into(imageProfileReceiver);
        userText.setText(messageReceiverName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        chatToolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        button_send=findViewById(R.id.button_send);
        edit=findViewById(R.id.editText);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_send.setBackgroundDrawable(ActivityCompat.getDrawable(getApplicationContext(),R.drawable.send_btn));

            }
        });
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().trim().length()==0){
                        checkTypingStatus("noOne");
                    }else{
                        checkTypingStatus(messageReceiverID);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        retriveStatusTyping();

       button_send.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_send));
               String textMessage=edit.getText().toString();
               if(TextUtils.isEmpty(textMessage)){
                   //text empty
               }else{
                   sendMessage(textMessage);
                   scrollview.fullScroll(ScrollView.FOCUS_DOWN);
               }

           }
       });
       readMessage();
       seenMessage();

    }
    private void checkTypingStatus(String typing){
        userRef=FirebaseDatabase.getInstance().getReference("Users").child(messageSenderID);
        HashMap<String,Object> hashmap =new HashMap<>();
        hashmap.put("TypingTo",typing);
        userRef.updateChildren(hashmap);

    }

    private void seenMessage() {
        databaseReferenceForSeen=FirebaseDatabase.getInstance().getReference().child("Chats");
        seenLisnter=databaseReferenceForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChat chat= ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(messageReceiverID) && chat.getSender().equals(messageSenderID)){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isSeen",true);
                        ds.getRef().updateChildren(hashMap);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readMessage() {
        chatList=new ArrayList<>();
        chatsRefBase=FirebaseDatabase.getInstance().getReference().child("Chats");
        chatsRefBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(messageReceiverID) && chat.getSender().equals(messageSenderID) ||
                            chat.getReceiver().equals(messageSenderID) && chat.getSender().equals(messageReceiverID)){
                        chatList.add(chat);
                    }

                }
                adapterChat=new AdapterChat(Chat_Room.this,chatList,imageReceiver);
                adapterChat.notifyDataSetChanged();
                recyclerViewChat.setAdapter(adapterChat);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String textMessage) {
        chatsRefBase=FirebaseDatabase.getInstance().getReference();
        String timStamp= String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",messageSenderID);
        hashMap.put("receiver",messageReceiverID);
        hashMap.put("message",textMessage);
        hashMap.put("timestamp",timStamp);
        hashMap.put("isSeen",false);
        chatsRefBase.child("Chats").push().setValue(hashMap);
        //reset editText
        edit.setText("");

    }
    public void retriveStatusTyping(){
        userRef=FirebaseDatabase.getInstance().getReference().child("Users").child(messageSenderID);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String typingStatus=dataSnapshot.child("TypingTo").getValue().toString();
                    if(typingStatus.equals(messageSenderID)){
                        typeStatus.setText("Typing...");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    @Override
    protected void onPause() {
        super.onPause();
        checkTypingStatus("noOne");
        databaseReferenceForSeen.removeEventListener(seenLisnter);
    }
}
