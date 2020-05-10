package com.example.marsev.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marsev.ItemInCartArrayAdapter;
import com.example.marsev.NotificationInfoProperty;
import com.example.marsev.NotificationsAdapter;
import com.example.marsev.R;
import com.example.marsev.ShopItemsInCartProperty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {
    private ListView listView;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private ArrayList<NotificationInfoProperty> notifications;
    private String userId;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.notifications_list);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getUid();
        fStore = FirebaseFirestore.getInstance();
        if (fAuth.getCurrentUser() != null){
            fStore.collection("Sales").document(userId).collection("Sales").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    notifications = new ArrayList<NotificationInfoProperty>();
                    if (task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            if(document.getString("Item name").equals("zzzzzzzzzzzzzzzzzzzzzzzzz")){

                            }else {
                                NotificationInfoProperty notificationInfoProperty = document.toObject(NotificationInfoProperty.class);
                                notifications.add(new NotificationInfoProperty(document.getString("Buyer name"),document.getString("Item name")));
                            }
                        }
                        ArrayAdapter<NotificationInfoProperty> adapter = new NotificationsAdapter(getActivity().getApplicationContext(), 0, notifications);

                        listView.setAdapter(adapter);

                    } else {
                        Log.d("MissionActivity", "Error getting documents: ", task.getException());
                    }

                }
            });

        }else{

        }
    }
}
