package com.scorpio.foodestimationsystem.authentication;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.databinding.ActivityLoginBinding;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginScreen";

    private ActivityLoginBinding binding;

    private Boolean isLoginLayout = true;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onPostCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initListeners();
    }

    private void initListeners() {
        binding.createNewAccount.setOnClickListener(v ->
                changeLayout()
        );

        binding.login.setOnClickListener(v -> {

            changeLoader(true);

            if (isLoginLayout)
                login();
            else
                register();
        });
    }

    private void changeLayout() {
        isLoginLayout = !isLoginLayout;

        if (isLoginLayout) {
            binding.fullName.setVisibility(View.INVISIBLE);
            binding.createNewAccount.setText("Create new account?");
            binding.login.setText("Sign in");
            binding.mainHeading.setText("Sign in");
        } else {
            binding.fullName.setVisibility(View.VISIBLE);
            binding.createNewAccount.setText("Already have an account?");
            binding.login.setText("Register");
            binding.mainHeading.setText("Register");
        }
    }

    private void login() {
        String email = binding.username.getText().toString();
        String password = binding.password.getText().toString();

        if (email.isEmpty()) {
            binding.username.setError("Enter email here.");
            binding.username.requestFocus();
            changeLoader(false);
            return;
        }

        if (password.isEmpty()) {
            binding.password.setError("Enter password here.");
            binding.password.requestFocus();
            changeLoader(false);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        moveToNextScreen();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        changeLoader(false);
                        Toast.makeText(this, "Authentication failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void register() {
        String fullName = binding.fullName.getText().toString();
        String email = binding.username.getText().toString();
        String password = binding.password.getText().toString();

        if (fullName.isEmpty()) {
            binding.fullName.setError("Enter your name here.");
            binding.fullName.requestFocus();
            changeLoader(false);
            return;
        }

        if (email.isEmpty()) {
            binding.username.setError("Enter email here.");
            binding.username.requestFocus();
            changeLoader(false);
            return;
        }

        if (password.isEmpty()) {
            binding.password.setError("Enter password here.");
            binding.password.requestFocus();
            changeLoader(false);
            return;
        }

        if (password.length() < 6) {
            binding.password.setError("Password must be greater than 6 characters");
            binding.password.requestFocus();
            changeLoader(false);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName).build();

                        if (user != null)
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                            moveToNextScreen();
                                        }
                                    });
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        changeLoader(false);
                        Toast.makeText(LoginActivity.this, "Error Registering User. Please try again." + task.getResult(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void changeLoader(Boolean startLoading) {
        if (startLoading) {
            binding.loading.setVisibility(View.VISIBLE);
            binding.login.setEnabled(false);
            binding.login.setClickable(false);
            binding.createNewAccount.setEnabled(false);
            binding.createNewAccount.setClickable(false);
        } else {
            binding.loading.setVisibility(View.GONE);
            binding.login.setEnabled(true);
            binding.login.setClickable(true);
            binding.createNewAccount.setEnabled(true);
            binding.createNewAccount.setClickable(true);
        }
    }

    private void moveToNextScreen() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            moveToNextScreen();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isLoginLayout) {
            changeLayout();
        } else {
            super.onBackPressed();
        }
    }
}