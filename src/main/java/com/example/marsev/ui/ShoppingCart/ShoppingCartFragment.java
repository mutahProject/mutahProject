package com.example.marsev.ui.ShoppingCart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.marsev.CheckOut;
import com.example.marsev.ItemArrayAdapter;
import com.example.marsev.ItemInCartArrayAdapter;
import com.example.marsev.R;
import com.example.marsev.ShopItemsInCartProperty;
import com.example.marsev.ShopItemsProperty;
import com.example.marsev.ShopsProperty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShoppingCartFragment extends Fragment {

    ListView listView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ArrayList<ShopItemsInCartProperty> items;
    String userId, itemName;
    TextView totalPrice;
    Button checkOut, clear;
    int total;
    Intent intent;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        final View root = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        listView = (ListView) root.findViewById(R.id.items_inside_cart_list);
        totalPrice = root.findViewById(R.id.total_price);
        checkOut = root.findViewById(R.id.checkout);
        clear = root.findViewById(R.id.clear_cart);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getUid();
        fStore = FirebaseFirestore.getInstance();
        total =0;
        intent = new Intent(getActivity().getApplicationContext(), CheckOut.class);
        if (fAuth.getCurrentUser() != null){
            fStore.collection("Cart").document(userId).collection("Items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    items = new ArrayList<ShopItemsInCartProperty>();
                    if (task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            ShopItemsInCartProperty shopItemsProperty = document.toObject(ShopItemsInCartProperty.class);
                            items.add(new ShopItemsInCartProperty(document.getString("Owner"),document.getString("ItemName")
                                    , document.getString("Image"), document.getString("ItemPrice"), document.getString("Description")));
                            total += Integer.parseInt(document.getString("Quantity")) * Integer.parseInt(document.getString("ItemPrice"));
                        }
                        ArrayAdapter<ShopItemsInCartProperty> adapter = new ItemInCartArrayAdapter(getActivity().getApplicationContext(), 0, items);

                        listView.setAdapter(adapter);
                        totalPrice.setText(String.valueOf(total));
                        intent.putExtra("Total", totalPrice.getText().toString());
                    } else {
                        Log.d("MissionActivity", "Error getting documents: ", task.getException());
                    }

                }
            });

        }else{

        }

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fStore.collection("Cart").document(userId).collection("Items")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            itemName = document.getString("ItemName");
                            fStore.collection("Cart").document(userId).collection("Items")
                                    .document(itemName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Navigation.findNavController(root).navigate(R.id.action_nav_shopping_cart_self);
                                }
                            });
                        }

                    }
                });


            }
        });
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
