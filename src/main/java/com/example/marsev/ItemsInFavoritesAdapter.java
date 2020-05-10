package com.example.marsev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsInFavoritesAdapter extends ArrayAdapter<ShopItemsProperty> {
    private Context context;
    private List<ShopItemsProperty> selectedShopItemProperty;
    private String quantityNumberString;
    private int quantityNumberInt;
    private ImageButton addToCart, removeFromFav, increaseQuantity, decreaseQuantity;
    ShopItemsProperty shopItemsProperty;
    private  String Iname, Idescription, Iimage, Iprice, Iquantity, userID, owner;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    private Intent intent;
    public ItemsInFavoritesAdapter(Context context, int resource, ArrayList<ShopItemsProperty> objects) {
        super(context, resource, objects);

        this.context = context;
        this.selectedShopItemProperty = objects;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        final ShopItemsProperty shopItemsProperty = selectedShopItemProperty.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.items_in_favorites_layout, null);

        final TextView itemName = (TextView) view.findViewById(R.id.item_name_in_fav);
        final TextView description = (TextView) view.findViewById(R.id.description_in_fav);
        final TextView price = (TextView) view.findViewById(R.id.price_in_fav);
        final ImageView image = (ImageView) view.findViewById(R.id.image_in_fav);
        final TextView quantity = view.findViewById(R.id.numberoforders_in_fav);

        addToCart = view.findViewById(R.id.cartbutton_in_fav);
        removeFromFav = view.findViewById(R.id.favbutton_in_fav);
        increaseQuantity = view.findViewById(R.id.increasebutton_in_fav);
        decreaseQuantity = view.findViewById(R.id.decreasebuttton_in_fav);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        itemName.setText(shopItemsProperty.getItemName());
        description.setText(shopItemsProperty.getDescription());
        price.setText(shopItemsProperty.getPrice());
        Iname = itemName.getText().toString();
        Picasso.with(context).load(Uri.parse(shopItemsProperty.getImg())).into(image);




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


        if (fAuth.getCurrentUser() == null){
            increaseQuantity.setVisibility(View.INVISIBLE);
            decreaseQuantity.setVisibility(View.INVISIBLE);
            quantity.setVisibility(View.INVISIBLE);
            addToCart.setVisibility(View.INVISIBLE);
            removeFromFav.setVisibility(View.INVISIBLE);
            addToCart.setClickable(false);
            removeFromFav.setClickable(false);
        }

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iname = itemName.getText().toString();
                Idescription = description.getText().toString();
                Iprice = price.getText().toString();
                Iquantity = quantity.getText().toString();
                owner = shopItemsProperty.getHashCode();
                userID = fAuth.getCurrentUser().getUid();


                final DocumentReference documentReference = db.collection("Cart").document(userID)
                        .collection("Items").document(Iname);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                documentReference.update("Quantity", Iquantity);
                            }else{
                                Map<String, Object> usercart = new HashMap<>();
                                usercart.put("Quantity", Iquantity);
                                usercart.put("ItemName", Iname);
                                usercart.put("ItemPrice", Iprice);
                                usercart.put("Description", Idescription);
                                usercart.put("Owner", owner);
                                usercart.put("Image", shopItemsProperty.getImg());
                                documentReference.set(usercart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Tag","Successful");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Tag","Failed");
                                    }
                                });
                            }
                        }
                    }
                });

            }
        });
        removeFromFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Iname = itemName.getText().toString();
                userID = fAuth.getUid();
                final DocumentReference documentReference = db.collection("Favorites").document(userID)
                        .collection("Items").document(Iname);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()){
                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Navigation.findNavController(view).navigate(R.id.action_nav_favorites_self);
                                }
                            });
                        }
                    }
                });
            }
        });


        return view;
    }
}
