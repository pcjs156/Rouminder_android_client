package com.example.rouminder;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpinnerAdapter extends BaseAdapter {
  Context context;
  int flags[];
  LayoutInflater inflater;

  public SpinnerAdapter(Context applicationContext, int[] flags) {
      this.context = applicationContext;
      this.flags = flags;
      inflater = (LayoutInflater.from(applicationContext));
  }

  @Override
    public int getCount() {
      return flags.length;
  }

  @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      view = inflater.inflate(R.layout.item_highlight, null);
        ImageView icon = (ImageView)  view.findViewById(R.id.imageViewHighlight);
        icon.setImageResource(flags[i]);
        return view;
    }

}
