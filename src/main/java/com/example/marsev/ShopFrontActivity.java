package com.example.marsev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopFrontActivity extends AppCompatActivity {
    private Intent intent, getIntent, intent2, intent3;
    private ArrayList<ShopItemsProperty> items;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID, shopName, listOfItems, order, buyerName, buyerPhone;
    private int number, total;
    public  static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference mStorageRef;
    private ImageView image;
    private Button backButton, viewSalesButton;
    private Map<String, Object> list;
    private boolean checker = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_front);
        image = findViewById(R.id.shop_image_in_shop_front);
        final TextView description = findViewById(R.id.shop_description_in_shop_front);
        final ListView itemsList = findViewById(R.id.items_in_shop_front);
        backButton =findViewById(R.id.back_in_shop_fron);
        viewSalesButton = findViewById(R.id.view_sales_in_shop_front);
        viewSalesButton.setVisibility(View.INVISIBLE);
        intent = new Intent(this, AddAnItemActivity.class);
        intent2 = new Intent(this, ProfileActivity.class);
        intent3 = new Intent(this, SalesActivity.class);
        getIntent = getIntent();
        list = new HashMap<>();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("Shop Images");
        userID = fAuth.getUid();
        total=0;
        shopName = getIntent.getStringExtra("Shop Name");
        getSupportActionBar().setTitle(shopName);
        if(shopName != null){
        fStore.collection("Shops").document(userID).collection("User shops")
                .document(shopName).collection("Items").get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                items = new ArrayList<ShopItemsProperty>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        items.add(new ShopItemsProperty(document.getString("Store Name"), document.getString("Image"), document.getString("Price"),
                                document.getString("Description"),document.getString("Name")));

                    }
                }
                    ArrayAdapter<ShopItemsProperty> adapter = new ShopFrontItemsAdapter(ShopFrontActivity.this, 0, items);

                    itemsList.setAdapter(adapter);
            }
        });}
        if (getIntent.getStringExtra("Shop Name") != null){
            DocumentReference documentReference = fStore.collection("Shops").document(userID)
                    .collection("User shops").document(getIntent.getStringExtra("Shop Name").trim().toLowerCase());
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    description.setText(documentSnapshot.getString("Description"));
                    if (documentSnapshot.getString("Image") != null){
                        Picasso.with(ShopFrontActivity.this).load(Uri.parse(documentSnapshot.getString("Image"))).into(image);
                    }


                }
            });
        }


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent2);
            }
        });

        /*viewSalesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = 0;
                fStore.collection("Sales").document(userID).collection("Sales").orderBy("Order Number").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()){

                            if (number == 0){
                                order = document.getString("Order Number");
                                listOfItems = document.getString("Quantity")+": "+ document.getString("Item name");
                                if(document.getString("Price") != null){
                                    total = Integer.parseInt( document.getString("Price"));}
                                buyerName = document.getString("Buyer name");
                                buyerPhone = document.getString("Buyer number");
                                number = 1;
                            }else{
                                if (document.getString("Order Number").equals(order)){
                                    listOfItems = listOfItems +", "+document.getString("Quantity")+": "+document.getString("Item name");
                                    if(document.getString("Price") != null){
                                        total += Integer.parseInt(document.getString("Price"));}
                                }else {
                                    DocumentReference documentReference = fStore.collection("Sales").document(order + " Order");
                                    list.put("All Items", listOfItems);
                                    list.put("Total", String.valueOf(total));
                                    list.put("Buyer name", buyerName);
                                    list.put("Buyer number", buyerPhone);
                                    list.put("Owner", userID);
                                    documentReference.set(list).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            checker = true;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            checker = false;
                                        }
                                    });
                                    number = 0;
                                }
                            }

                        }
                        if (checker = true){
                        startActivity(intent3);}

                    }
                });
            }
        });*/
        // fStore.collection("Orders").document(userID)
        //                                .collection("Order List")

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                intent.putExtra("Shop Name",getIntent.getStringExtra("Shop Name"));
                intent.putExtra("Owner", getIntent.getStringExtra("Owner"));
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.avtivity_shop_front_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void openFileChooser(){
        Intent intent5 = new Intent();
        intent5.setType("image/*");
        intent5.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent5, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            image.setImageURI(imageUri);
            uploadFile();
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadFile(){


        StorageReference fireReference = mStorageRef.child(getIntent.getStringExtra("Shop Name")+ "."+ getFileExtension(imageUri));

        fireReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                setToDataStore();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }
    private void setToDataStore(){

        StorageReference fireReference = mStorageRef.child(getIntent.getStringExtra("Shop Name")+ "."+ getFileExtension(imageUri));
        fireReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null){
                    String downloadUri = uri.toString();

                    DocumentReference documentReference = fStore.collection("Shops").document(userID)
                            .collection("User shops").document(getIntent.getStringExtra("Shop Name").trim().toLowerCase());
                    documentReference.update("Image", downloadUri);
                    DocumentReference documentReference1 = fStore.collection("Shops").document(getIntent.getStringExtra("Shop Name").trim().toLowerCase());
                    documentReference1.update("Image", downloadUri);


                }

            }
        });
    }
}
