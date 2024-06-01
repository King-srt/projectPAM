//package com.example.projectakhir;
//
//import static android.content.ContentValues.TAG;
//
//import com.example.projectakhir.Adapter.CommentAdapter;
//import com.example.projectakhir.Model.Comment;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.speech.RecognizerIntent;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RatingBar;
//import android.widget.TextView;
//import android.view.WindowManager;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//public class RV_detailWisata extends AppCompatActivity {
//
//    private EditText commentEditText;
//    private Button sendButton;
//    private static final int SPEECH_REQUEST_CODE = 123;
//
//    private RecyclerView mRecyclerViewComments;
//
//    private CommentAdapter commentAdapter;
//    private List<Comment> commentList;
//    private String destinationId;
//    private ImageView image;
//    private RatingBar ratingBar;
//
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore commentsRef;
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_rv_detail_wisata);
//        db = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//        commentsRef = FirebaseFirestore.getInstance();
//        ratingBar = findViewById(R.id.ratingBar);
//        commentEditText = findViewById(R.id.edit_text_comment);
//        sendButton = findViewById(R.id.button_send_comment);
//        mRecyclerViewComments = findViewById(R.id.recycler_comments);
//        commentEditText.requestFocus();
//        mRecyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
//        commentList = new ArrayList<>();
//        commentAdapter = new CommentAdapter(this, commentList,);
//        mRecyclerViewComments.setAdapter(commentAdapter);
//        ImageView imageView = findViewById(R.id.image_detail);
//        String imageUrl = getIntent().getStringExtra("image_url");
//        Glide.with(this)
//                .load(imageUrl)
//                .placeholder(R.drawable.border) // Gambar placeholder untuk sementara
//                .error(R.drawable.error_image) // Gambar yang akan ditampilkan kalo nanti ada salah
//                .into(imageView);
//        String imageName = getIntent().getStringExtra("image_name");
//        String imageDesc = getIntent().getStringExtra("image_desc");
//        destinationId = getIntent().getStringExtra("destination_id");
//        TextView nameTextView = findViewById(R.id.text_name_detail);
//        TextView descriptionTextView = findViewById(R.id.description_detail);
//        Glide.with(this).load(imageUrl).into(imageView);
//        nameTextView.setText(imageName);
//        descriptionTextView.setText(imageDesc);
//        fetchComments();
//        fetchRatingFromFirestore();
//        commentEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startSpeechToText();
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//            }
//        });
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String commentText = commentEditText.getText().toString().trim();
//                if (!commentText.isEmpty()) {
//                    addComment(commentText);
//                    commentEditText.setText("");
//                } else {
//                    Toast.makeText(RV_detailWisata.this, "Please enter a comment", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                if (fromUser) {
//                    // Jika perubahan rating berasal dari pengguna
//                    sendRatingToFirestore(rating);
//                }
//            }
//
//            private void sendRatingToFirestore(float rating) {
//                // Mendapatkan referensi ke koleksi 'wisata' di Firestore
//                CollectionReference wisataRef = FirebaseFirestore.getInstance().collection("ratings");
//
//                // Misalkan Anda memiliki destId yang sudah Anda dapatkan sebelumnya
//                String destId = getIntent().getStringExtra("imageId");
//                String userId = getIntent().getStringExtra("userId");
//                String destName = getIntent().getStringExtra("");
//
//                // Mendapatkan referensi ke dokumen dengan destId
//                DocumentReference docRef = wisataRef.document(destId);
//
//                // Mengirim rating ke koleksi 'ratings' di dokumen dengan destId
//                Map<String, Object> ratingData = new HashMap<>();
//                ratingData.put("userId", userId);
//                ratingData.put("rating", rating);
//
//                // Menambahkan data rating ke koleksi 'ratings' di dokumen dengan destId
//                docRef.collection("ratings").document(userId).get().addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//
//                        // Rating sudah ada, perbarui rating yang ada
//                        docRef.collection("ratings").document(userId).update("rating", rating)
//                                .addOnSuccessListener(aVoid -> {
//                                    Log.d(TAG, "Rating berhasil diperbarui");
//                                    Toast.makeText(getApplicationContext(), "Rating berhasil diperbarui", Toast.LENGTH_SHORT).show();
//                                })
//                                .addOnFailureListener(e -> {
//                                    Log.w(TAG, "Gagal memperbarui rating", e);
//                                    Toast.makeText(getApplicationContext(), "Gagal memperbarui rating", Toast.LENGTH_SHORT).show();
//                                });
//
//                    } else {
//                        // Rating belum ada, tambahkan rating baru
//                        docRef.collection("ratings").document(userId).set(ratingData)
//                                .addOnSuccessListener(aVoid -> {
//                                    Log.d(TAG, "Rating berhasil ditambahkan");
//                                    Toast.makeText(getApplicationContext(), "Rating berhasil ditambahkan", Toast.LENGTH_SHORT).show();
//                                })
//                                .addOnFailureListener(e -> {
//                                    Log.w(TAG, "Gagal menambahkan rating", e);
//                                    Toast.makeText(getApplicationContext(), "Gagal menambahkan rating", Toast.LENGTH_SHORT).show();
//                                });
//                    }
//                });
//            }
//
//        });
//    }
//    private void addComment(String commentText) {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            String userId = currentUser.getUid();
//            String userName = getIntent().getStringExtra("userName");
//            long timestamp = System.currentTimeMillis();
//            String destId = getIntent().getStringExtra("imageId");
//            Comment comment = new Comment(userId, userName, commentText, timestamp);
//
//            // Dapatkan referensi ke koleksi komentar di bawah dokumen tujuan
//            db.collection("komentar").document(destId).collection("Comment").add(comment)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Toast.makeText(RV_detailWisata.this, "Comment added successfully", Toast.LENGTH_SHORT).show();
//                            fetchComments();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(RV_detailWisata.this, "Failed to add comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } else {
//            Toast.makeText(RV_detailWisata.this, "User not logged in", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
//            // Ambil hasil suara dalam bentuk ArrayList
//            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//
//            // Ambil teks hasil pengenalan suara pertama dan tampilkan di EditText
//            if (result != null && !result.isEmpty()) {
//                String recognizedText = result.get(0);
//                commentEditText.setText(recognizedText);
//            }
//        } else {
//            // Tampilkan pesan kesalahan jika pengenalan suara gagal
//            Toast.makeText(this, "Failed to recognize speech", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    private void fetchComments() {
//        String destId = getIntent().getStringExtra("imageId");
//        db.collection("komentar").document(destId).collection("Comment").orderBy("timestamp").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            List<Comment> commentList = new ArrayList<>();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Comment comment = document.toObject(Comment.class);
//                                commentList.add(comment);
//                            }
//                            CommentAdapter commentAdapter = new CommentAdapter(RV_detailWisata.this, commentList);
//                            mRecyclerViewComments.setAdapter(commentAdapter);
//                            commentAdapter.notifyDataSetChanged();
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                            Toast.makeText(RV_detailWisata.this, "Failed to fetch comments: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//    }
//    private void calculateAverageRating(String destId) {
//        // Mendapatkan referensi ke koleksi 'ratings' di Firestore
//        CollectionReference ratingsRef = FirebaseFirestore.getInstance().collection("ratings").document(destId).collection("ratings");
//
//        // Mengambil semua rating dari koleksi 'ratings' untuk destinasi tertentu
//        ratingsRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                int totalRatings = 0;
//                float averageRating = 0;
//
//                // Iterasi melalui semua rating
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    // Ambil nilai rating dari setiap dokumen
//                    float rating = document.getDouble("rating").floatValue();
//
//                    // Tambahkan rating ke totalRatings
//                    totalRatings++;
//
//                    // Tambahkan rating ke averageRating
//                    averageRating += rating;
//                }
//
//                // Hitung rata-rata
//                if (totalRatings > 0) {
//                    averageRating /= totalRatings;
//
//                    // Simpan atau gunakan averageRating sesuai kebutuhan Anda
//                    Log.d(TAG, "Average rating: " + averageRating);
//                } else {
//                    // Tidak ada rating yang ditemukan
//                    Log.d(TAG, "Tidak ada rating yang ditemukan untuk destinasi ini");
//                }
//            } else {
//                Log.d(TAG, "Gagal mengambil rating: ", task.getException());
//            }
//        });
//    }
//
//    private void startSpeechToText() {
//        // Buat intent untuk memicu layanan pengenalan suara
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//
//        // Mulai pengenalan suara dengan startActivityForResult
//        startActivityForResult(intent, SPEECH_REQUEST_CODE);
//    }
//
//    private void fetchRatingFromFirestore() {
//        // Mendapatkan referensi ke koleksi 'wisata' di Firestore
//        CollectionReference wisataRef = FirebaseFirestore.getInstance().collection("ratings");
//
//        // Misalkan Anda memiliki destId yang sudah Anda dapatkan sebelumnya
//        String destId = getIntent().getStringExtra("imageId");
//        String userId = getIntent().getStringExtra("userId");
//
//        // Mendapatkan referensi ke dokumen dengan destId
//        DocumentReference docRef = wisataRef.document(destId);
//
//        // Mengambil data rating dari koleksi 'ratings' di dokumen dengan destId
//        docRef.collection("ratings").document(userId).get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                // Jika rating ada, ambil nilai rating
//                float rating = documentSnapshot.getDouble("rating").floatValue();
//
//                // Tampilkan nilai rating dalam rating bar
//                ratingBar.setRating(rating); // Gantilah yourRatingBar dengan objek RatingBar Anda
//            } else {
//                // Jika rating tidak ada, atur rating bar menjadi nilai default
//                ratingBar.setRating(0); // Gantilah yourRatingBar dengan objek RatingBar Anda
//            }
//        }).addOnFailureListener(e -> {
//            Log.w(TAG, "Gagal mengambil rating", e);
//            Toast.makeText(getApplicationContext(), "Gagal mengambil rating", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//
//}
