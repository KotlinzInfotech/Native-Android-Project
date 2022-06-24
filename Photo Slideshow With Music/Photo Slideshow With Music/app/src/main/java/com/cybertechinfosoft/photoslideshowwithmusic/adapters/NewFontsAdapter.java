package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.cybertechinfosoft.photoslideshowwithmusic.OnStickerSelected;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ImageEditorActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.NewTitleActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.util.StickersAndFontsUtils;

public class NewFontsAdapter extends RecyclerView.Adapter<NewFontsAdapter.Holder> {
    private Context mContext;
    private OnStickerSelected onStickerSelected;
    String packageName = "com.example.textnsticker";

    public class Holder extends RecyclerView.ViewHolder {
        private TextView textView;

        public Holder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.tvFonts);
        }
    }

    public OnStickerSelected getOnStickerSelected() {
        return this.onStickerSelected;
    }

    public void setOnStickerSelected(OnStickerSelected onStickerSelected) {
        this.onStickerSelected = onStickerSelected;
    }

    public NewFontsAdapter(Context context) {
        this.mContext = context;
    }

    public int getItemCount() {
        return StickersAndFontsUtils.fonts.length;
    }

    public void onBindViewHolder(final Holder holder, final int n) {
        final AssetManager assets = this.mContext.getAssets();
        final TextView access$000 = holder.textView;
        final StringBuilder sb = new StringBuilder();
        sb.append("fonts/");
        sb.append(StickersAndFontsUtils.fonts[n]);
        access$000.setTypeface(Typeface.createFromAsset(assets, sb.toString()));
        holder.itemView.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                final AssetManager assets = NewFontsAdapter.this.mContext.getAssets();
                final StringBuilder sb = new StringBuilder();
                sb.append("fonts/");
                sb.append(StickersAndFontsUtils.fonts[n]);
                final Typeface fromAsset = Typeface.createFromAsset(assets, sb.toString());
                if (NewFontsAdapter.this.mContext instanceof NewTitleActivity) {
                    NewFontsAdapter.this.onStickerSelected.onTextStyleChange(fromAsset);
                    return;
                }
                if (NewFontsAdapter.this.mContext instanceof ImageEditorActivity) {
                    ((ImageEditorActivity) NewFontsAdapter.this.mContext).chenageFontStyle(fromAsset);
                }
            }
        });
    }

    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(this.mContext).inflate(R.layout.layout_font_style_raw, viewGroup, false));
    }
}
