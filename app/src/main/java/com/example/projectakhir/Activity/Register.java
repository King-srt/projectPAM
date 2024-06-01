package com.example.projectakhir.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectakhir.Model.User;
import com.example.projectakhir.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity implements View.OnClickListener{

    EditText etName, etEmail, etAddress, etPasswd, etVerPasswd;
    Button btRegister;
    ImageView btBack;
    RadioButton rbMale, rbFemale;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    FirebaseDatabase userDatabase;
    DatabaseReference userReference;
    TextView loginSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loginSec = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);
        etName = findViewById(R.id.editTextName);
        etEmail = findViewById(R.id.editTextEmail);
        rbMale = findViewById(R.id.radioButtonMale);
        rbFemale = findViewById(R.id.radioButtonFemale);
        etAddress = findViewById(R.id.editTextAddress);
        etPasswd = findViewById(R.id.editTextPassword);
        etVerPasswd = findViewById(R.id.editTextPasswordVerify);
        btRegister = findViewById(R.id.buttonRegister);
        String firebaseUrl = "https://login-projectpam-default-rtdb.asia-southeast1.firebasedatabase.app/";
        userDatabase = FirebaseDatabase.getInstance(firebaseUrl);
        userReference = userDatabase.getReference("users");
//        btBack = findViewById(R.id.back);
        // Inisialisasi FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loginSec.setPaintFlags(loginSec.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btRegister.setOnClickListener(this);
//        btBack.setOnClickListener(this);
        loginSec.setOnClickListener(this);

    }

    public void onClick(View v) {
        if (v.getId() == R.id.buttonRegister) {
            String textEmail = etEmail.getText().toString().trim();
            String textPasswd = etPasswd.getText().toString();
            String textName = etName.getText().toString();
            String textAddress = etAddress.getText().toString();
            String textVerPasswd = etVerPasswd.getText().toString();
            String finalGender = rbMale.isChecked() ? "Laki-Laki" : "Perempuan";

            if (textEmail.isEmpty() || textPasswd.isEmpty() || textAddress.isEmpty() || textVerPasswd.isEmpty() || textName.isEmpty()) {
                Toast.makeText(this, "Terdapat Kolom Kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // Query untuk memeriksa apakah email sudah ada
            db.collection("users").document(textEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Email sudah ada dalam database
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Register.this, "Email sudah digunakan", Toast.LENGTH_SHORT).show();
                        } else {
                            // Email belum ada dalam database, lanjutkan proses registrasi
                            // Registrasi pengguna menggunakan Firebase Authentication
                            mAuth.createUserWithEmailAndPassword(textEmail, textPasswd)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Registrasi sukses, tambahkan pengguna ke Firebase Firestore
                                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                if (firebaseUser != null) {
                                                    String userId = firebaseUser.getUid();
                                                    User newUser = new User(textEmail, textName, finalGender, textAddress, textPasswd, textVerPasswd);
                                                    db.collection("users").document(userId).set(newUser)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    Toast.makeText(Register.this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(Register.this, Login.class);
                                                                    intent.putExtra("email",textEmail);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    Toast.makeText(Register.this, "Registrasi Gagal", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(Register.this, "Gagal mendapatkan informasi pengguna", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(Register.this, "Registrasi Gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (v.getId() == loginSec.getId()) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);

        }
    }




}


//    public void onClick(View v) {
//        if (v.getId() == R.id.buttonRegister) {
//            String textEmail = etEmail.getText().toString().trim();
//            String textPasswd = etPasswd.getText().toString();
//            String textName = etName.getText().toString();
//            String textAddress = etAddress.getText().toString();
//            String textVerPasswd = etVerPasswd.getText().toString();
//            String finalGender = rbMale.isChecked() ? "Laki-Laki" : "Perempuan";
//
//            if (textEmail.isEmpty() || textPasswd.isEmpty() || textAddress.isEmpty() || textVerPasswd.isEmpty() || textName.isEmpty()) {
//                Toast.makeText(this, "Terdapat Kolom Kosong", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            progressBar.setVisibility(View.VISIBLE);
//
//            // Query untuk memeriksa apakah email sudah ada
//            userReference.orderByChild("email").equalTo(textEmail).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        // Email sudah ada dalam database
//                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(Register.this, "Email sudah digunakan", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // Email belum ada dalam database, tambahkan pengguna baru
//                        String userId = userReference.push().getKey();
//                        if (userId != null) {
//                            User newUser = new User(textEmail, textName, finalGender, textAddress, textPasswd, textVerPasswd);
//                            userReference.child(userId).setValue(newUser)
//                                    .addOnCompleteListener(task -> {
//                                        if (task.isSuccessful()) {
//                                            progressBar.setVisibility(View.GONE);
//                                            Toast.makeText(Register.this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
//                                            Intent intent = new Intent (Register.this,Login.class);
//                                            startActivity(intent);
//                                            finish();
//                                        } else {
//                                            progressBar.setVisibility(View.GONE);
//                                            Toast.makeText(Register.this, "Registrasi Gagal", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        } else {
//                            progressBar.setVisibility(View.GONE);
//                            Toast.makeText(Register.this, "Gagal membuat ID pengguna", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(Register.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }