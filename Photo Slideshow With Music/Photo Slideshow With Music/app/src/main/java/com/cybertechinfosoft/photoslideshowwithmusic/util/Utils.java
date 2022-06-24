package com.cybertechinfosoft.photoslideshowwithmusic.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.data.MusicData;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;


public class Utils {
    public static final String CAMERA_IMAGE = "imglist";
    public static String CAMERA_IMG_PATH = "";
    public static final int DEFAULT_ALPHA = 50;
    public static final int DEFAULT_ANGLE = 90;
    public static final int DEFAULT_TINT = -16777216;
    public static final int END_COLOR = -1;
    public static final String FFPAUDIO = "ffpaudio";
    public static long FIRST_IMG_TIME = -1L;
    public static final Utils INSTANCE;
    public static long LAST_IMG_TIME = -1L;
    public static final String Privacy_policy = "";
    private static final int RequestPermissionCode = 222;
    public static final int START_COLOR = -16777216;
    public static final int TIDE_COUNT = 3;
    public static final int TIDE_HEIGHT_DP = 30;
    public static int TaskCount = 0;
    public static String autostart_app_name = "";
    public static ArrayList<String> cameraImageUri;
    public static ArrayList<String> capturedImagePath;
    public static String[] forExit;
    public static int height = 0;
    static int i = 0;
    public static boolean isBackCamAvailable = true;
    public static boolean isFromCameraNotification = false;
    public static boolean isFrontCamAvailable = true;
    public static boolean isOrignal = true;
    public static boolean isSSMrunning = false;
    public static int isUserAggry = -1;
    public static boolean isVideoCreationRunning = false;
    private static String json_audio_file = "https://raw.githubusercontent.com/CyberyechInfosoft/onlinesongs/master/onlinesong.json";
    public static ArrayList<AsynkModel> mAsynkList;
    public static ArrayList<ProgressModel> mPrgModel;
    public static ArrayList<String> selectedImageUri;
    public static boolean showDialog = false;
    public static int width;
    public ArrayList<String> AllAudioFileLink;
    public ArrayList<String> LocalFileName;
    EPreferences MyPref;
    public static boolean watchadCheck = false;
    static {
        INSTANCE = new Utils();
        Utils.mPrgModel = new ArrayList<ProgressModel>();
        Utils.mAsynkList = new ArrayList<AsynkModel>();
        Utils.capturedImagePath = new ArrayList<String>();
        Utils.selectedImageUri = new ArrayList<String>();
        Utils.cameraImageUri = new ArrayList<String>();
    }

    public static void applyFontToMenuItem(final Context context, final MenuItem menuItem) {
        final Typeface fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Comfortaa-Regular.ttf");
        final SpannableString title = new SpannableString(menuItem.getTitle());
        title.setSpan((Object) new CustomTypefaceSpan("", fromAsset), 0, title.length(), 18);
        menuItem.setTitle((CharSequence) title);
    }

