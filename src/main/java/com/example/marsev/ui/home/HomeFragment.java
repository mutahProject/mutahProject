package com.example.marsev.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.marsev.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    private AppBarConfiguration appBarConfiguration;
    @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView = getView().findViewById(R.id.btm_nav_bar);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_search, R.id.nav_explore, R.id.nav_notifications)
                .build();
        NavController navController = Navigation.findNavController(getActivity(), R.id.home_fragment_host);

        //NavigationUI.setupActionBarWithNavController(getActivity(), navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }

}
