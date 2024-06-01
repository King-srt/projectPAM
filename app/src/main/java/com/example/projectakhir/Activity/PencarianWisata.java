package com.example.projectakhir.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.projectakhir.Activity.ActivityDetailWisata;
import com.example.projectakhir.Activity.ProfilUser;
import com.example.projectakhir.Activity.RV_DaftarWisata;
import com.example.projectakhir.Activity.SimpanWisata;
import com.example.projectakhir.Adapter.DestinationAdapter;
import com.example.projectakhir.Model.Destination;
import com.example.projectakhir.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PencarianWisata extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private DestinationAdapter mAdapter;
    private List<Destination> mDestinationList;
    private CollectionReference mDestinationRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView btnSearch;
    private EditText etSearch;
    private ProgressBar progressBar;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pencarian_wisata);
        mRecyclerView = findViewById(R.id.recycler_view);
        btnSearch = findViewById(R.id.button_search);
        etSearch = findViewById(R.id.edit_text_search);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDestinationList = new ArrayList<>();
        progressBar = findViewById(R.id.progress_bar);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
        } else {
            name = "Guest"; // Atur default jika pengguna tidak masuk
        }
        mDestinationRef = db.collection("destinations");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_search);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    goToHomeActivity();
                    return true;
                } else if (item.getItemId() == R.id.navigation_search) {
                    return true;
                } else if (item.getItemId() == R.id.navigation_save) {
                    goToSaveActivity();
                    return true;
                } else if (item.getItemId() == R.id.navigation_profile) {
                    goToProfileActivity();
                    return true;
                }

                return false;
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        mDestinationRef.orderBy("city").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mDestinationList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Destination destination = documentSnapshot.toObject(Destination.class);
                            mDestinationList.add(destination);
                        }

                        mAdapter = new DestinationAdapter(getApplicationContext(), mDestinationList);
                        mRecyclerView.setAdapter(mAdapter);

                        mAdapter.setOnItemClickListener(new DestinationAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Destination clickedDestination = mDestinationList.get(position);
                                Intent intent = new Intent(getApplicationContext(), ActivityDetailWisata.class);
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
                        Toast.makeText(getApplicationContext(), "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToHomeActivity() {
        startActivity(new Intent(getApplicationContext(), RV_DaftarWisata.class));
        overrideActivityTransition(0,0);
    }

    private void goToSaveActivity() {
        startActivity(new Intent(getApplicationContext(), SimpanWisata.class));
        overrideActivityTransition(0,0);
    }

    private void overrideActivityTransition(int enterAnim, int exitAnim) {
        overridePendingTransition(enterAnim, exitAnim);
    }

    private void goToProfileActivity() {
        startActivity(new Intent(getApplicationContext(), ProfilUser.class));
        overrideActivityTransition(0,0);
    }


        private void performSearch() {
        progressBar.setVisibility(View.VISIBLE);
            String queryText = etSearch.getText().toString();
            mDestinationRef.whereGreaterThanOrEqualTo("name", queryText)
                    .whereLessThanOrEqualTo("name", queryText + '\uf8ff')

                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            mDestinationList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Destination destination = documentSnapshot.toObject(Destination.class);
                                mDestinationList.add(destination);
                            }

                            mAdapter = new DestinationAdapter(getApplicationContext(), mDestinationList);
                            mRecyclerView.setAdapter(mAdapter);
                            progressBar.setVisibility(View.GONE);

                            mAdapter.setOnItemClickListener(new DestinationAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Destination clickedDestination = mDestinationList.get(position);
                                    Intent intent = new Intent(getApplicationContext(), ActivityDetailWisata.class);
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
                            Toast.makeText(getApplicationContext(), "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

