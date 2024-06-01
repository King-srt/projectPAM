//package com.example.projectakhir;
//
//import static android.content.ContentValues.TAG;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.projectakhir.Activity.ActivityInputReview;
//import com.example.projectakhir.Adapter.CommentAdapter;
//import com.example.projectakhir.Model.Comment;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerViewReviews;
//    private CommentAdapter reviewAdapter;
//    private List<Comment> reviewList;
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore commentsRef;
//    private FirebaseFirestore db;
//    String urlImages, imageNames, imageDescs, imageIds, userNames, userIds, ratings;
//    private String destinationId;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_wisata);
//        db = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//        commentsRef = FirebaseFirestore.getInstance();
//        ImageView imageView = findViewById(R.id.ivBromo);
//        String imageUrl = getIntent().getStringExtra("image_url");
//        Glide.with(this)
//                .load(imageUrl)
//                .placeholder(R.drawable.border) // Gambar placeholder untuk sementara
//                .error(R.drawable.error_image) // Gambar yang akan ditampilkan kalo nanti ada salah
//                .into(imageView);
//        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
//        recyclerViewReviews.setHasFixedSize(true);
//        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
//
//        // Inisialisasi daftar review dengan data dummy
//        reviewList = new ArrayList<>();
//
//        reviewAdapter = new CommentAdapter(getApplicationContext(), reviewList);
//        recyclerViewReviews.setAdapter(reviewAdapter);
//
//        Button btnBerikanReview = findViewById(R.id.btnBerikanReview);
//
//        btnBerikanReview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ActivityInputReview.class);
//                startActivityForResult(intent, 1);
//            }
//        });
//
//        String imageName = getIntent().getStringExtra("image_name");
//        String imageDesc = getIntent().getStringExtra("image_desc");
//        destinationId = getIntent().getStringExtra("destination_id");
//        TextView nameTextView = findViewById(R.id.tvTitle);
//        TextView locationTextView = findViewById(R.id.tvLocation);
//        TextView descriptionTextView = findViewById(R.id.tvDescription);
////        Glide.with(this).load(imageUrl).into(imageView);
//        descriptionTextView.setText("Gunung Bromo di Jawa Timur adalah destinasi wisata populer yang terkenal dengan pemandangan matahari terbit yang menakjubkan, kawah aktif, dan lautan pasir. Bagian dari Taman Nasional Bromo Tengger Semeru, Bromo juga dikenal karena budaya suku Tengger yang unik. Aktivitas favorit di sini termasuk mendaki, berkuda, dan menikmati keindahan alam");
//        nameTextView.setText(imageName);
////        descriptionTextView.setText(imageDesc);
////        addDummyComments();
//        fetchComments();
//        Intent intent = getIntent();
//        urlImages = intent.getStringExtra("image_url");
//        imageNames = intent.getStringExtra("image_name");
//        imageDescs = intent.getStringExtra("image_desc");
//        imageIds = intent.getStringExtra("imageId");
//        userNames = intent.getStringExtra("userName");
//        userIds = intent.getStringExtra("userId");
//        ratings = intent.getStringExtra("rating");
//
//    }
//
//
////    private void addDummyComments() {
////        reviewList.add(new Comment("user_id_1", "Alice", "Great experience!", System.currentTimeMillis() - (24 * 60 * 60 * 1000))); // Yesterday
////        reviewList.add(new Comment("user_id_2", "Bob", "Not bad, but could be better.", System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000))); // Two days ago
////        reviewList.add(new Comment("user_id_3", "Charlie", "Amazing place, had a lot of fun!", System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000))); // Last week
////        reviewList.add(new Comment("user_id_4", "David", "Quite disappointed with the service.", System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000))); // Last month
////    }
//
//
//
//    private void fetchComments() {
//        String destId = getIntent().getStringExtra("imageId");
//        db.collection("komentar").document(destId).collection("Comment")
//                .orderBy("timestamp")
//                .limit(10) // Mengambil maksimal 10 komentar
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            List<Comment> commentList = new ArrayList<>();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Comment comment = document.toObject(Comment.class);
//                                commentList.add(comment);
//                            }
////
//                            CommentAdapter commentAdapter = new CommentAdapter(getApplicationContext(), commentList);
//                            recyclerViewReviews.setAdapter(commentAdapter);
//                            commentAdapter.notifyDataSetChanged();
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                            Toast.makeText(getApplicationContext(), "Failed to fetch comments: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//}