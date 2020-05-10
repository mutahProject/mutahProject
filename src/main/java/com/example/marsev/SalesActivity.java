package com.example.marsev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SalesActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ListView listView;
    private String userId;
    private ArrayList<SalesProperty> sale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        getSupportActionBar().setTitle("Sales");

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getUid();
        fStore = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.receipt_list);
        sale = new ArrayList<>();
        fStore.collection("Sales").whereEqualTo("Owner", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {

                        sale.add(new SalesProperty(document.getString("Buyer name"),document.getString("All Items"),
                                document.getString("Total"), document.getString("Buyer number")));
                    }

                    ArrayAdapter<SalesProperty> adapter = new SalesAdapter(SalesActivity.this, 0, sale);

                    listView.setAdapter(adapter);


                } else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
