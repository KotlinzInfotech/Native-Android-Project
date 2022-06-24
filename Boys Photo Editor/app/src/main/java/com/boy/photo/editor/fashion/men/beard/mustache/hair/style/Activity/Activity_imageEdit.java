package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Adapters.EffectAdapter;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Adapters.FrameAdapter;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Adapters.StickerAdapter;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Adapters.fontStyle_Adapter;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.App.AppController;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.CustomTextView;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.CustomTextView.OperationListener;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.FrameModel;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.StickerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Activity_imageEdit extends AppCompatActivity implements View.OnTouchListener {
    Activity activity = Activity_imageEdit.this;
    public static String str_url;
    public static Bitmap bitmap;
    public static Bitmap final_bitmap = null;
    public static Canvas canvas;
    public FrameLayout frameLayout;
    public static Bitmap text_bitmap;
    LinearLayout ll_brightness;
    private SeekBar brightness_seekbar;
    EditText edittext;
    ImageView img_color;
    ImageView img_done, ivCancle;
    ImageView img_fontstyle;
    public ImageView img_gravity;
    ImageView img_keyboard;
    LinearLayout ll_linear;
    boolean flagforeffect = true;
    String[] fonts = new String[]{"font1.ttf", "font2.ttf", "font3.ttf",
            "font4.TTF", "font5.ttf", "font6.TTF", "font9.ttf", "font11.ttf",
            "font12.ttf", "font14.TTF", "font16.TTF", "font17.ttf",
            "font20.ttf"};
    public ArrayList<FrameModel> list_frame1;
    public ArrayList<Integer> list_frame2;
    public ArrayList<Integer> list_frame3;
    public ArrayList<Integer> list_frame4;
    public ArrayList<Integer> list_frame5;
    public ArrayList<Integer> list_frame6;
    public ArrayList<Integer> list_frame7;
    public ArrayList<Integer> list_frame8;
    private LinearLayout ll_colorlist;
    private LinearLayout ll_fontlist;
    private CustomTextView current_textView;
    public static StickerView currentView;
    private int picked_color = -1;
    GridView grid_colorlist;
    GridView grid_fontlist;
    private ImageView img_back;
    LinearLayout ll_img_effect;
    public static ImageView img_gallrey1;
    InputMethodManager input_method_manager;
    private boolean isFrame = true;
    public final ArrayList<View> stickers = new ArrayList();
    LinearLayout ll_opacity;
    LinearLayout llEraser;
    private SeekBar seekbar_opacity;
    private ImageView img_save;
    LinearLayout ll_sticker;
    LinearLayout ll_flip;
    public int stickerId;
    public final ArrayList<Integer> sticker_list = new ArrayList();
    LinearLayout ll_text;
    private Typeface typeface;
    private int w = 0;

    RelativeLayout layoutFrame;
    RecyclerView rvFrame;
    public RecyclerView rvSticker;
    RecyclerView rvEffect;
    FrameAdapter frameAdapter;
    StickerAdapter stickerAdapter;
    EffectAdapter effectAdapter;
    LinearLayout llcategoryMain;
    LinearLayout llStickerCategory;
    LinearLayout llItem;
    ImageView ivSticker, ivEraser, ivEffect, ivBrightness, ivFlip, ivOpacity, ivtext;
    LinearLayout llTextBottom;
    LinearLayout llBottomSheet;

    RelativeLayout rlStickerMain;
    ImageView ivSubBack;
    BottomSheetBehavior sheetBehavior;


    public boolean IsSticker = false;

    private boolean IsFrom;
    private Bitmap savebitmap;
    private String imagePath;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity_main);
        IsFrom = getIntent().getBooleanExtra("IsFrom", false);
        imagePath = getIntent().getStringExtra("imagePath");
        savebitmap = BitmapFactory.decodeFile(imagePath);
        BindView();
        setArryListOfFrame();
        setArraylistForSticker();
        setArryListOfGoggles();
        setArraylistForMustache();
        setArraylistForTurban();
        setArraylistForTatoo();
        setArraylistForCap();
        setArraylistForHair();
        setArraylistForBeard();
        effectselection();
        initView();
        sheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        BottomSheet();
        openDialog();
        PutAnalyticsEvent();
    }

    //Firebase AnalyticsEvent
    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity_imageEdit");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void initView() {
        img_gallrey1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Activity_imageEdit.this.currentView != null) {
                    Activity_imageEdit.this.currentView.setInEdit(false);
                }
                if (Activity_imageEdit.this.current_textView != null) {
                    Activity_imageEdit.this.current_textView.setInEdit(false);
                }
            }
        });
        frameLayout.setOnTouchListener(this);
        this.img_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.mInterstitialAd != null) {
                    AppController.activity = activity;
                    AppController.AdsId = 4;
                    AppController.mInterstitialAd.show(activity);
                } else {
                    onBackPressed();
                }
            }
        });
        this.seekbar_opacity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Activity_imageEdit.this.currentView.setAlpha(((float) progress) / 255.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        this.ll_opacity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ivOpacity.setImageResource(R.drawable.ic_opacity_press);
                ivFlip.setImageResource(R.drawable.ic_flip_unpress);
                ivBrightness.setImageResource(R.drawable.ic_brightness_unpress);
                ivEffect.setImageResource(R.drawable.ic_effect_unpress);
                ivSticker.setImageResource(R.drawable.ic_smile_unpress);
                ivEraser.setImageResource(R.drawable.ic_eraseredit_unpress);
                ivtext.setImageResource(R.drawable.ic_text_unpress);

                brightness_seekbar.setVisibility(View.GONE);
                layoutFrame.setVisibility(View.GONE);
                rvEffect.setVisibility(View.GONE);
                if (currentView == null) {
                    Toast.makeText(activity, "Please Insert Sticker First", Toast.LENGTH_SHORT).show();
                } else {
                    seekbar_opacity.setVisibility(View.VISIBLE);
                }
            }
        });
        llEraser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ivEraser.setImageResource(R.drawable.ic_eraseredit_press);
                ivOpacity.setImageResource(R.drawable.ic_opacity_unpress);
                ivFlip.setImageResource(R.drawable.ic_flip_unpress);
                ivBrightness.setImageResource(R.drawable.ic_brightness_unpress);
                ivEffect.setImageResource(R.drawable.ic_effect_unpress);
                ivSticker.setImageResource(R.drawable.ic_smile_unpress);
                ivtext.setImageResource(R.drawable.ic_text_unpress);
                if (currentView != null) {
                    currentView.setInEdit(false);
                }
                if (current_textView != null) {
                    current_textView.setInEdit(false);
                }
                final_bitmap = getMainFrameBitmap();
                startActivity(new Intent(activity, EraserActivity.class));
                finish();
            }
        });

        frameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentView != null) {
                    currentView.setInEdit(false);
                }
                if (current_textView != null) {
                    current_textView.setInEdit(false);
                }
            }
        });

        this.ll_img_effect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ivEffect.setImageResource(R.drawable.ic_effect_press);
                ivSticker.setImageResource(R.drawable.ic_smile_unpress);
                ivEraser.setImageResource(R.drawable.ic_eraseredit_unpress);
                ivBrightness.setImageResource(R.drawable.ic_brightness_unpress);
                ivFlip.setImageResource(R.drawable.ic_flip_unpress);
                ivOpacity.setImageResource(R.drawable.ic_opacity_unpress);
                ivtext.setImageResource(R.drawable.ic_text_unpress);

                if (Activity_imageEdit.this.currentView != null) {
                    Activity_imageEdit.this.currentView.setInEdit(false);
                }
                if (Activity_imageEdit.this.current_textView != null) {
                    Activity_imageEdit.this.current_textView.setInEdit(false);
                }
                Activity_imageEdit.this.seekbar_opacity.setVisibility(View.GONE);
                Activity_imageEdit.this.brightness_seekbar.setVisibility(View.GONE);
                layoutFrame.setVisibility(View.GONE);
                rvEffect.setVisibility(View.VISIBLE);
                if (Activity_imageEdit.this.flagforeffect) {
                    Activity_imageEdit.this.brightness_seekbar.setVisibility(View.GONE);
                    rvEffect.setVisibility(View.VISIBLE);
                    Activity_imageEdit.this.flagforeffect = false;
                    return;
                }
                Activity_imageEdit.this.flagforeffect = true;
            }
        });
        this.img_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentView != null) {
                    currentView.setInEdit(false);
                }
                if (current_textView != null) {
                    current_textView.setInEdit(false);
                }
                final_bitmap = getMainFrameBitmap();
                saveImage(Activity_imageEdit.final_bitmap);
                if (AppController.mInterstitialAd != null) {
                    AppController.activity = activity;
                    AppController.AdsId = 5;
                    AppController.mInterstitialAd.show(activity);
                } else {
                    startActivity(new Intent(activity, Activity_Share.class));
                    finish();
                }
            }
        });

        this.ll_brightness.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ivBrightness.setImageResource(R.drawable.ic_brightness_press);
                ivEffect.setImageResource(R.drawable.ic_effect_unpress);
                ivSticker.setImageResource(R.drawable.ic_smile_unpress);
                ivEraser.setImageResource(R.drawable.ic_eraseredit_unpress);
                ivFlip.setImageResource(R.drawable.ic_flip_unpress);
                ivOpacity.setImageResource(R.drawable.ic_opacity_unpress);
                ivtext.setImageResource(R.drawable.ic_text_unpress);

                seekbar_opacity.setVisibility(View.GONE);
                layoutFrame.setVisibility(View.GONE);
                rvEffect.setVisibility(View.GONE);
                brightness_seekbar.setVisibility(View.VISIBLE);
            }
        });
        this.brightness_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBrightness(img_gallrey1, progress + 100);
                brightness_seekbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        this.ll_sticker.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FrameVisible();
            }
        });
        this.ll_text.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ivtext.setImageResource(R.drawable.ic_text_press);
                ivOpacity.setImageResource(R.drawable.ic_opacity_unpress);
                ivFlip.setImageResource(R.drawable.ic_flip_unpress);
                ivBrightness.setImageResource(R.drawable.ic_brightness_unpress);
                ivEffect.setImageResource(R.drawable.ic_effect_unpress);
                ivSticker.setImageResource(R.drawable.ic_smile_unpress);
                ivEraser.setImageResource(R.drawable.ic_eraseredit_unpress);
                seekbar_opacity.setVisibility(View.GONE);
                brightness_seekbar.setVisibility(View.GONE);
                llTextBottom.setVisibility(View.VISIBLE);
                llItem.setVisibility(View.GONE);
                ll_linear.setVisibility(View.GONE);
                input_method_manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                input_method_manager.toggleSoftInput(2, 0);
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        this.ll_flip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ivFlip.setImageResource(R.drawable.ic_flip_press);
                ivBrightness.setImageResource(R.drawable.ic_brightness_unpress);
                ivEffect.setImageResource(R.drawable.ic_effect_unpress);
                ivSticker.setImageResource(R.drawable.ic_smile_unpress);
                ivEraser.setImageResource(R.drawable.ic_eraseredit_unpress);
                ivOpacity.setImageResource(R.drawable.ic_opacity_unpress);
                ivtext.setImageResource(R.drawable.ic_text_unpress);
                seekbar_opacity.setVisibility(View.GONE);
                brightness_seekbar.setVisibility(View.GONE);
                if (isFrame) {
                    Activity_imageEdit.img_gallrey1.setRotationY(180.0f);
                    isFrame = false;
                    return;
                }
                img_gallrey1.setRotationY(360.0f);
                isFrame = true;
            }
        });
    }

    private void BindView() {
        rlStickerMain = findViewById(R.id.rl_sticker_main);
        ivSubBack = findViewById(R.id.iv_sub_back);
        ll_sticker = findViewById(R.id.sticker);
        ll_flip = findViewById(R.id.ll_flip);
        img_back = findViewById(R.id.imgg_back);
        ll_img_effect = findViewById(R.id.img_effect);
        ll_opacity = findViewById(R.id.ll_opacity);
        llEraser = findViewById(R.id.ll_eraser);
        ll_brightness = findViewById(R.id.ll_brightness);
        img_save = findViewById(R.id.imgsave);
        brightness_seekbar = findViewById(R.id.brightness_bar);
        seekbar_opacity = findViewById(R.id.opacity_bar);
        ll_linear = findViewById(R.id.linear);
        img_gallrey1 = findViewById(R.id.img_gallrey1);
        ll_text = findViewById(R.id.text);
        frameLayout = findViewById(R.id.framelayout);
        if (IsFrom) {
            img_gallrey1.setImageBitmap(savebitmap);
        } else {
            img_gallrey1.setImageBitmap(Activity_Crop.bitmap_cropped);
        }

        layoutFrame = findViewById(R.id.rl_frame);
        rvFrame = findViewById(R.id.rvFrame);
        rvSticker = findViewById(R.id.rv_sticker);
        rvEffect = findViewById(R.id.rv_effect);
        llcategoryMain = findViewById(R.id.ll_category_main);
        llStickerCategory = findViewById(R.id.ll_sticker_item);
        llItem = findViewById(R.id.ll_item);
        ivSticker = findViewById(R.id.iv_sticker);
        ivEraser = findViewById(R.id.iv_eraser);
        ivEffect = findViewById(R.id.ic_effect);
        ivBrightness = findViewById(R.id.iv_brightness);
        ivFlip = findViewById(R.id.iv_flip);
        ivOpacity = findViewById(R.id.iv_opacity);
        ivtext = findViewById(R.id.iv_text);
        llTextBottom = findViewById(R.id.ll_text_bottom);
        llBottomSheet = findViewById(R.id.bottom_sheet);
        ivSticker.setImageResource(R.drawable.ic_smile_press);


        ivSubBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                llStickerCategory.setVisibility(View.GONE);
                layoutFrame.setVisibility(View.GONE);
                llcategoryMain.setVisibility(View.VISIBLE);
            }
        });
    }


    public void setCurrentEdit(StickerView stickerView) {
        if (this.currentView != null) {
            this.currentView.setInEdit(false);
        }
        this.currentView = stickerView;
        stickerView.setInEdit(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void effectselection() {
        ArrayList<Integer> effectList = new ArrayList();
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));
        effectList.add(Integer.valueOf(R.drawable.flower));

        effectAdapter = new EffectAdapter(activity, effectList);
        rvEffect.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvEffect.setAdapter(effectAdapter);
        effectAdapter.notifyDataSetChanged();
    }


    private Bitmap getMainFrameBitmap() {
        rlStickerMain.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rlStickerMain.getWidth(), rlStickerMain.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.setBitmap(bitmap);
        canvas.drawColor(0);
        rlStickerMain.draw(new Canvas(bitmap));
        return bitmap;
    }

    private void saveImage(Bitmap bitmap2) {
        Bitmap bitmap = bitmap2;
        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File dir = new File(filepath.getAbsolutePath() + "/" + getString(R.string.app_name));
        dir.mkdirs();
        String FileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        File file = new File(dir, FileName);
        file.renameTo(file);
        String _uri = "file://" + filepath.getAbsolutePath() + "/" + getString(R.string.app_name) + "/" + FileName;
        str_url = filepath.getAbsolutePath() + "/" + getString(R.string.app_name) + "/" + FileName;
        MediaScannerConnection.scanFile(this, new String[]{str_url}, null,
                new OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
        try {
            OutputStream output = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBrightness(ImageView image, int value) {
        float[] mainMatrix = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f};
        float brightness = (float) (value - 255);
        mainMatrix[4] = brightness;
        mainMatrix[9] = brightness;
        mainMatrix[14] = brightness;
        image.setColorFilter(new ColorMatrixColorFilter(mainMatrix));
    }

    private void setArryListOfFrame() {
        this.list_frame1 = new ArrayList();
        this.list_frame1.add(new FrameModel(R.drawable.ic_hair_press, R.drawable.ic_hair_unpress));
        this.list_frame1.add(new FrameModel(R.drawable.ic_bread_press, R.drawable.ic_bread_unpress));
        this.list_frame1.add(new FrameModel(R.drawable.ic_mustache_press, R.drawable.ic_mustache_unpress));
        this.list_frame1.add(new FrameModel(R.drawable.ic_turben_press, R.drawable.ic_turben_unpress));
        this.list_frame1.add(new FrameModel(R.drawable.ic_cap_press, R.drawable.ic_cap_unpress));
        this.list_frame1.add(new FrameModel(R.drawable.ic_googles_press, R.drawable.ic_googles_unpress));
        this.list_frame1.add(new FrameModel(R.drawable.ic_tatto_press, R.drawable.ic_tatto_unpress));
        this.list_frame1.add(new FrameModel(R.drawable.ic_sticker_press, R.drawable.ic_sticker_unpress));
    }

    private void setArryListOfGoggles() {
        this.list_frame2 = new ArrayList();
        this.list_frame2.add(Integer.valueOf(R.drawable.g1));
        this.list_frame2.add(Integer.valueOf(R.drawable.g4));
        this.list_frame2.add(Integer.valueOf(R.drawable.g17));
        this.list_frame2.add(Integer.valueOf(R.drawable.g18));
        this.list_frame2.add(Integer.valueOf(R.drawable.g35));
        this.list_frame2.add(Integer.valueOf(R.drawable.g36));
        this.list_frame2.add(Integer.valueOf(R.drawable.g27));
        this.list_frame2.add(Integer.valueOf(R.drawable.g29));
        this.list_frame2.add(Integer.valueOf(R.drawable.g5));
        this.list_frame2.add(Integer.valueOf(R.drawable.g20));
        this.list_frame2.add(Integer.valueOf(R.drawable.g21));
        this.list_frame2.add(Integer.valueOf(R.drawable.g6));
        this.list_frame2.add(Integer.valueOf(R.drawable.g7));
        this.list_frame2.add(Integer.valueOf(R.drawable.g8));
        this.list_frame2.add(Integer.valueOf(R.drawable.g9));
        this.list_frame2.add(Integer.valueOf(R.drawable.g2));
        this.list_frame2.add(Integer.valueOf(R.drawable.g3));
        this.list_frame2.add(Integer.valueOf(R.drawable.g10));
        this.list_frame2.add(Integer.valueOf(R.drawable.g33));
        this.list_frame2.add(Integer.valueOf(R.drawable.g34));
        this.list_frame2.add(Integer.valueOf(R.drawable.g11));
        this.list_frame2.add(Integer.valueOf(R.drawable.g14));
        this.list_frame2.add(Integer.valueOf(R.drawable.g15));
        this.list_frame2.add(Integer.valueOf(R.drawable.g12));
        this.list_frame2.add(Integer.valueOf(R.drawable.g22));
        this.list_frame2.add(Integer.valueOf(R.drawable.g28));
        this.list_frame2.add(Integer.valueOf(R.drawable.g19));
        this.list_frame2.add(Integer.valueOf(R.drawable.g26));
        this.list_frame2.add(Integer.valueOf(R.drawable.g16));
        this.list_frame2.add(Integer.valueOf(R.drawable.g23));
        this.list_frame2.add(Integer.valueOf(R.drawable.g24));
        this.list_frame2.add(Integer.valueOf(R.drawable.g25));
        this.list_frame2.add(Integer.valueOf(R.drawable.g13));
        this.list_frame2.add(Integer.valueOf(R.drawable.g30));
        this.list_frame2.add(Integer.valueOf(R.drawable.g31));
        this.list_frame2.add(Integer.valueOf(R.drawable.g32));

    }

    private void setArraylistForMustache() {
        this.list_frame3 = new ArrayList();
        this.list_frame3.add(Integer.valueOf(R.drawable.m34));
        this.list_frame3.add(Integer.valueOf(R.drawable.m35));
        this.list_frame3.add(Integer.valueOf(R.drawable.m36));
        this.list_frame3.add(Integer.valueOf(R.drawable.m37));
        this.list_frame3.add(Integer.valueOf(R.drawable.m38));
        this.list_frame3.add(Integer.valueOf(R.drawable.m39));
        this.list_frame3.add(Integer.valueOf(R.drawable.m1));
        this.list_frame3.add(Integer.valueOf(R.drawable.m2));
        this.list_frame3.add(Integer.valueOf(R.drawable.m9));
        this.list_frame3.add(Integer.valueOf(R.drawable.m10));
        this.list_frame3.add(Integer.valueOf(R.drawable.m30));
        this.list_frame3.add(Integer.valueOf(R.drawable.m3));
        this.list_frame3.add(Integer.valueOf(R.drawable.m4));
        this.list_frame3.add(Integer.valueOf(R.drawable.m5));
        this.list_frame3.add(Integer.valueOf(R.drawable.m31));
        this.list_frame3.add(Integer.valueOf(R.drawable.m32));
        this.list_frame3.add(Integer.valueOf(R.drawable.m33));
        this.list_frame3.add(Integer.valueOf(R.drawable.m11));
        this.list_frame3.add(Integer.valueOf(R.drawable.m12));
        this.list_frame3.add(Integer.valueOf(R.drawable.m13));
        this.list_frame3.add(Integer.valueOf(R.drawable.m14));
        this.list_frame3.add(Integer.valueOf(R.drawable.m15));
        this.list_frame3.add(Integer.valueOf(R.drawable.m16));
        this.list_frame3.add(Integer.valueOf(R.drawable.m17));
        this.list_frame3.add(Integer.valueOf(R.drawable.m18));
        this.list_frame3.add(Integer.valueOf(R.drawable.m19));
        this.list_frame3.add(Integer.valueOf(R.drawable.m20));
        this.list_frame3.add(Integer.valueOf(R.drawable.m21));
        this.list_frame3.add(Integer.valueOf(R.drawable.m47));
        this.list_frame3.add(Integer.valueOf(R.drawable.m48));
        this.list_frame3.add(Integer.valueOf(R.drawable.m22));
        this.list_frame3.add(Integer.valueOf(R.drawable.m23));
        this.list_frame3.add(Integer.valueOf(R.drawable.m40));
        this.list_frame3.add(Integer.valueOf(R.drawable.m41));
        this.list_frame3.add(Integer.valueOf(R.drawable.m42));
        this.list_frame3.add(Integer.valueOf(R.drawable.m43));
        this.list_frame3.add(Integer.valueOf(R.drawable.m44));
        this.list_frame3.add(Integer.valueOf(R.drawable.m25));
        this.list_frame3.add(Integer.valueOf(R.drawable.m28));
        this.list_frame3.add(Integer.valueOf(R.drawable.m29));
        this.list_frame3.add(Integer.valueOf(R.drawable.m45));
        this.list_frame3.add(Integer.valueOf(R.drawable.m46));

    }

    private void setArraylistForTurban() {
        this.list_frame4 = new ArrayList();

        this.list_frame4.add(Integer.valueOf(R.drawable.turban21));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban22));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban23));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_10));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_1));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_2));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_26));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_3));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_25));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_5));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_6));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_11));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_27));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban24));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban25));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban26));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_7));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_8));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_9));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_12));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban29));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban30));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban31));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_4));
        this.list_frame4.add(Integer.valueOf(R.drawable.t_13));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban16));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban17));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban18));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban19));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban20));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban27));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban28));
        this.list_frame4.add(Integer.valueOf(R.drawable.turban32));

    }

    private void setArraylistForHair() {
        this.list_frame5 = new ArrayList();
        this.list_frame5.add(Integer.valueOf(R.drawable.img_1));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_2));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_3));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_4));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_34));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_10));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_11));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_12));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_13));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_14));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_15));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_16));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_17));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_18));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_19));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_20));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_21));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_22));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_23));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_5));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_6));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_7));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_8));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_24));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_25));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_26));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_27));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_9));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_28));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_29));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_30));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_31));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_32));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_33));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_35));
        this.list_frame5.add(Integer.valueOf(R.drawable.img_36));
    }

    private void setArraylistForCap() {
        this.list_frame6 = new ArrayList();
        this.list_frame6.add(Integer.valueOf(R.drawable.cap1));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap2));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap3));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap4));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap5));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap6));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap7));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap8));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap9));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap10));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap11));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap12));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap13));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap14));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap15));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap16));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap17));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap18));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap19));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap20));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap21));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap22));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap23));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap24));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap25));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap26));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap27));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap28));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap29));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap31));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap32));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap33));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap34));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap35));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap36));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap37));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap38));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap39));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap40));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap41));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap42));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap44));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap45));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap46));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap47));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap48));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap49));
        this.list_frame6.add(Integer.valueOf(R.drawable.cap50));
    }

    private void setArraylistForTatoo() {
        this.list_frame7 = new ArrayList();
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo1));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo2));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo3));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo4));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo5));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo6));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo7));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo8));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo9));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo10));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo11));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo12));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo13));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo14));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo15));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo16));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo17));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo18));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo20));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo21));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo22));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo23));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo24));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo25));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo26));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo27));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo28));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo29));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo30));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo31));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo32));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo33));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo34));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo35));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo36));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo37));

        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo38));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo39));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo40));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo41));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo42));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo43));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo44));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo45));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo46));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo47));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo48));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo49));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo50));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo51));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo52));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo53));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo54));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo55));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo56));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo57));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo58));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo59));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo60));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo61));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo62));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo63));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo64));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo65));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo66));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo67));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo68));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo69));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo70));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo71));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo72));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo73));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo74));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo75));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo76));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo77));
        this.list_frame7.add(Integer.valueOf(R.drawable.tatoo78));

    }

    private void setArraylistForBeard() {
        this.list_frame8 = new ArrayList();
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi1));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi2));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi3));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi4));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi6));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi7));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi8));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi10));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi11));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi12));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi13));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi14));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi15));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi16));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi_38));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi_39));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi_40));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi_41));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi20));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi21));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi22));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi23));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi24));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi25));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi26));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi27));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi28));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi29));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi30));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi31));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi32));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi33));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi34));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi35));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi36));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi_37));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi_42));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi_43));
        this.list_frame8.add(Integer.valueOf(R.drawable.mustachi_44));
    }

    private void setArraylistForSticker() {
        this.sticker_list.add(Integer.valueOf(R.drawable.s1));
        this.sticker_list.add(Integer.valueOf(R.drawable.s2));
        this.sticker_list.add(Integer.valueOf(R.drawable.s3));
        this.sticker_list.add(Integer.valueOf(R.drawable.s4));
        this.sticker_list.add(Integer.valueOf(R.drawable.s5));
        this.sticker_list.add(Integer.valueOf(R.drawable.s6));
        this.sticker_list.add(Integer.valueOf(R.drawable.s7));
        this.sticker_list.add(Integer.valueOf(R.drawable.s9));
        this.sticker_list.add(Integer.valueOf(R.drawable.s10));
        this.sticker_list.add(Integer.valueOf(R.drawable.s11));
        this.sticker_list.add(Integer.valueOf(R.drawable.s12));
        this.sticker_list.add(Integer.valueOf(R.drawable.s13));
        this.sticker_list.add(Integer.valueOf(R.drawable.s14));
        this.sticker_list.add(Integer.valueOf(R.drawable.s15));
        this.sticker_list.add(Integer.valueOf(R.drawable.s16));
        this.sticker_list.add(Integer.valueOf(R.drawable.s17));
        this.sticker_list.add(Integer.valueOf(R.drawable.s19));
        this.sticker_list.add(Integer.valueOf(R.drawable.s22));
        this.sticker_list.add(Integer.valueOf(R.drawable.s23));
        this.sticker_list.add(Integer.valueOf(R.drawable.s24));
        this.sticker_list.add(Integer.valueOf(R.drawable.s25));
        this.sticker_list.add(Integer.valueOf(R.drawable.s26));
        this.sticker_list.add(Integer.valueOf(R.drawable.s27));
        this.sticker_list.add(Integer.valueOf(R.drawable.s28));
    }

    public void openDialog() {
        final TextView textView = new TextView(this);
        edittext = findViewById(R.id.edit_text);
        edittext.requestFocus();
        ll_fontlist = findViewById(R.id.ll_fontlist);
        ll_fontlist.setVisibility(View.GONE);
        grid_fontlist = findViewById(R.id.grid_fontlist);
        grid_fontlist.setAdapter(new fontStyle_Adapter(this, this.fonts));
        grid_fontlist.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int i, long l) {
                Activity_imageEdit.this.typeface = Typeface.createFromAsset(
                        Activity_imageEdit.this.getAssets(),
                        Activity_imageEdit.this.fonts[i]);
                Activity_imageEdit.this.edittext
                        .setTypeface(Activity_imageEdit.this.typeface);
                textView.setTypeface(Activity_imageEdit.this.typeface);
            }
        });
        ll_colorlist = findViewById(R.id.ll_colorlist);
        ll_colorlist.setVisibility(View.GONE);
        grid_colorlist = findViewById(R.id.grid_colorlist);
        ArrayList colors = HSVColors();
        final ArrayList arrayList = colors;
        grid_colorlist.setAdapter(new ArrayAdapter<Integer>(getApplicationContext(), 17367043, colors) {
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setBackgroundColor(((Integer) arrayList.get(position)).intValue());
                view.setText("");
                view.setLayoutParams(new LayoutParams(-1, -1));
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                params.width = 80;
                params.height = 80;
                view.setLayoutParams(params);
                view.requestLayout();
                return view;
            }
        });
        grid_colorlist.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int i, long l) {
                Activity_imageEdit.this.picked_color = ((Integer) adapterView.getItemAtPosition(i)).intValue();
                Activity_imageEdit.this.edittext.setTextColor(Activity_imageEdit.this.picked_color);
                textView.setTextColor(Activity_imageEdit.this.picked_color);
            }
        });
        img_keyboard = findViewById(R.id.img_keyboard);
        img_keyboard.setImageResource(R.drawable.ic_pen_press);
        img_keyboard.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ((InputMethodManager) Activity_imageEdit.this.getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(Activity_imageEdit.this.edittext, 2);
                ll_fontlist.setVisibility(View.GONE);
                ll_colorlist.setVisibility(View.GONE);
                img_keyboard.setImageResource(R.drawable.ic_pen_press);
                img_fontstyle.setImageResource(R.drawable.ic_font_unpress);
                img_color.setImageResource(R.drawable.ic_color_unpress);
                img_gravity.setImageResource(R.drawable.ic_align_unpress);

            }
        });
        img_fontstyle = findViewById(R.id.img_fontstyle);
        img_fontstyle.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ll_fontlist.setVisibility(View.VISIBLE);
                ll_colorlist.setVisibility(View.GONE);
                img_fontstyle.setImageResource(R.drawable.ic_font_press);
                img_keyboard.setImageResource(R.drawable.ic_pen_unpress);
                img_color.setImageResource(R.drawable.ic_color_unpress);
                img_gravity.setImageResource(R.drawable.ic_align_unpress);
                ((InputMethodManager) Activity_imageEdit.this.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(Activity_imageEdit.this.edittext.getWindowToken(), 0);
            }
        });
        img_color = findViewById(R.id.img_color);
        img_color.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ((InputMethodManager) Activity_imageEdit.this.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(Activity_imageEdit.this.edittext.getWindowToken(), 0);
                ll_colorlist.setVisibility(View.VISIBLE);
                ll_fontlist.setVisibility(View.GONE);
                img_color.setImageResource(R.drawable.ic_color_press);
                img_fontstyle.setImageResource(R.drawable.ic_font_unpress);
                img_keyboard.setImageResource(R.drawable.ic_pen_unpress);
                img_gravity.setImageResource(R.drawable.ic_align_unpress);
            }
        });
        img_gravity = findViewById(R.id.img_gravity);
        img_gravity.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                img_gravity.setImageResource(R.drawable.ic_align_press);
                img_color.setImageResource(R.drawable.ic_color_unpress);
                img_fontstyle.setImageResource(R.drawable.ic_font_unpress);
                img_keyboard.setImageResource(R.drawable.ic_pen_unpress);
                if (w == 0) {
                    w = 1;
                    img_gravity.setImageDrawable(getResources().getDrawable(R.drawable.ic_align_press));
                    edittext.setGravity(5);
                    textView.setGravity(5);
                } else if (w == 1) {
                    img_gravity.setImageDrawable(getResources().getDrawable(R.drawable.ic_align_press));
                    edittext.setGravity(3);
                    textView.setGravity(3);
                    w = 2;
                } else if (w == 2) {
                    w = 0;
                    img_gravity.setImageDrawable(getResources().getDrawable(R.drawable.ic_align_press));
                    edittext.setGravity(17);
                    textView.setGravity(17);
                }
            }
        });
        img_done = findViewById(R.id.img_done);
        ivCancle = findViewById(R.id.iv_cancel);
        final TextView txtEnteredText = findViewById(R.id.txtEntered_Text);
        txtEnteredText.setDrawingCacheEnabled(true);
        img_done.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Activity_imageEdit.this.input_method_manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                String st = Activity_imageEdit.this.edittext.getText().toString();
                if (st.isEmpty()) {
                    Toast.makeText(Activity_imageEdit.this, "text empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                txtEnteredText.setText(st);
                txtEnteredText.setTypeface(Activity_imageEdit.this.typeface);
                txtEnteredText.setTextColor(Activity_imageEdit.this.picked_color);
                txtEnteredText.setGravity(17);
                ImageView textImg = new ImageView(Activity_imageEdit.this);
                txtEnteredText.buildDrawingCache();
                textImg.setImageBitmap(txtEnteredText.getDrawingCache());
                Activity_imageEdit.text_bitmap = Activity_imageEdit.loadBitmapFromView(textImg);
                Activity_imageEdit.text_bitmap = Activity_imageEdit.this.CropBitmapTransparency(Activity_imageEdit.text_bitmap);
                txtEnteredText.setDrawingCacheEnabled(false);
                ((InputMethodManager) Activity_imageEdit.this.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(Activity_imageEdit.this.edittext.getWindowToken(), 0);
                final CustomTextView stickerTextView = new CustomTextView(Activity_imageEdit.this);
                stickerTextView.setBitmap(Activity_imageEdit.text_bitmap);
                frameLayout.addView(stickerTextView, new FrameLayout.LayoutParams(-1, -1, 17));
                Activity_imageEdit.this.stickers.add(stickerTextView);
                stickerTextView.setInEdit(true);
                Activity_imageEdit.this.setCurrentEditForText(stickerTextView);
                stickerTextView.setOperationListener(new OperationListener() {
                    public void onDeleteClick() {
                        if (Activity_imageEdit.this.stickers != null) {
                            Activity_imageEdit.this.stickers.remove(stickerTextView);
                            frameLayout.removeView(stickerTextView);
                        }
                    }

                    public void onEdit(CustomTextView customTextView) {
                        Activity_imageEdit.this.current_textView.setInEdit(false);
                        Activity_imageEdit.this.current_textView = customTextView;
                        Activity_imageEdit.this.current_textView.setInEdit(true);
                    }

                    public void onTop(CustomTextView customTextView) {
                    }
                });
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                llItem.setVisibility(View.VISIBLE);
                ll_linear.setVisibility(View.VISIBLE);
                llTextBottom.setVisibility(View.GONE);

            }
        });
        ivCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                llItem.setVisibility(View.VISIBLE);
                ll_linear.setVisibility(View.VISIBLE);
                llTextBottom.setVisibility(View.GONE);
            }
        });
    }

    protected void onResume() {
        super.onResume();
    }

    private void setCurrentEditForText(CustomTextView customTextView) {
        if (this.current_textView != null) {
            this.current_textView.setInEdit(false);
        }
        this.current_textView = customTextView;
        customTextView.setInEdit(true);
    }

    Bitmap CropBitmapTransparency(Bitmap sourceBitmap) {
        int minX = sourceBitmap.getWidth();
        int minY = sourceBitmap.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < sourceBitmap.getHeight(); y++) {
            for (int x = 0; x < sourceBitmap.getWidth(); x++) {
                if (((sourceBitmap.getPixel(x, y) >> 24) & 255) > 0) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }
        if (maxX < minX || maxY < minY) {
            return null;
        }
        return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1,
                (maxY - minY) + 1);
    }

    public static Bitmap loadBitmapFromView(View v) {
        if (v.getMeasuredHeight() <= 0) {
            v.measure(-2, -2);
            bitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                    v.getMeasuredHeight(), Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(canvas);
            return bitmap;
        }
        bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(canvas);
        return bitmap;
    }

    public static ArrayList HSVColors() {
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i <= 360; i += 20) {
            list.add(HSVColor(i, 1.0f, 1.0f));
        }
        for (int j = 0; j <= 360; j += 20) {
            list.add(HSVColor(j, 0.25f, 1.0f));
            list.add(HSVColor(j, 0.5f, 1.0f));
            list.add(HSVColor(j, 0.75f, 1.0f));
        }
        for (int k = 0; k <= 360; k += 20) {
            list.add(HSVColor(k, 1.0f, 0.5f));
            list.add(HSVColor(k, 1.0f, 0.75f));
        }
        for (float n = 0.0f; n <= 1.0f; n += 0.1f) {
            list.add(HSVColor(0.0f, 0.0f, n));
        }
        return list;
    }

    public static int HSVColor(float hue, float saturation, float black) {
        return Color.HSVToColor(255, new float[]{hue, saturation, black});
    }

    @Override
    public void onBackPressed() {
        ShowBackDialog();
    }

    private void ShowBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message).setCancelable(false)
                .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(activity, MainActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Stay here", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void SetSticker(ArrayList<Integer> StickerList) {
        stickerAdapter = new StickerAdapter(activity, StickerList);
        rvSticker.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvSticker.setAdapter(stickerAdapter);
        stickerAdapter.notifyDataSetChanged();
    }


    public void FrameVisible() {
        rvEffect.setVisibility(View.GONE);
        seekbar_opacity.setVisibility(View.GONE);
        brightness_seekbar.setVisibility(View.GONE);
        llcategoryMain.setVisibility(View.GONE);
        layoutFrame.setVisibility(View.VISIBLE);
        llStickerCategory.setVisibility(View.VISIBLE);
        frameAdapter = new FrameAdapter(activity, list_frame1);
        rvFrame.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvFrame.setAdapter(frameAdapter);

        IsSticker = true;
        SetSticker(list_frame5);

        ivSticker.setImageResource(R.drawable.ic_smile_press);
        ivEraser.setImageResource(R.drawable.ic_eraseredit_unpress);
        ivEffect.setImageResource(R.drawable.ic_effect_unpress);
        ivBrightness.setImageResource(R.drawable.ic_brightness_unpress);
        ivFlip.setImageResource(R.drawable.ic_flip_unpress);
        ivOpacity.setImageResource(R.drawable.ic_opacity_unpress);
        ivtext.setImageResource(R.drawable.ic_text_unpress);

    }

    private void BottomSheet() {
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                /*switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        llTextBottom.setVisibility(View.VISIBLE);
                        llItem.setVisibility(View.GONE);
                        ll_linear.setVisibility(View.GONE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        llItem.setVisibility(View.VISIBLE);
                        ll_linear.setVisibility(View.VISIBLE);
                        llTextBottom.setVisibility(View.GONE);
                    }
                    break;
                }*/
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        IsSticker = true;
        return true;
    }
}
