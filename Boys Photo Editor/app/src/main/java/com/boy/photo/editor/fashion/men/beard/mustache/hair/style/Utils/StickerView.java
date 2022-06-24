package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;


public class StickerView extends AppCompatImageView {
    private static final float BITMAP_SCALE = 0.7f;
    private static final String TAG = "StickerView";
    private float MAX_SCALE = 1.2f;
    private float MIN_SCALE = 0.5f;
    private float last_length;
    private float lastRotate_degree;
    private float last_X;
    private float last_Y;
    private Paint local_paint;
    private Bitmap delete_bitmap;
    private int delete_bitmapHeight;
    private int delete_bitmapWidth;
    private DisplayMetrics display_matrix;
    private Rect rect_delete;
    private Rect rect_flip;
    private Rect rect_resize;
    private Rect rect_top;
    private final float pointer_limitDis = 20.0f;
    private final float pointer_zoomCoeff = 0.09f;
    private int topBitmap_height;
    private int topBitmap_width;
    private Bitmap resize_bitmap;
    private int resizeBitmap_height;
    private int resizeBitmap_width;
    private final long sticker_id = 0;
    private Bitmap top_bitmap;
    private float oringin_width = 0.0f;
    private Bitmap flip_Bitmap;
    private int flip_bitmapHeight;
    private int flip_bitmapWidth;
    private double halfdiagonal_length;
    private boolean horizon_mirror = false;
    private boolean edit = true;
    private boolean Resize = false;
    private boolean Side;
    private boolean pointer_down = false;
    private Bitmap bitmap;
    private int screen_Height;
    private int screen_width;
    private Matrix matrix = new Matrix();
    private PointF id = new PointF();
    private float old_Dis;
    private OperationListener operation_listener;


    public interface OperationListener {
        void onDeleteClick();

        void onEdit(StickerView stickerView);

        void onTop(StickerView stickerView);
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StickerView(Context context) {
        super(context);
        init();
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.rect_delete = new Rect();
        this.rect_resize = new Rect();
        this.rect_flip = new Rect();
        this.rect_top = new Rect();
        this.local_paint = new Paint();
        this.local_paint.setColor(getResources().getColor(R.color.colorPrimary));
        this.local_paint.setAntiAlias(true);
        this.local_paint.setDither(true);
        this.local_paint.setStyle(Style.STROKE);
        this.local_paint.setStrokeWidth(2.0f);
        this.display_matrix = getResources().getDisplayMetrics();
        this.screen_width = this.display_matrix.widthPixels;
        this.screen_Height = this.display_matrix.heightPixels;
    }

