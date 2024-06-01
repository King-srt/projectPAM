package com.example.projectakhir.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectakhir.Model.Comment;
import com.example.projectakhir.Model.Rating;
import com.example.projectakhir.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ActivityInputReview extends AppCompatActivity {

    private Button sendButton;

    private String destinationId;
    private ImageView image;
    private RatingBar ratingBar;
    private TextView reviewEditText;
    private float dataRating;

    private FirebaseAuth mAuth;
    private FirebaseFirestore commentsRef;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_review);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        commentsRef = FirebaseFirestore.getInstance();
        ratingBar = findViewById(R.id.ratingBar);
        sendButton = findViewById(R.id.btnKirimReview);

        destinationId = getIntent().getStringExtra("destination_id");
        reviewEditText = findViewById(R.id.reviewEditText);
         reviewEditText.requestFocus();
         fetchRatingFromFirestore();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = reviewEditText.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    addComment(commentText);
                    reviewEditText.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    // Jika perubahan rating berasal dari pengguna
                    sendRatingToFirestore(rating);
                    takeRatings(rating);

                }
            }

            private void sendRatingToFirestore(float rating) {
                // Mendapatkan referensi ke koleksi 'wisata' di Firestore
                CollectionReference wisataRef = FirebaseFirestore.getInstance().collection("ratings");

                // Misalkan Anda memiliki destId yang sudah Anda dapatkan sebelumnya
                String destId = getIntent().getStringExtra("imageId");
                String userId = getIntent().getStringExtra("userId");
                String destName = getIntent().getStringExtra("");
                // Mendapatkan referensi ke dokumen dengan destId
                DocumentReference docRef = wisataRef.document(destId);

                // Mengirim rating ke koleksi 'ratings' di dokumen dengan destId
//                Map<String, Object> ratingData = new HashMap<>();
//                ratingData.put("userId", userId);
//                ratingData.put("rating", rating);
                Rating ratingData = new Rating (userId, destId,rating);

                // Menambahkan data rating ke koleksi 'ratings' di dokumen dengan destId
                docRef.collection("ratings").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        // Rating sudah ada, perbarui rating yang ada
                        docRef.collection("ratings").document(userId).update("rating", rating)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Rating berhasil diperbarui");
                                    Toast.makeText(getApplicationContext(), "Rating berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                    updateCommentsRating(destId, userId, rating);
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Gagal memperbarui rating", e);
                                    Toast.makeText(getApplicationContext(), "Gagal memperbarui rating", Toast.LENGTH_SHORT).show();
                                });
                        takeRatings(rating);

                    } else {
                        // Rating belum ada, tambahkan rating baru
                        docRef.collection("ratings").document(userId).set(ratingData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Rating berhasil ditambahkan");
                                    Toast.makeText(getApplicationContext(), "Rating berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Gagal menambahkan rating", e);
                                    Toast.makeText(getApplicationContext(), "Gagal menambahkan rating", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
            }

        });
}
    private void updateCommentsRating(String destId, String userId, double newRating) {
        // Dapatkan referensi ke koleksi komentar di Firestore untuk destinasi yang sesuai
        CollectionReference commentsRef = FirebaseFirestore.getInstance()
                .collection("komentar")
                .document(destId)
                .collection("Comment");

        // Dapatkan komentar pengguna dari koleksi komentar
        commentsRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Update rating pada setiap komentar pengguna
                        DocumentReference commentRef = commentsRef.document(documentSnapshot.getId());
                        commentRef.update("rating", newRating)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Rating komentar berhasil diperbarui");
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Gagal memperbarui rating komentar", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Gagal mendapatkan komentar pengguna", e);
                });
    }
//    private void addComment(String commentText) {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            String userId = currentUser.getUid();
//            String userName = getIntent().getStringExtra("userName");
//            long timestamp = System.currentTimeMillis();
//            String destId = getIntent().getStringExtra("imageId");
//            Comment comment = new Comment(userId, userName, commentText, timestamp,dataRating);
//
//            // Dapatkan referensi ke koleksi komentar di bawah dokumen tujuan
//            db.collection("komentar").document(destId).collection("Comment").add(comment)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Toast.makeText(getApplicationContext(), "Comment added successfully", Toast.LENGTH_SHORT).show();
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getApplicationContext(), "Failed to add comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } else {
//            Toast.makeText(getApplicationContext(), "User not logged in", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void addComment(String commentText) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String userName = getIntent().getStringExtra("userName");
            long timestamp = System.currentTimeMillis();
            String destId = getIntent().getStringExtra("imageId");

            // Dapatkan rating pengguna dari Firestore
            DocumentReference userRatingRef = FirebaseFirestore.getInstance()
                    .collection("ratings")
                    .document(destId)
                    .collection("ratings")
                    .document(userId);

            userRatingRef.get().addOnSuccessListener(documentSnapshot -> {
                double ratingValue = 0; // Default rating pengguna

                if (documentSnapshot.exists()) {
                    Rating userRating = documentSnapshot.toObject(Rating.class);
                    if (userRating != null) {
                        ratingValue = userRating.getRating();
                    }
                } else {
                    // Handle jika rating pengguna tidak ditemukan
                }

                // Buat objek komentar dengan rating yang sesuai
                Comment comment = new Comment(userId, userName, commentText, timestamp, ratingValue);

                // Dapatkan referensi ke koleksi komentar di bawah dokumen tujuan
                db.collection("komentar")
                        .document(destId)
                        .collection("Comment")
                        .add(comment)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getApplicationContext(), "Comment added successfully", Toast.LENGTH_SHORT).show();

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Failed to add comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }).addOnFailureListener(e -> {
                // Handle jika gagal mengambil rating pengguna
            });
        } else {
            Toast.makeText(getApplicationContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchRatingFromFirestore() {
        // Mendapatkan referensi ke koleksi 'wisata' di Firestore
        CollectionReference wisataRef = FirebaseFirestore.getInstance().collection("ratings");

        // Misalkan Anda memiliki destId yang sudah Anda dapatkan sebelumnya
        String destId = getIntent().getStringExtra("imageId");
        String userId = getIntent().getStringExtra("userId");

        // Mendapatkan referensi ke dokumen dengan destId
        DocumentReference docRef = wisataRef.document(destId);

        // Mengambil data rating dari koleksi 'ratings' di dokumen dengan destId
        docRef.collection("ratings").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Jika rating ada, ambil nilai rating
                float rating = documentSnapshot.getDouble("rating").floatValue();

                // Tampilkan nilai rating dalam rating bar
                ratingBar.setRating(rating); // Gantilah yourRatingBar dengan objek RatingBar Anda
            } else {
                // Jika rating tidak ada, atur rating bar menjadi nilai default
                ratingBar.setRating(0); // Gantilah yourRatingBar dengan objek RatingBar Anda
            }
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Gagal mengambil rating", e);
            Toast.makeText(getApplicationContext(), "Gagal mengambil rating", Toast.LENGTH_SHORT).show();
        });
}

    public void takeRatings (float rating){
        dataRating = rating;
    }

    public void UpdateUI(){
        Intent intent = new Intent(getApplicationContext(), ActivityDetailWisata.class);
        startActivity(intent);
        overridePendingTransition(1, 1);
    }
}