package com.example.projectakhir.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectakhir.Model.User;
import com.example.projectakhir.R;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 90001;
    private GoogleSignInClient mGoogleSignInClient;

    TextView register;
    EditText email,password;
    Button btnLogin;
    ImageView autologin,facebookLogin;
    FirebaseDatabase userDatabase;
    FirebaseAuth mAuth;
    DatabaseReference userReference;
    ProgressBar progressBar;
    FirebaseFirestore db;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progressBar);
        register = findViewById(R.id.registerLink);
        register.setPaintFlags(register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btnLogin = findViewById(R.id.btnLogin);
        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        btnLogin.setOnClickListener(this);
        register.setOnClickListener(this);
        autologin = findViewById(R.id.imageView4);
        callbackManager = CallbackManager.Factory.create();
        db = FirebaseFirestore.getInstance();
        String firebaseUrl = "https://login-projectpam-default-rtdb.asia-southeast1.firebasedatabase.app/";
        userDatabase = FirebaseDatabase.getInstance(firebaseUrl);
        userReference = userDatabase.getReference("users");
        mAuth = FirebaseAuth.getInstance();




//        if (isNotNull()){
//            Intent mainIntent = new Intent(Login.this, RV_DaftarWisata.class);
//            startActivity(mainIntent);
//            finish();
//        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        autologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
//         Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        Intent intent  = getIntent();
        String emails = intent.getStringExtra("email");
        email.setText(emails);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(Login.this, "Success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                saveUserToDatabase(user); // Save user data to database
                            }
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == register.getId()) {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        } else
            if (v.getId() == btnLogin.getId()) {
            final String emailS = email.getText().toString();
            final String passwordS = password.getText().toString();
            if (emailS.isEmpty() || passwordS.isEmpty()) {
                Toast.makeText(this, "Email atau password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                // Authenticate using FirebaseAuth
                mAuth.signInWithEmailAndPassword(emailS, passwordS)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        Intent intent = new Intent(Login.this, RV_DaftarWisata.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private boolean isNotNull() {
        FirebaseUser userLogin = mAuth.getCurrentUser();
        if (userLogin!=null){
            return true;
        }
        return false;
    }
    private void signIn() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Sign out success, now request user to choose Google account for sign in
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    // Sign out failed
                    Toast.makeText(Login.this, "Sign out failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(Login.this, RV_DaftarWisata.class);
            startActivity(intent);
            finish();
        }
    }

    private void saveUserToDatabase(FirebaseUser user) {
        String uid = user.getUid();
        String email = user.getEmail();

        // Create a user object or a map to store user information
        User newUser = new User(email, "", "", "", "", "");
        db.collection("users").document(uid).set(newUser)

                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User data saved to database");
                        } else {
                            Log.w(TAG, "Failed to save user data", task.getException());
                        }
                    }
                });
    }
}