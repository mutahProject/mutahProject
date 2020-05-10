package com.example.marsev;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.MyViewHolder> {

    List<ShopItemsProperty> shopItemsProperties;


    public ItemRecyclerAdapter(ArrayList<ShopItemsProperty> shopItemsProperties) {

        this.shopItemsProperties = shopItemsProperties;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shop_item_property_layout, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ShopItemsProperty shopItemsProperty = shopItemsProperties.get(position);
    }

    @Override
    public int getItemCount() {
        return shopItemsProperties.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private String quantityNumberString;
        private int quantityNumberInt;
        private ImageButton addToCart, addToFav, increaseQuantity, decreaseQuantity;
        ShopItemsProperty shopItemsProperty;
        private  String Iname, Idescription, Iimage, Iprice, Iquantity;
        FirebaseAuth fAuth;

        public MyViewHolder(@NonNull View view) {
            super(view);
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


            itemName.setText(shopItemsProperty.getItemName());
            description.setText(shopItemsProperty.getDescription());
            price.setText(shopItemsProperty.getPrice());


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
            Iname = itemName.toString();
            Idescription = description.toString();
            Iprice = price.toString();
            Iquantity = quantity.toString();

            if (fAuth.getCurrentUser() == null){
                addToCart.setClickable(false);
                addToFav.setClickable(false);
            }
            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}

