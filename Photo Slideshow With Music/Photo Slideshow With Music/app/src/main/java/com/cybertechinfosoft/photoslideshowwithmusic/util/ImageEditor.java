package com.cybertechinfosoft.photoslideshowwithmusic.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.cybertechinfosoft.photoslideshowwithmusic.activity.ImageEditorActivity;

public class ImageEditor {
    public static final int IMAGE_EDIT_ACTIVITY_TEXT_AND_STICKER = 1111;
    public Context context;

    public static final class ActivityBuilder {
        private String path;

        public ActivityBuilder(String str) {
            this.path = str;
        }

        public void Start(Activity activity) {
            try {
                activity.startActivityForResult(new Intent(activity, ImageEditorActivity.class).putExtra("IMAGE_SOURCE", this.path), ImageEditor.IMAGE_EDIT_ACTIVITY_TEXT_AND_STICKER);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "Activity Not Found", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private ImageEditor(Context context) {
        this.context = context;
    }

    public static ActivityBuilder startEditing(String str) {
        return new ActivityBuilder(str);
    }
}