    protected void onDraw(Canvas canvas) {
        if (this.bitmap != null) {
            float[] arrayOfFloat = new float[9];
            this.matrix.getValues(arrayOfFloat);
            float f1 = ((0.0f * arrayOfFloat[0]) + (0.0f * arrayOfFloat[1]))
                    + arrayOfFloat[2];
            float f2 = ((0.0f * arrayOfFloat[3]) + (0.0f * arrayOfFloat[4]))
                    + arrayOfFloat[5];
            float f3 = ((arrayOfFloat[0] * ((float) this.bitmap.getWidth())) + (0.0f * arrayOfFloat[1]))
                    + arrayOfFloat[2];
            float f4 = ((arrayOfFloat[3] * ((float) this.bitmap.getWidth())) + (0.0f * arrayOfFloat[4]))
                    + arrayOfFloat[5];
            float f5 = ((0.0f * arrayOfFloat[0]) + (arrayOfFloat[1] * ((float) this.bitmap
                    .getHeight()))) + arrayOfFloat[2];
            float f6 = ((0.0f * arrayOfFloat[3]) + (arrayOfFloat[4] * ((float) this.bitmap
                    .getHeight()))) + arrayOfFloat[5];
            float f7 = ((arrayOfFloat[0] * ((float) this.bitmap.getWidth())) + (arrayOfFloat[1] * ((float) this.bitmap
                    .getHeight()))) + arrayOfFloat[2];
            float f8 = ((arrayOfFloat[3] * ((float) this.bitmap.getWidth())) + (arrayOfFloat[4] * ((float) this.bitmap
                    .getHeight()))) + arrayOfFloat[5];
            canvas.save();
            canvas.drawBitmap(this.bitmap, this.matrix, null);
            this.rect_delete.left = (int) (f3 - ((float) (this.delete_bitmapWidth / 2)));
            this.rect_delete.right = (int) (((float) (this.delete_bitmapWidth / 2)) + f3);
            this.rect_delete.top = (int) (f4 - ((float) (this.delete_bitmapHeight / 2)));
            this.rect_delete.bottom = (int) (((float) (this.delete_bitmapHeight / 2)) + f4);
            this.rect_resize.left = (int) (f7 - ((float) (this.resizeBitmap_width / 2)));
            this.rect_resize.right = (int) (((float) (this.resizeBitmap_width / 2)) + f7);
            this.rect_resize.top = (int) (f8 - ((float) (this.resizeBitmap_height / 2)));
            this.rect_resize.bottom = (int) (((float) (this.resizeBitmap_height / 2)) + f8);
            this.rect_top.left = (int) (f1 - ((float) (this.flip_bitmapWidth / 2)));
            this.rect_top.right = (int) (((float) (this.flip_bitmapWidth / 2)) + f1);
            this.rect_top.top = (int) (f2 - ((float) (this.flip_bitmapHeight / 2)));
            this.rect_top.bottom = (int) (((float) (this.flip_bitmapHeight / 2)) + f2);
            this.rect_flip.left = (int) (f5 - ((float) (this.topBitmap_width / 2)));
            this.rect_flip.right = (int) (((float) (this.topBitmap_width / 2)) + f5);
            this.rect_flip.top = (int) (f6 - ((float) (this.topBitmap_height / 2)));
            this.rect_flip.bottom = (int) (((float) (this.topBitmap_height / 2)) + f6);
            if (this.edit) {
                canvas.drawLine(f1, f2, f3, f4, this.local_paint);
                canvas.drawLine(f3, f4, f7, f8, this.local_paint);
                canvas.drawLine(f5, f6, f7, f8, this.local_paint);
                canvas.drawLine(f5, f6, f1, f2, this.local_paint);
                canvas.drawBitmap(this.delete_bitmap, null, this.rect_delete,
                        null);
                canvas.drawBitmap(this.resize_bitmap, null, this.rect_resize,
                        null);
                canvas.drawBitmap(this.flip_Bitmap, null, this.rect_flip, null);
                canvas.drawBitmap(this.top_bitmap, null, this.rect_top, null);
            }
            canvas.restore();
        }
    }

    public void setImageResource(int resId) {
        setBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }

    public void setBitmap(Bitmap bitmap) {
        this.matrix.reset();
        this.bitmap = bitmap;
        setDiagonalLength();
        initBitmaps();
        int w = this.bitmap.getWidth();
        int h = this.bitmap.getHeight();
        this.oringin_width = (float) w;
        float initScale = (this.MIN_SCALE + this.MAX_SCALE) / 2.0f;
        this.matrix.postTranslate((float) ((this.screen_width / 2) - (w / 2)),
                (float) ((this.screen_Height / 2) - (h / 2)));
        invalidate();
    }

    private void setDiagonalLength() {
        this.halfdiagonal_length = Math.hypot((double) this.bitmap.getWidth(),
                (double) this.bitmap.getHeight()) / 2.0d;
    }

