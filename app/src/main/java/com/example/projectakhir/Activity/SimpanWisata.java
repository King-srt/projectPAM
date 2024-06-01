package com.example.projectakhir.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectakhir.Adapter.DestinationAdapter;
import com.example.projectakhir.Model.Destination;
import com.example.projectakhir.Model.User;
import com.example.projectakhir.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SimpanWisata extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DestinationAdapter mAdapter;
    private List<Destination> mDestinationList;
    private CollectionReference mDestinationRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String name;
    private TextView tvGreeting,tvKosong;
    private ImageView imgKosong;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simpan_wisata2);
//        mRecyclerView = findViewById(R.id.recycler_viewSimpan);
//        progressBar = findViewById(R.id.progressBarSimpan);
        mRecyclerView = findViewById(R.id.recommendation_list); //recycler view inisiasi
        progressBar = findViewById(R.id.progress_bar);
        tvKosong = findViewById(R.id.tvKosong);
        imgKosong = findViewById(R.id.imgKosong);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDestinationList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDestinationRef = db.collection("users").document(user.getUid()).collection("destinationSaved");
        tvGreeting = findViewById(R.id.greeting_text);
        progressBar.setVisibility(View.GONE);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_save);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    goToHomeActivity();
                } else if (item.getItemId() == R.id.navigation_search) {
                    goToSearchActivity();
                } else if (item.getItemId() == R.id.navigation_save) {
                    goToSaveActivity();
                } else if (item.getItemId() == R.id.navigation_profile) {
                    goToProfileActivity();
                }

                return false; // Kembalikan false jika tidak ada item yang sesuai
            }
        });
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                name = user.getName();
                                tvGreeting.setText("Hi "+name);
//                                Toast.makeText(getApplicationContext(), "Data Ditemukan", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        loadData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        showNull();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showNull();
    }

    public void loadData(){
        mDestinationRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mDestinationList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Destination destination = documentSnapshot.toObject(Destination.class);
                            mDestinationList.add(destination);
                            showNull();
                        }
                        mAdapter = new DestinationAdapter(SimpanWisata.this, mDestinationList);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                        mAdapter.setOnItemClickListener(new DestinationAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Destination clickedDestination = mDestinationList.get(position);
                                Intent intent = new Intent(SimpanWisata.this, ActivityDetailWisata.class);
                                intent.putExtra("image_url", clickedDestination.getImageUrl());
                                intent.putExtra("image_name", clickedDestination.getName());
                                intent.putExtra("image_desc", clickedDestination.getDescription());
                                intent.putExtra("imageId", clickedDestination.getId());
                                intent.putExtra("userName", name);
                                intent.putExtra("userId", mAuth.getCurrentUser().getUid());
                                intent.putExtra("rating", clickedDestination.getRating());
                                startActivity(intent);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SimpanWisata.this, "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


    private void goToHomeActivity() {
        startActivity(new Intent(getApplicationContext(), RV_DaftarWisata.class));
        overrideActivityTransition(0,0);
    }

    private void goToSearchActivity() {
        startActivity(new Intent(getApplicationContext(), PencarianWisata.class));
        overrideActivityTransition(0,0);
    }

    private void goToSaveActivity() {
//        startActivity(new Intent(getApplicationContext(), SimpanWisata.class));
//        overrideActivityTransition(0,0);
    }

    private void overrideActivityTransition(int enterAnim, int exitAnim) {
        overridePendingTransition(enterAnim, exitAnim);
    }

    private void goToProfileActivity() {
        startActivity(new Intent(getApplicationContext(), ProfilUser.class));
        overrideActivityTransition(0,0);
    }

    public void showNull(){
        if (mDestinationList.isEmpty()){
            tvKosong.setVisibility(View.VISIBLE);
            imgKosong.setVisibility(View.VISIBLE);
        } else {
            tvKosong.setVisibility(View.GONE);
            imgKosong.setVisibility(View.GONE);
        }
    }





}