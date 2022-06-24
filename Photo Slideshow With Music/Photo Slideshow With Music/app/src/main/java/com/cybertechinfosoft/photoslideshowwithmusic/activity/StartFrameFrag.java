package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ak.android.widget.colorpickerseekbar.ColorPickerSeekBar;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.OnStickerSelected;
import com.cybertechinfosoft.photoslideshowwithmusic.OnTextStickerListeners;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.NewFontsAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.NewStickerAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.VideoTitleFrameAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.listeners.MultiTouchListener;
import com.cybertechinfosoft.photoslideshowwithmusic.onSaveStoryTitle;
import com.cybertechinfosoft.photoslideshowwithmusic.util.StickersAndFontsUtils;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class StartFrameFrag extends Fragment implements View.OnClickListener, onSaveStoryTitle, OnTextStickerListeners, OnStickerSelected {
    private static final int GALLERY_REQUEST_CODE = 11;
    public static View bottomSheet;
    public static boolean isSavingDone;
    public static String lastsaveTempPath;
    private static onSaveStoryTitle onSaveStoryTitle;
    private static RelativeLayout rlMain;
    public static RelativeLayout rlMainStartFragment;
    TextView.OnEditorActionListener actionListener;
    AlertDialog alertDialog;
    private MyApplication application;
    private ColorPickerSeekBar csbChooseColor;
    EditText edtCaption;
    private Bitmap finalBitmap;
    private FrameLayout flSticker;
    ImageView focusedImageView;
    TextView focusedTextView;
    private NewFontsAdapter fontsAdapter;
    private ArrayList<Integer> frames;
    private ImageView ivRemoveView;
    private ImageView ivSelectedImage;
    private ImageView ivbtnNavigation;
    private MultiTouchListener listener;
    private LinearLayout llMainInflatId;
    private RecyclerView rvEditItems;
    private RecyclerView rvFonts;
    private RecyclerView rvStickers;
    private NewStickerAdapter stickerAdapter;
    String textString;
    private VideoTitleFrameAdapter titleFrameAdapter;
    private View view;

    public StartFrameFrag() {
        this.textString = null;
        this.actionListener = (TextView.OnEditorActionListener) new TextView.OnEditorActionListener() {
            public boolean onEditorAction(final TextView textView, final int n, final KeyEvent keyEvent) {
                if (n == 6) {
                    if (textView.getText().toString() != null) {
                        StartFrameFrag.this.closeInput((View) textView);
                        StartFrameFrag.this.addText(textView.getText().toString());
                        StartFrameFrag.this.edtCaption.setVisibility(View.GONE);
                        StartFrameFrag.this.edtCaption.setText((CharSequence) "");
                    } else {
                        StartFrameFrag.this.closeInput((View) textView);
                        Toast.makeText((Context) StartFrameFrag.this.getActivity(), R.string.please_add_some_text, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        };
    }

    private void addListener() {
        this.edtCaption.setOnEditorActionListener(this.actionListener);
        StartFrameFrag.rlMain.setOnClickListener((View.OnClickListener) this);
        csbChooseColor.setOnColorSeekbarChangeListener(new ColorPickerSeekBar.OnColorSeekBarChangeListener() {
            @Override
            public void onColorChanged(SeekBar seekBar, int color, boolean fromUser) {
                if (StartFrameFrag.this.focusedTextView != null) {
                    StartFrameFrag.this.focusedTextView.setTextColor(color);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        this.ivbtnNavigation.setOnClickListener((View.OnClickListener) this);
    }

    private void addPhotoSticker(final Bitmap imageBitmap) {
        final FrameLayout.LayoutParams frameLayout$LayoutParams = new FrameLayout.LayoutParams(200, 200);
        frameLayout$LayoutParams.gravity = 17;
        final ImageView focusedImageView = new ImageView(this.view.getContext());
        focusedImageView.setImageBitmap(imageBitmap);
        focusedImageView.setOnTouchListener((View.OnTouchListener) this.listener);
        this.focusedImageView = focusedImageView;
        this.focusedTextView = null;
        this.rvEditItems.setVisibility(View.VISIBLE);
        NewTitleActivity.setStartFrameState(R.id.imgEditGallery);
        this.rvFonts.setVisibility(View.INVISIBLE);
        this.csbChooseColor.setVisibility(View.INVISIBLE);
        this.flSticker.addView((View) focusedImageView, (ViewGroup.LayoutParams) frameLayout$LayoutParams);
        NewTitleActivity.isStickerAdded = true;
    }

    private void addStickerOnImage(final int imageResource) {
        final FrameLayout.LayoutParams frameLayout$LayoutParams = new FrameLayout.LayoutParams(200, 200);
        frameLayout$LayoutParams.gravity = 17;
        final ImageView focusedImageView = new ImageView(this.view.getContext());
        focusedImageView.setImageResource(imageResource);
        focusedImageView.setOnTouchListener((View.OnTouchListener) this.listener);
        this.focusedImageView = focusedImageView;
        this.focusedTextView = null;
        this.rvEditItems.setVisibility(View.VISIBLE);
        NewTitleActivity.setStartFrameState(R.id.imgEditGallery);
        this.rvFonts.setVisibility(View.INVISIBLE);
        this.csbChooseColor.setVisibility(View.INVISIBLE);
        this.flSticker.addView((View) focusedImageView, (ViewGroup.LayoutParams) frameLayout$LayoutParams);
        NewTitleActivity.isStickerAdded = true;
    }

    private void addText(final String text) {
        final FrameLayout.LayoutParams frameLayout$LayoutParams = new FrameLayout.LayoutParams(-2, -2);
        frameLayout$LayoutParams.gravity = 17;
        final TextView focusedTextView = new TextView((Context) this.getActivity());
        focusedTextView.setText((CharSequence) text);
        focusedTextView.setTextColor(-16776961);
        focusedTextView.setTextSize(40.0f);
        focusedTextView.setOnTouchListener((View.OnTouchListener) this.listener);
        focusedTextView.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                StartFrameFrag.this.focusedTextView = (TextView) view;
                StartFrameFrag.this.csbChooseColor.setVisibility(View.VISIBLE);
                StartFrameFrag.this.rvFonts.setVisibility(View.VISIBLE);
                StartFrameFrag.this.rvEditItems.setVisibility(View.INVISIBLE);
            }
        });
        this.focusedTextView = focusedTextView;
        this.focusedImageView = null;
        this.rvEditItems.setVisibility(View.INVISIBLE);
        this.flSticker.addView((View) focusedTextView, (ViewGroup.LayoutParams) frameLayout$LayoutParams);
        NewTitleActivity.isTextAdded = true;
    }

    private void bindView(View view) {
        this.llMainInflatId = (LinearLayout) view.findViewById(R.id.llMainInflatId);
        inflatLayout();
        this.ivRemoveView = (ImageView) view.findViewById(R.id.ivRemoveView);
        this.ivSelectedImage = (ImageView) view.findViewById(R.id.ivSelectedImage);
        rlMain = (RelativeLayout) view.findViewById(R.id.rlMain);
        this.csbChooseColor = (ColorPickerSeekBar) view.findViewById(R.id.csbChooseColor);
        this.rvEditItems = (RecyclerView) view.findViewById(R.id.rvEditItems);
        this.flSticker = (FrameLayout) view.findViewById(R.id.flSticker);
        this.edtCaption = (EditText) view.findViewById(R.id.edtCaption);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        this.rvStickers = (RecyclerView) view.findViewById(R.id.rvStickers);
        this.rvFonts = (RecyclerView) view.findViewById(R.id.rvFonts);
        rlMainStartFragment = (RelativeLayout) view.findViewById(R.id.RlMainStartFragment);
        this.ivbtnNavigation = (ImageView) view.findViewById(R.id.ivbtnNavigation);
    }

    private void focuseEdittext() {
        this.edtCaption.requestFocus();
        this.edtCaption.postDelayed((Runnable) new Runnable() {
            @Override
            public void run() { ((InputMethodManager) StartFrameFrag.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput((View) StartFrameFrag.this.edtCaption, 0);
            }
        }, 100L);
    }

    public static Bitmap getBitmap() {
        StartFrameFrag.rlMain.setDrawingCacheEnabled(true);
        StartFrameFrag.rlMain.buildDrawingCache(true);
        final Bitmap bitmap = Bitmap.createBitmap(StartFrameFrag.rlMain.getDrawingCache());
        StartFrameFrag.rlMain.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static onSaveStoryTitle getOnSaveStoryTitle() {
        return StartFrameFrag.onSaveStoryTitle;
    }

    private void inflatLayout() {
        this.llMainInflatId.addView(this.getActivity().getLayoutInflater().inflate(R.layout.fragment_start, (ViewGroup) this.llMainInflatId, false));
    }

    private void init() {
        this.application = MyApplication.getInstance();
        this.frames = new ArrayList<Integer>();
        this.listener = new MultiTouchListener(this);
        setOnSaveStoryTitle(this);
        this.loadStartFrames();
        StickersAndFontsUtils.loadStickers();
        (this.stickerAdapter = new NewStickerAdapter((Context) this.getActivity(), StickersAndFontsUtils.stickerlist)).setOnStickerSelected(this);
        this.setUpStickerAdapter();
        (this.fontsAdapter = new NewFontsAdapter((Context) this.getActivity())).setOnStickerSelected(this);
        this.setUpFontsAdapter();
        if (MyApplication.isStoryAdded) {
            this.ivSelectedImage.setImageURI(Uri.parse(this.application.getSelectedImages().get(0).imagePath));
            this.application.startFrame = 0;
            NewTitleActivity.selectedFramePos = 1;
        } else {
            this.application.startFrame = this.frames.get(0);
            this.ivSelectedImage.setImageResource(this.application.startFrame);
            NewTitleActivity.selectedFramePos = 0;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("init startframe frame pos ");
        sb.append(NewTitleActivity.selectedFramePos);
        this.reinitLayout();
        this.setUpRecycler();
        this.csbChooseColor.setVisibility(View.INVISIBLE);
        this.rvFonts.setVisibility(View.INVISIBLE);
    }

    private void loadStartFrames() {
        if (this.frames == null) {
            this.frames = new ArrayList();
        } else {
            this.frames.clear();
        }
        this.frames.add(Integer.valueOf(R.drawable.default1_start));
        if (MyApplication.isStoryAdded) {
            this.frames.add(Integer.valueOf(0));
        }
        this.frames.add(Integer.valueOf(R.drawable.bday1_start));
        this.frames.add(Integer.valueOf(R.drawable.bday2_start));
        this.frames.add(Integer.valueOf(R.drawable.bday3_start));
        this.frames.add(Integer.valueOf(R.drawable.frame_title1_start));
        this.frames.add(Integer.valueOf(R.drawable.frame_title2_start));
        this.frames.add(Integer.valueOf(R.drawable.frame_title3_start));
        this.frames.add(Integer.valueOf(R.drawable.frame_title4_start));
        this.frames.add(Integer.valueOf(R.drawable.frame_title5_start));
        this.frames.add(Integer.valueOf(R.drawable.frame_title6_start));
        this.frames.add(Integer.valueOf(R.drawable.frame_title7_start));
        this.frames.add(Integer.valueOf(R.drawable.frame_title8_start));
        this.frames.add(Integer.valueOf(R.drawable.sunday1_start));
        this.frames.add(Integer.valueOf(R.drawable.sunday2_start));
        this.frames.add(Integer.valueOf(R.drawable.sunday3_start));
        this.frames.add(Integer.valueOf(R.drawable.love1_start));
        this.frames.add(Integer.valueOf(R.drawable.love2_start));
        this.frames.add(Integer.valueOf(R.drawable.love3_start));
        this.frames.add(Integer.valueOf(R.drawable.anni1_start));
        this.frames.add(Integer.valueOf(R.drawable.anni2_start));
        this.frames.add(Integer.valueOf(R.drawable.anni3_start));
        this.frames.add(Integer.valueOf(R.drawable.mybaby1_start));
        this.frames.add(Integer.valueOf(R.drawable.mybaby2_start));
        this.frames.add(Integer.valueOf(R.drawable.mybaby3_start));
        this.frames.add(Integer.valueOf(R.drawable.angel1_start));
        this.frames.add(Integer.valueOf(R.drawable.angel2_start));
        this.frames.add(Integer.valueOf(R.drawable.angel3_start));
        this.frames.add(Integer.valueOf(R.drawable.dparty1_start));
        this.frames.add(Integer.valueOf(R.drawable.dparty2_start));
        this.frames.add(Integer.valueOf(R.drawable.dparty3_start));
        this.frames.add(Integer.valueOf(R.drawable.thankyou1_start));
        this.frames.add(Integer.valueOf(R.drawable.thankyou2_start));
        this.frames.add(Integer.valueOf(R.drawable.thankyou3_start));
        this.frames.add(Integer.valueOf(R.drawable.myhero1_start));
        this.frames.add(Integer.valueOf(R.drawable.myhero2_start));
        this.frames.add(Integer.valueOf(R.drawable.myhero3_start));
    }

    private void reinitLayout() {
        this.ivSelectedImage.post((Runnable) new Runnable() {
            @Override
            public void run() {
                StartFrameFrag.this.flSticker.getLayoutParams().height = StartFrameFrag.this.ivSelectedImage.getMeasuredHeight();
                StartFrameFrag.this.flSticker.getLayoutParams().width = StartFrameFrag.this.ivSelectedImage.getMeasuredWidth();
                StartFrameFrag.this.flSticker.requestLayout();
            }
        });
    }

    private void saveImageNew() {
        final long currentTimeMillis = System.currentTimeMillis();
        final StringBuilder sb = new StringBuilder();
        sb.append("startfrage save start frame starttime ");
        sb.append(System.currentTimeMillis());
        MyApplication.isStartRemoved = false;
        if (StartFrameFrag.lastsaveTempPath != null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("start  if : ");
            sb2.append(StartFrameFrag.lastsaveTempPath);
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("Start_frame_");
        sb3.append(System.currentTimeMillis());
        sb3.append(".jpeg");
        final String string = sb3.toString();
        final File file = new File(FileUtils.TEMP_IMG_DIRECTORY.getAbsolutePath());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(file.getAbsolutePath());
        sb4.append("/");
        sb4.append(string);
        final File file2 = new File(sb4.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        if (!file2.exists()) {
            try {
                file2.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        final StringBuilder sb5 = new StringBuilder();
        sb5.append(FileUtils.TEMP_IMG_DIRECTORY);
        sb5.append("/");
        sb5.append(string);
        StartFrameFrag.lastsaveTempPath = sb5.toString();
        this.finalBitmap = getBitmap();
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(file2);
            this.finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, (OutputStream) fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            this.getActivity().sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(StartFrameFrag.lastsaveTempPath))));
            StartFrameFrag.isSavingDone = true;
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("startfrage save start frame total ");
        sb6.append(System.currentTimeMillis() - currentTimeMillis);
    }

    static void setOnSaveStoryTitle(final onSaveStoryTitle onSaveStoryTitle) {
        StartFrameFrag.onSaveStoryTitle = onSaveStoryTitle;
    }

    private void setUpFontsAdapter() {
        this.rvFonts.setHasFixedSize(true);
        this.rvFonts.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager((Context) this.getActivity(), RecyclerView.HORIZONTAL, false));
        this.rvFonts.setItemAnimator((RecyclerView.ItemAnimator) new DefaultItemAnimator());
        this.rvFonts.setAdapter((RecyclerView.Adapter) this.fontsAdapter);
    }

    private void setUpRecycler() {
        this.titleFrameAdapter = new VideoTitleFrameAdapter(this.frames, this, true);
        this.rvEditItems.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager((Context) this.getActivity(), RecyclerView.HORIZONTAL, false));
        this.rvEditItems.setItemAnimator((RecyclerView.ItemAnimator) new DefaultItemAnimator());
        this.rvEditItems.setAdapter((RecyclerView.Adapter) this.titleFrameAdapter);
    }

    private void setUpStickerAdapter() {
        this.rvStickers.setLayoutManager((RecyclerView.LayoutManager) new GridLayoutManager((Context) this.getActivity(), 4));
        this.rvStickers.setHasFixedSize(true);
        this.rvStickers.setItemAnimator((RecyclerView.ItemAnimator) new DefaultItemAnimator());
        this.rvStickers.setAdapter((RecyclerView.Adapter) this.stickerAdapter);
    }

    public void closeInput(final View view) {
        this.csbChooseColor.setVisibility(View.VISIBLE);
        this.rvFonts.setVisibility(View.VISIBLE);
        view.postDelayed((Runnable) new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 2);
            }
        }, 100L);
    }

    public int getFrameVisibility() {
        return this.rvEditItems.getVisibility();
    }

    public String getPath(final Uri uri) {
        if (uri == null) {
            return null;
        }
        final Cursor query = this.getActivity().getContentResolver().query(uri, new String[]{"_data"}, (String) null, (String[]) null, (String) null);
        if (query != null) {
            final int columnIndexOrThrow = query.getColumnIndexOrThrow("_data");
            query.moveToFirst();
            return query.getString(columnIndexOrThrow);
        }
        return uri.getPath();
    }

    public void onActivityCreated(@Nullable final Bundle bundle) {
        super.onActivityCreated(bundle);
        this.bindView(this.view);
        this.init();
        this.addListener();
    }

    public void onActivityResult(final int n, final int n2, final Intent intent) {
        super.onActivityResult(n, n2, intent);
        if (n == 11 && n2 == -1) {
            CropImage.activity(intent.getData()).setActivityTitle(this.getActivity().getString(R.string.crop_image)).start(this.getContext(), this);
        }
        if (n == 203) {
            final CropImage.ActivityResult activityResult = CropImage.getActivityResult(intent);
            if (n2 == -1) {
                final Bitmap decodeFile = BitmapFactory.decodeFile(((CropImageView.CropResult) activityResult).getUri().getPath());
                this.addPhotoSticker(Bitmap.createScaledBitmap(decodeFile, decodeFile.getWidth() / 2, decodeFile.getHeight() / 2, false));
            }
        }
    }

    public void onAddCameraImage() {
        this.startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 11);
    }

    public void onAddImageSticker() {
        this.edtCaption.setVisibility(View.GONE);
        if (StartFrameFrag.bottomSheet.getVisibility() == View.VISIBLE) {
            Utils.slideDown((Context) this.getActivity(), StartFrameFrag.bottomSheet);
            this.rvEditItems.setVisibility(View.VISIBLE);
            NewTitleActivity.setStartFrameState(R.id.imgEditGallery);
            return;
        }
        if (StartFrameFrag.bottomSheet.getVisibility() == View.GONE) {
            this.rvEditItems.setVisibility(View.GONE);
            Utils.getScreenShot((View) StartFrameFrag.rlMainStartFragment, (Context) this.getActivity(), StartFrameFrag.bottomSheet);
            Utils.slideUp((Context) this.getActivity(), StartFrameFrag.bottomSheet);
            NewTitleActivity.setStartFrameState(R.id.imgEditSticker);
        }
    }

    public void onAddTextSticker() {
        this.rvEditItems.setVisibility(View.GONE);
        if (this.edtCaption.getVisibility() == View.VISIBLE) {
            this.edtCaption.setVisibility(View.GONE);
            return;
        }
        this.edtCaption.setVisibility(View.VISIBLE);
        this.focuseEdittext();
    }

    public void onClick(final View view) {
        final int id = view.getId();
        if (id == R.id.ivbtnNavigation) {
            this.edtCaption.setVisibility(View.GONE);
            Utils.slideDown((Context) this.getActivity(), StartFrameFrag.bottomSheet);
            this.rvEditItems.setVisibility(View.VISIBLE);
            NewTitleActivity.setStartFrameState(R.id.imgEditGallery);
            return;
        }
        if (id != R.id.rlMain) {
            return;
        }
        this.focusedTextView = null;
        this.focusedImageView = null;
        this.csbChooseColor.setVisibility(View.GONE);
        this.rvFonts.setVisibility(View.GONE);
        this.rvEditItems.setVisibility(View.VISIBLE);
    }

    @Nullable
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        this.view = layoutInflater.inflate(R.layout.activity_title_frames, viewGroup, false);
        this.setHasOptionsMenu(true);
        return this.view;
    }

    public void onRemoveHold(final View view) {
        if (view instanceof TextView) {
            this.csbChooseColor.setVisibility(View.VISIBLE);
            this.rvFonts.setVisibility(View.VISIBLE);
            this.rvEditItems.setVisibility(View.GONE);
            NewTitleActivity.setStartFrameState(0);
        } else if (view instanceof ImageView) {
            this.csbChooseColor.setVisibility(View.INVISIBLE);
            this.rvFonts.setVisibility(View.INVISIBLE);
            NewTitleActivity.setStartFrameState(0);
        } else {
            this.csbChooseColor.setVisibility(View.INVISIBLE);
            this.rvFonts.setVisibility(View.INVISIBLE);
            NewTitleActivity.setStartFrameState(R.id.imgEditGallery);
        }
        this.ivRemoveView.setVisibility(View.GONE);
    }

    public void onSaveImageNew() {
        this.saveImageNew();
    }

    public void onStickerTouch(final int n) {
        Utils.slideDown((Context) this.getActivity(), StartFrameFrag.bottomSheet);
        this.addStickerOnImage(n);
    }

    public void onTextFocusChanged(final View view) {
        if (view instanceof TextView) {
            (this.focusedTextView = (TextView) view).setFocusableInTouchMode(true);
            return;
        }
        final boolean b = view instanceof ImageView;
    }

    public void onTextHoldAndMove(final View view) {
        if (view instanceof TextView) {
            this.focusedTextView = (TextView) view;
            this.focusedImageView = null;
        } else if (view instanceof ImageView) {
            this.focusedImageView = (ImageView) view;
            this.focusedTextView = null;
        }
        this.ivRemoveView.setVisibility(View.VISIBLE);
        this.ivRemoveView.bringToFront();
    }

    public void onTextRemoved(final View view) {
        if (view instanceof TextView) {
            this.flSticker.removeView(view);
        } else if (view instanceof ImageView) {
            this.flSticker.removeView(view);
        }
        if (this.flSticker != null && this.flSticker.getChildCount() == 0) {
            this.csbChooseColor.setVisibility(View.INVISIBLE);
        }
    }

    public void onTextRemovedAnimation(final boolean b) {
        if (b) {
            this.ivRemoveView.setBackgroundColor(-65536);
            this.ivRemoveView.setImageResource(R.drawable.delete_open);
            return;
        }
        this.ivRemoveView.setBackgroundColor(0);
        this.ivRemoveView.setImageResource(R.drawable.delete_close);
    }

    public void onTextStyleChange(final Typeface typeface) {
        this.focusedTextView.setTypeface(typeface);
    }

    public void setFrame() {
        if (MyApplication.isStoryAdded && NewTitleActivity.selectedFramePos == 1) {
            this.ivSelectedImage.setImageURI(Uri.parse(this.application.getSelectedImages().get(0).imagePath));
        } else {
            this.ivSelectedImage.setImageResource((int) this.frames.get(NewTitleActivity.selectedFramePos));
        }
        if (this.titleFrameAdapter != null) {
            this.titleFrameAdapter.notifyDataSetChanged();
        }
        this.reinitLayout();
        NewTitleActivity.isFrameChanged = true;
    }

    public void setFrameVisibility(final int n) {
        if (n == R.id.imgEditCamera) {
            this.rvEditItems.setVisibility(View.GONE);
            this.edtCaption.setVisibility(View.GONE);
            return;
        }
        if (n == R.id.imgEditGallery) {
            this.rvFonts.setVisibility(View.GONE);
            if (this.rvEditItems.getVisibility() != 8 && this.rvEditItems.getVisibility() != 4) {
                this.rvEditItems.setVisibility(View.GONE);
            } else {
                this.rvEditItems.setVisibility(View.VISIBLE);
            }
            this.edtCaption.setVisibility(View.GONE);
            return;
        }
        this.rvEditItems.setVisibility(View.VISIBLE);
    }

    public void setSelectedFrame() {
        if (MyApplication.isStoryAdded && NewTitleActivity.selectedFramePos == 1) {
            this.ivSelectedImage.setImageURI(Uri.parse(this.application.getSelectedImages().get(0).imagePath));
        } else {
            this.ivSelectedImage.setImageResource((int) this.frames.get(NewTitleActivity.selectedFramePos));
        }
        this.reinitLayout();
        NewTitleActivity.isFrameChanged = true;
        EndFrameFrag.getOnSaveStoryTitle().setFrame();
    }

    public void setUserVisibleHint(final boolean userVisibleHint) {
        super.setUserVisibleHint(userVisibleHint);
        if (userVisibleHint) {
            if (this.titleFrameAdapter != null) {
                this.titleFrameAdapter.notifyDataSetChanged();
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("init uservisibility frame pos ");
            sb.append(NewTitleActivity.selectedFramePos);
        }
    }
}
