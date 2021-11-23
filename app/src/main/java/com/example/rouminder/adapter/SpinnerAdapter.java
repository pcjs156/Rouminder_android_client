package com.example.rouminder.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.BaseAdapter;

import androidx.cardview.widget.CardView;

import com.example.rouminder.R;

public class SpinnerAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    Color[] colors;

    public SpinnerAdapter(Context applicationContext, Color[] colors) {
        this.context = applicationContext;
        this.colors = colors;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        if (colors != null) return colors.length;
        return 0;
    }

    @Override
    public Object getItem(int position){
        return colors[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_highlight, parent, false);

        CardView icon = (CardView) convertView.findViewById(R.id.imageViewHighlight);
        icon.setCardBackgroundColor(colors[position].toArgb());

        return convertView;
    }
}
