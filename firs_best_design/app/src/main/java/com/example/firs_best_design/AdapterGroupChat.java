package com.example.firs_best_design;

import android.content.Context;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.MyHolder> {
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<ChatGrouModel> chatGroupList;
    FirebaseAuth auth;


    public AdapterGroupChat(Context context, List<ChatGrouModel> chatGroupList) {
        this.context = context;
        this.chatGroupList = chatGroupList;
        auth=FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if(i==MSG_TYPE_RIGHT){
            View v= LayoutInflater.from(context).inflate(R.layout.layout_group_right,parent,false);
            return new MyHolder(v);
        }else{
            View v= LayoutInflater.from(context).inflate(R.layout.layout_group_left,parent,false);
            return new MyHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int i) {
        String message=chatGroupList.get(i).getMessage();
        String timeStamp=chatGroupList.get(i).getTimestamp();
        if(timeStamp!=null){
            Calendar cal=Calendar.getInstance(Locale.FRENCH);
            cal.setTimeInMillis(Long.parseLong(timeStamp));
            String dateTime= DateFormat.format("hh:mm aa",cal).toString();
            holder.timeIv.setText(dateTime);
        }
        String sender=chatGroupList.get(i).getSender();
        holder.messageIv.setText(message);
         DatabaseReference userRef=FirebaseDatabase.getInstance().getReference().child("Users").child(sender);
                userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                holder.nameIv.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return chatGroupList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(chatGroupList.get(position).getSender().equals(auth.getCurrentUser().getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView messageIv,timeIv,nameIv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            messageIv=itemView.findViewById(R.id.messageIv);
            nameIv=itemView.findViewById(R.id.nameIv);
            timeIv=itemView.findViewById(R.id.timeIv);






        }
    }
}
