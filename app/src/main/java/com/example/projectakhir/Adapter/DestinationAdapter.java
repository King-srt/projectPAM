package com.example.projectakhir.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectakhir.Model.Destination;
import com.example.projectakhir.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {

    private Context mContext;
    private List<Destination> mDestinationList;
    private OnItemClickListener mListener;
    private FirebaseFirestore mFirestore,destFirestore;
    FirebaseAuth mAuth;

    // Constructor Adapter
    public DestinationAdapter(Context context, List<Destination> destinationList) {
        mContext = context;
        mDestinationList = destinationList;
        mFirestore = FirebaseFirestore.getInstance();
        destFirestore = FirebaseFirestore.getInstance();
    }

    // Interface untuk menangani event klik pada item nantinya
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Method untuk menetapkan listener untuk event klik pada item
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    // Override method onCreateViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_wisata, parent, false);
        return new ViewHolder(view);
    }

    // Override method onBindViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Destination destination = mDestinationList.get(position);
        holder.bind(destination);

        // Mengecek status bookmark dari Firestore dan menetapkannya ke ikon bookmark
        mFirestore.collection("users")
                .document(mAuth.getUid())
                .collection("destinationSaved")
                .document(destination.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                holder.addBookmark.setImageResource(R.drawable.baseline_bookmark_added_24);
                                holder.saveDest.setText("Tersimpan");

                            } else {
                                holder.addBookmark.setImageResource(R.drawable.baseline_bookmark_add_24);
                                holder.saveDest.setText("Simpan");
                            }
                        } else {
                            Toast.makeText(mContext, "Failed to fetch bookmark status", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    // Override method getItemCount
    @Override
    public int getItemCount() {
        return mDestinationList.size();
    }

    // ViewHolder class , ini isi dari item_recycler view nya
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageContact;
        TextView textName,description,saveDest,ratingDestTotal,avgRatingDest;
        ImageView addBookmark; // ImageView untuk bookmark
        RatingBar ratingbar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageContact = itemView.findViewById(R.id.image_contact);
            textName = itemView.findViewById(R.id.text_name);
            description = itemView.findViewById(R.id.description);
            addBookmark = itemView.findViewById(R.id.addBookmark); // Inisialisasi ImageView bookmark
            saveDest = itemView.findViewById(R.id.saveThisDestination);
            avgRatingDest = itemView.findViewById(R.id.ratingAverage);
            ratingbar = itemView.findViewById(R.id.ratingBar);
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            // Set listener untuk event klik pada item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
            // Set listener untuk event klik pada bookmark
            addBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Destination destination = mDestinationList.get(position);
                        toggleBookmark(destination);
                    }
                }
            });

            saveDest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Destination destination = mDestinationList.get(position);
                        toggleBookmark(destination);
                    }
                }
            });
        }

        // Method untuk mengikat data ke ViewHolder
        public void bind(Destination destination) {
            textName.setText(destination.getName());
            description.setText(destination.getLittleDesc());
            Glide.with(mContext).load(destination.getImageUrl()).into(imageContact);
            calculateAndDisplayAvgRating(destination.getId(),avgRatingDest,ratingbar);

        }
    }

    private void toggleBookmark(final Destination destination) {
        if (!destination.isBookmarked()) {
            // Jika belum disimpan, tambahkan ke daftar simpan pengguna
            mFirestore.collection("users")
                    .document(mAuth.getUid()) // Ganti "user_id" dengan ID pengguna yang sesuai
                    .collection("destinationSaved")
                    .document(destination.getId())
                    .set(destination)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            destination.setBookmarked(true); // Perbarui status bookmark pada objek destination menjadi true
                            int position = mDestinationList.indexOf(destination);
                            if (position != -1) {
                                mDestinationList.set(position, destination); // Update status bookmark di mDestinationList
                                notifyItemChanged(position); // Perbarui UI sesuai dengan status bookmark yang baru
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle error
                        }
                    });

        } else {
            // Jika sudah disimpan, hapus dari daftar simpan pengguna
            mFirestore.collection("users")
                    .document(mAuth.getUid()) // Ganti "user_id" dengan ID pengguna yang sesuai
                    .collection("destinationSaved")
                    .document(destination.getId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            destination.setBookmarked(false); // Perbarui status bookmark pada objek destination menjadi false
                            int position = mDestinationList.indexOf(destination);
                            if (position != -1) {
                                mDestinationList.set(position, destination); // Update status bookmark di mDestinationList
                                notifyItemChanged(position);
                                // Perbarui UI sesuai dengan status bookmark yang baru
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle error
                        }
                    });

        }

    }

    public void calculateAndDisplayAvgRating(String destId,TextView ratingss,RatingBar ratingbarDest) {

        CollectionReference wisataRef = FirebaseFirestore.getInstance().collection("ratings");

        DocumentReference docRef = wisataRef.document(destId);
        docRef.collection("ratings").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int totalRatings = 0;
                int userCount = 0;

                for (DocumentSnapshot document : task.getResult()) {
                    if (document.contains("rating")) {
                        long rating = document.getLong("rating");
                        totalRatings += rating;
                        userCount++;
                    }
                }
                if (userCount > 0) {
                    double avgRating = (double) totalRatings / userCount;
                    String formattedAvgRating = String.format("%.1f", avgRating);
                    String resultText = formattedAvgRating;
//                    String resultText = formattedAvgRating + " ( " + userCount + " )";
                    ratingss.setText(resultText);
                    ratingbarDest.setRating((float) avgRating);

                } else {
                    String noRatingsText = "";
                    ratingss.setText(noRatingsText);
                }
            } else {
                ratingss.setText("Error getting ratings");
            }
        });
    }



}
