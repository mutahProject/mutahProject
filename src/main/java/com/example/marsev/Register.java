package com.example.marsev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    TextView mName, mEmail, mPassword, mPhone, mRegister;
    String name, email, password, phone;
    Button registerbtm;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");
        mRegister = findViewById(R.id.registered);
        mEmail = findViewById(R.id.regEmail);
        mPassword = findViewById(R.id.regPassword);
        mName = findViewById(R.id.fName);
        mPhone = findViewById(R.id.phone);
        registerbtm = findViewById(R.id.registerbtn);
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        final Intent intent = new Intent(this, LoginActivity.class);
        final Intent intent_main = new Intent(this, MainActivity.class);

        mRegister.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                email = mEmail.getText().toString().trim();
                if (!isNotEmpty(email)){
                    mEmail.setError("Must enter an email!");
                }
            }
        });
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                password = mPassword.getText().toString().trim();
                if(!isNotEmpty(password)){
                    mPassword.setError("Must enter a password!");
                }
            }
        });
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = mName.getText().toString();
                if(!isNotEmpty(name)){
                    mName.setError("Must enter a name!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phone = mPhone.getText().toString();
                if(!isNotEmpty(phone)){
                    mPhone.setError("Must enter a phone number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        registerbtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidEmail(email)){
                    mEmail.setError("Invalid email format!");
                }
                if (!isValidPassword(password)){
                    mPassword.setError("Password must be at least of 8 characters long!");
                }
                if(mPassword.getError() == null && mEmail.getError() == null &&
                        mName.getError() == null && mPhone.getError() == null){
                    fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this, "Successful", Toast.LENGTH_SHORT).show();
                                userID = fAuth.getCurrentUser().getUid();
                                Map<String, Object> user = new HashMap<>();
                                user.put("Email", email);
                                user.put("Name", name);
                                user.put("Phone", phone);
                                DocumentReference documentReference = db.collection("Users").document(userID);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Tag","Successful");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Tag","Failed");
                                    }
                                });
                                startActivity(intent_main);

                            }else{
                                Toast.makeText(Register.this, "F", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });



    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public static boolean isValidPassword(CharSequence target){
        return (!TextUtils.isEmpty(target) && target.length()>=8);
    }
    public static boolean isNotEmpty(CharSequence target){
        return (!TextUtils.isEmpty(target));
    }
}
