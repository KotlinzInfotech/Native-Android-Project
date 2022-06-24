package com.cybertechinfosoft.photoslideshowwithmusic.util;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.cybertechinfosoft.photoslideshowwithmusic.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class ActivityAnimUtil {
    private static Method clip_revealMethod;

    static {
        clip_revealMethod = null;
        try {
            clip_revealMethod = ActivityOptions.class.getDeclaredMethod(
                    "makeClipRevealAnimation", new Class[]{View.class,
                            Integer.TYPE, Integer.TYPE, Integer.TYPE,
                            Integer.TYPE});
        } catch (Exception e) {
        }
    }

    public static boolean startActivitySafely(View v, Intent intent) {
        boolean success = false;
        try {
            success = startActivity(v, intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(v.getContext(), "APPLICATION NOT FOUND", Toast.LENGTH_SHORT)
                    .show();
        }
        return success;
    }

    private static boolean startActivity(View v, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Context context = v.getContext();
        Bundle optsBundle = null;
        if (v != null) {
            ActivityOptions opts = null;
            try {
                if (clip_revealMethod != null) {
                    int width = v.getMeasuredWidth();
                    int height = v.getMeasuredHeight();
                    try {
                        opts = (ActivityOptions) clip_revealMethod.invoke(
                                null,
                                new Object[]{v, Integer.valueOf(0),
                                        Integer.valueOf(0),
                                        Integer.valueOf(width),
                                        Integer.valueOf(height)});
                    } catch (IllegalAccessException e) {
                        clip_revealMethod = null;
                    } catch (InvocationTargetException e2) {
                        clip_revealMethod = null;
                    }
                }
                if (opts == null) {
                    if (!Utils.isLmpOrAbove()) {
                        opts = ActivityOptions.makeScaleUpAnimation(v, 0, 0,
                                v.getMeasuredWidth(), v.getMeasuredHeight());
                        optsBundle = opts == null ? opts.toBundle() : null;
                    }
                }
                if (opts == null && Utils.isLmpMR1()) {
                    opts = ActivityOptions.makeCustomAnimation(context,
                            R.anim.task_open_enter, R.anim.no_anim);
                }
                if (opts == null) {
                }
            } catch (SecurityException e3) {
                return false;
            }
        }
        context.startActivity(intent, optsBundle);
        return true;
    }
}
