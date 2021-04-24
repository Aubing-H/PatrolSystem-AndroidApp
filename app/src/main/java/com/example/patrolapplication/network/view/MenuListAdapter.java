package com.example.patrolapplication.network.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.patrolapplication.R;

public class MenuListAdapter extends ArrayAdapter<String> {
    private int menuItemId;

    public MenuListAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        menuItemId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(menuItemId, parent, false);
        TextView tv = view.findViewById(R.id.menuListItemText);
        tv.setText(item);
        return view;
    }
}
