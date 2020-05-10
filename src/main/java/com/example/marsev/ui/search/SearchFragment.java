package com.example.marsev.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.marsev.R;
import com.example.marsev.ShopFrontForCustomerActivity;
import com.example.marsev.ShopsArrayAdapter;
import com.example.marsev.ShopsProperty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private ArrayList<ShopsProperty> shops;
    private Intent intent;
    private ListView listView;
    private EditText search;
    private String searchedItem;
    private ImageButton preformSearch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        final View root = inflater.inflate(R.layout.fragment_search, container, false);

        listView = root.findViewById(R.id.searched_stores_list);
        search = root.findViewById(R.id.searchBar);
        preformSearch = root.findViewById(R.id.preform_search_btn);
        shops = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        intent = new Intent(getActivity().getApplicationContext(), ShopFrontForCustomerActivity.class);




        preformSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchedItem = search.getText().toString().trim().toLowerCase();
                if (isNotEmpty(searchedItem)) {
                    fStore.collection("Shops").whereEqualTo("Name", searchedItem)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {


                                    shops.add(new ShopsProperty("", document.getString("Name"), document.getString("Image"),
                                            document.getString("Description"), document.getString("Phone"), document.getString("Owner")));
                                }

                                if (getContext() != null) {
                                    ArrayAdapter<ShopsProperty> adapter = new ShopsArrayAdapter(getContext(), 0, shops);

                                    listView.setAdapter(adapter);
                                }

                            } else {
                                Log.d("MissionActivity", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                    //Navigation.findNavController(root).navigate(R.id.action_nav_search_self);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShopsProperty shopsProperty = (ShopsProperty) listView.getItemAtPosition(i);
                intent.putExtra("Owner", shopsProperty.getOwner());
                intent.putExtra("Shop Name", shopsProperty.getName());
                startActivity(intent);
            }
        });



        return root;
    }
    public static boolean isNotEmpty(CharSequence target){
        return (!TextUtils.isEmpty(target));
    }
}
