package com.example.projectakhir.Activity;

import static android.content.ContentValues.TAG;

import com.example.projectakhir.Adapter.CommentAdapter;
import com.example.projectakhir.Model.Comment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ActivityDetailWisata extends AppCompatActivity {

    private RecyclerView mRecyclerViewComments;

    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    private ImageView image;
    private RatingBar ratingBar;
    String urlImages, imageNames, imageDescs, imageIds, userNames, userIds, ratings;
    private String destinationId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore commentsRef;
    private FirebaseFirestore db;
    private Button btnReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_wisata);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        btnReview = findViewById(R.id.btnBerikanReview);
        commentsRef = FirebaseFirestore.getInstance();
        mRecyclerViewComments = findViewById(R.id.recyclerViewReviews);
        mRecyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewComments.setHasFixedSize(true); // Disarankan, tetapi tidak kritis
        commentList = new ArrayList<>();
        destinationId = getIntent().getStringExtra("destination_id");
        commentAdapter = new CommentAdapter(this, commentList,destinationId);
        mRecyclerViewComments.setAdapter(commentAdapter);
        ImageView imageView = findViewById(R.id.ivBromo);
        String imageUrl = getIntent().getStringExtra("image_url");
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.border) // Gambar placeholder untuk sementara
                .error(R.drawable.error_image) // Gambar yang akan ditampilkan kalo nanti ada salah
                .into(imageView);
        String imageName = getIntent().getStringExtra("image_name");
        String imageDesc = getIntent().getStringExtra("image_desc");

        TextView nameTextView = findViewById(R.id.tvTitle);
        TextView locationTextView = findViewById(R.id.tvLocation);
        TextView descriptionTextView = findViewById(R.id.tvDescription);
        Glide.with(this).load(imageUrl).into(imageView);
        nameTextView.setText(imageName);
        descriptionTextView.setText(imageDesc);
        fetchComments();
        Intent intent = getIntent();
        urlImages = intent.getStringExtra("image_url");
        imageNames = intent.getStringExtra("image_name");
        imageDescs = intent.getStringExtra("image_desc");
        imageIds = intent.getStringExtra("imageId");
        userNames = intent.getStringExtra("userName");
        userIds = intent.getStringExtra("userId");
        ratings = intent.getStringExtra("rating");

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lntent = new Intent(getApplicationContext(), ActivityInputReview.class);
                lntent.putExtra("image_url", urlImages);
                lntent.putExtra("image_name", imageNames);
                lntent.putExtra("image_desc", imageDescs);
                lntent.putExtra("imageId", imageIds);
                lntent.putExtra("userName", userNames);
                lntent.putExtra("userId", userIds);
                lntent.putExtra("rating", ratings);
                startActivity(lntent);
            }
        });
    }
    private void fetchComments() {
        String destId = getIntent().getStringExtra("imageId");
        db.collection("komentar").document(destId).collection("Comment")
                .orderBy("timestamp")
                .limit(10) // Mengambil maksimal 10 komentar
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Comment> commentList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Comment comment = document.toObject(Comment.class);
                                commentList.add(comment);
                            }
                            CommentAdapter commentAdapter = new CommentAdapter(getApplicationContext(), commentList,destId);
                            mRecyclerViewComments.setAdapter(commentAdapter);
                            commentAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(getApplicationContext(), "Failed to fetch comments: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
