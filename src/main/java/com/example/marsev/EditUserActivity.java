package com.example.marsev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {

    private Button submit;
    private EditText nameText, emailText, phoneText;
    private ImageView usersImage;
    private String name, email, phone, userID;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private Intent intent;
    public  static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        fStore = FirebaseFirestore.getInstance();
        fAuth =FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        intent = new Intent(this, ProfileActivity.class);

        submit = findViewById(R.id.edit_profile_btn);
        nameText = findViewById(R.id.users_name_in_edit);
        emailText = findViewById(R.id.users_email_in_edit);
        phoneText = findViewById(R.id.users_phone_in_edit);
        usersImage = findViewById(R.id.users_image_in_Edit);



        mStorageRef = FirebaseStorage.getInstance().getReference("User images");

        getSupportActionBar().setTitle("Edit user info");

        userID = fAuth.getUid();

        DocumentReference documentReference = fStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                nameText.setText(documentSnapshot.getString("Name"));
                emailText.setText(documentSnapshot.getString("Email"));
                phoneText.setText(documentSnapshot.getString("Phone"));
                if (documentSnapshot.getString("Image") != null){
                    Picasso.with(EditUserActivity.this).load(Uri.parse(documentSnapshot.getString("Image"))).into(usersImage);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameText.getText().toString();
                email = emailText.getText().toString();
                phone = phoneText.getText().toString();
                if (isNotEmpty(name) && isNotEmpty(email) && isValidEmail(email) && isNotEmpty(phone)){
                    user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("emailChang", "success");
                        }
                    });
                    Map<String, Object> user = new HashMap<>();
                    user.put("Email", email);
                    user.put("Name", name);
                    user.put("Phone", phone);
                    fStore.collection("Users").document(userID).update(user);
                    startActivity(intent);
                }else {
                    Toast.makeText(EditUserActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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
