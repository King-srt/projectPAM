package com.example.projectakhir.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class RV_DaftarWisata extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DestinationAdapter mAdapter;
    private List<Destination> mDestinationList;
    private CollectionReference mDestinationRef;
    private CollectionReference destFirestore;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String name;
    private ProgressBar progressBar;
    private List<Destination> originalOrderList;
    ImageView buttonUser, bookMark;
    TextView tvGreeting;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_daftar_wisata);
//        buttonUser = findViewById(R.id.buttonProfilPic);
        mRecyclerView = findViewById(R.id.recommendation_list); //recycler view inisiasi
        progressBar = findViewById(R.id.progress_bar);
        tvGreeting = findViewById(R.id.greeting_text);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDestinationList = new ArrayList<>();
        originalOrderList = new ArrayList<>();
//        bookMark = findViewById(R.id.book);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mDestinationRef = db.collection("destinations");
        destFirestore = db.collection("destinations");

        progressBar.setVisibility(View.GONE);
//        buttonUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RV_DaftarWisata.this, ProfilUser.class);
//                startActivity(intent);
//            }
//        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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


        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }


        retrieveOriginalOrderData(); // Ambil data dari Firestore

        // Setup TextWatcher untuk EditText pencarian
//        EditText editTextSearch = findViewById(R.id.edit_text_search);
//        editTextSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String searchText = s.toString().toLowerCase().trim();
//                searchDestinations(searchText);
//            }
//        });

        // Mengambil data dari koleksi "destination"
        progressBar.setVisibility(View.VISIBLE);
        mDestinationRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mDestinationList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Destination destination = documentSnapshot.toObject(Destination.class);
                            mDestinationList.add(destination);
                        }
                        progressBar.setVisibility(View.GONE);
                        mAdapter = new DestinationAdapter(RV_DaftarWisata.this, mDestinationList);
                        mRecyclerView.setAdapter(mAdapter);

                        mAdapter.setOnItemClickListener(new DestinationAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Destination clickedDestination = mDestinationList.get(position);
                                Intent intent = new Intent(RV_DaftarWisata.this, ActivityDetailWisata.class);
                                intent.putExtra("image_url", clickedDestination.getImageUrl());
                                intent.putExtra("image_name", clickedDestination.getName());
                                intent.putExtra("image_desc", clickedDestination.getDescription());
                                intent.putExtra("imageId", clickedDestination.getId());
                                intent.putExtra("userName", name);
                                intent.putExtra("userId", mAuth.getCurrentUser().getUid());
                                intent.putExtra("rating", clickedDestination.getRating());
                                intent.putExtra("image_addr",clickedDestination.getAddress());
                                startActivity(intent);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RV_DaftarWisata.this, "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Mendapatkan data pengguna dari Firestore jika tersedia
        if (currentUser != null) {
            String userId = currentUser.getUid();
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

//        bookMark.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RV_DaftarWisata.this, SimpanWisata.class);
//                startActivity(intent);
//            }
//        });
//    }
    }
    // Metode untuk melakukan pencarian
    private void searchDestinations(String searchText) {
        mDestinationList.clear();

        if (searchText.isEmpty()) {
            mDestinationList.addAll(originalOrderList);
        } else {
            for (Destination destination : originalOrderList) {
                if (destination.getName().toLowerCase().contains(searchText) || destination.getDescription().toLowerCase().contains(searchText)) {
                    mDestinationList.add(destination);
                }
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    // Metode untuk mengambil data dari Firestore dan mengisi originalOrderList
    private void retrieveOriginalOrderData() {
        progressBar.setVisibility(View.VISIBLE);
        mDestinationRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        originalOrderList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Destination destination = documentSnapshot.toObject(Destination.class);
                            originalOrderList.add(destination);

                        }
                        // Setel status bookmark berdasarkan data yang diambil dari Firestore

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error if any
                    }
                });
        progressBar.setVisibility(View.GONE);
    }
    private void goToHomeActivity() {
        // Tidak perlu membuat intent karena kita sudah berada di HomeActivity
        // Namun, Anda bisa menambahkan logika tambahan jika diperlukan
    }

    private void goToSearchActivity() {
        startActivity(new Intent(getApplicationContext(), PencarianWisata.class));
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

}
