package com.example.marsev.ui.favorite;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marsev.ItemArrayAdapter;
import com.example.marsev.ItemInCartArrayAdapter;
import com.example.marsev.ItemsInFavoritesAdapter;
import com.example.marsev.R;
import com.example.marsev.ShopItemsProperty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private ListView listView;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private ArrayList<ShopItemsProperty> items;
    private String userId;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);



        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(R.id.favorites_list);
        fAuth = FirebaseAuth.getInstance();
        fStore =FirebaseFirestore.getInstance();
        userId = fAuth.getUid();

        fStore.collection("Favorites").document(userId).collection("Items")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                items = new ArrayList<ShopItemsProperty>();
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        ShopItemsProperty shopItemsProperty = document.toObject(ShopItemsProperty.class);
                        items.add(new ShopItemsProperty(document.getString("Owner"), document.getString("Image"), document.getString("ItemPrice"),
                                document.getString("Description"),document.getString("ItemName")));
                    }
                    if (getContext() != null){
                        ArrayAdapter<ShopItemsProperty> adapter = new ItemsInFavoritesAdapter(getContext(), 0, items);

                        listView.setAdapter(adapter);
                    }

                } else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }

            }
        });


    }
}
