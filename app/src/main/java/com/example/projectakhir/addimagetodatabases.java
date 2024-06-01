package com.example.projectakhir;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectakhir.Model.Destination;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.view.inputmethod.InputMethodManager;
public class addimagetodatabases extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;
    private ImageView imageView;
    private EditText editTextName,editCityName,editAddressName;
    private EditText editTextDescription;
    private Button buttonAdd;
    private Uri imageUri;

    private DatabaseReference destReference;
    FirebaseDatabase destDatabase;
    private StorageReference storageRef;
    private FirebaseFirestore firestoreDB;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addimagetodatabases);
        imageView = findViewById(R.id.imageDestination);
        editAddressName = findViewById(R.id.editTextAddress);
        editTextName = findViewById(R.id.editTextDestinationName);
        editCityName = findViewById(R.id.editTextCityName);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonAdd = findViewById(R.id.buttonAddData);
        String firebaseUrl = "https://login-projectpam-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseUrl);
        destReference = firebaseDatabase.getReference("destination");
        storageRef = FirebaseStorage.getInstance().getReference();
        firestoreDB = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBarAdd);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDestination();
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void addDestination() {
        final String name = editTextName.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();
        final String city= editCityName.getText().toString().trim();
        final String address = editAddressName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(name)) {
            editCityName.setError("City is required");
            editCityName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Description is required");
            editTextDescription.requestFocus();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // Cek apakah keyboard sedang ditampilkan
        if (imm != null && getCurrentFocus() != null) {
            // Sembunyikan keyboard
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);}
        progressBar.setVisibility(View.VISIBLE);
        // Upload image to Firebase Storage
        final StorageReference fileRef = storageRef.child("destImages/" + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = fileRef.putFile(imageUri);
        String id = destReference.push().getKey();
        String imageUrl = imageUri.toString();
        Destination destination = new Destination(id, imageUrl, name, description,city,address);
       // firestoreDB.collection("daftarWisata").document(id).set(destination);
        destReference.child(id).setValue(destination);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    // Update destination data with download URL
                    destination.setImageUrl(downloadUri.toString());
                    destReference.child(id).setValue(destination);
                    firestoreDB.collection("destinations")
                                            .document(id)
                                            .set(destination)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Destination data updated successfully
                                    Toast.makeText(addimagetodatabases.this, "Destination added", Toast.LENGTH_SHORT).show();
                                    editTextName.setText("");
                                    editTextDescription.setText("");
                                    editCityName.setText("");
                                    imageView.setImageDrawable(null);
                                    progressBar.setVisibility(View.GONE);
                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(addimagetodatabases.this, "Failed to add destination: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Handle failures
                    Toast.makeText(addimagetodatabases.this, "Failed to upload image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void addDestination() {
//        final String name = editTextName.getText().toString().trim();
//        final String description = editTextDescription.getText().toString().trim();
//
//        if (TextUtils.isEmpty(name)) {
//            editTextName.setError("Name is required");
//            editTextName.requestFocus();
//            return;
//        }
//
//        if (TextUtils.isEmpty(description)) {
//            editTextDescription.setError("Description is required");
//            editTextDescription.requestFocus();
//            return;
//        }
//
//        if (imageUri == null) {
//            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Upload image to Firebase Storage
//        final StorageReference fileRef = storageRef.child("destImages/" + System.currentTimeMillis() + ".jpg");
//        UploadTask uploadTask = fileRef.putFile(imageUri);
//        final String id = destReference.push().getKey();
//        String imageUrl = imageUri.toString();
//        final Destination destination = new Destination(id, imageUrl, name, description);
//
//        // Upload image to Firestore Storage
//        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//
//                // Continue with the task to get the download URL
//                return fileRef.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri downloadUri = task.getResult();
//
//                    // Update destination data with download URL
//                    destination.setImageUrl(downloadUri.toString());
//                    destReference.child(id).setValue(destination)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    // Destination data updated successfully
//                                    Toast.makeText(addimagetodatabases.this, "Destination added", Toast.LENGTH_SHORT).show();
//                                    editTextName.setText("");
//                                    editTextDescription.setText("");
//                                    imageView.setImageDrawable(null);
//
//                                    // Add destination data to Firestore
//                                    firestoreDB.collection("destinations")
//                                            .document(id)
//                                            .set(destination)
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    // Destination data added to Firestore successfully
//                                                    // You can add any additional handling here if needed
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    // Handle Firestore data addition failure
//                                                    Toast.makeText(addimagetodatabases.this, "Failed to add destination to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(addimagetodatabases.this, "Failed to add destination: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                } else {
//                    // Handle failures
//                    Toast.makeText(addimagetodatabases.this, "Failed to upload image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

}
