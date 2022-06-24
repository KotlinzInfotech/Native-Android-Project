package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;

public class fontStyle_Adapter extends BaseAdapter {
	private Context context;
	private final String[] m_Values;

	static class RecordHolder {
		TextView txt_font;
		Typeface typeface;

		RecordHolder() {
		}
	}

	public fontStyle_Adapter(Context context, String[] mobileValues) {
		this.context = context;
		this.m_Values = mobileValues;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		RecordHolder holder;
		View row = convertView;
		if (row == null) {
			row = ((Activity) this.context).getLayoutInflater().inflate(
					R.layout.listitem_fontstyle, parent, false);
			holder = new RecordHolder();
			holder.txt_font = (TextView) row.findViewById(R.id.img_grid_item);
			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}
		holder.typeface = Typeface.createFromAsset(this.context.getAssets(),
				this.m_Values[position]);
		holder.txt_font.setTypeface(holder.typeface);
		return row;
	}

	public int getCount() {
		return this.m_Values.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
}