    private void initBitmaps() {
        if (this.bitmap.getWidth() >= this.bitmap.getHeight()) {
            float minWidth = (float) (this.screen_width / 8);
            if (((float) this.bitmap.getWidth()) < minWidth) {
                this.MIN_SCALE = 1.0f;
            } else {
                this.MIN_SCALE = (1.0f * minWidth)
                        / ((float) this.bitmap.getWidth());
            }
            if (this.bitmap.getWidth() > this.screen_width) {
                this.MAX_SCALE = 1.0f;
            } else {
                this.MAX_SCALE = (((float) this.screen_width) * 1.0f)
                        / ((float) this.bitmap.getWidth());
            }
        } else {
            float minHeight = (float) (this.screen_width / 8);
            if (((float) this.bitmap.getHeight()) < minHeight) {
                this.MIN_SCALE = 1.0f;
            } else {
                this.MIN_SCALE = (1.0f * minHeight)
                        / ((float) this.bitmap.getHeight());
            }
            if (this.bitmap.getHeight() > this.screen_width) {
                this.MAX_SCALE = 1.0f;
            } else {
                this.MAX_SCALE = (((float) this.screen_width) * 1.0f)
                        / ((float) this.bitmap.getHeight());
            }
        }
        this.top_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_top_enable);
        this.delete_bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_delete);
        this.flip_Bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_flip);
        this.resize_bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_resize);
        this.delete_bitmapWidth = (int) (((float) this.delete_bitmap.getWidth()) * BITMAP_SCALE);
        this.delete_bitmapHeight = (int) (((float) this.delete_bitmap
                .getHeight()) * BITMAP_SCALE);
        this.resizeBitmap_width = (int) (((float) this.resize_bitmap.getWidth()) * BITMAP_SCALE);
        this.resizeBitmap_height = (int) (((float) this.resize_bitmap
                .getHeight()) * BITMAP_SCALE);
        this.flip_bitmapWidth = (int) (((float) this.flip_Bitmap.getWidth()) * BITMAP_SCALE);
        this.flip_bitmapHeight = (int) (((float) this.flip_Bitmap.getHeight()) * BITMAP_SCALE);
        this.topBitmap_width = (int) (((float) this.top_bitmap.getWidth()) * BITMAP_SCALE);
        this.topBitmap_height = (int) (((float) this.top_bitmap.getHeight()) * BITMAP_SCALE);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = true;
        switch (MotionEventCompat.getActionMasked(event)) {
            case 0:
                if (!isInButton(event, this.rect_delete)) {
                    if (!isInResize(event)) {
                        if (!isInButton(event, this.rect_flip)) {
                            if (!isInButton(event, this.rect_top)) {
                                if (!isInBitmap(event)) {
                                    handled = false;
                                    break;
                                }
                                this.Side = true;
                                this.last_X = event.getX(0);
                                this.last_Y = event.getY(0);
                                break;
                            }
                            bringToFront();
                            if (this.operation_listener != null) {
                                this.operation_listener.onTop(this);
                                break;
                            }
                        }
                        PointF localPointF = new PointF();
                        midDiagonalPoint(localPointF);
                        this.matrix.postScale(-1.0f, 1.0f, localPointF.x,
                                localPointF.y);
                        this.horizon_mirror = !this.horizon_mirror;
                        invalidate();
                        break;
                    }
                    this.Resize = true;
                    this.lastRotate_degree = rotationToStartPoint(event);
                    midPointToStartPoint(event);
                    this.last_length = diagonalLength(event);
                    break;
                } else if (this.operation_listener != null) {
                    this.operation_listener.onDeleteClick();
                    break;
                }
                break;
            case 1:
            case 3:
                this.Resize = false;
                this.Side = false;
                this.pointer_down = false;
                break;
            case 2:
                float scale;
                if (!this.pointer_down) {
                    if (!this.Resize) {
                        if (this.Side) {
                            float x = event.getX(0);
                            float y = event.getY(0);
                            this.matrix.postTranslate(x - this.last_X, y
                                    - this.last_Y);
                            this.last_X = x;
                            this.last_Y = y;
                            invalidate();
                            break;
                        }
                    }
                    this.matrix
                            .postRotate(
                                    (rotationToStartPoint(event) - this.lastRotate_degree) * 2.0f,
                                    this.id.x, this.id.y);
                    this.lastRotate_degree = rotationToStartPoint(event);
                    scale = diagonalLength(event) / this.last_length;
                    if ((((double) diagonalLength(event))
                            / this.halfdiagonal_length > ((double) this.MIN_SCALE) || scale >= 1.0f)
                            && (((double) diagonalLength(event))
                            / this.halfdiagonal_length < ((double) this.MAX_SCALE) || scale <= 1.0f)) {
                        this.last_length = diagonalLength(event);
                    } else {
                        scale = 1.0f;
                        if (!isInResize(event)) {
                            this.Resize = false;
                        }
                    }
                    this.matrix.postScale(scale, scale, this.id.x, this.id.y);
                    invalidate();
                    break;
                }
                float disNew = spacing(event);
                if (disNew == 0.0f || disNew < 20.0f) {
                    scale = 1.0f;
                } else {
                    scale = (((disNew / this.old_Dis) - 1.0f) * 0.09f) + 1.0f;
                }
                float scaleTemp = (((float) Math.abs(this.rect_flip.left
                        - this.rect_resize.left)) * scale)
                        / this.oringin_width;
                if ((scaleTemp > this.MIN_SCALE || scale >= 1.0f)
                        && (scaleTemp < this.MAX_SCALE || scale <= 1.0f)) {
                    this.last_length = diagonalLength(event);
                } else {
                    scale = 1.0f;
                }
                this.matrix.postScale(scale, scale, this.id.x, this.id.y);
                invalidate();
                break;
            case 5:
                if (spacing(event) > 20.0f) {
                    this.old_Dis = spacing(event);
                    this.pointer_down = true;
                    midPointToStartPoint(event);
                } else {
                    this.pointer_down = false;
                }
                this.Side = false;
                this.Resize = false;
                break;
        }
        if (handled && this.operation_listener != null) {
            this.operation_listener.onEdit(this);
        }
        return handled;
    }

    public StickerPropertyModel calculate(StickerPropertyModel model) {
        float[] v = new float[9];
        this.matrix.getValues(v);
        float tx = v[2];
        Log.d(TAG, "tx : " + tx + " ty : " + v[5]);
        float scalex = v[0];
        float skewy = v[3];
        float rScale = (float) Math
                .sqrt((double) ((scalex * scalex) + (skewy * skewy)));
        Log.d(TAG, "rScale : " + rScale);
        float rAngle = (float) Math.round(Math.atan2((double) v[1],
                (double) v[0]) * 57.29577951308232d);
        Log.d(TAG, "rAngle : " + rAngle);
        PointF localPointF = new PointF();
        midDiagonalPoint(localPointF);
        Log.d(TAG, " width  : " + (((float) this.bitmap.getWidth()) * rScale)
                + " height " + (((float) this.bitmap.getHeight()) * rScale));
        float minX = localPointF.x;
        float minY = localPointF.y;
        Log.d(TAG, "midX : " + minX + " midY : " + minY);
        model.setDegree((float) Math.toRadians((double) rAngle));
        model.setScaling((((float) this.bitmap.getWidth()) * rScale)
                / ((float) this.screen_width));
        model.setxLocation(minX / ((float) this.screen_width));
        model.setyLocation(minY / ((float) this.screen_width));
        model.setStickerId(this.sticker_id);
        if (this.horizon_mirror) {
            model.setHorizonMirror(1);
        } else {
            model.setHorizonMirror(2);
        }
        return model;
    }

    private boolean isInBitmap(final MotionEvent motionEvent) {
        final float[] array = new float[9];
        this.matrix.getValues(array);
        return this.pointInRect(
                new float[]{
                        0.0f * array[0] + 0.0f * array[1] + array[2],
                        array[0] * this.bitmap.getWidth() + 0.0f * array[1]
                                + array[2],
                        array[0] * this.bitmap.getWidth() + array[1]
                                * this.bitmap.getHeight() + array[2],
                        0.0f * array[0] + array[1] * this.bitmap.getHeight()
                                + array[2]}, new float[]{
                        0.0f * array[3] + 0.0f * array[4] + array[5],
                        array[3] * this.bitmap.getWidth() + 0.0f * array[4]
                                + array[5],
                        array[3] * this.bitmap.getWidth() + array[4]
                                * this.bitmap.getHeight() + array[5],
                        0.0f * array[3] + array[4] * this.bitmap.getHeight()
                                + array[5]}, motionEvent.getX(0),
                motionEvent.getY(0));
    }

    private boolean pointInRect(float[] xRange, float[] yRange, float x, float y) {
        double a1 = Math.hypot((double) (xRange[0] - xRange[1]),
                (double) (yRange[0] - yRange[1]));
        double a2 = Math.hypot((double) (xRange[1] - xRange[2]),
                (double) (yRange[1] - yRange[2]));
        double a3 = Math.hypot((double) (xRange[3] - xRange[2]),
                (double) (yRange[3] - yRange[2]));
        double a4 = Math.hypot((double) (xRange[0] - xRange[3]),
                (double) (yRange[0] - yRange[3]));
        double b1 = Math.hypot((double) (x - xRange[0]),
                (double) (y - yRange[0]));
        double b2 = Math.hypot((double) (x - xRange[1]),
                (double) (y - yRange[1]));
        double b3 = Math.hypot((double) (x - xRange[2]),
                (double) (y - yRange[2]));
        double b4 = Math.hypot((double) (x - xRange[3]),
                (double) (y - yRange[3]));
        double u1 = ((a1 + b1) + b2) / 2.0d;
        double u2 = ((a2 + b2) + b3) / 2.0d;
        double u3 = ((a3 + b3) + b4) / 2.0d;
        double u4 = ((a4 + b4) + b1) / 2.0d;
        return Math
                .abs((a1 * a2)
                        - (((Math.sqrt((((u1 - a1) * u1) * (u1 - b1))
                        * (u1 - b2)) + Math
                        .sqrt((((u2 - a2) * u2) * (u2 - b2))
                                * (u2 - b3))) + Math
                        .sqrt((((u3 - a3) * u3) * (u3 - b3))
                                * (u3 - b4))) + Math
                        .sqrt((((u4 - a4) * u4) * (u4 - b4))
                                * (u4 - b1)))) < 0.5d;
    }

    private boolean isInButton(MotionEvent event, Rect rect) {
        int left = rect.left;
        int right = rect.right;
        int top = rect.top;
        int bottom = rect.bottom;
        if (event.getX(0) < ((float) left) || event.getX(0) > ((float) right)
                || event.getY(0) < ((float) top)
                || event.getY(0) > ((float) bottom)) {
            return false;
        }
        return true;
    }

    private boolean isInResize(MotionEvent event) {
        int top = this.rect_resize.top - 20;
        int right = this.rect_resize.right + 20;
        int bottom = this.rect_resize.bottom + 20;
        if (event.getX(0) < ((float) (this.rect_resize.left - 20))
                || event.getX(0) > ((float) right)
                || event.getY(0) < ((float) top)
                || event.getY(0) > ((float) bottom)) {
            return false;
        }
        return true;
    }

    private void midPointToStartPoint(MotionEvent event) {
        float[] arrayOfFloat = new float[9];
        this.matrix.getValues(arrayOfFloat);
        this.id.set(
                ((((arrayOfFloat[0] * 0.0f) + (arrayOfFloat[1] * 0.0f)) + arrayOfFloat[2]) + event
                        .getX(0)) / 2.0f,
                ((((arrayOfFloat[3] * 0.0f) + (arrayOfFloat[4] * 0.0f)) + arrayOfFloat[5]) + event
                        .getY(0)) / 2.0f);
    }

    private void midDiagonalPoint(PointF paramPointF) {
        float[] arrayOfFloat = new float[9];
        this.matrix.getValues(arrayOfFloat);
        float f3 = ((arrayOfFloat[0] * ((float) this.bitmap.getWidth())) + (arrayOfFloat[1] * ((float) this.bitmap
                .getHeight()))) + arrayOfFloat[2];
        paramPointF
                .set(((((arrayOfFloat[0] * 0.0f) + (arrayOfFloat[1] * 0.0f)) + arrayOfFloat[2]) + f3) / 2.0f,
                        ((((arrayOfFloat[3] * 0.0f) + (arrayOfFloat[4] * 0.0f)) + arrayOfFloat[5]) + (((arrayOfFloat[3] * ((float) this.bitmap
                                .getWidth())) + (arrayOfFloat[4] * ((float) this.bitmap
                                .getHeight()))) + arrayOfFloat[5])) / 2.0f);
    }

    private float rotationToStartPoint(MotionEvent event) {
        float[] arrayOfFloat = new float[9];
        this.matrix.getValues(arrayOfFloat);
        return (float) Math
                .toDegrees(Math.atan2(
                        (double) (event.getY(0) - (((arrayOfFloat[3] * 0.0f) + (arrayOfFloat[4] * 0.0f)) + arrayOfFloat[5])),
                        (double) (event.getX(0) - (((arrayOfFloat[0] * 0.0f) + (arrayOfFloat[1] * 0.0f)) + arrayOfFloat[2]))));
    }

    private float diagonalLength(MotionEvent event) {
        return (float) Math.hypot((double) (event.getX(0) - this.id.x),
                (double) (event.getY(0) - this.id.y));
    }

    private float spacing(MotionEvent event) {
        if (event.getPointerCount() != 2) {
            return 0.0f;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    public void setOperationListener(OperationListener operationListener) {
        this.operation_listener = operationListener;
    }

    public void setInEdit(boolean isInEdit) {
        this.edit = isInEdit;
        invalidate();
    }


}