    public static boolean checkConnectivity(final Context context, final boolean b) {
        if (isNetworkConnected(context)) {
            return true;
        }
        if (b) {
            Toast.makeText(context, (CharSequence) "Data/Wifi Not Available", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static boolean checkPermission(final Context context) {
        return ContextCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") == 0;
    }

    public static float dp2px(final Resources resources, final float n) {
        return n * resources.getDisplayMetrics().density + 0.5f;
    }


    public static void getScreenShot(final View view, final Context context, final View view2) {
        view.setDrawingCacheEnabled(true);
        final Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view2.setBackgroundDrawable(new BitmapDrawable(FastBlur.blurBitmap(FastBlur.blurBitmap(bitmap, 25, context), 25, context)));
    }

    public static void hideKeyBoard(final Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        final View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 2);
        }
    }

    public static boolean isLmpMR1() {
        return Build.VERSION.SDK_INT == 22;
    }

    public static boolean isLmpMR1OrAbove() {
        return Build.VERSION.SDK_INT >= 22;
    }

    public static boolean isLmpOrAbove() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean isNetworkConnected(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static void permissionDailog(final Context context) {
        final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder(context);
        final LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        alertDialog$Builder.setCancelable(true);
        final View inflate = layoutInflater.inflate(R.layout.permission_denied, null);
        alertDialog$Builder.setView(inflate);
        final Button button = (Button) inflate.findViewById(R.id.btnGotoSetting);
        final Button button2 = (Button) inflate.findViewById(R.id.btnExitApp);
        final AlertDialog create = alertDialog$Builder.create();
        button.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.i = 1;
                create.dismiss();
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                context.startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create.dismiss();
                ((Activity) context).finish();
            }
        });
        create.show();
    }

    public static void requestPermission(final Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, 222);
    }

    public static void setFont(final Activity activity, final int n) {
        final View viewById = activity.findViewById(n);
        final Typeface fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/Comfortaa-Regular.ttf");
        if (viewById instanceof TextView) {
            ((TextView) activity.findViewById(n)).setTypeface(fromAsset);
            return;
        }
        if (viewById instanceof Button) {
            ((Button) activity.findViewById(n)).setTypeface(fromAsset);
        }
    }
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static void setFont(final Activity activity, final View view) {
        final Typeface fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/Comfortaa-Regular.ttf");
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(fromAsset);
            return;
        }
        if (view instanceof Button) {
            ((Button) view).setTypeface(fromAsset);
            return;
        }
        if (view instanceof EditText) {
            ((EditText) view).setTypeface(fromAsset);
            return;
        }
        if (view instanceof CheckBox) {
            ((CheckBox) view).setTypeface(fromAsset);
        }
    }

    public static void setFont(final Activity activity, final TextView textView) {
        textView.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Comfortaa-Regular.ttf"));
    }

    @SuppressLint({"NewApi"})
    public static void setStatusBarColor(final Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            final Window window = activity.getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static void slideDown(final Context context, final View view) {
        final Animation loadAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_down);
        view.setVisibility(View.GONE);
        view.startAnimation(loadAnimation);
    }

    public static void slideUp(final Context context, final View view) {
        final Animation loadAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_up);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(loadAnimation);
    }

    public static float sp2px(final Resources resources, final float n) {
        return n * resources.getDisplayMetrics().scaledDensity;
    }

    public void fillJsonData() {
        String makeServiceCall = new HttpHandler().makeServiceCall(json_audio_file);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Response from url: ");
        stringBuilder.append(makeServiceCall);
        if (makeServiceCall != null) {
            try {
                JSONArray jSONArray = new JSONObject(makeServiceCall).getJSONArray("audio_list");
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    String string = jSONObject.getString("url");
                    String string2 = jSONObject.getString("display_name");
                    this.AllAudioFileLink.add(string);
                    this.LocalFileName.add(string2);
                }
            } catch (JSONException e) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Json parsing error: ");
                stringBuilder.append(e.getMessage());
            }
        } else {
        }
    }

    public final String getAudioFolderPath() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getOutputPath());
        sb.append("ffpaudio");
        sb.append(File.separator);
        final String string = sb.toString();
        final File file = new File(string);
        if (!file.exists()) {
            file.mkdirs();
        }
        return string;
    }

    public ArrayList<MusicData> getOnlineAudioFiles(Context context) {
        this.MyPref = EPreferences.getInstance(context);
        this.AllAudioFileLink = new ArrayList();
        this.LocalFileName = new ArrayList();
        final ArrayList<MusicData> list = new ArrayList();
        fillJsonData();
        String audioFolderPath = getAudioFolderPath();
        for (int i = 0; i < this.AllAudioFileLink.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(audioFolderPath);
            stringBuilder.append((String) this.LocalFileName.get(i));
            if (new File(stringBuilder.toString()).exists()) {
                for (int i2 = 0; i2 < mPrgModel.size(); i2++) {
                    if (((ProgressModel) mPrgModel.get(i2)).Uri.equals(this.AllAudioFileLink.get(i))) {
                        MusicData musicData = new MusicData();
                        musicData.track_displayName = (String) this.LocalFileName.get(i);
                        musicData.isAvailableOffline = ((ProgressModel) mPrgModel.get(i2)).isAvailableOffline;
                        musicData.isDownloading = ((ProgressModel) mPrgModel.get(i2)).isDownloading;
                        musicData.SongDownloadUri = ((ProgressModel) mPrgModel.get(i2)).Uri;
                        list.add(musicData);
                        break;
                    }
                }
                if (mPrgModel.size() == 0) {
                    ArrayList allURL = this.MyPref.getAllURL();
                    if (allURL != null && allURL.size() > 0) {
                        for (int i3 = 0; i3 < allURL.size(); i3++) {
                            if (((String) allURL.get(i3)).equals(this.AllAudioFileLink.get(i))) {
                                MusicData musicData2 = new MusicData();
                                musicData2.track_displayName = (String) this.LocalFileName.get(i);
                                musicData2.isAvailableOffline = false;
                                musicData2.isDownloading = false;
                                musicData2.SongDownloadUri = (String) this.AllAudioFileLink.get(i);
                                list.add(musicData2);
                            }
                        }
                    }
                }
            } else {
                MusicData musicData3 = new MusicData();
                musicData3.track_displayName = (String) this.LocalFileName.get(i);
                musicData3.isAvailableOffline = false;
                musicData3.SongDownloadUri = (String) this.AllAudioFileLink.get(i);
                list.add(musicData3);
            }
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("mMusicDatas.size() = ");
        stringBuilder2.append(list.size());
        return list;
    }


    public final String getOutputPath() {
        final StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.APP_DIRECTORY.getAbsolutePath());
        sb.append(File.separator);
        final String string = sb.toString();
        final File file = new File(string);
        if (!file.exists()) {
            file.mkdirs();
        }
        return string;
    }

    public static class Utility {
        public static boolean isNetworkAvailable(Context ctx) {
            ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        public static ArrayList<String> listIcon;
        public static ArrayList<String> listName;
        public static ArrayList<String> listUrl;
        public static ArrayList<String> listStatus;
        public static ArrayList<String> listIcon1;
        public static ArrayList<String> listName1;
        public static ArrayList<String> listUrl1;
        public static ArrayList<String> listStatus1;
        static {
            listIcon = new ArrayList();
            listName = new ArrayList();
            listUrl = new ArrayList();
            listStatus = new ArrayList();

            listIcon1 = new ArrayList();
            listName1 = new ArrayList();
            listUrl1 = new ArrayList();
            listStatus1 = new ArrayList();
        }
    }
}
