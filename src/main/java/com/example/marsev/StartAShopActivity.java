package com.example.marsev;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.MapView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class StartAShopActivity extends AppCompatActivity {
    private EditText name, phone, description;
    private MapView map;
    private Button submit, cancel;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_a_shop);
        name = findViewById(R.id.shops_name);
        phone = findViewById(R.id.shops_phone);
        description = findViewById(R.id.shops_description);
        map = findViewById(R.id.shops_location_map);
        submit = findViewById(R.id.create_a_shop_btn);
        cancel = findViewById(R.id.cancel_shop_btn);

        intent = new Intent(this, ProfileActivity.class);

        fAuth = FirebaseAuth.getInstance();
        fStore =FirebaseFirestore.getInstance();
        userID = fAuth.getUid();

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!isNotEmpty(name.getText().toString())){
                    name.setError("Must enter a name!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!isNotEmpty(phone.getText().toString())){
                    phone.setError("Must enter a phone number!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!isNotEmpty(description.getText().toString())){
                    description.setError("Must enter a name!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNotEmpty(name.getText().toString()) && isNotEmpty(phone.getText().toString())  && isNotEmpty(description.getText().toString()) ){
                    Map<String, Object> shop = new HashMap<>();
                    final Map<String, Object> defaultRecord = new HashMap<>();
                    shop.put("Description", description.getText().toString());
                    shop.put("Location", "Null");
                    shop.put("Phone", phone.getText().toString());
                    shop.put("Name", name.getText().toString().trim().toLowerCase());
                    shop.put("Owner", userID);
                    shop.put("Image", "Null");
                    DocumentReference documentReference = fStore.collection("Shops").document(userID)
                            .collection("User shops").document(name.getText().toString().trim().toLowerCase());
                    documentReference.set(shop);
                    DocumentReference documentReference1 = fStore.collection("Shops").document(name.getText().toString().trim().toLowerCase());
                    documentReference1.set(shop);
                    final DocumentReference documentReference2 = fStore.collection("Sales").document(userID).collection("Sales").document();
                    documentReference2.addSnapshotListener(StartAShopActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (documentSnapshot.exists()){

                            if (documentSnapshot.getString("Order Number").equals("zzzzzzzzzzzzzzzzzzzzzzzzz")){

                            }else{
                                defaultRecord.put("Item name", "zzzzzzzzzzzzzzzzzzzzzzzzz");
                                defaultRecord.put("Order Number", "zzzzzzzzzzzzzzzzzzzzzzzzz");
                                documentReference2.set(defaultRecord);
                            }}
                        }
                    });

                    startActivity(intent);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

    }
    public static boolean isNotEmpty(CharSequence target){
        return (!TextUtils.isEmpty(target));
    }
}
