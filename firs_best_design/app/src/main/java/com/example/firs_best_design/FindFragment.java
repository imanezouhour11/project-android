package com.example.firs_best_design;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFragment extends Fragment {
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference UsersRef;
    private DatabaseReference ChatRequestRef, NotificationRef,ContactsRef;
    private String receiverUser, senderUser;
    private FirebaseAuth auth;
    Toolbar tool;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            setHasOptionsMenu(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_find, container, false);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        FindFriendsRecyclerList = (RecyclerView) v.findViewById(R.id.findFriends_recyler_view);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        auth = FirebaseAuth.getInstance();
        senderUser = auth.getCurrentUser().getUid();


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(UsersRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendView> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendView>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FindFriendView holder, final int position, @NonNull Contacts model) {
                        holder.userName.setText(model.getName());
                        holder.userStatus.setText(model.getStatus());
                        Picasso.with(getContext()).load(model.getImage()).placeholder(R.drawable.images).into(holder.profileImage);
                        holder.profileImage.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.animation_user_photo));
                        holder.itemView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.animation_holder));
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                receiverUser = getRef(position).getKey();
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                                builder.setTitle("Do you want to add this person ?");
                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                            ChatRequestRef.child(senderUser).child(receiverUser)
                                                    .child("request_type").setValue("sent")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                ChatRequestRef.child(receiverUser).child(senderUser)
                                                                        .child("request_type").setValue("received")
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                                                    chatNotificationMap.put("from", senderUser);
                                                                                    chatNotificationMap.put("type", "request");
                                                                                    NotificationRef.child(receiverUser).push()
                                                                                            .setValue(chatNotificationMap)
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {

                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        }


                                });
                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();

                            }
                        });




                        }




                    @NonNull
                    @Override
                    public FindFriendView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview, viewGroup, false);
                        FindFriendView viewHolder = new FindFriendView(view);
                        return viewHolder;
                    }

                };


        FindFriendsRecyclerList.setAdapter(adapter);

        adapter.startListening();
    }


    public static class FindFriendView extends RecyclerView.ViewHolder {
        TextView userName, userStatus;
        CircleImageView profileImage;


        public FindFriendView(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.username_item_chat);
            userStatus = itemView.findViewById(R.id.status_item_chat);
            profileImage = itemView.findViewById(R.id.recycle_image_user);
        }

    }


}

