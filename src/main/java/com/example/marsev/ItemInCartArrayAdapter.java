package com.example.marsev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.marsev.ui.ShoppingCart.ShoppingCartFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemInCartArrayAdapter extends ArrayAdapter<ShopItemsInCartProperty> {
    private Context context;
    private List<ShopItemsInCartProperty> selectedShopItemProperty;
    private String quantityNumberString, userID, iName;
    private int quantityNumberInt;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;





    public ItemInCartArrayAdapter(Context context, int resource, ArrayList<ShopItemsInCartProperty> objects) {
        super(context, resource, objects);

        this.context = context;
        this.selectedShopItemProperty = objects;

    }
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        ShopItemsInCartProperty shopItemsProperty = selectedShopItemProperty.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_in_cart, null);

        final TextView itemName = (TextView) view.findViewById(R.id.item_name);
        final TextView description = (TextView) view.findViewById(R.id.description);
        final TextView price = (TextView) view.findViewById(R.id.price);
        final ImageView image = (ImageView) view.findViewById(R.id.image);
        final TextView quantity = view.findViewById(R.id.numberofordersinsidecart);
        ImageButton removeFromCart = view.findViewById(R.id.removefromcart);
        ImageButton increaseQuantity = view.findViewById(R.id.increaseinsidecartbtn);
        ImageButton decreaseQuantity = view.findViewById(R.id.decreaseinsidecardntn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        itemName.setText(shopItemsProperty.getName());
        description.setText(shopItemsProperty.getDescription());
        price.setText(shopItemsProperty.getPrice());
        Picasso.with(context).load(Uri.parse(shopItemsProperty.getImg())).into(image);


        if (fAuth.getCurrentUser() != null){
            userID = fAuth.getUid();
            iName = itemName.getText().toString();
            DocumentReference documentReference = fStore.collection("Cart").document(userID)
                    .collection("Items").document(iName);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        quantity.setText(document.getString("Quantity"));
                    }
                }
            });
        }


        removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                userID = fAuth.getUid();
                iName = itemName.getText().toString();
                final DocumentReference documentReference = fStore.collection("Cart").document(userID)
                        .collection("Items").document(iName);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()){
                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Navigation.findNavController(view).navigate(R.id.action_nav_shopping_cart_self);
                                }
                            });
                        }
                    }
                });


            }
        });




        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantityNumberString = quantity.getText().toString();
                quantityNumberInt = Integer.parseInt(quantityNumberString) + 1;
                quantityNumberString = Integer.toString(quantityNumberInt);
                quantity.setText(quantityNumberString);
            }
        });
        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                quantityNumberString = quantity.getText().toString();
                quantityNumberInt = Integer.parseInt(quantityNumberString) - 1;
                quantityNumberString = Integer.toString(quantityNumberInt);
                quantity.setText(quantityNumberString);
            }
        });



        return view;
    }
}
