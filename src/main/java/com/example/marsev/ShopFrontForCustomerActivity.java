package com.example.marsev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShopFrontForCustomerActivity extends AppCompatActivity {
    private ListView listView;
    private Intent getIntent;
    FirebaseFirestore fStore;
    ArrayList<ShopItemsProperty> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_front_for_customer);

        getSupportActionBar().setTitle("Shop Items");

        listView = findViewById(R.id.shop_items_for_customers);

        fStore = FirebaseFirestore.getInstance();
        getIntent = getIntent();

        fStore.collection("Shops").document(getIntent.getStringExtra("Owner")).collection("User shops")
                .document(getIntent.getStringExtra("Shop Name")).collection("Items")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                items = new ArrayList<ShopItemsProperty>();
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        items.add(new ShopItemsProperty("dd", document.getString("Image"), document.getString("Price"),
                                document.getString("Description"),document.getString("Name")));
                    }

                    ArrayAdapter<ShopItemsProperty> adapter = new ItemArrayAdapter(ShopFrontForCustomerActivity.this, 0, items);

                    listView.setAdapter(adapter);


                } else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }

            }
        });
    }
}
