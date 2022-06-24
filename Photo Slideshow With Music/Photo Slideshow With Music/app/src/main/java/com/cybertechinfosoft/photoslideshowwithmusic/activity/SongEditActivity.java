package com.cybertechinfosoft.photoslideshowwithmusic.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.audiocutter.soundfile.CheapSoundFile;
import com.cybertechinfosoft.photoslideshowwithmusic.MyApplication;
import com.cybertechinfosoft.photoslideshowwithmusic.R;
import com.cybertechinfosoft.photoslideshowwithmusic.data.MusicData;
import com.cybertechinfosoft.photoslideshowwithmusic.util.SongMetadataReader;
import com.cybertechinfosoft.photoslideshowwithmusic.util.Utils;
import com.cybertechinfosoft.photoslideshowwithmusic.video.FileUtils;
import com.cybertechinfosoft.photoslideshowwithmusic.view.MarkerView;
import com.cybertechinfosoft.photoslideshowwithmusic.view.WaveformView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class SongEditActivity extends AppCompatActivity implements MarkerView.MarkerListener, WaveformView.WaveformListener {
    Activity activity = SongEditActivity.this;
    public static final String EDIT = "com.ringdroid.action.EDIT";
    public static boolean isFromSdcard;
    private boolean isFromItemClick;
    private MusicAdapter mAdapter;
    private AlertDialog mAlertDialog;
    private String mArtist;
    boolean mCanSeekAccurately;
    private float mDensity;
    private MarkerView mEndMarker;
    private int mEndPos;
    private TextView mEndText;
    private boolean mEndVisible;
    private String mExtension;
    private ImageButton mFfwdButton;
    private View.OnClickListener mFfwdListener;
    private File mFile;
    private String mFilename;
    private boolean mFinishActivity;
    private int mFlingVelocity;
    private Handler mHandler;
    private boolean mIsPlaying;
    private boolean mKeyDown;
    private int mLastDisplayedEndPos;
    private int mLastDisplayedStartPos;
    private Thread mLoadSoundFileThread;
    private boolean mLoadingKeepGoing;
    private long mLoadingLastUpdateTime;
    private int mMarkerBottomOffset;
    private int mMarkerLeftInset;
    private int mMarkerRightInset;
    private int mMarkerTopOffset;
    private int mMaxPos;
    private ArrayList<MusicData> mMusicDatas;
    private RecyclerView mMusicList;
    private int mOffset;
    private int mOffsetGoal;
    private ImageButton mPlayButton;
    private int mPlayEndMsec;
    private View.OnClickListener mPlayListener;
    private int mPlayStartMsec;
    private MediaPlayer mPlayer;
    private ProgressDialog mProgressDialog;
    private Thread mRecordAudioThread;
    private String mRecordingFilename;
    private Uri mRecordingUri;
    private ImageButton mRewindButton;
    private View.OnClickListener mRewindListener;
    private Thread mSaveSoundFileThread;
    private CheapSoundFile mSoundFile;
    private MarkerView mStartMarker;
    private int mStartPos;
    private TextView mStartText;
    private boolean mStartVisible;
    private TextWatcher mTextWatcher;
    private Runnable mTimerRunnable;
    private String mTitle;
    private boolean mTouchDragging;
    private int mTouchInitialEndPos;
    private int mTouchInitialOffset;
    private int mTouchInitialStartPos;
    private float mTouchStart;
    private long mWaveformTouchStartMsec;
    private WaveformView mWaveformView;
    private int mWidth;
    private MusicData selectedMusicData;
    private Toolbar toolbar;

    public SongEditActivity() {
        this.mFilename = "record";
        this.isFromItemClick = false;
        this.mExtension = ".mp3";
        this.mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                if (SongEditActivity.this.mStartPos != SongEditActivity.this.mLastDisplayedStartPos && !SongEditActivity.this.mStartText.hasFocus()) {
                    SongEditActivity.this.mStartText.setText((CharSequence) SongEditActivity.this.formatTime(SongEditActivity.this.mStartPos));
                    SongEditActivity.this.mLastDisplayedStartPos = SongEditActivity.this.mStartPos;
                }
                if (SongEditActivity.this.mEndPos != SongEditActivity.this.mLastDisplayedEndPos && !SongEditActivity.this.mEndText.hasFocus()) {
                    SongEditActivity.this.mEndText.setText((CharSequence) SongEditActivity.this.formatTime(SongEditActivity.this.mEndPos));
                    SongEditActivity.this.mLastDisplayedEndPos = SongEditActivity.this.mEndPos;
                }
                SongEditActivity.this.mHandler.postDelayed(SongEditActivity.this.mTimerRunnable, 100L);
            }
        };
        this.mPlayListener = (View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                SongEditActivity.this.onPlay(SongEditActivity.this.mStartPos);
            }
        };
        this.mRewindListener = (View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (SongEditActivity.this.mIsPlaying) {
                    int access$3700;
                    if ((access$3700 = SongEditActivity.this.mPlayer.getCurrentPosition() - 5000) < SongEditActivity.this.mPlayStartMsec) {
                        access$3700 = SongEditActivity.this.mPlayStartMsec;
                    }
                    SongEditActivity.this.mPlayer.seekTo(access$3700);
                    return;
                }
                SongEditActivity.this.mStartMarker.requestFocus();
                SongEditActivity.this.markerFocus(SongEditActivity.this.mStartMarker);
            }
        };
        this.mFfwdListener = (View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View view) {
                if (SongEditActivity.this.mIsPlaying) {
                    int access$3800;
                    if ((access$3800 = SongEditActivity.this.mPlayer.getCurrentPosition() + 5000) > SongEditActivity.this.mPlayEndMsec) {
                        access$3800 = SongEditActivity.this.mPlayEndMsec;
                    }
                    SongEditActivity.this.mPlayer.seekTo(access$3800);
                    return;
                }
                SongEditActivity.this.mEndMarker.requestFocus();
                SongEditActivity.this.markerFocus(SongEditActivity.this.mEndMarker);
            }
        };
        this.mTextWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            public void afterTextChanged(Editable s) {
                if (SongEditActivity.this.mStartText.hasFocus()) {
                    try {
                        SongEditActivity.this.mStartPos = SongEditActivity.this.mWaveformView
                                .secondsToPixels(Double
                                        .parseDouble(SongEditActivity.this.mStartText
                                                .getText().toString()));
                        SongEditActivity.this.updateDisplay();
                    } catch (NumberFormatException e) {
                    }
                }
                if (SongEditActivity.this.mEndText.hasFocus()) {
                    try {
                        SongEditActivity.this.mEndPos = SongEditActivity.this.mWaveformView
                                .secondsToPixels(Double
                                        .parseDouble(SongEditActivity.this.mEndText
                                                .getText().toString()));
                        SongEditActivity.this.updateDisplay();
                    } catch (NumberFormatException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        };
    }

    private void afterSavingRingtone(final CharSequence charSequence, final String track_data, final File file, final int n) {
        if (file.length() <= 512L) {
            file.delete();
            new AlertDialog.Builder((Context) this).setTitle(R.string.too_small_error).setMessage(R.string.alert_ok_button).setPositiveButton(R.string.alert_ok_button, (DialogInterface.OnClickListener) null).setCancelable(false).show();
            return;
        }
        final long length = file.length();
        final StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append((Object) this.getResources().getText(R.string.artist_name));
        final String string = sb.toString();
        final ContentValues contentValues = new ContentValues();
        contentValues.put("_data", track_data);
        contentValues.put("title", charSequence.toString());
        contentValues.put("_size", length);
        contentValues.put("mime_type", "audio/mpeg");
        contentValues.put("artist", string);
        contentValues.put("duration", n);
        contentValues.put("is_music", true);
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("duaration is ");
        sb2.append(n);
        this.getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(track_data), contentValues);
        this.selectedMusicData.track_data = track_data;
        this.selectedMusicData.track_duration = n * 1000;
        MyApplication.getInstance().setMusicData(this.selectedMusicData);
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("SongEdit aftersave track_data");
        sb3.append(this.selectedMusicData.track_data);
        this.setResult(-1);
        this.finish();
    }

    private void bindView() {
        this.mMusicList = (RecyclerView) this.findViewById(R.id.rvMusicList);
        this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);
    }

    private void closeThread(final Thread thread) {
        if (thread == null || !thread.isAlive()) {
            return;
        }
        try {
            thread.join();
        } catch (InterruptedException ex) {
        }
    }

    private void PutAnalyticsEvent() {
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SongEditActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void enableDisableButtons() {
        if (this.mIsPlaying) {
            this.mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
            this.mPlayButton.setContentDescription(this.getResources().getText(R.string.stop));
            return;
        }
        this.mPlayButton.setImageResource(android.R.drawable.ic_media_play);
        this.mPlayButton.setContentDescription(this.getResources().getText(R.string.play));
    }

    private void finishOpeningSoundFile() {
        this.mWaveformView.setSoundFile(this.mSoundFile);
        this.mWaveformView.recomputeHeights(this.mDensity);
        this.mMaxPos = this.mWaveformView.maxPos();
        this.mLastDisplayedStartPos = -1;
        this.mLastDisplayedEndPos = -1;
        this.mTouchDragging = false;
        this.mOffset = 0;
        this.mOffsetGoal = 0;
        this.mFlingVelocity = 0;
        this.resetPositions();
        if (this.mEndPos > this.mMaxPos) {
            this.mEndPos = this.mMaxPos;
        }
        this.updateDisplay();
        if (this.isFromItemClick) {
            this.onPlay(this.mStartPos);
        }
    }

    private String formatDecimal(final double n) {
        final int n2 = (int) n;
        final int n3 = (int) ((n - n2) * 100.0 + 0.5);
        int n4 = n2;
        int n5 = n3;
        if (n3 >= 100) {
            final int n6 = n2 + 1;
            final int n7 = n3 - 100;
            n4 = n6;
            if ((n5 = n7) < 10) {
                n5 = n7 * 10;
                n4 = n6;
            }
        }
        if (n5 < 10) {
            final StringBuilder sb = new StringBuilder();
            sb.append(n4);
            sb.append(".0");
            sb.append(n5);
            return sb.toString();
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(n4);
        sb2.append(".");
        sb2.append(n5);
        return sb2.toString();
    }

    private String formatTime(final int n) {
        if (this.mWaveformView != null && this.mWaveformView.isInitialized()) {
            return this.formatDecimal(this.mWaveformView.pixelsToSeconds(n));
        }
        return "";
    }

    private long getCurrentTime() {
        return System.nanoTime() / 1000000L;
    }

    private String getExtensionFromFilename(final String s) {
        return s.substring(s.lastIndexOf(46), s.length());
    }

    private void handleFatalError(final CharSequence charSequence, final CharSequence charSequence2, final Exception ex) {
    }

    private void handlePause() {
        synchronized (this) {
            if (this.mPlayer != null && this.mPlayer.isPlaying()) {
                this.mPlayer.pause();
            }
            this.mWaveformView.setPlayback(-1);
            this.mIsPlaying = false;
            this.enableDisableButtons();
        }
    }

    private void init() {
        this.setSupportActionBar(this.toolbar);
        final TextView textView = (TextView) this.toolbar.findViewById(R.id.toolbar_title);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView.setText((CharSequence) this.getString(R.string.select_music));
        Utils.setFont((Activity) this, textView);
        new LoadMusics().execute(new Void[0]);
    }

    private void loadFromFile() {
        this.mFile = new File(this.mFilename);
        this.mExtension = this.getExtensionFromFilename(this.mFilename);
        final SongMetadataReader songMetadataReader = new SongMetadataReader((Activity) this, this.mFilename);
        this.mTitle = songMetadataReader.mTitle;
        this.mArtist = songMetadataReader.mArtist;
        String title;
        final String s = title = this.mTitle;
        if (this.mArtist != null) {
            title = s;
            if (this.mArtist.length() > 0) {
                final StringBuilder sb = new StringBuilder();
                sb.append(s);
                sb.append(" - ");
                sb.append(this.mArtist);
                title = sb.toString();
            }
        }
        this.setTitle((CharSequence) title);
        this.mLoadingLastUpdateTime = this.getCurrentTime();
        this.mLoadingKeepGoing = true;
        this.mFinishActivity = false;
        (this.mProgressDialog = new ProgressDialog((Context) this, R.style.Theme_MovieMaker_AlertDialog)).setProgressStyle(1);
        this.mProgressDialog.setTitle(R.string.progress_dialog_loading);
        this.mProgressDialog.setCancelable(true);
        this.mProgressDialog.setOnCancelListener((DialogInterface.OnCancelListener) new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialogInterface) {
                SongEditActivity.this.mLoadingKeepGoing = false;
            }
        });
        this.mProgressDialog.show();
        final CheapSoundFile.ProgressListener cheapSoundFile$ProgressListener = (CheapSoundFile.ProgressListener) new CheapSoundFile.ProgressListener() {
            public boolean reportProgress(final double n) {
                final long access$1200 = SongEditActivity.this.getCurrentTime();
                if (access$1200 - SongEditActivity.this.mLoadingLastUpdateTime > 100L) {
                    SongEditActivity.this.mProgressDialog.setProgress((int) (SongEditActivity.this.mProgressDialog.getMax() * n));
                    SongEditActivity.this.mLoadingLastUpdateTime = access$1200;
                }
                return SongEditActivity.this.mLoadingKeepGoing;
            }
        };
        this.mCanSeekAccurately = false;
        new Thread() {
            @Override
            public void run() {
                SongEditActivity.this.mCanSeekAccurately = SeekTest.CanSeekAccurately(SongEditActivity.this.getPreferences(0));
                try {
                    final MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(SongEditActivity.this.mFile.getAbsolutePath());
                    mediaPlayer.setAudioStreamType(3);
                    mediaPlayer.prepare();
                    SongEditActivity.this.mPlayer = mediaPlayer;
                } catch (final IOException ex) {
                    SongEditActivity.this.mHandler.post((Runnable) new Runnable() {
                        @Override
                        public void run() {
                            SongEditActivity.this.handleFatalError("ReadError", SongEditActivity.this.getResources().getText(R.string.read_error), ex);
                        }
                    });
                }
            }
        }.start();
        (this.mLoadSoundFileThread = new Thread() {
            @Override
            public void run() {
                try {
                    SongEditActivity.this.mSoundFile = CheapSoundFile.create(SongEditActivity.this.mFile.getAbsolutePath(), cheapSoundFile$ProgressListener);
                    if (SongEditActivity.this.mSoundFile == null) {
                        SongEditActivity.this.mProgressDialog.dismiss();
                        final String[] split = SongEditActivity.this.mFile.getName().toLowerCase(Locale.ENGLISH).split("\\.");
                        final String s;
                        if (split.length < 2) {
                            s = SongEditActivity.this.getResources().getString(R.string.no_extension_error);
                        } else {
                            final StringBuilder sb = new StringBuilder();
                            sb.append(SongEditActivity.this.getResources().getString(R.string.bad_extension_error));
                            sb.append(" ");
                            sb.append(split[split.length - 1]);
                            s = sb.toString();
                        }
                        SongEditActivity.this.mHandler.post((Runnable) new Runnable() {
                            @Override
                            public void run() {
                                SongEditActivity.this.showFinalAlert(new Exception(), s);
                            }
                        });
                        return;
                    }
                    SongEditActivity.this.mProgressDialog.dismiss();
                    if (SongEditActivity.this.mLoadingKeepGoing) {
                        SongEditActivity.this.mHandler.post((Runnable) new Runnable() {
                            @Override
                            public void run() {
                                SongEditActivity.this.finishOpeningSoundFile();
                            }
                        });
                        return;
                    }
                    if (SongEditActivity.this.mFinishActivity) {
                        SongEditActivity.this.finish();
                    }
                } catch (final Exception ex) {
                    SongEditActivity.this.mProgressDialog.dismiss();
                    ex.printStackTrace();
                    SongEditActivity.this.mHandler.post((Runnable) new Runnable() {
                        @Override
                        public void run() {
                            SongEditActivity.this.showFinalAlert(ex, SongEditActivity.this.getResources().getText(R.string.read_error));
                        }
                    });
                }
            }
        }).start();
    }

    private void loadGui() {
        this.setContentView(R.layout.activity_add_music);
        this.bindView();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.mDensity = displayMetrics.density;
        this.mMarkerLeftInset = (int) (this.mDensity * 46.0f);
        this.mMarkerRightInset = (int) (this.mDensity * 48.0f);
        this.mMarkerTopOffset = (int) (this.mDensity * 10.0f);
        this.mMarkerBottomOffset = (int) (this.mDensity * 10.0f);
        (this.mStartText = (TextView) this.findViewById(R.id.starttext)).addTextChangedListener(this.mTextWatcher);
        (this.mEndText = (TextView) this.findViewById(R.id.endtext)).addTextChangedListener(this.mTextWatcher);
        (this.mPlayButton = (ImageButton) this.findViewById(R.id.play)).setOnClickListener(this.mPlayListener);
        (this.mRewindButton = (ImageButton) this.findViewById(R.id.rew)).setOnClickListener(this.mRewindListener);
        (this.mFfwdButton = (ImageButton) this.findViewById(R.id.ffwd)).setOnClickListener(this.mFfwdListener);
        this.enableDisableButtons();
        (this.mWaveformView = (WaveformView) this.findViewById(R.id.waveform)).setListener((WaveformView.WaveformListener) this);
        this.mMaxPos = 0;
        this.mLastDisplayedStartPos = -1;
        this.mLastDisplayedEndPos = -1;
        if (this.mSoundFile != null && !this.mWaveformView.hasSoundFile()) {
            this.mWaveformView.setSoundFile(this.mSoundFile);
            this.mWaveformView.recomputeHeights(this.mDensity);
            this.mMaxPos = this.mWaveformView.maxPos();
        }
        (this.mStartMarker = (MarkerView) this.findViewById(R.id.startmarker)).setListener((MarkerView.MarkerListener) this);
        this.mStartMarker.setAlpha(1.0f);
        this.mStartMarker.setFocusable(true);
        this.mStartMarker.setFocusableInTouchMode(true);
        this.mStartVisible = true;
        (this.mEndMarker = (MarkerView) this.findViewById(R.id.endmarker)).setListener((MarkerView.MarkerListener) this);
        this.mEndMarker.setAlpha(1.0f);
        this.mEndMarker.setFocusable(true);
        this.mEndMarker.setFocusableInTouchMode(true);
        this.mEndVisible = true;
        this.updateDisplay();
    }

    private String makeRingtoneFilename(CharSequence charSequence, final String s) {
        FileUtils.TEMP_DIRECTORY_AUDIO.mkdirs();
        final File temp_DIRECTORY_AUDIO = FileUtils.TEMP_DIRECTORY_AUDIO;
        final StringBuilder sb = new StringBuilder();
        sb.append(charSequence);
        sb.append(s);
        File file = new File(temp_DIRECTORY_AUDIO, sb.toString());
        if ((file).exists()) {
            FileUtils.deleteFile(file);
            try {
                (file).createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return (file).getAbsolutePath();
    }

    public static void onAbout(final Activity activity) {
        try {
            String versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
            new AlertDialog.Builder((Context) activity).setTitle(R.string.about_title).setMessage((CharSequence) activity.getString(R.string.about_text)).setPositiveButton(R.string.alert_ok_button, (DialogInterface.OnClickListener) null).setCancelable(false).show();
        } catch (PackageManager.NameNotFoundException ex) {
        }
    }

    private void onPlay(final int n) {
        synchronized (this) {
            if (this.mIsPlaying) {
                this.handlePause();
                return;
            }
            if (this.mPlayer == null) {
                return;
            }
            try {
                this.mPlayStartMsec = this.mWaveformView.pixelsToMillisecs(n);
                if (n < this.mStartPos) {
                    this.mPlayEndMsec = this.mWaveformView.pixelsToMillisecs(this.mStartPos);
                } else if (n > this.mEndPos) {
                    this.mPlayEndMsec = this.mWaveformView.pixelsToMillisecs(this.mMaxPos);
                } else {
                    this.mPlayEndMsec = this.mWaveformView.pixelsToMillisecs(this.mEndPos);
                }
                this.mPlayer.setOnCompletionListener((MediaPlayer.OnCompletionListener) new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(final MediaPlayer mediaPlayer) {
                        SongEditActivity.this.handlePause();
                    }
                });
                this.mIsPlaying = true;
                this.mPlayer.seekTo(this.mPlayStartMsec);
                this.mPlayer.start();
                this.updateDisplay();
                this.enableDisableButtons();
            } catch (Exception ex) {
                this.showFinalAlert(ex, R.string.play_error);
            }
        }
    }

    private void onSave() {
        if (this.mIsPlaying) {
            this.handlePause();
        }
        this.saveRingtone("temp");
    }

    private void resetPositions() {
        this.mStartPos = this.mWaveformView.secondsToPixels(0.0);
        this.mEndPos = this.mWaveformView.secondsToPixels(15.0);
    }

    private void saveRingtone(final CharSequence charSequence) {
        final String ringtoneFilename = this.makeRingtoneFilename(charSequence, ".mp3");
        if (ringtoneFilename == null) {
            this.showFinalAlert(new Exception(), R.string.no_unique_filename);
            return;
        }
        final double pixelsToSeconds = this.mWaveformView.pixelsToSeconds(this.mStartPos);
        final double pixelsToSeconds2 = this.mWaveformView.pixelsToSeconds(this.mEndPos);
        final int secondsToFrames = this.mWaveformView.secondsToFrames(pixelsToSeconds);
        final int secondsToFrames2 = this.mWaveformView.secondsToFrames(pixelsToSeconds2);
        final int n = (int) (pixelsToSeconds2 - pixelsToSeconds + 0.5);
        (this.mProgressDialog = new ProgressDialog((Context) this, R.style.Theme_MovieMaker_AlertDialog)).setProgressStyle(0);
        this.mProgressDialog.setMessage((CharSequence) this.getString(R.string.please_wait_));
        this.mProgressDialog.setIndeterminate(true);
        this.mProgressDialog.setCancelable(false);
        this.mProgressDialog.show();
        new Thread() {
            @Override
            public void run() {
                final File file = new File(ringtoneFilename);
                try {
                    SongEditActivity.this.mSoundFile.WriteFile(file, secondsToFrames, secondsToFrames2 - secondsToFrames);
                    CheapSoundFile.create(ringtoneFilename, (CheapSoundFile.ProgressListener) new CheapSoundFile.ProgressListener() {
                        public boolean reportProgress(final double n) {
                            return true;
                        }
                    });
                    SongEditActivity.this.mProgressDialog.dismiss();
                    SongEditActivity.this.mHandler.post((Runnable) new Runnable() {
                        @Override
                        public void run() {
                            SongEditActivity.this.afterSavingRingtone(charSequence, ringtoneFilename, file, n);
                        }
                    });
                } catch (Exception ex) {
                    SongEditActivity.this.mProgressDialog.dismiss();
                    final CharSequence charSequence;
                    if (ex.getMessage().equals("No space left on device")) {
                        charSequence = SongEditActivity.this.getResources().getText(R.string.no_space_error);
                        ex = null;
                    } else {
                        charSequence = SongEditActivity.this.getResources().getText(R.string.write_error);
                    }
                    final Exception finalEx = ex;
                    SongEditActivity.this.mHandler.post((Runnable) new Runnable() {
                        @Override
                        public void run() {
                            SongEditActivity.this.handleFatalError("WriteError", charSequence, finalEx);
                        }
                    });
                }
            }
        }.start();
    }

    private void setOffsetGoal(final int offsetGoalNoUpdate) {
        this.setOffsetGoalNoUpdate(offsetGoalNoUpdate);
        this.updateDisplay();
    }

    private void setOffsetGoalEnd() {
        this.setOffsetGoal(this.mEndPos - this.mWidth / 2);
    }

    private void setOffsetGoalEndNoUpdate() {
        this.setOffsetGoalNoUpdate(this.mEndPos - this.mWidth / 2);
    }

    private void setOffsetGoalNoUpdate(final int mOffsetGoal) {
        if (this.mTouchDragging) {
            return;
        }
        this.mOffsetGoal = mOffsetGoal;
        if (this.mOffsetGoal + this.mWidth / 2 > this.mMaxPos) {
            this.mOffsetGoal = this.mMaxPos - this.mWidth / 2;
        }
        if (this.mOffsetGoal < 0) {
            this.mOffsetGoal = 0;
        }
    }

    private void setOffsetGoalStart() {
        this.setOffsetGoal(this.mStartPos - this.mWidth / 2);
    }

    private void setOffsetGoalStartNoUpdate() {
        this.setOffsetGoalNoUpdate(this.mStartPos - this.mWidth / 2);
    }

    private void setUpRecyclerView() {
        this.mAdapter = new MusicAdapter(this.mMusicDatas);
        this.mMusicList.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        this.mMusicList.setItemAnimator((RecyclerView.ItemAnimator) new DefaultItemAnimator());
        this.mMusicList.setAdapter((RecyclerView.Adapter) this.mAdapter);
    }

    private void showFinalAlert(final Exception ex, final int n) {
        this.showFinalAlert(ex, this.getResources().getText(n));
    }

    private void showFinalAlert(final Exception ex, final CharSequence message) {
        CharSequence title;
        if (ex != null) {
            title = this.getResources().getText(R.string.alert_title_failure);
            this.setResult(0, new Intent());
        } else {
            title = this.getResources().getText(R.string.alert_title_success);
        }
        new AlertDialog.Builder((Context) this).setTitle(title).setMessage(message).setPositiveButton(R.string.alert_ok_button, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                SongEditActivity.this.finish();
            }
        }).setCancelable(false).show();
    }

    private int trap(final int n) {
        if (n < 0) {
            return 0;
        }
        if (n > this.mMaxPos) {
            return this.mMaxPos;
        }
        return n;
    }

    private synchronized void updateDisplay() {
        int currentPosition;
        int millisecsToPixels;
        if (this.mIsPlaying) {
            currentPosition = this.mPlayer.getCurrentPosition();
            millisecsToPixels = this.mWaveformView.millisecsToPixels(currentPosition);
            this.mWaveformView.setPlayback(millisecsToPixels);
            setOffsetGoalNoUpdate(millisecsToPixels - (this.mWidth / 2));
            if (currentPosition >= this.mPlayEndMsec) {
                handlePause();
            }
        }
        millisecsToPixels = 0;
        if (!this.mTouchDragging) {
            if (this.mFlingVelocity != 0) {
                currentPosition = this.mFlingVelocity / 30;
                if (this.mFlingVelocity > 80) {
                    this.mFlingVelocity -= 80;
                } else if (this.mFlingVelocity < -80) {
                    this.mFlingVelocity += 80;
                } else {
                    this.mFlingVelocity = 0;
                }
                this.mOffset += currentPosition;
                if (this.mOffset + (this.mWidth / 2) > this.mMaxPos) {
                    this.mOffset = this.mMaxPos - (this.mWidth / 2);
                    this.mFlingVelocity = 0;
                }
                if (this.mOffset < 0) {
                    this.mOffset = 0;
                    this.mFlingVelocity = 0;
                }
                this.mOffsetGoal = this.mOffset;
            } else {
                currentPosition = this.mOffsetGoal - this.mOffset;
                currentPosition = currentPosition > 10 ? currentPosition / 10 : currentPosition > 0 ? 1 : currentPosition < -10 ? currentPosition / 10 : currentPosition < 0 ? -1 : 0;
                this.mOffset += currentPosition;
            }
        }
        this.mWaveformView.setParameters(this.mStartPos, this.mEndPos, this.mOffset);
        this.mWaveformView.invalidate();
        MarkerView markerView = this.mStartMarker;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getResources().getText(R.string.start_marker));
        stringBuilder.append(" ");
        stringBuilder.append(formatTime(this.mStartPos));
        markerView.setContentDescription(stringBuilder.toString());
        markerView = this.mEndMarker;
        stringBuilder = new StringBuilder();
        stringBuilder.append(getResources().getText(R.string.end_marker));
        stringBuilder.append(" ");
        stringBuilder.append(formatTime(this.mEndPos));
        markerView.setContentDescription(stringBuilder.toString());
        currentPosition = (this.mStartPos - this.mOffset) - this.mMarkerLeftInset;
        if (this.mStartMarker.getWidth() + currentPosition < 0) {
            if (this.mStartVisible) {
                this.mStartMarker.setAlpha(0.0f);
                this.mStartVisible = false;
            }
            currentPosition = 0;
        } else if (!this.mStartVisible) {
            this.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SongEditActivity.this.mStartVisible = true;
                    SongEditActivity.this.mStartMarker.setAlpha(1.0f);
                }
            }, 0);
        }
        int width = ((this.mEndPos - this.mOffset) - this.mEndMarker.getWidth()) + this.mMarkerRightInset;
        if (this.mEndMarker.getWidth() + width >= 0) {
            if (!this.mEndVisible) {
                this.mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SongEditActivity.this.mEndVisible = true;
                        SongEditActivity.this.mEndMarker.setAlpha(1.0f);
                    }
                }, 0);
            }
            millisecsToPixels = width;
        } else if (this.mEndVisible) {
            this.mEndMarker.setAlpha(0.0f);
            this.mEndVisible = false;
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.setMargins(currentPosition, this.mMarkerTopOffset, -this.mStartMarker.getWidth(), -this.mStartMarker.getHeight());
        this.mStartMarker.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.setMargins(millisecsToPixels, (this.mWaveformView.getMeasuredHeight() - this.mEndMarker.getHeight()) - this.mMarkerBottomOffset, -this.mStartMarker.getWidth(), -this.mStartMarker.getHeight());
        this.mEndMarker.setLayoutParams(layoutParams2);
    }

    public void markerDraw() {
    }

    public void markerEnter(final MarkerView markerView) {
    }

    public void markerFocus(final MarkerView markerView) {
        this.mKeyDown = false;
        if (markerView == this.mStartMarker) {
            this.setOffsetGoalStartNoUpdate();
        } else {
            this.setOffsetGoalEndNoUpdate();
        }
        this.mHandler.postDelayed((Runnable) new Runnable() {
            @Override
            public void run() {
                SongEditActivity.this.updateDisplay();
            }
        }, 100L);
    }

    public void markerKeyUp() {
        this.mKeyDown = false;
        this.updateDisplay();
    }

    public void markerLeft(final MarkerView markerView, final int n) {
        this.mKeyDown = true;
        if (markerView == this.mStartMarker) {
            final int mStartPos = this.mStartPos;
            this.mStartPos = this.trap(this.mStartPos - n);
            this.mEndPos = this.trap(this.mEndPos - (mStartPos - this.mStartPos));
            this.setOffsetGoalStart();
        }
        if (markerView == this.mEndMarker) {
            if (this.mEndPos == this.mStartPos) {
                this.mStartPos = this.trap(this.mStartPos - n);
                this.mEndPos = this.mStartPos;
            } else {
                this.mEndPos = this.trap(this.mEndPos - n);
            }
            this.setOffsetGoalEnd();
        }
        this.updateDisplay();
    }

    public void markerRight(final MarkerView markerView, final int n) {
        this.mKeyDown = true;
        if (markerView == this.mStartMarker) {
            final int mStartPos = this.mStartPos;
            this.mStartPos += n;
            if (this.mStartPos > this.mMaxPos) {
                this.mStartPos = this.mMaxPos;
            }
            this.mEndPos += this.mStartPos - mStartPos;
            if (this.mEndPos > this.mMaxPos) {
                this.mEndPos = this.mMaxPos;
            }
            this.setOffsetGoalStart();
        }
        if (markerView == this.mEndMarker) {
            this.mEndPos += n;
            if (this.mEndPos > this.mMaxPos) {
                this.mEndPos = this.mMaxPos;
            }
            this.setOffsetGoalEnd();
        }
        this.updateDisplay();
    }

    public void markerTouchEnd(final MarkerView markerView) {
        this.mTouchDragging = false;
        if (markerView == this.mStartMarker) {
            this.setOffsetGoalStart();
            return;
        }
        this.setOffsetGoalEnd();
    }

    public void markerTouchMove(final MarkerView markerView, float n) {
        n -= this.mTouchStart;
        if (markerView == this.mStartMarker) {
            this.mStartPos = this.trap((int) (this.mTouchInitialStartPos + n));
            this.mEndPos = this.trap((int) (this.mTouchInitialEndPos + n));
        } else {
            this.mEndPos = this.trap((int) (this.mTouchInitialEndPos + n));
            if (this.mEndPos < this.mStartPos) {
                this.mEndPos = this.mStartPos;
            }
        }
        this.updateDisplay();
    }

    public void markerTouchStart(final MarkerView markerView, final float mTouchStart) {
        this.mTouchDragging = true;
        this.mTouchStart = mTouchStart;
        this.mTouchInitialStartPos = this.mStartPos;
        this.mTouchInitialEndPos = this.mEndPos;
    }

    public void onConfigurationChanged(final Configuration configuration) {
        final int zoomLevel = this.mWaveformView.getZoomLevel();
        super.onConfigurationChanged(configuration);
        this.loadGui();
        this.mHandler.postDelayed((Runnable) new Runnable() {
            @Override
            public void run() {
                SongEditActivity.this.mStartMarker.requestFocus();
                SongEditActivity.this.markerFocus(SongEditActivity.this.mStartMarker);
                SongEditActivity.this.mWaveformView.setZoomLevel(zoomLevel);
                SongEditActivity.this.mWaveformView.recomputeHeights(SongEditActivity.this.mDensity);
                SongEditActivity.this.updateDisplay();
            }
        }, 500L);
    }

    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.mPlayer = null;
        this.mIsPlaying = false;
        this.mAlertDialog = null;
        this.mProgressDialog = null;
        this.mLoadSoundFileThread = null;
        this.mRecordAudioThread = null;
        this.mSaveSoundFileThread = null;
        this.mRecordingFilename = null;
        this.mRecordingUri = null;
        this.mSoundFile = null;
        this.mKeyDown = false;
        this.mHandler = new Handler();
        this.loadGui();
        this.init();
        this.mHandler.postDelayed(this.mTimerRunnable, 100L);
        PutAnalyticsEvent();
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        if (!TextUtils.isEmpty((CharSequence) this.mFilename) && this.mFilename.equals("record")) {
            menu.clear();
        } else {
            this.getMenuInflater().inflate(R.menu.menu_selection, menu);
            menu.removeItem(R.id.menu_clear);
        }
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

    protected void onDestroy() {
        this.mLoadingKeepGoing = false;
        this.closeThread(this.mLoadSoundFileThread);
        this.closeThread(this.mRecordAudioThread);
        this.closeThread(this.mSaveSoundFileThread);
        this.mLoadSoundFileThread = null;
        this.mRecordAudioThread = null;
        this.mSaveSoundFileThread = null;
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
        if (this.mAlertDialog != null) {
            this.mAlertDialog.dismiss();
            this.mAlertDialog = null;
        }
        if (this.mPlayer != null) {
            if (this.mPlayer.isPlaying()) {
                this.mPlayer.stop();
            }
            this.mPlayer.release();
            this.mPlayer = null;
        }
        if (this.mRecordingFilename != null) {
            try {
                if (!new File(this.mRecordingFilename).delete()) {
                    this.showFinalAlert(new Exception(), R.string.delete_tmp_error);
                }
                this.getContentResolver().delete(this.mRecordingUri, (String) null, (String[]) null);
            } catch (SecurityException ex) {
                this.showFinalAlert(ex, R.string.delete_tmp_error);
            }
        }
        super.onDestroy();
    }

    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (n == 62) {
            this.onPlay(this.mStartPos);
            return true;
        }
        return super.onKeyDown(n, keyEvent);
    }

    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        final int itemId = menuItem.getItemId();
        if (itemId != android.R.id.home) {
            if (itemId == R.id.menu_done) {
                this.onSave();
                MyApplication.getInstance().setMusicData(this.selectedMusicData);
                SongEditActivity.isFromSdcard = true;
            }
        } else {
            if (MyApplication.isShowAd == 1) {
                onBackPressed();
                MyApplication.isShowAd = 0;
            } else {
                if (MyApplication.mInterstitialAd != null) {
                    MyApplication.activity = activity;
                    MyApplication.AdsId = 115;
                    MyApplication.mInterstitialAd.show(activity);
                    MyApplication.isShowAd = 1;

                } else {
                    onBackPressed();
                }
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    protected void onPause() {
        super.onPause();
        this.handlePause();
    }

    public void waveformDraw() {
        this.mWidth = this.mWaveformView.getMeasuredWidth();
        if (this.mOffsetGoal != this.mOffset && !this.mKeyDown) {
            this.updateDisplay();
            return;
        }
        if (this.mIsPlaying) {
            this.updateDisplay();
            return;
        }
        if (this.mFlingVelocity != 0) {
            this.updateDisplay();
        }
    }

    public void waveformFling(final float n) {
        this.mTouchDragging = false;
        this.mOffsetGoal = this.mOffset;
        this.mFlingVelocity = (int) (-n);
        this.updateDisplay();
    }

    public void waveformTouchEnd() {
        this.mTouchDragging = false;
        this.mOffsetGoal = this.mOffset;
        if (this.getCurrentTime() - this.mWaveformTouchStartMsec < 300L) {
            if (this.mIsPlaying) {
                final int pixelsToMillisecs = this.mWaveformView.pixelsToMillisecs((int) (this.mTouchStart + this.mOffset));
                if (pixelsToMillisecs >= this.mPlayStartMsec && pixelsToMillisecs < this.mPlayEndMsec) {
                    this.mPlayer.seekTo(pixelsToMillisecs);
                    return;
                }
                this.handlePause();
            } else {
                this.onPlay((int) (this.mTouchStart + this.mOffset));
            }
        }
    }

    public void waveformTouchMove(final float n) {
        this.mOffset = this.trap((int) (this.mTouchInitialOffset + (this.mTouchStart - n)));
        this.updateDisplay();
    }

    public void waveformTouchStart(final float mTouchStart) {
        this.mTouchDragging = true;
        this.mTouchStart = mTouchStart;
        this.mTouchInitialOffset = this.mOffset;
        this.mFlingVelocity = 0;
        this.mWaveformTouchStartMsec = this.getCurrentTime();
    }

    public void waveformZoomIn() {
        this.mWaveformView.zoomIn();
        this.mStartPos = this.mWaveformView.getStart();
        this.mEndPos = this.mWaveformView.getEnd();
        this.mMaxPos = this.mWaveformView.maxPos();
        this.mOffset = this.mWaveformView.getOffset();
        this.mOffsetGoal = this.mOffset;
        this.updateDisplay();
    }

    public void waveformZoomOut() {
        this.mWaveformView.zoomOut();
        this.mStartPos = this.mWaveformView.getStart();
        this.mEndPos = this.mWaveformView.getEnd();
        this.mMaxPos = this.mWaveformView.maxPos();
        this.mOffset = this.mWaveformView.getOffset();
        this.mOffsetGoal = this.mOffset;
        this.updateDisplay();
    }

    public class LoadMusics extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;

        protected Void doInBackground(final Void... array) {
            SongEditActivity.this.mMusicDatas = (ArrayList<MusicData>) MyApplication.getInstance().getMusicFiles();
            if (SongEditActivity.this.mMusicDatas.size() > 0) {
                SongEditActivity.this.selectedMusicData = SongEditActivity.this.mMusicDatas.get(0);
                SongEditActivity.this.mFilename = SongEditActivity.this.selectedMusicData.getTrack_data();
            } else {
                SongEditActivity.this.mFilename = "record";
            }
            return null;
        }

        protected void onPostExecute(final Void void1) {
            super.onPostExecute(void1);
            this.pDialog.dismiss();
            if (!SongEditActivity.this.mFilename.equals("record")) {
                SongEditActivity.this.setUpRecyclerView();
                SongEditActivity.this.loadFromFile();
                SongEditActivity.this.supportInvalidateOptionsMenu();
                return;
            }
            if (SongEditActivity.this.mMusicDatas.size() > 0) {
                Toast.makeText(SongEditActivity.this.getApplicationContext(), (CharSequence) SongEditActivity.this.getApplicationContext().getString(R.string.no_music_found_in_device_please_add_music_in_sdcard), Toast.LENGTH_LONG).show();
            }
        }

        protected void onPreExecute() {
            super.onPreExecute();
            (this.pDialog = new ProgressDialog((Context) SongEditActivity.this, R.style.Theme_MovieMaker_AlertDialog)).setTitle((CharSequence) SongEditActivity.this.getString(R.string.please_wait_));
            this.pDialog.setMessage((CharSequence) SongEditActivity.this.getString(R.string.loading_music_));
            this.pDialog.show();
        }
    }

    public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {
        SparseBooleanArray booleanArray;
        RadioButton mButton;
        int mSelectedChoice;
        private ArrayList<MusicData> musicDatas;

        public MusicAdapter(final ArrayList<MusicData> musicDatas) {
            this.mSelectedChoice = 0;
            this.booleanArray = new SparseBooleanArray();
            this.musicDatas = musicDatas;
            this.booleanArray.put(0, true);
        }

        public int getItemCount() {
            return this.musicDatas.size();
        }

        public void onBindViewHolder(final Holder holder, final int n) {
            holder.radioMusicName.setText((CharSequence) this.musicDatas.get(n).track_displayName);
            holder.radioMusicName.setChecked(this.booleanArray.get(n, false));
            Utils.setFont((Activity) SongEditActivity.this, (TextView) holder.radioMusicName);
            holder.radioMusicName.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
                public void onClick(final View view) {
                    MusicAdapter.this.booleanArray.clear();
                    MusicAdapter.this.booleanArray.put(n, true);
                    SongEditActivity.this.onPlay(-1);
                    MusicAdapter.this.playMusic(n);
                    SongEditActivity.this.isFromItemClick = true;
                    MusicAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public Holder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
            return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_list_items, viewGroup, false));
        }

        public void playMusic(final int mSelectedChoice) {
            if (this.mSelectedChoice != mSelectedChoice) {
                SongEditActivity.this.selectedMusicData = SongEditActivity.this.mMusicDatas.get(mSelectedChoice);
                SongEditActivity.this.mFilename = SongEditActivity.this.selectedMusicData.getTrack_data();
                SongEditActivity.this.loadFromFile();
            }
            this.mSelectedChoice = mSelectedChoice;
        }

        public class Holder extends RecyclerView.ViewHolder {
            public CheckBox radioMusicName;

            public Holder(final View view) {
                super(view);
                this.radioMusicName = (CheckBox) view.findViewById(R.id.radioMusicName);
            }
        }
    }
}
