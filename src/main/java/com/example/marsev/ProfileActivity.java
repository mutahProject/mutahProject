package com.example.marsev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private Intent intent, intent2, intent3 , intent4, intent5;
    private String userID, buyerName, buyerPhone, listOfItems, order;
    private ImageView usersImage;
    private TextView usersName, userEmail, usersPhone;
    private Button editProfile, deleteAccount, startAShop, logout, back, viewShops, viewSales;
    private AlertDialog.Builder builder;
    public  static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference mStorageRef;
    private int total, number = 0;
    private boolean checker = false;
    private Map<String, Object> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");

        usersImage = findViewById(R.id.users_img_in_profile);
        usersName = findViewById(R.id.display_name_in_profile);
        userEmail = findViewById(R.id.display_email_in_profile);
        usersPhone = findViewById(R.id.display_phone_in_profile);
        editProfile = findViewById(R.id.edit_profile_btn);
        deleteAccount = findViewById(R.id.delete_account_btn);
        startAShop = findViewById(R.id.start_a_shop_btn);
        logout = findViewById(R.id.logout_from_profile_btn);
        back = findViewById(R.id.back_from_profile);
        viewShops = findViewById(R.id.view_shops_btn);
        viewSales = findViewById(R.id.view_sales_in_profile);
        list = new HashMap<>();
        builder = new AlertDialog.Builder(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("User images");
        intent = new Intent(this, MainActivity.class);
        intent2 = new Intent(this, EditUserActivity.class);
        intent3 = new Intent(this, StartAShopActivity.class);
        intent4 = new Intent(this, ViewYourShopsActivity.class);
        intent5 = new Intent(this, SalesActivity.class);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                startActivity(intent);
            }
        });
        userID = fAuth.getUid();
        DocumentReference documentReference = fStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                usersName.setText(documentSnapshot.getString("Name"));
                userEmail.setText(documentSnapshot.getString("Email"));
                usersPhone.setText(documentSnapshot.getString("Phone"));
                //userNameForImage = documentSnapshot.getString("Name");
                if (documentSnapshot.getString("Image") != null){
                    Picasso.with(ProfileActivity.this).load(Uri.parse(documentSnapshot.getString("Image"))).into(usersImage);
                }


            }
        });
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage("Are you want to delete your account?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                fAuth.getCurrentUser().delete();
                                fStore.collection("Users").document(userID).delete();
                                fStore.collection("Cart").document(userID).delete();
                                fAuth.signOut();
                                startActivity(intent);
                                finish();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });
                AlertDialog alert = builder.create();

                alert.setTitle("Confirmation");
                alert.show();
            }
        });
        viewSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = 0;

                fStore.collection("Sales").document(userID).collection("Sales").orderBy("Order Number").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (final QueryDocumentSnapshot document : task.getResult()){

                            if (number == 0){
                                order = document.getString("Order Number");
                                listOfItems = document.getString("Quantity")+": "+ document.getString("Item name");
                                if(document.getString("Price") != null){
                                    total = Integer.parseInt( document.getString("Price")) * Integer.parseInt(document.getString("Quantity"));}
                                buyerName = document.getString("Buyer name");
                                buyerPhone = document.getString("Buyer number");
                                number = 1;
                            }else{
                                if (document.getString("Order Number").equals(order)){
                                    listOfItems += ", "+document.getString("Quantity")+": "+document.getString("Item name");
                                    if(document.getString("Price") != null){
                                        total += Integer.parseInt(document.getString("Price")) * Integer.parseInt(document.getString("Quantity"));}
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
                                    total = 0;
                                    listOfItems = document.getString("Quantity")+": "+document.getString("Item name");
                                    if(document.getString("Price") != null){
                                        total += Integer.parseInt(document.getString("Price")) * Integer.parseInt(document.getString("Quantity"));}
                                    order = document.getString("Order Number");
                                    buyerName = document.getString("Buyer name");
                                    buyerPhone = document.getString("Buyer number");

                                }
                            }

                        }
                        if (checker = true){
                            startActivity(intent5);}

                    }
                });
            }
        });
        startAShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent3);
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent2);
            }
        });

        viewShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent4);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
        usersImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
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
            usersImage.setImageURI(imageUri);
            uploadFile();
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadFile(){

        StorageReference fireReference = mStorageRef.child(userID+ "."+ getFileExtension(imageUri));

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
        StorageReference fireReference = mStorageRef.child(userID+ "."+ getFileExtension(imageUri));
        fireReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null){
                    Uri downloadUri = uri;
                    DocumentReference documentReference = fStore.collection("Users").document(userID);
                    documentReference.update("Image", downloadUri.toString());
                }

            }
        });
    }
}
