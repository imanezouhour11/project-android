package com.example.firs_best_design;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {
    private FloatingActionButton btn_add_group;
    private SearchView searchView;
    private DatabaseReference databaseReference,groupRef;
    private RecyclerView recycleGroup;
    private FirebaseAuth auth;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
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
        }
    }

    public SearchView getSearchView() {
        return searchView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_groups, container, false);
        btn_add_group=v.findViewById(R.id.add_group);
        searchView=v.findViewById(R.id.searchView_groups);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Groups");
        recycleGroup=v.findViewById(R.id.groups_recyler_view);
        recycleGroup.setLayoutManager(new LinearLayoutManager(getContext()));
        groupRef=FirebaseDatabase.getInstance().getReference().child("Groups");
        auth=FirebaseAuth.getInstance();
        btn_add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),Create_Group_activity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Group> options=
                new FirebaseRecyclerOptions.Builder<Group>()
                        .setQuery(groupRef,Group.class).build();
        FirebaseRecyclerAdapter<Group, GroupHolderView> adapter=

                new FirebaseRecyclerAdapter<Group, GroupHolderView>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final GroupHolderView holder, int i, @NonNull Group group) {
                        final String usersIDs=getRef(i).getKey();
                        final String[] imageProfile = {"default image"};
                        groupRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("image")){
                                    imageProfile[0] =dataSnapshot.child("image").getValue().toString();
                                    Picasso.with(getContext()).load(imageProfile[0]).into(holder.iconGoup);
                                }
                                final String nameGroup=dataSnapshot.child("name").getValue().toString();
                                final String category=dataSnapshot.child("category").getValue().toString();
                                holder.nameGroup.setText(nameGroup);
                                holder.category.setText(category);
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(),ChatGroupActivity.class);
                                        intent.putExtra("viti_name",usersIDs);
                                        intent.putExtra("groupName",nameGroup);
                                        intent.putExtra("iconGroup",imageProfile[0]);
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
                    public GroupHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview,parent,false);
                        return new GroupHolderView(v);
                    }
                };
        recycleGroup.setAdapter(adapter);
        adapter.startListening();
    }
    public static class GroupHolderView extends  RecyclerView.ViewHolder{
        TextView nameGroup,category;
        CircleImageView iconGoup;
        public GroupHolderView(@NonNull View itemView) {
            super(itemView);
            iconGoup=itemView.findViewById(R.id.recycle_image_user);
            nameGroup=itemView.findViewById(R.id.username_item_chat);
            category=itemView.findViewById(R.id.status_item_chat);

        }
    }

}
