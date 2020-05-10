package com.example.marsev.ui.explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marsev.R;
import com.example.marsev.ShopItemsProperty;
import com.example.marsev.ItemArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {
    ListView listView;
    FirebaseFirestore fStore;
    ArrayList<ShopItemsProperty> items;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_explorer, container, false);
        listView = (ListView) root.findViewById(R.id.explorer_list);

        fStore = FirebaseFirestore.getInstance();

        fStore.collection("Items")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                items = new ArrayList<ShopItemsProperty>();
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        ShopItemsProperty shopItemsProperty = document.toObject(ShopItemsProperty.class);
                        items.add(new ShopItemsProperty(document.getString("Owner"), document.getString("Image"), document.getString("Price"),
                                document.getString("Description"),document.getString("Name")));
                    }
                    if (getContext() != null){
                        ArrayAdapter<ShopItemsProperty> adapter = new ItemArrayAdapter(getContext(), 0, items);

                        listView.setAdapter(adapter);
                    }

                } else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }

            }
        });





        return root;
    }


}
