package com.example.hydro_sync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private List<CustomMenuItem> menuItems;

    public MenuAdapter(Context context, List<CustomMenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.menuitem, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.card_ic);
        TextView textView = convertView.findViewById(R.id.title);

        CustomMenuItem menuItem = menuItems.get(position);
        imageView.setImageResource(menuItem.getIconResId());
        textView.setText(menuItem.getName());

        return convertView;
    }
}
