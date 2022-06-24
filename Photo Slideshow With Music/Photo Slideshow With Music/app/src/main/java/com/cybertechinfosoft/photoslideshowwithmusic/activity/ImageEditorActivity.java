package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ak.android.widget.colorpickerseekbar.ColorPickerSeekBar;
import com.cybertechinfosoft.photoslideshowwithmusic.OnTextStickerListeners;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.ImageEffectsAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.NewFontsAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.adapters.NewStickerAdapter;
import com.cybertechinfosoft.photoslideshowwithmusic.listeners.ItemClickSupport;
import com.cybertechinfosoft.photoslideshowwithmusic.listeners.MultiTouchListener;
import com.cybertechinfosoft.photoslideshowwithmusic.util.BitmapCompression;
import com.cybertechinfosoft.photoslideshowwithmusic.util.FilterHelper;
import com.cybertechinfosoft.photoslideshowwithmusic.util.StickersAndFontsUtils;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.insta.InstaFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class ImageEditorActivity extends AppCompatActivity implements OnTextStickerListeners, View.OnClickListener, ItemClickSupport.OnItemClickListener {
    public static List<Bitmap> bitmapPreview;
    TextView.OnEditorActionListener actionListener;
    private Bitmap bitmap;
    private LinearLayout bottomSheet;
    private ColorPickerSeekBar csbColorPicker;
    int divisionRatio;
    private EditText edtCaption;
    private FrameLayout flMainLayout;
    ImageView focusedImageView;
    TextView focusedTextView;
    private NewFontsAdapter fontsAdapter;
    private ImageEffectsAdapter galleryImageAdapter;
    private int height;
    int id;
    private String imagePath;
    private ImageView imgEditCrop;
    private ImageView imgEditFilter;
    private ImageView imgEditSticker;
    private ImageView imgEditText;
    private ImageView ivRemoveView;
    private GPUImageView ivSelectedImage;
    private RelativeLayout layoutMain;
    private MultiTouchListener listener;
    private int pos;
    private RelativeLayout rlMain;
    private RecyclerView rvFonts;
    private RecyclerView rvStickers;
    private RecyclerView square_effect_fragment_container;
    private NewStickerAdapter stickerAdapter;
    private Toolbar toolbar;
    private int width;

    static {
        ImageEditorActivity.bitmapPreview = new ArrayList<Bitmap>();
    }

    public ImageEditorActivity() {
        this.imagePath = null;
        this.divisionRatio = 1;
        this.actionListener = (TextView.OnEditorActionListener) new TextView.OnEditorActionListener() {
            public boolean onEditorAction(final TextView textView, final int n, final KeyEvent keyEvent) {
                if (n == 6) {
                    if (textView.getText().toString() != null && textView.getText().toString().trim().length() > 0) {
                        ImageEditorActivity.this.closeInput((View) textView);
                        ImageEditorActivity.this.addText(textView.getText().toString());
                        ImageEditorActivity.this.edtCaption.setVisibility(View.GONE);
                        ImageEditorActivity.this.edtCaption.setText((CharSequence) "");
                    } else {
                        ImageEditorActivity.this.closeInput((View) textView);
                        Toast.makeText((Context) ImageEditorActivity.this, R.string.please_add_some_text, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        };
    }

    private void addListener() {
        this.edtCaption.setOnEditorActionListener(this.actionListener);
//        this.csbColorPicker.setOnColorChangeListener((ColorSeekBar.OnColorChangeListener) new ColorSeekBar.OnColorChangeListener() {
//            @Override
//            public void onColorChangeListener(final int n, final int n2, final int textColor) {
//                if (ImageEditorActivity.this.focusedTextView != null) {
//                    ImageEditorActivity.this.focusedTextView.setTextColor(textColor);
//                }
//            }
//        });
        csbColorPicker.setOnColorSeekbarChangeListener(new ColorPickerSeekBar.OnColorSeekBarChangeListener() {
            @Override
            public void onColorChanged(SeekBar seekBar, int color, boolean fromUser) {
//                view.setBackgroundColor(color);
                if (ImageEditorActivity.this.focusedTextView != null) {
                    ImageEditorActivity.this.focusedTextView.setTextColor(color);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        this.imgEditCrop.setOnClickListener((View.OnClickListener) this);
        this.imgEditFilter.setOnClickListener((View.OnClickListener) this);
        this.imgEditText.setOnClickListener((View.OnClickListener) this);
        this.imgEditSticker.setOnClickListener((View.OnClickListener) this);
        this.rlMain.setOnClickListener((View.OnClickListener) this);
        this.findViewById(R.id.ivbtnNavigation).setOnClickListener((View.OnClickListener) this);
    }

    private void addStickerOnImage(final int imageResource) {
        final FrameLayout.LayoutParams frameLayout$LayoutParams = new FrameLayout.LayoutParams(200, 200);
        frameLayout$LayoutParams.gravity = 17;
        final ImageView focusedImageView = new ImageView((Context) this);
        focusedImageView.setImageResource(imageResource);
        focusedImageView.setOnTouchListener((View.OnTouchListener) this.listener);
        this.focusedImageView = focusedImageView;
        this.focusedTextView = null;
        this.flMainLayout.addView((View) focusedImageView, (ViewGroup.LayoutParams) frameLayout$LayoutParams);
        this.rvFonts.setVisibility(View.GONE);
        this.setFocus();
    }

    private void addText(final String text) {
        final FrameLayout.LayoutParams frameLayout$LayoutParams = new FrameLayout.LayoutParams(-2, -2);
        frameLayout$LayoutParams.gravity = 17;
        final TextView focusedTextView = new TextView((Context) this);
        focusedTextView.setText((CharSequence) text);
        focusedTextView.setTextColor(-16776961);
        focusedTextView.setTextSize(40.0f);
        focusedTextView.setOnTouchListener((View.OnTouchListener) this.listener);
        focusedTextView.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                ImageEditorActivity.this.focusedTextView = (TextView) view;
                ImageEditorActivity.this.csbColorPicker.setVisibility(View.VISIBLE);
                ImageEditorActivity.this.square_effect_fragment_container.setVisibility(View.GONE);
                ImageEditorActivity.this.rvFonts.setVisibility(View.VISIBLE);
                ImageEditorActivity.this.setFocus();
                ImageEditorActivity.this.imgEditText.setSelected(true);
            }
        });
        this.focusedTextView = focusedTextView;
        this.focusedImageView = null;
        final AssetManager assets = this.getAssets();
        final StringBuilder sb = new StringBuilder();
        sb.append("fonts/");
        sb.append(StickersAndFontsUtils.fonts[0]);
        this.chenageFontStyle(Typeface.createFromAsset(assets, sb.toString()));
        this.flMainLayout.addView((View) focusedTextView, (ViewGroup.LayoutParams) frameLayout$LayoutParams);
        this.square_effect_fragment_container.setVisibility(View.GONE);
        this.rvFonts.setVisibility(View.VISIBLE);
    }

    private void bindView() {
        this.rlMain = (RelativeLayout) findViewById(R.id.rlMain);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setContentInsetsAbsolute(0, 0);
        this.ivSelectedImage = (GPUImageView) findViewById(R.id.ivSelectedImage);
        this.square_effect_fragment_container = (RecyclerView) findViewById(R.id.rvFilters);
        this.csbColorPicker = (ColorPickerSeekBar) findViewById(R.id.csbColorPicker);
        this.rvStickers = (RecyclerView) findViewById(R.id.rvStickers);
        this.rvFonts = (RecyclerView) findViewById(R.id.rvFonts);
        this.edtCaption = (EditText) findViewById(R.id.ectCaption);
        this.flMainLayout = (FrameLayout) findViewById(R.id.flMainLayout);
        this.ivRemoveView = (ImageView) findViewById(R.id.ivRemoveView);
        this.layoutMain = (RelativeLayout) findViewById(R.id.rlMainEditor);
        this.bottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        this.imgEditCrop = (ImageView) findViewById(R.id.imgEditCrop);
        this.imgEditFilter = (ImageView) findViewById(R.id.imgEditFilter);
        this.imgEditText = (ImageView) findViewById(R.id.imgEditText);
        this.imgEditSticker = (ImageView) findViewById(R.id.imgEditSticker);
    }

    private static int exifToDegrees(final int n) {
        if (n == 6) {
            return 90;
        }
        if (n == 3) {
            return 180;
        }
        if (n == 8) {
            return 270;
        }
        return 0;
    }

    private void focuseEdittext() {
        this.edtCaption.requestFocus();
        this.edtCaption.postDelayed((Runnable) new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) ImageEditorActivity.this.getSystemService(INPUT_METHOD_SERVICE)).showSoftInput((View) ImageEditorActivity.this.edtCaption, 0);
            }
        }, 100L);
    }

    private String getRealPathFromURI(final String s) {
        final Uri parse = Uri.parse(s);
        final Cursor query = this.getContentResolver().query(parse, (String[]) null, (String) null, (String[]) null, (String) null);
        if (query == null) {
            return parse.getPath();
        }
        query.moveToFirst();
        return query.getString(query.getColumnIndex("_data"));
    }

    private void init() {
        this.setSupportActionBar(this.toolbar);
        final TextView textView = (TextView) this.toolbar.findViewById(R.id.toolbar_title);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView.setText((CharSequence) this.getString(R.string.edit));
        Utils.setFont((Activity) this, textView);
        this.listener = new MultiTouchListener(this);
        this.fontsAdapter = new NewFontsAdapter((Context) this);
        this.imagePath = this.getIntent().getExtras().getString("IMAGE_SOURCE");
        this.bitmap = BitmapFactory.decodeFile(this.imagePath);
        ExifInterface exifInterface;
        try {
            exifInterface = new ExifInterface(this.imagePath);
        } catch (IOException ex) {
            ex.printStackTrace();
            exifInterface = null;
        }
        final int attributeInt = exifInterface.getAttributeInt("Orientation", 1);
        final int exifToDegrees = exifToDegrees(attributeInt);
        final Matrix matrix = new Matrix();
        if (attributeInt != 0.0f) {
            matrix.preRotate((float) exifToDegrees);
            this.bitmap = Bitmap.createBitmap(this.bitmap, 0, 0, this.bitmap.getWidth(), this.bitmap.getHeight(), matrix, false);
            System.gc();
        }
        if (this.bitmap.getWidth() > 3200 || this.bitmap.getHeight() > 3200) {
            this.divisionRatio = 2;
        }
        this.bitmap = Bitmap.createScaledBitmap(this.bitmap, this.bitmap.getWidth() / this.divisionRatio, this.bitmap.getHeight() / this.divisionRatio, false);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.height = displayMetrics.heightPixels;
        this.width = displayMetrics.widthPixels;
        if (this.bitmap.getWidth() > this.width && this.bitmap.getHeight() > this.height) {
            this.bitmap = BitmapCompression.resizeImage(this.bitmap, this.width, this.height);
        } else if (this.bitmap.getWidth() > this.width) {
            this.bitmap = BitmapCompression.resizeImage(this.bitmap, this.width, this.bitmap.getHeight());
        } else if (this.bitmap.getHeight() > this.height) {
            this.bitmap = BitmapCompression.resizeImage(this.bitmap, this.bitmap.getWidth(), this.height);
        }
        this.ivSelectedImage.getLayoutParams().height = this.bitmap.getHeight();
        this.ivSelectedImage.getLayoutParams().width = this.bitmap.getWidth();
        this.ivSelectedImage.setImage(this.bitmap);
        this.ivSelectedImage.post((Runnable) new Runnable() {
            @Override
            public void run() {
                ImageEditorActivity.this.flMainLayout.getLayoutParams().height = ImageEditorActivity.this.ivSelectedImage.getMeasuredHeight();
                ImageEditorActivity.this.flMainLayout.getLayoutParams().width = ImageEditorActivity.this.ivSelectedImage.getMeasuredWidth();
                ImageEditorActivity.this.flMainLayout.requestLayout();
            }
        });
        this.toolbar.bringToFront();
        StickersAndFontsUtils.loadStickers();
        this.stickerAdapter = new NewStickerAdapter((Context) this, StickersAndFontsUtils.stickerlist);
        this.setUpStickerAdapter();
        this.csbColorPicker.setVisibility(View.INVISIBLE);
        this.rvFonts.setVisibility(View.INVISIBLE);
        this.setImageBlurSource();
        new BlurImageTask().execute(new Bitmap[]{this.bitmap});
        this.square_effect_fragment_container.setVisibility(View.VISIBLE);
        this.imgEditFilter.setSelected(true);
    }
    public static Bitmap overlayBitmapToCenter(final Bitmap bitmap, final Bitmap bitmap2) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final int width2 = bitmap2.getWidth();
        final int height2 = bitmap2.getHeight();
        final float n = (float) (width * 0.5 - width2 * 0.5);
        final float n2 = (float) (height * 0.5 - height2 * 0.5);
        final Bitmap bitmap3 = Bitmap.createBitmap(width, height, bitmap.getConfig());
        final Canvas canvas = new Canvas(bitmap3);
        canvas.drawBitmap(bitmap, new Matrix(), (Paint) null);
        canvas.drawBitmap(bitmap2, n, n2, (Paint) null);
        return bitmap3;
    }
    private void saveImage() {
        final InstaFilter filter = FilterHelper.getFilter((Context) this, this.pos);
        final GPUImage gpuImage = new GPUImage((Context) this);
        gpuImage.setFilter(filter);
        gpuImage.setImage(this.bitmap);
        this.rlMain.setDrawingCacheEnabled(true);
        final Bitmap copy = this.rlMain.getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
        this.rlMain.setDrawingCacheEnabled(false);
        final Bitmap overlayBitmapToCenter = overlayBitmapToCenter(gpuImage.getBitmapWithFilterApplied(), copy);
        if (!FileUtils.TEMP_IMG_DIRECTORY.exists()) {
            FileUtils.TEMP_IMG_DIRECTORY.mkdirs();
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()));
        sb.append(".jpeg");
        final String string = sb.toString();
        final File file = new File(FileUtils.TEMP_IMG_DIRECTORY, string);
        file.renameTo(file);
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(FileUtils.TEMP_IMG_DIRECTORY);
        sb2.append("/");
        sb2.append(string);
        final String string2 = sb2.toString();
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            overlayBitmapToCenter.compress(Bitmap.CompressFormat.JPEG, 100, (OutputStream) fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(string2))));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        final Intent intent = new Intent();
        intent.putExtra("ImgPath", string2);
        this.setResult(-1, intent);
        this.ivSelectedImage.setVisibility(View.VISIBLE);
        this.finish();
    }

    private void setFocus() {
        this.imgEditCrop.setSelected(false);
        this.imgEditFilter.setSelected(false);
        this.imgEditText.setSelected(false);
        this.imgEditSticker.setSelected(false);
    }

    private void setImageBlurSource() {
        this.square_effect_fragment_container.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager((Context) this, RecyclerView.HORIZONTAL, false));
        this.galleryImageAdapter = new ImageEffectsAdapter((Context) this);
        this.square_effect_fragment_container.setAdapter((RecyclerView.Adapter) this.galleryImageAdapter);
        ItemClickSupport.addTo(this.square_effect_fragment_container).setOnItemClickListener((ItemClickSupport.OnItemClickListener) this);
    }

    private void setUpFontsAdapter() {
        this.rvFonts.setHasFixedSize(true);
        this.rvFonts.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager((Context) this, RecyclerView.HORIZONTAL, false));
        this.rvFonts.setItemAnimator((RecyclerView.ItemAnimator) new DefaultItemAnimator());
        this.rvFonts.setAdapter((RecyclerView.Adapter) this.fontsAdapter);
    }

    private void setUpStickerAdapter() {
        this.rvStickers.setHasFixedSize(true);
        this.rvStickers.setLayoutManager((RecyclerView.LayoutManager) new GridLayoutManager((Context) this, 4));
        this.rvStickers.setItemAnimator((RecyclerView.ItemAnimator) new DefaultItemAnimator());
        this.rvStickers.setAdapter((RecyclerView.Adapter) this.stickerAdapter);
    }

    public void addSticker(final int n) {
        this.bottomSheet.setVisibility(View.GONE);
        this.addStickerOnImage(n);
    }

    public void chenageFontStyle(final Typeface typeface) {
        if (this.focusedTextView != null) {
            this.focusedTextView.setTypeface(typeface);
        }
    }

    public void closeInput(final View view) {
        this.csbColorPicker.setVisibility(View.VISIBLE);
        this.rvFonts.setVisibility(View.VISIBLE);
        view.postDelayed((Runnable) new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) view.getContext().getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 2);
            }
        }, 100L);
    }

    protected void onActivityResult(final int n, final int n2, final Intent intent) {
        super.onActivityResult(n, n2, intent);
        if (n == 203) {
            final CropImage.ActivityResult activityResult = CropImage.getActivityResult(intent);
            if (n2 == -1) {
                this.imagePath = ((CropImageView.CropResult) activityResult).getUri().getPath();
                final Bitmap decodeFile = BitmapFactory.decodeFile(this.imagePath);
                this.divisionRatio = 1;
                if (this.bitmap.getWidth() > 3200 || this.bitmap.getHeight() > 3200) {
                    this.divisionRatio = 2;
                }
                this.bitmap = Bitmap.createScaledBitmap(decodeFile, decodeFile.getWidth() / this.divisionRatio, decodeFile.getHeight() / this.divisionRatio, false);
                if (this.bitmap.getWidth() > this.width && this.bitmap.getHeight() > this.height) {
                    this.bitmap = BitmapCompression.resizeImage(this.bitmap, this.width, this.height);
                } else if (this.bitmap.getWidth() > this.width) {
                    this.bitmap = BitmapCompression.resizeImage(this.bitmap, this.width, this.bitmap.getHeight());
                } else if (this.bitmap.getHeight() > this.height) {
                    this.bitmap = BitmapCompression.resizeImage(this.bitmap, this.bitmap.getWidth(), this.height);
                }
                this.ivSelectedImage.getGPUImage().deleteImage();
                this.ivSelectedImage.getLayoutParams().height = this.bitmap.getHeight();
                this.ivSelectedImage.getLayoutParams().width = this.bitmap.getWidth();
                this.ivSelectedImage.requestLayout();
                final StringBuilder sb = new StringBuilder();
                sb.append("screen :- ");
                sb.append(this.height);
                sb.append("width :: ");
                sb.append(this.width);
                this.ivSelectedImage.setImage(this.bitmap);
                this.ivSelectedImage.post((Runnable) new Runnable() {
                    @Override
                    public void run() {
                        ImageEditorActivity.this.flMainLayout.getLayoutParams().height = ImageEditorActivity.this.bitmap.getHeight();
                        ImageEditorActivity.this.flMainLayout.getLayoutParams().width = ImageEditorActivity.this.bitmap.getWidth();
                        ImageEditorActivity.this.flMainLayout.requestLayout();
                    }
                });
            }
        }
    }

    public void onBackPressed() {
        if (this.edtCaption.getVisibility() != 0 && !this.edtCaption.hasFocus() && this.bottomSheet.getVisibility() != 0) {
            super.onBackPressed();
            return;
        }
        this.edtCaption.setVisibility(View.GONE);
        Utils.hideKeyBoard((Activity) this);
        this.bottomSheet.setVisibility(View.GONE);
        this.setFocus();
        this.imgEditFilter.setSelected(true);
        this.square_effect_fragment_container.setVisibility(View.VISIBLE);
    }

    public void onClick(final View view) {
        final int id = view.getId();
        Utils.hideKeyBoard((Activity) this);
        if (id == R.id.imgEditText) {
            if (this.edtCaption.getVisibility() == 0) {
                this.edtCaption.setVisibility(View.GONE);
            } else {
                this.square_effect_fragment_container.setVisibility(View.GONE);
                this.bottomSheet.setVisibility(View.GONE);
                this.csbColorPicker.setVisibility(View.GONE);
                this.rvFonts.setVisibility(View.GONE);
                this.edtCaption.setVisibility(View.VISIBLE);
                this.focuseEdittext();
            }
            this.setFocus();
            this.imgEditText.setSelected(true);
            return;
        }
        if (id == R.id.imgEditSticker) {
            this.csbColorPicker.setVisibility(View.GONE);
            this.rvFonts.setVisibility(View.GONE);
            this.edtCaption.setVisibility(View.GONE);
            Utils.hideKeyBoard((Activity) this);
            this.setFocus();
            this.square_effect_fragment_container.setVisibility(View.GONE);
            if (this.bottomSheet.getVisibility() == 0) {
                this.bottomSheet.setVisibility(View.GONE);
                return;
            }
            Utils.getScreenShot((View) this.layoutMain, (Context) this, (View) this.bottomSheet);
            this.bottomSheet.setVisibility(View.VISIBLE);
        } else {
            if (id == R.id.imgEditCrop) {
                this.edtCaption.setVisibility(View.GONE);
                this.square_effect_fragment_container.setVisibility(View.GONE);
                this.bottomSheet.setVisibility(View.GONE);
                this.csbColorPicker.setVisibility(View.GONE);
                this.rvFonts.setVisibility(View.GONE);
                CropImage.activity(Uri.fromFile(new File(this.imagePath))).setActivityTitle("Crop Image").start((Activity) this);
                this.setFocus();
                return;
            }
            if (id == R.id.imgEditFilter) {
                this.bottomSheet.setVisibility(View.GONE);
                this.csbColorPicker.setVisibility(View.GONE);
                this.rvFonts.setVisibility(View.GONE);
                this.edtCaption.setVisibility(View.GONE);
                if (this.square_effect_fragment_container.getVisibility() == 8) {
                    this.setFocus();
                    this.imgEditFilter.setSelected(true);
                    this.square_effect_fragment_container.setVisibility(View.VISIBLE);
                    return;
                }
                this.setFocus();
                this.square_effect_fragment_container.setVisibility(View.GONE);
            } else {
                if (id == R.id.rlMain) {
                    this.csbColorPicker.setVisibility(View.GONE);
                    this.rvFonts.setVisibility(View.GONE);
                    this.edtCaption.setVisibility(View.GONE);
                    this.setFocus();
                    this.imgEditFilter.setSelected(true);
                    this.square_effect_fragment_container.setVisibility(View.VISIBLE);
                    return;
                }
                if (id == R.id.ivbtnNavigation) {
                    this.bottomSheet.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_image_editor);
        this.bindView();
        this.init();
        this.addListener();
        this.setUpFontsAdapter();
        PutAnalyticsEvent();
    }
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ImageEditorActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(R.menu.title_main_menu, menu);
        menu.findItem(R.id.action_skip).setVisible(false);
        menu.findItem(R.id.action_done).setTitle(R.string.done);
        for (int i = 0; i < menu.size(); ++i) {
            final MenuItem item = menu.getItem(i);
            final SubMenu subMenu = item.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); ++j) {
                    Utils.applyFontToMenuItem(this.getApplicationContext(), subMenu.getItem(j));
                }
            }
            Utils.applyFontToMenuItem(this.getApplicationContext(), item);
        }
        return true;
    }

    public void onItemClicked(final RecyclerView recyclerView, final int n, final View view) {
        this.pos = n;
        final StringBuilder sb = new StringBuilder();
        sb.append("Item click position ");
        sb.append(n);
        this.galleryImageAdapter.setSelectedItem(n);
        final InstaFilter filter = FilterHelper.getFilter((Context) this, n);
        if (filter != null) {
            this.ivSelectedImage.setFilter(filter);
        }
    }

    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        final int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            Utils.hideKeyBoard((Activity) this);
            this.onBackPressed();
        } else if (itemId == R.id.action_done) {
            Utils.hideKeyBoard((Activity) this);
            this.id = R.id.action_done;
            this.saveImage();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onRemoveHold(final View view) {
        if (view instanceof TextView) {
            this.csbColorPicker.setVisibility(View.VISIBLE);
            this.rvFonts.setVisibility(View.VISIBLE);
        } else {
            this.csbColorPicker.setVisibility(View.INVISIBLE);
            this.rvFonts.setVisibility(View.INVISIBLE);
        }
        this.toolbar.setVisibility(View.VISIBLE);
        this.ivRemoveView.setVisibility(View.GONE);
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
        this.toolbar.setVisibility(View.INVISIBLE);
        this.ivRemoveView.setVisibility(View.VISIBLE);
        this.csbColorPicker.setVisibility(View.INVISIBLE);
        this.rvFonts.setVisibility(View.INVISIBLE);
        this.ivRemoveView.bringToFront();
        this.square_effect_fragment_container.setVisibility(View.GONE);
    }

    public void onTextRemoved(final View view) {
        if (view instanceof TextView) {
            this.flMainLayout.removeView(view);
        } else if (view instanceof ImageView) {
            this.flMainLayout.removeView(view);
        }
        if (this.flMainLayout != null && this.flMainLayout.getChildCount() == 0) {
            this.csbColorPicker.setVisibility(View.INVISIBLE);
            this.rvFonts.setVisibility(View.INVISIBLE);
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

    public class BlurImageTask extends AsyncTask<Bitmap, Void, Void> {
        protected Void doInBackground(final Bitmap... array) {
            ImageEditorActivity.bitmapPreview.clear();
            final Bitmap image = array[0];
            ImageEditorActivity.bitmapPreview.add(image);
            for (int i = 1; i < 12; ++i) {
                final InstaFilter filter = FilterHelper.getFilter((Context) ImageEditorActivity.this, i);
                final GPUImage gpuImage = new GPUImage((Context) ImageEditorActivity.this);
                gpuImage.setFilter(filter);
                gpuImage.setImage(image);
                ImageEditorActivity.bitmapPreview.add(gpuImage.getBitmapWithFilterApplied());
                gpuImage.deleteImage();
                this.publishProgress(new Void[0]);
            }
            return null;
        }

        protected void onPostExecute(final Void void1) {
            super.onPostExecute(void1);
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onProgressUpdate(final Void... array) {
            super.onProgressUpdate(array);
            ImageEditorActivity.this.galleryImageAdapter.notifyDataSetChanged();
        }
    }
}
