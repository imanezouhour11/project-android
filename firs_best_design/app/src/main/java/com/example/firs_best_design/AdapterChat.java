package com.example.firs_best_design;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<ModelChat> chatList;
    String imgURL;
    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imgURL) {
        this.context = context;
        this.chatList = chatList;
        this.imgURL = imgURL;
    }

    @Override
    public int getItemViewType(int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if(i==MSG_TYPE_RIGHT){
                View v= LayoutInflater.from(context).inflate(R.layout.right_chat,parent,false);
                return new MyHolder(v);
        }else{
            View v= LayoutInflater.from(context).inflate(R.layout.left_chat,parent,false);
            return new MyHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        String message=chatList.get(i).getMessage();
        String timeStamp=chatList.get(i).getTimestamp();
        if(timeStamp!=null){
            Calendar cal=Calendar.getInstance(Locale.FRENCH);
            cal.setTimeInMillis(Long.parseLong(timeStamp));
            String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
            holder.timeIv.setText(dateTime);
        }

        holder.messageIv.setText(message);
        try{
            Picasso.with(context).load(imgURL).placeholder(R.drawable.images).into(holder.profileIv);
        }catch(Exception e){

        }

        if(i==chatList.size()-1){
           if(chatList.get(i).isSeen()){
               holder.isSeenIv.setText("Seen");
           }else{
               holder.isSeenIv.setText("Delivred");
                    }
        }else{
            holder.isSeenIv.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        CircleImageView profileIv;
        TextView messageIv,timeIv,isSeenIv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv=itemView.findViewById(R.id.profileIv);
            messageIv=itemView.findViewById(R.id.messageIv);
            timeIv=itemView.findViewById(R.id.timeIv);
            isSeenIv=itemView.findViewById(R.id.isSeenIv);


        }
    }
}
