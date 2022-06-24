package com.cybertechinfosoft.photoslideshowwithmusic.util;

import android.net.*;
import android.app.*;
import android.provider.*;
import android.database.*;
import java.util.*;
import android.content.*;

public class SongMetadataReader
{
    public Uri GENRES_URI;
    public Activity mActivity;
    public String mAlbum;
    public String mArtist;
    public int mDuration;
    public String mFilename;
    public String mGenre;
    public String mTitle;
    public int mYear;

    public SongMetadataReader(final Activity mActivity, final String mFilename) {
        this.GENRES_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        this.mActivity = null;
        this.mFilename = "";
        this.mTitle = "";
        this.mArtist = "";
        this.mAlbum = "";
        this.mGenre = "";
        this.mYear = -1;
        this.mActivity = mActivity;
        this.mFilename = mFilename;
        this.mTitle = this.getBasename(mFilename);
        try {
            this.ReadMetadata();
        }
        catch (Exception ex) {}
    }

    private void ReadMetadata() {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        final Cursor query = this.mActivity.getContentResolver().query(this.GENRES_URI, new String[] { "_id", "name" }, (String)null, (String[])null, (String)null);
        query.moveToFirst();
        while (!query.isAfterLast()) {
            hashMap.put(query.getString(0), query.getString(1));
            query.moveToNext();
        }
        this.mGenre = "";
        for (final String s : hashMap.keySet()) {
            final ContentResolver contentResolver = this.mActivity.getContentResolver();
            final Uri genreUri = this.makeGenreUri(s);
            final StringBuilder sb = new StringBuilder();
            sb.append("_data LIKE \"");
            sb.append(this.mFilename);
            sb.append("\"");
            if (contentResolver.query(genreUri, new String[] { "_data" }, sb.toString(), (String[])null, (String)null).getCount() != 0) {
                this.mGenre = hashMap.get(s);
                break;
            }
        }
        final Uri contentUriForPath = MediaStore.Audio.Media.getContentUriForPath(this.mFilename);
        final ContentResolver contentResolver2 = this.mActivity.getContentResolver();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("_data LIKE \"");
        sb2.append(this.mFilename);
        sb2.append("\"");
        final Cursor query2 = contentResolver2.query(contentUriForPath, new String[] { "_id", "title", "artist", "album", "year", "duration", "_data" }, sb2.toString(), (String[])null, (String)null);
        if (query2.getCount() == 0) {
            this.mTitle = this.getBasename(this.mFilename);
            this.mArtist = "";
            this.mAlbum = "";
            this.mYear = -1;
            return;
        }
        query2.moveToFirst();
        this.mTitle = this.getStringFromColumn(query2, "title");
        if (this.mTitle == null || this.mTitle.length() == 0) {
            this.mTitle = this.getBasename(this.mFilename);
        }
        this.mArtist = this.getStringFromColumn(query2, "artist");
        this.mAlbum = this.getStringFromColumn(query2, "album");
        this.mYear = this.getIntegerFromColumn(query2, "year");
        this.mDuration = this.getIntegerFromColumn(query2, "duration");
    }

    private String getBasename(final String s) {
        return s.substring(s.lastIndexOf(47) + 1, s.lastIndexOf(46));
    }

    private int getIntegerFromColumn(final Cursor cursor, final String s) {
        final Integer value = cursor.getInt(cursor.getColumnIndexOrThrow(s));
        if (value != null) {
            return value;
        }
        return -1;
    }

    private String getStringFromColumn(final Cursor cursor, final String s) {
        final String string = cursor.getString(cursor.getColumnIndexOrThrow(s));
        if (string != null && string.length() > 0) {
            return string;
        }
        return null;
    }

    private Uri makeGenreUri(final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.GENRES_URI.toString());
        sb.append("/");
        sb.append(s);
        sb.append("/");
        sb.append("members");
        return Uri.parse(sb.toString());
    }
}
