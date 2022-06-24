package com.cybertechinfosoft.photoslideshowwithmusic.util;

import android.content.Context;

import org.insta.IFAmaroFilter;
import org.insta.IFEarlybirdFilter;
import org.insta.IFHefeFilter;
import org.insta.IFHudsonFilter;
import org.insta.IFLomofiFilter;
import org.insta.IFNashvilleFilter;
import org.insta.IFNormalFilter;
import org.insta.IFRiseFilter;
import org.insta.IFSutroFilter;
import org.insta.IFToasterFilter;
import org.insta.IFWaldenFilter;
import org.insta.InstaFilter;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class FilterHelper extends GPUImageFilter {
    private static final int FILTER_NUM = 12;
    private static InstaFilter[] filters;

    public static void destroyFilters() {
        if (filters != null) {
            for (int i = 0; i < filters.length; i++) {
                try {
                    if (filters[i] != null) {
                        filters[i].destroy();
                        filters[i] = null;
                    }
                } catch (Throwable th) {
                }
            }
        }
    }
    public static InstaFilter getFilter(Context context, int index) {
        if (filters == null) {
            filters = new InstaFilter[FILTER_NUM];
        }
        switch (index) {
            case 0 /*0*/:
                try {
                    filters[index] = new IFNormalFilter(context);
                    break;
                } catch (Throwable th) {
                    break;
                }
            case 1 /*1*/:
                filters[index] = new IFNashvilleFilter(context);
                break;
            case 2/*2*/:
                filters[index] = new IFHefeFilter(context);
                break;
            case 3/*3*/:
                filters[index] = new IFWaldenFilter(context);
                break;
            case 4 /*4*/:
                filters[index] = new IFHudsonFilter(context);
                break;
            case 5 /*5*/:
                filters[index] = new IFNormalFilter(context);
                break;
            case 6/*6*/:
                filters[index] = new IFEarlybirdFilter(context);
                break;
            case 7 /*7*/:
                filters[index] = new IFAmaroFilter(context);
                break;
            case 8/*8*/:
                filters[index] = new IFRiseFilter(context);
                break;
            case 9 /*9*/:
                filters[index] = new IFLomofiFilter(context);
                break;
            case 10 /*10*/:
                filters[index] = new IFSutroFilter(context);
                break;
            case 11 /*11*/:
                filters[index] = new IFToasterFilter(context);
                break;

        }
        return filters[index];
    }
}
