package com.example.projectakhir.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.bumptech.glide.Glide;
import com.example.projectakhir.Model.User;
import com.example.projectakhir.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private CircleImageView profilPic;
    private String ImageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_user);
        ImageView addPicture = findViewById(R.id.addPicture);
        Button btEditProfile = findViewById(R.id.bteditProfile);
        Button btLogout = findViewById(R.id.btlogout);
        TextView userName = findViewById(R.id.userName);
        profilPic = findViewById(R.id.profilPic);
        TextView tvGender = findViewById(R.id.tvGender);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPhoneNumber = findViewById(R.id.tvPhoneNumber);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    goToHomeActivity();
                } else
                if (item.getItemId() == R.id.navigation_search) {
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


        profilPic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v);
                return true;
            }
        });

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userId = currentUser.getUid();
            StorageReference profilePicRef = storageRef.child("profile_pics/" + userId + ".jpg");

            profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    ImageUrls = uri.toString();
                    Glide.with(ProfilUser.this)
                            .load(uri)
                            .into(profilPic);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle error
                    Toast.makeText(ProfilUser.this, "Failed to load profile picture", Toast.LENGTH_SHORT).show();
                }
            });
        }

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference docRef = db.collection("users").document(userId);

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        String username = user.getName();
                        String fullName = user.getName();
                        String gender = user.getGender();
                        String email = user.getEmail();
                        String phoneNumber = user.getPhoneNumber();
                        String country = user.getCountry();

                        userName.setText(username);
                        tvName.setText(fullName);
                        tvGender.setText(gender);
                        tvEmail.setText(email);
                        tvPhoneNumber.setText(phoneNumber);
                    } else {
                        Toast.makeText(ProfilUser.this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfilUser.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ProfilUser.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        btEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilUser.this, EditProfil.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                StorageReference profilePicRef = storageRef.child("profile_pics/" + userId + ".jpg");
                profilePicRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(ProfilUser.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        profilPic.setImageURI(imageUri);
                                        db.collection("users").document(userId).update("imageUrl", imageUrl)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(ProfilUser.this, "Image URL updated in Firestore", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ProfilUser.this, "Failed to update image URL in Firestore", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfilUser.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void downloadImage(String url) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading image...");
        request.setTitle("Image Download");

        // Allow the file to be scanned by MediaScanner
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Uri.parse(url).getLastPathSegment());

        // Get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
    private void goToHomeActivity() {
        startActivity(new Intent(getApplicationContext(), RV_DaftarWisata.class));
        overrideActivityTransition(0,0);
        
    }

    private void goToSearchActivity() {
        startActivity(new Intent(getApplicationContext(), PencarianWisata.class));
        overrideActivityTransition(0,0);
    }

    private void goToProfileActivity() {
//        Intent intent = new Intent(P.this, ProfilUser.class);
//        startActivity(intent);
    }
    private void goToSaveActivity() {
        startActivity(new Intent(getApplicationContext(), SimpanWisata.class));
        overrideActivityTransition(0,0);
    }

    private void overrideActivityTransition(int enterAnim, int exitAnim) {
        overridePendingTransition(enterAnim, exitAnim);
    }
    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.download_image ) {
                        downloadImage(ImageUrls);
                        return true;
                }
                return false;
            }

        });
        popupMenu.show();
    }

}
