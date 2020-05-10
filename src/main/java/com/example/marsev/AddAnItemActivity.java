package com.example.marsev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddAnItemActivity extends AppCompatActivity {
    private EditText name, price, per, stock;
    private ImageView image;
    private Button add, clear;
    private Intent intent, intent2, intent3;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId, time;
    public  static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference mStorageRef;
    private Map<String, Object> item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_an_item);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference("Items Images");
        intent = getIntent();
        intent2 = new Intent(this, ViewYourShopsActivity.class);

        name = findViewById(R.id.insert_item_name);
        price = findViewById(R.id.insert_price);
        per = findViewById(R.id.insert_per);
        stock = findViewById(R.id.insert_stock);
        image = findViewById(R.id.insert_item_image);
        add = findViewById(R.id.insert_subbmit);
        clear = findViewById(R.id.insert_clear);

        getSupportActionBar().setTitle("Add an Item");



        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!isNotEmpty(name.getText().toString())){
                    name.setError("Must enter a name!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!isNotEmpty(name.getText().toString())){
                    name.setError("Must enter a price!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        per.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!isNotEmpty(name.getText().toString())){
                    name.setError("Must enter a quantity!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        stock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!isNotEmpty(name.getText().toString())){
                    name.setError("Must enter stock!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userId = fAuth.getCurrentUser().getUid();
                time = String.valueOf(System.currentTimeMillis());
                item = new HashMap<>();

                item.put("Name", name.getText().toString());
                item.put("Price", price.getText().toString());
                item.put("Description", per.getText().toString());
                item.put("Stock", stock.getText().toString());
                item.put("Image", "Null");
                item.put("Document", time);
                item.put("Store Name", intent.getStringExtra("Shop Name"));
                item.put("Owner", intent.getStringExtra("Owner"));
                uploadFile();
                DocumentReference documentReference = fStore.collection("Shops").document(userId)
                        .collection("User shops").document(intent.getStringExtra("Shop Name"))
                        .collection("Items").document(name.getText().toString());
                documentReference.set(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        fStore.collection("Items").document(time).
                                set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                startActivity(intent2);
                            }
                        });
                    }
                });





            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent2);
            }
        });

    }
    public static boolean isNotEmpty(CharSequence target){
        return (!TextUtils.isEmpty(target));
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

        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadFile(){


        StorageReference fireReference = mStorageRef.child(name.getText().toString()+ "."+ getFileExtension(imageUri));

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

        StorageReference fireReference = mStorageRef.child(name.getText().toString()+ "."+ getFileExtension(imageUri));
        fireReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null){
                    String downloadUri = uri.toString();


                    DocumentReference documentReference = fStore.collection("Shops").document(userId)
                            .collection("User shops").document(intent.getStringExtra("Shop Name"))
                            .collection("Items").document(name.getText().toString());
                    documentReference.update("Image", downloadUri);

                    DocumentReference documentReference1 = fStore.collection("Items").document(time);
                    documentReference1.update("Image", downloadUri);

                }

            }
        });
    }

}
