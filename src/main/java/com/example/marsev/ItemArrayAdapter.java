package com.example.marsev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemArrayAdapter extends ArrayAdapter<ShopItemsProperty> {

    private Context context;
    private List<ShopItemsProperty> selectedShopItemProperty;
    private String quantityNumberString;
    private int quantityNumberInt;
    private ImageButton addToCart, addToFav, increaseQuantity, decreaseQuantity;
    ShopItemsProperty shopItemsProperty;
    private  String Iname, Idescription, Iimage, Iprice, Iquantity, userID, owner;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    private Intent intent;



    public ItemArrayAdapter(Context context, int resource, ArrayList<ShopItemsProperty> objects) {
        super(context, resource, objects);

        this.context = context;
        this.selectedShopItemProperty = objects;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        final ShopItemsProperty shopItemsProperty = selectedShopItemProperty.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shop_item_property_layout, null);

        final TextView itemName = (TextView) view.findViewById(R.id.item_name);
        final TextView description = (TextView) view.findViewById(R.id.description);
        final TextView price = (TextView) view.findViewById(R.id.price);
        final ImageView image = (ImageView) view.findViewById(R.id.image);
        final TextView quantity = view.findViewById(R.id.numberoforders);

        addToCart = view.findViewById(R.id.cartbutton);
        addToFav = view.findViewById(R.id.favbutton);
        increaseQuantity = view.findViewById(R.id.increasebutton);
        decreaseQuantity = view.findViewById(R.id.decreasebuttton);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        itemName.setText(shopItemsProperty.getItemName());
        description.setText(shopItemsProperty.getDescription());
        price.setText(shopItemsProperty.getPrice());
        Picasso.with(context).load(shopItemsProperty.getImg()).into(image);




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
            addToFav.setVisibility(View.INVISIBLE);
            addToCart.setClickable(false);
            addToFav.setClickable(false);
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
        addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iname = itemName.getText().toString();
                Idescription = description.getText().toString();
                Iprice = price.getText().toString();
                Iquantity = quantity.getText().toString();
                owner = shopItemsProperty.getHashCode();
                userID = fAuth.getCurrentUser().getUid();


                final DocumentReference documentReference = db.collection("Favorites").document(userID)
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


        return view;
    }

}
