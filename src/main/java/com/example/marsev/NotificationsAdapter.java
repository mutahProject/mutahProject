package com.example.marsev;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends ArrayAdapter<NotificationInfoProperty> {
    private Context context;
    private List<NotificationInfoProperty> selectedNotification;


    public NotificationsAdapter(Context context, int resource, ArrayList<NotificationInfoProperty> objects) {
        super(context, resource, objects);

        this.context = context;
        this.selectedNotification = objects;

    }
    public View getView(int position, View convertView, ViewGroup parent) {


        NotificationInfoProperty notificationInfoProperty = selectedNotification.get(position);


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.notification_layout, null);

        TextView textView = (TextView) view.findViewById(R.id.notification_in_notifications);



        textView.setText(notificationInfoProperty.getName() + " bought items: "+ notificationInfoProperty.getDescription());





        return view;
    }
}
