package com.example.marsev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CheckOut extends AppCompatActivity {

    private TextView name, phone, totalPrice;
    private Button submitBtn;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userID, time;
    private String num,  itemName, price, listOfItems;
    static String stock = "1";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        fStore = FirebaseFirestore.getInstance();
        fAuth =FirebaseAuth.getInstance();
        name = findViewById(R.id.name_in_check_out);
        phone = findViewById(R.id.phone_in_check_out);
        totalPrice = findViewById(R.id.total_price_in_check_out);
        submitBtn = findViewById(R.id.submit_order_in_check_out);
        intent = new Intent(this, MainActivity.class);
        listOfItems = "";

        getSupportActionBar().setTitle("Check out");

        num="1";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                price= null;
            } else {
                price= extras.getString("Total");
            }
        } else {
            price= (String) savedInstanceState.getSerializable("Total");
        }
        totalPrice.setText(price);

        if (fAuth.getCurrentUser() != null){
            userID = fAuth.getUid();
            DocumentReference documentReference = fStore.collection("Users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    name.setText(documentSnapshot.getString("Name"));
                    phone.setText(documentSnapshot.getString("Phone"));
                }
            });
        }
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userID = fAuth.getUid();
                time = String.valueOf(System.currentTimeMillis());
                final Map<String, Object> list = new HashMap<>();
                final Map<String, Object> list2 = new HashMap<>();
                fStore.collection("Cart").document(userID).collection("Items")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()) {

                            list.put("Item number "+ num+ " name" , document.getString("ItemName"));
                            num = String.valueOf(Integer.parseInt(num)+1);
                            DocumentReference documentReference = fStore.collection("Sales")
                                    .document(document.getString("Owner")).collection("Sales").document();
                            list2.put("Item name", document.getString("ItemName"));
                            list2.put("Price", document.getString("ItemPrice"));
                            list2.put("Buyer name", name.getText().toString());
                            list2.put("Buyer number", phone.getText().toString());
                            list2.put("Order Number", time);
                            list2.put("Owner", document.getString("Owner"));
                            list2.put("Quantity", document.getString("Quantity"));
                            documentReference.set(list2);

                        }
                        DocumentReference documentReference = fStore.collection("Orders").document(userID)
                                .collection("Order List").document();
                        //documentReference.set(list);
                        list.put("total price", totalPrice.getText().toString());
                        list.put("name", name.getText().toString());
                        list.put("number", phone.getText().toString());
                        documentReference.set(list);
                    }
                });

                fStore.collection("Cart").document(userID).collection("Items")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            itemName = document.getString("ItemName");
                            fStore.collection("Cart").document(userID).collection("Items")
                                    .document(itemName).delete();
                        }

                    }
                });


                startActivity(intent);
                /*DocumentReference document = fStore.collection("Stores").document("Store1")
                        .collection("Products").document();
                document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        stock = documentSnapshot.getString("Stock");
                        itemName = documentSnapshot.getString("name");
                    }
                });
                stock = String.valueOf(Integer.parseInt(stock)-1);
                fStore.collection("Stores").document("Store1")
                        .collection("Products").document(itemName).update("Stock", stock);
                if (Integer.parseInt(stock)<=0){
                    fStore.collection("Stores").document("Store1")
                            .collection("Products").document(itemName).delete();
                }*/


            }
        });


    }
}
