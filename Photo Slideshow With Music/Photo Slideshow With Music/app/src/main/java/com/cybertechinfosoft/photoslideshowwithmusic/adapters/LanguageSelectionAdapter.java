package com.cybertechinfosoft.photoslideshowwithmusic.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.LauncherActivity;
import com.cybertechinfosoft.photoslideshowwithmusic.util.EPreferences;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Language;
import com.cybertechinfosoft.photoslideshowwithmusic.util.LanguageHelper;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import java.util.ArrayList;

public class LanguageSelectionAdapter extends RecyclerView.Adapter<LanguageSelectionAdapter.MyViewHolder> {
    private Activity activity;
    SparseBooleanArray booleanArray;
    Language lang;
    private ArrayList<Language> langAndLocales;
    int mSelectedChoice;
    int pos;

    public LanguageSelectionAdapter(final Activity activity, final ArrayList<Language> langAndLocales) {
        this.mSelectedChoice = 0;
        this.booleanArray = new SparseBooleanArray();
        this.activity = activity;
        this.langAndLocales = langAndLocales;
    }

    public int getItemCount() {
        return this.langAndLocales.size();
    }

    public void onBindViewHolder(final MyViewHolder myViewHolder, final int n) {
        final Language language = this.langAndLocales.get(n);
        Utils.setFont(this.activity, (TextView) myViewHolder.languageName);
        final AppCompatRadioButton languageName = myViewHolder.languageName;
        final StringBuilder sb = new StringBuilder();
        sb.append(language.getLanguageName());
        sb.append(" ( ");
        sb.append(language.getLanguageNameInDefaultLocale());
        sb.append(" ) ");
        languageName.setText((CharSequence) sb.toString());
        final AppCompatRadioButton languageName2 = myViewHolder.languageName;
        final EPreferences instance = EPreferences.getInstance((Context) this.activity);
        boolean checked = false;
        if (instance.getInt("lang_position", 0) == n) {
            checked = true;
        }
        languageName2.setChecked(checked);
    }

    public MyViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_language_list, viewGroup, false));
    }

    public void playMusic(final int mSelectedChoice) {
        if (this.mSelectedChoice != mSelectedChoice) {
            this.lang = this.langAndLocales.get(mSelectedChoice);
        }
        this.mSelectedChoice = mSelectedChoice;
    }

    public void relaunch(final Activity activity) {
        final Intent intent = new Intent((Context) activity, (Class) LauncherActivity.class);
        intent.addFlags(32768);
        intent.addFlags(268435456);
        activity.startActivity(intent);
        Runtime.getRuntime().exit(0);
        activity.finish();
    }

    public void switchLanguage(final Activity activity, final String s) {
        LanguageHelper.setLanguage((Context) activity, s);
        this.relaunch(activity);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AppCompatRadioButton languageName;

        public MyViewHolder(final View view) {
            super(view);
            (this.languageName = (AppCompatRadioButton) view.findViewById(R.id.languageName)).setOnClickListener((View.OnClickListener) new View.OnClickListener() {
                public void onClick(final View view) {
                    final Language language = LanguageSelectionAdapter.this.langAndLocales.get(MyViewHolder.this.getAdapterPosition());
                    final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder((Context) LanguageSelectionAdapter.this.activity, R.style.Theme_MovieMaker_AlertDialog);
                    alertDialog$Builder.setTitle(R.string.delete_video_);
                    final StringBuilder sb = new StringBuilder();
                    sb.append(LanguageSelectionAdapter.this.activity.getResources().getString(R.string.are_you_sure_to_change_));
                    sb.append(" ?");
                    alertDialog$Builder.setMessage((CharSequence) sb.toString());
                    alertDialog$Builder.setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialogInterface, final int n) {
                            EPreferences.getInstance((Context) LanguageSelectionAdapter.this.activity).putInt("lang_position", MyViewHolder.this.getAdapterPosition());
                            LanguageSelectionAdapter.this.switchLanguage(LanguageSelectionAdapter.this.activity, language.getLanguageCode());
                        }
                    });
                    alertDialog$Builder.setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialogInterface, final int n) {
                            LanguageSelectionAdapter.this.notifyDataSetChanged();
                        }
                    });
                    alertDialog$Builder.show();
                }
            });
        }
    }
}
