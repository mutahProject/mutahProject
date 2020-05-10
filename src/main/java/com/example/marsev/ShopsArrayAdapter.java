package com.example.marsev;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShopsArrayAdapter extends ArrayAdapter<ShopsProperty> {
    private Context context;
    private List<ShopsProperty> selectedShop;

    //constructor, call on creation
    public ShopsArrayAdapter(Context context, int resource, ArrayList<ShopsProperty> objects) {
        super(context, resource, objects);

        this.context = context;
        this.selectedShop = objects;
    }
    public View getView(int position, View convertView, ViewGroup parent) {


        ShopsProperty shopsProperty = selectedShop.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shops_property, null);

        TextView storeName = (TextView) view.findViewById(R.id.store_name);
        TextView description = (TextView) view.findViewById(R.id.shop_description);
        ImageView image = (ImageView) view.findViewById(R.id.store_logo);
        TextView viewMore = view.findViewById(R.id.view_more);

        storeName.setText(shopsProperty.getName());
        description.setText("\n\nNumber: "+shopsProperty.getPhone() +"\n"+shopsProperty.getDescription() );
        Picasso.with(context).load(Uri.parse(shopsProperty.getImgLogo())).into(image);





        return view;
    }

}
