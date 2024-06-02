package com.example.projectakhir.Adapter;

import static android.content.ContentValues.TAG;
import static android.content.Intent.getIntent;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectakhir.Model.Comment;
import com.example.projectakhir.Model.Rating;
import com.example.projectakhir.Model.User;
import com.example.projectakhir.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mCommentList;
    private StorageReference storageRef;
    private String destIds,userIds;
    RatingBar ratingBar;

    public CommentAdapter(Context context, List<Comment> commentList, String idd) {
        mContext = context;
        mCommentList = commentList;
        destIds = idd;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = mCommentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;
        TextView messageTextView;
        TextView timeTextView,tvRatingText;
        CircleImageView profileImages;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.tvName);
            messageTextView = itemView.findViewById(R.id.tvQuestion);
            timeTextView = itemView.findViewById(R.id.tvTime);
            profileImages = itemView.findViewById(R.id.profileImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvRatingText = itemView.findViewById(R.id.tvRatingText);

        }

        public void bind(Comment comment) {
            // Bind data komentar ke tampilan ViewHolder
            ratingBar.setRating((float) comment.getRating());
            String floatRating = String.valueOf(comment.getRating());
            tvRatingText.setText(floatRating);

            usernameTextView.setText(comment.getUserName());
            messageTextView.setText(comment.getCommentText());
            String time = getFormattedDate(comment.getTimestamp());
            timeTextView.setText(time);
            storageRef = FirebaseStorage.getInstance().getReference();

            StorageReference profilePicRef = storageRef.child("profile_pics/" + comment.getUserId() + ".jpg");
            profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(mContext)
                        .load(uri)
                        .placeholder(R.drawable.profil_placeholder) // Gambar placeholder
                        .into(profileImages);
            }).addOnFailureListener(exception -> {
                // Jika terjadi kesalahan, Anda bisa menampilkan gambar default atau menangani kesalahan lainnya
                profileImages.setImageResource(R.drawable.profil_placeholder);
            });

        }
    }

    public String getFormattedDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

}
