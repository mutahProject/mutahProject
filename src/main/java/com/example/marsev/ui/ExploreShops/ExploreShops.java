package com.example.marsev.ui.ExploreShops;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marsev.ItemArrayAdapter;
import com.example.marsev.ItemInCartArrayAdapter;
import com.example.marsev.R;
import com.example.marsev.ShopFrontActivity;
import com.example.marsev.ShopFrontForCustomerActivity;
import com.example.marsev.ShopItemsProperty;
import com.example.marsev.ShopsArrayAdapter;
import com.example.marsev.ShopsProperty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ExploreShops extends Fragment {
    private ListView listView;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userID;
    private ArrayList<ShopsProperty> shops;
    private Intent intent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_explore_shops, container, false);
        listView = (ListView) root.findViewById(R.id.shops_list);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getUid();
        shops = new ArrayList<>();
        intent= new Intent(getActivity().getApplicationContext(), ShopFrontForCustomerActivity.class);
        fStore.collection("Shops").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {

                        shops.add(new ShopsProperty("", document.getString("Name"), document.getString("Image"),
                                document.getString("Description"), document.getString("Phone"), document.getString("Owner")));
                    }
                    if (getContext() != null){
                        ArrayAdapter<ShopsProperty> adapter = new ShopsArrayAdapter(getContext(), 0, shops);

                        listView.setAdapter(adapter);
                    }

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




        return root;
    }


}
