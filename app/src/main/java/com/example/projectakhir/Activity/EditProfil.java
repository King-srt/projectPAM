package com.example.projectakhir.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectakhir.Model.User;
import com.example.projectakhir.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfil extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Inisialisasi FirebaseFirestore di sini
        TextView Back = findViewById(R.id.textViewBack);
        EditText etNama = findViewById(R.id.editTextName);
        EditText etNoHp = findViewById(R.id.etNoHp);
        EditText etEmail = findViewById(R.id.editEmail);
        Button buttonSaveChanges = findViewById(R.id.button2);
        RadioButton rbMale = findViewById(R.id.rbMale);
        RadioButton rbFemale = findViewById(R.id.rbFemale);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Mendapatkan referensi ke dokumen pengguna di Firestore
            DocumentReference userRef = db.collection("users").document(userId);

            // Mendapatkan data pengguna dari Firestore
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);

                            // Set nilai-nilai UI dengan nilai-nilai data yang ada di Firestore
                            etNama.setText(user.getName());
                            etEmail.setText(user.getEmail());
                            etNoHp.setText(user.getPhoneNumber());
                            if (user.getGender().equals("Laki-Laki")) {
                                rbMale.setChecked(true);
                            } else {
                                rbFemale.setChecked(true);
                            }
                        } else {
                            Toast.makeText(EditProfil.this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditProfil.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


            // Tombol simpan perubahan diklik
            buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Perbarui nilai-nilai data pengguna dengan nilai-nilai baru dari UI
                    String newName = etNama.getText().toString().trim();
                    String newEmail = etEmail.getText().toString().trim();
                    String newPhoneNumber = etNoHp.getText().toString().trim();
                    String newGender = rbMale.isChecked() ? "Laki-Laki" : "Perempuan";

                    // Simpan perubahan kembali ke Firestore
                    userRef.update("name", newName);
                    userRef.update("email", newEmail);
                    userRef.update("phoneNumber", newPhoneNumber);
                    userRef.update("gender", newGender)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(EditProfil.this, "Editing Successful", Toast.LENGTH_SHORT).show();
                                        UpdateUI();
                                    } else {
                                        Toast.makeText(EditProfil.this, "Editing Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }


        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUI();
            }
        });
    }

    public void UpdateUI(){
        Intent intent = new Intent(getApplicationContext(), ProfilUser.class);
        startActivity(intent);
        finish();
    }
}
