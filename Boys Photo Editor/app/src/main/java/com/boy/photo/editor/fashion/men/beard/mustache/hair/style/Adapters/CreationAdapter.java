package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CreationAdapter extends BaseAdapter {
    private static LayoutInflater layoutInflater;
    private Activity activity;
    private int imageSize;
    ArrayList<String> imagegallary;
    SparseBooleanArray sparseBooleanArray;
    View view;

    static {
        CreationAdapter.layoutInflater = null;
    }

    public CreationAdapter(final Activity dactivity,
                           final ArrayList<String> imagegallary) {
        this.imagegallary = new ArrayList<String>();
        this.activity = dactivity;
        this.imagegallary = imagegallary;
        CreationAdapter.layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.sparseBooleanArray = new SparseBooleanArray(this.imagegallary.size());
    }


    public int getCount() {
        return this.imagegallary.size();
    }

    public Object getItem(final int n) {
        return n;
    }

    public long getItemId(final int n) {
        return n;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;
        int width = this.activity.getResources().getDisplayMetrics().widthPixels;
        if (row == null) {
            row = LayoutInflater.from(this.activity).inflate(R.layout.gallary_list, parent, false);
            holder = new ViewHolder();
            holder.frm = (FrameLayout) row.findViewById(R.id.frame);
            holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        Glide.with(this.activity).load((String) this.imagegallary.get(position)).into(holder.imgIcon);
        System.gc();
        return row;
    }
    static class ViewHolder {
        public FrameLayout frm;
        ImageView imgIcon;
    }
}

