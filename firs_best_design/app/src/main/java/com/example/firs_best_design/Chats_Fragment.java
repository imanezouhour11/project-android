package com.example.firs_best_design;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Chats_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Chats_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView_chats;
    SearchView searchView;
    private List<Chats> inbox;
    private Toolbar toolbar;
    private DatabaseReference chatsRef, UsersRef;
    private FirebaseAuth auth;
    private String currentUserId;
    String retImage = "default_image";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Chats_Fragment() {
        // Required empty public constructor


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chats_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Chats_Fragment newInstance(String param1, String param2) {
        Chats_Fragment fragment = new Chats_Fragment();
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

    public SearchView getSearchView() {
        return searchView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chats_, container, false);
        recyclerView_chats = v.findViewById(R.id.chats_recyler_view);
        searchView = v.findViewById(R.id.searchView_chats);
        toolbar = v.findViewById(R.id.toolbar_home);
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView_chats.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(chatsRef, Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, ChatViewHolder> adapter =

                new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int i, @NonNull Contacts model) {
                        final String usersIDs = getRef(i).getKey();
                        final String[] imageProfile = {"default image"};

                        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")) {
                                    imageProfile[0] = dataSnapshot.child("image").getValue().toString();
                                    Picasso.with(getContext()).load(imageProfile[0]).into(holder.profileImage);
                                }
                                final String retUsername = dataSnapshot.child("name").getValue().toString();
                                final String retStatus = dataSnapshot.child("status").getValue().toString();
                                holder.username.setText(retUsername);
                                holder.status.setText(retStatus);
                                holder.profileImage.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.animation_user_photo));
                                holder.itemView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.animation_holder));
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getContext(), Chat_Room.class);
                                        intent.putExtra("viti_name", usersIDs);
                                        intent.putExtra("username", retUsername);
                                        intent.putExtra("image", imageProfile[0]);
                                        startActivity(intent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
                        return new ChatViewHolder(v);
                    }
                };
        recyclerView_chats.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView username, status;
        CircleImageView profileImage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.recycle_image_user);
            status = itemView.findViewById(R.id.status_item_chat);
            username = itemView.findViewById(R.id.username_item_chat);

        }
    }
}
