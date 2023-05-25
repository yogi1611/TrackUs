package tsukasa.finatic.schooltracking;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tsukasa.finatic.schooltracking.Interface.IFirebaseLoadDone;
import tsukasa.finatic.schooltracking.Interface.IRecycleItemClickListener;
import tsukasa.finatic.schooltracking.Model.User;
import tsukasa.finatic.schooltracking.Service.MyLocationReceiver;
import tsukasa.finatic.schooltracking.ViewHolder.UserViewHolder;
import tsukasa.finatic.schooltracking.databinding.ActivityHomeBinding;
import tsukasa.finatic.schooltracking.utils.AllPeopleActivity;
import tsukasa.finatic.schooltracking.utils.Common;

public class HomeActivity extends AppCompatActivity implements IFirebaseLoadDone {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();
    RecyclerView recycler_friend_list;
    FirebaseRecyclerAdapter<User, UserViewHolder> adapter, searchAdapter;
    IFirebaseLoadDone firebaseLoadDone;

    DatabaseReference publicLocation;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(HomeActivity.this,AllPeopleActivity.class));
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        View headerView = navigationView.getHeaderView(0);
        TextView txt_user_logged = (TextView) headerView.findViewById(R.id.txt_logged_email);
        txt_user_logged.setText(Common.loggedUser.getEmail());





        //View
        searchBar = findViewById(R.id.main_search_bar1);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    if (adapter != null) {
                        //if close search, restore default
                        recycler_friend_list.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        recycler_friend_list = (RecyclerView) findViewById(R.id.recycler_friend_list);
        recycler_friend_list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_friend_list.setLayoutManager(layoutManager);
        recycler_friend_list.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager) layoutManager).getOrientation()));


        //Update Location

        updateLocation();

        firebaseLoadDone = this;
        loadFriendList();
        loadSearchData();


    }

    private void loadSearchData() {

        List<String> lstUserEmail = new ArrayList<>();
        DatabaseReference userList =  FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);



        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot userSnapshot: snapshot.getChildren())
                {
                    User user = userSnapshot.getValue(User.class);
                    lstUserEmail.add(user.getEmail());

                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(lstUserEmail);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                String databaseError="Error";
                firebaseLoadDone.onFirebaseLoadFailed(databaseError);

            }
        });




    }

    private void loadFriendList() {

        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {

                Uri xx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

//                holder.imgView.setImageURI(model.getImg());

                holder.txt_user_email.setText(new StringBuilder(model.getEmail()));

                holder.setIRecyclerItemClickListener(new IRecycleItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        //show tracking
                        Common.trackingUser = model;
                        startActivity(new Intent(HomeActivity.this,TrackingActivity.class));


                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_user,parent,false);
                return new UserViewHolder(itemView);
            }
        };

        adapter.startListening();
        recycler_friend_list.setAdapter(adapter);


    }

    @Override
    protected void onStop() {
        if(adapter!=null) adapter.stopListening();
        if(searchAdapter!=null) searchAdapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null) adapter.startListening();
        if(searchAdapter!=null) searchAdapter.startListening();
    }



    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
       
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Turn on location and provide location accesss", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());

    }

    private void buildLocationRequest() {

        long locationFastestInterval = 500;
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis((long) 10f)
                .setMaxUpdateDelayMillis(100)
                .build();

//            locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,locationInterval);
//            locationRequest.setSmallestDisplacement(10f);
//            locationRequest.setFastestInterval(3000);
//            locationRequest.setInterval(5000);
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void startSearch(String search_value) {
        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST)
                .orderByChild("name")
                .startAt(search_value);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {

                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()));

                //Event
                holder.setIRecyclerItemClickListener(new IRecycleItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        Common.trackingUser = model;
                        startActivity(new Intent(HomeActivity.this,TrackingActivity.class));
                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_user,parent,false);
                return new UserViewHolder(itemView);
            }
        };

        //don't forget this line if we don't want our blan list to lad
        searchAdapter.startListening();

        recycler_friend_list.setAdapter(searchAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        //Here going to app people's activity
        findViewById(R.id.nav_find_people).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AllPeopleActivity.class));
            }
        });

        findViewById(R.id.nav_add_people).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, FriendRequestActivity.class));
            }
        });

        findViewById(R.id.nav_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (v.getId() == R.id.nav_sign_out) {
                        AuthUI.getInstance()
                                .signOut(HomeActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // user is now signed out
                                        startActivity(new Intent(HomeActivity.this, MainActivity.class));
                                        finish();
                                    }
                                });
                    }
                }

        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public PendingIntent getPendingIntent() {
        Intent intent = new Intent(HomeActivity.this, MyLocationReceiver.class);
        intent.setAction(MyLocationReceiver.ACTION);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_MUTABLE);
        else
            return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


    }


    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);

    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    //Sign Out

}