package com.example.marsev;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.content.Context;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class SalesAdapter extends ArrayAdapter<SalesProperty> {
    private Context context;
    private List<SalesProperty> selectedSale;
    private TextView nameAndNumber, items, total;

    //constructor, call on creation
    public SalesAdapter(Context context, int resource, ArrayList<SalesProperty> objects) {
        super(context, resource, objects);

        this.context = context;
        this.selectedSale = objects;
    }
    public View getView(int position, View convertView, ViewGroup parent) {


        SalesProperty salesProperty = selectedSale.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.receipts_layout, null);

        nameAndNumber = view.findViewById(R.id.name_in_receipt);
        items = view.findViewById(R.id.receipts);
        total = view.findViewById(R.id.total_in_receipt);

        nameAndNumber.setText(salesProperty.getsaleName() +"  "+salesProperty.getsaleNumber());
        items.setText(salesProperty.getsaleItems());
        total.setText("Total: "+salesProperty.getsaleTotal());







        return view;
    }
}
