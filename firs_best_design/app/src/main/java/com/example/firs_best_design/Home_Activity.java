package com.example.firs_best_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home_Activity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    boolean activated;
    Fragment fragment;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private TextView username_nav;
    private String currentUserID;
    private DatabaseReference databaseReference;
    private CircleImageView image_nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);
        toolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        fragment=null;
        activated=false;
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction ft=fragmentManager.beginTransaction();
        ft.replace(R.id.container_frag,new HomeFragment());
        ft.commit();
        final Intent intent =new Intent(this,Profile.class);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 fragment=null;
                navigationView.setCheckedItem(item);
                int id=item.getItemId();
                switch (id){
                    case R.id.profil: startActivity(intent);
                       break;
                    case  R.id.settings:  break;
                    case R.id.contacts:
                        fragment=new ContactsFragment();
                        toolbar.getMenu().clear();
                        MenuInflater inflater_contact=getMenuInflater();
                        inflater_contact.inflate(R.menu.toolbar_menu,toolbar.getMenu());
                        final MenuItem menuItemContact=toolbar.getMenu().findItem(R.id.search);
                        final View v_contact=menuItemContact.getActionView();
                        menuItemContact.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                SearchView frag = ((ContactsFragment) fragment).getSearchView();
                                frag.setVisibility(View.VISIBLE);
                                return true;
                            } });

                    break;
                    case R.id.chats:   fragment  =new Chats_Fragment();
                        toolbar.getMenu().clear();
                        MenuInflater inflater2=getMenuInflater();
                        inflater2.inflate(R.menu.toolbar_menu,toolbar.getMenu());
                        final MenuItem menuItem=toolbar.getMenu().findItem(R.id.search);
                        final View v=menuItem.getActionView();
                        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                SearchView frag = ((Chats_Fragment) fragment).getSearchView();
                                frag.setVisibility(View.VISIBLE);
                                return true;
                            } });
                        break;
                    case R.id.groups:  fragment=new GroupsFragment();
                        toolbar.getMenu().clear();
                        MenuInflater inflaterGroup=getMenuInflater();
                        inflaterGroup.inflate(R.menu.toolbar_menu,toolbar.getMenu());
                        final MenuItem menuItemGroup=toolbar.getMenu().findItem(R.id.search);
                        final View groupV=menuItemGroup.getActionView();
                        menuItemGroup.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                SearchView frag = ((GroupsFragment) fragment).getSearchView();
                                frag.setVisibility(View.VISIBLE);
                                return true;
                            } });
                    break;
                     case  R.id.home:  fragment=new HomeFragment();
                            toolbar.getMenu().clear();
                            MenuInflater inflater=getMenuInflater();
                            inflater.inflate(R.menu.menu_home,toolbar.getMenu());
                                    break;
                    case R.id.chats_private_request:fragment=new Private_request_chat();    break;
                    default: break;
                    case R.id.logout:auth.signOut();
                                Intent intent=new Intent(getApplicationContext(),Login_Activity.class);
                                startActivity(intent);
                }
                if(fragment!=null){
                    FragmentManager fragmentManager=getSupportFragmentManager();
                    FragmentTransaction ft=fragmentManager.beginTransaction();
                    ft.replace(R.id.container_frag,fragment);
                    ft.commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        if(!drawerLayout.isDrawerOpen(GravityCompat.START)){
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            toolbar.setNavigationIcon(R.drawable.ic_chevron);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });

        }
        username_nav=findViewById(R.id.username_text_nav);
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        currentUserID = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        View headerView = navigationView.getHeaderView(0);
        username_nav=headerView.findViewById(R.id.username_text_nav);
        image_nav=headerView.findViewById(R.id.profil_nav);
        retrieveUserInfo();

    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu,menu);
        final MenuItem menuItem=menu.findItem(R.id.search);
        final View v=menuItem.getActionView();
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SearchView frag=((Chats_Fragment)fragment).getSearchView();
                frag.setVisibility(View.VISIBLE);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        return super.onOptionsItemSelected(item);

    }
    private void retrieveUserInfo() {
        databaseReference.child("Users").child(currentUserID).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if( (dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && dataSnapshot.hasChild("image")){
                            String retrieveUsername=dataSnapshot.child("name").getValue().toString();
                            String retrieveImage=dataSnapshot.child("image").getValue().toString();
                            username_nav.setText(retrieveUsername);
                            Picasso.with(Home_Activity.this).load(retrieveImage).into(image_nav);
                        }else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retrieveUsername=dataSnapshot.child("name").getValue().toString();
                            username_nav.setText(retrieveUsername);

                        }else{
                            username_nav.setVisibility(View.VISIBLE);
                            Toast.makeText(Home_Activity.this,"please , Set ur account setting",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

}
