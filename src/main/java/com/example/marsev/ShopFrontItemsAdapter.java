package com.example.marsev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShopFrontItemsAdapter  extends ArrayAdapter<ShopItemsProperty> {
    private Context context;
    private List<ShopItemsProperty> selectedShopItemProperty;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userId, id, name, shopName;
    private ShopItemsProperty shopsItemsProperty;
    private Intent intent;


    public ShopFrontItemsAdapter(Context context, int resource, ArrayList<ShopItemsProperty> objects) {
        super(context, resource, objects);

        this.context = context;
        this.selectedShopItemProperty = objects;

    }
    public View getView(int position, View convertView, ViewGroup parent) {


        shopsItemsProperty = selectedShopItemProperty.get(position);


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.items_in_shop_front_layout, null);

        TextView storeName = (TextView) view.findViewById(R.id.item_in_shop_front_name_adapter);
        TextView description = (TextView) view.findViewById(R.id.item_in_shop_front_description_adapter);
        Button delete = view.findViewById(R.id.item_in_shop_front_delete_btn_adapter);
        ImageView image = view.findViewById(R.id.item_in_shop_front_image_adapter);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getUid();
        intent = new Intent(context, ShopFrontActivity.class);

        name = shopsItemsProperty.getItemName();
        shopName = shopsItemsProperty.getHashCode();


        storeName.setText(shopsItemsProperty.getItemName());
        description.setText(shopsItemsProperty.getDescription() );
        Picasso.with(context).load(Uri.parse(shopsItemsProperty.getImg())).into(image);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference documentReference = fStore.collection("Shops").document(userId)
                        .collection("User shops").document(shopName)
                        .collection("Items").document(name);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot snapshot = task.getResult();
                        id = snapshot.getString("Document");
                        fStore.collection("Shops").document(userId)
                                .collection("User shops").document(shopName)
                                .collection("Items").document(name).delete();
                        fStore.collection("Items").document(id).delete();
                    }
                });

            }
        });






        return view;
    }
}
