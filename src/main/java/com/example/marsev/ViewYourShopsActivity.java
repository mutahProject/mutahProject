package com.example.marsev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewYourShopsActivity extends AppCompatActivity {
    private ListView listView;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userID;
    private ArrayList<ShopsProperty> shops;
    private Intent intent, intent2;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_your_shops);
        listView = (ListView) findViewById(R.id.your_shops_list);
        button = findViewById(R.id.back_from_view_your_shops);

        getSupportActionBar().setTitle("Your shops");

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getUid();
        shops = new ArrayList<>();

        intent= new Intent(this, ShopFrontActivity.class);
        intent2 = new Intent(this, ProfileActivity.class);

        fStore.collection("Shops").document(userID).collection("User shops").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {

                        shops.add(new ShopsProperty("", document.getString("Name"), document.getString("Image"),
                                document.getString("Description"), document.getString("Phone"), document.getString("Owner")));
                    }

                    ArrayAdapter<ShopsProperty> adapter = new ShopsArrayAdapter(ViewYourShopsActivity.this, 0, shops);

                    listView.setAdapter(adapter);


                } else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShopsProperty shopsProperty = (ShopsProperty) listView.getItemAtPosition(i);
                intent.putExtra("Owner", shopsProperty.getOwner());
                intent.putExtra("Shop Name", shopsProperty.getName());
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent2);
            }
        });
    }
}
