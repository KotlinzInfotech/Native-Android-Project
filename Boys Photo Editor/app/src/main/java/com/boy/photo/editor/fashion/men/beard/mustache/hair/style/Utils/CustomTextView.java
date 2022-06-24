package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import androidx.core.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;

public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {
	private static final float BITMAP_SCALE = 0.7f;
	private static final String TAG = "CustomTextView";
	private float MAX_SCALE = 1.2f;
	private float MIN_SCALE = 0.5f;
	private Bitmap delete_Bitmap;
	private int delete_bitmapHeight;
	private int screen_width;
	private Matrix matrix = new Matrix();
	private PointF id = new PointF();
	private float old_Dis;
	private OperationListener operationListener;
	private float oringin_width = 0.0f;
	private int delete_bitmapWidth;
	private DisplayMetrics display_metrics;
	private Rect rect_delete;
	private Rect rect_flipV;
	private Rect rect_resize;
	private Rect rect_top;
	private boolean inEdit = true;
	private boolean inResize = false;
	private boolean inSide;
	private boolean pointer_down = false;
	private Bitmap flip_Bitmap;
	private int resize_bitmapWidth;
	private Bitmap top_bitmap;
	private int top_BitmapHeight;
	private int top_BitmapWidth;
	private int flip_BitmapHeight;
	private int flip_BitmapWidth;
	private double halfDiagonal_length;
	private boolean isHorizon_mirror = false;
	private float last_length;
	private float lastRotate_degree;
	private float lastX;
	private float lastY;
	private Paint local_paint;
	private Bitmap bitmap;
	private int screen_height;
	private final float pointer_limitDis = 20.0f;
	private final float pointer_zoomCoeff = 0.09f;
	private Bitmap resize_bitmap;
	private int resize_bitmapHeight;

	public interface OperationListener {
		void onDeleteClick();

		void onEdit(CustomTextView customStckerTextView);

		void onTop(CustomTextView customStckerTextView);
	}

	private void init() {
		this.rect_delete = new Rect();
		this.rect_resize = new Rect();
		this.rect_flipV = new Rect();
		this.rect_top = new Rect();
		this.local_paint = new Paint();
		this.local_paint.setColor(getResources().getColor(R.color.black));
		this.local_paint.setAntiAlias(true);
		this.local_paint.setDither(true);
		this.local_paint.setStyle(Style.STROKE);
		this.local_paint.setStrokeWidth(2.0f);
		this.display_metrics = getResources().getDisplayMetrics();
		this.screen_width = this.display_metrics.widthPixels;
		this.screen_height = this.display_metrics.heightPixels;
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
			this.rect_resize.left = (int) (f7 - ((float) (this.resize_bitmapWidth / 2)));
			this.rect_resize.right = (int) (((float) (this.resize_bitmapWidth / 2)) + f7);
			this.rect_resize.top = (int) (f8 - ((float) (this.resize_bitmapHeight / 2)));
			this.rect_resize.bottom = (int) (((float) (this.resize_bitmapHeight / 2)) + f8);
			this.rect_top.left = (int) (f1 - ((float) (this.flip_BitmapWidth / 2)));
			this.rect_top.right = (int) (((float) (this.flip_BitmapWidth / 2)) + f1);
			this.rect_top.top = (int) (f2 - ((float) (this.flip_BitmapHeight / 2)));
			this.rect_top.bottom = (int) (((float) (this.flip_BitmapHeight / 2)) + f2);
			this.rect_flipV.left = (int) (f5 - ((float) (this.top_BitmapWidth / 2)));
			this.rect_flipV.right = (int) (((float) (this.top_BitmapWidth / 2)) + f5);
			this.rect_flipV.top = (int) (f6 - ((float) (this.top_BitmapHeight / 2)));
			this.rect_flipV.bottom = (int) (((float) (this.top_BitmapHeight / 2)) + f6);
			if (this.inEdit) {
				canvas.drawLine(f1, f2, f3, f4, this.local_paint);
				canvas.drawLine(f3, f4, f7, f8, this.local_paint);
				canvas.drawLine(f5, f6, f7, f8, this.local_paint);
				canvas.drawLine(f5, f6, f1, f2, this.local_paint);
				canvas.drawBitmap(this.delete_Bitmap, null, this.rect_delete,
						null);
				canvas.drawBitmap(this.resize_bitmap, null, this.rect_resize,
						null);
				canvas.drawBitmap(this.flip_Bitmap, null, this.rect_flipV, null);
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
		this.matrix.postScale(1.5f, 1.5f, (float) (this.screen_height / 2),
				(float) (this.screen_width / 2));
		this.matrix.postTranslate((float) ((this.screen_width / 2) - (w / 2)),
				(float) ((this.screen_height / 2) - (h / 2)));
		invalidate();
	}

	private void setDiagonalLength() {
		this.halfDiagonal_length = Math.hypot((double) this.bitmap.getWidth(),
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
		this.top_bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_top_enable);
		this.delete_Bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_delete);
		this.flip_Bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_flip);
		this.resize_bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_resize);
		this.delete_bitmapWidth = (int) (((float) this.delete_Bitmap.getWidth()) * BITMAP_SCALE);
		this.delete_bitmapHeight = (int) (((float) this.delete_Bitmap
				.getHeight()) * BITMAP_SCALE);
		this.resize_bitmapWidth = (int) (((float) this.resize_bitmap.getWidth()) * BITMAP_SCALE);
		this.resize_bitmapHeight = (int) (((float) this.resize_bitmap
				.getHeight()) * BITMAP_SCALE);
		this.flip_BitmapWidth = (int) (((float) this.flip_Bitmap.getWidth()) * BITMAP_SCALE);
		this.flip_BitmapHeight = (int) (((float) this.flip_Bitmap.getHeight()) * BITMAP_SCALE);
		this.top_BitmapWidth = (int) (((float) this.top_bitmap.getWidth()) * BITMAP_SCALE);
		this.top_BitmapHeight = (int) (((float) this.top_bitmap.getHeight()) * BITMAP_SCALE);
	}

	public boolean onTouchEvent(MotionEvent event) {
		boolean handled = true;
		switch (MotionEventCompat.getActionMasked(event)) {
		case 0:
			if (!isInButton(event, this.rect_delete)) {
				if (!isInResize(event)) {
					if (!isInButton(event, this.rect_flipV)) {
						if (!isInButton(event, this.rect_top)) {
							if (!isInBitmap(event)) {
								handled = false;
								break;
							}
							this.inSide = true;
							this.lastX = event.getX(0);
							this.lastY = event.getY(0);
							break;
						}
						bringToFront();
						if (this.operationListener != null) {
							this.operationListener.onTop(this);
							break;
						}
					}
					PointF localPointF = new PointF();
					midDiagonalPoint(localPointF);
					this.matrix.postScale(-1.0f, 1.0f, localPointF.x,
							localPointF.y);
					this.isHorizon_mirror = !this.isHorizon_mirror;
					invalidate();
					break;
				}
				this.inResize = true;
				this.lastRotate_degree = rotationToStartPoint(event);
				midPointToStartPoint(event);
				this.last_length = diagonalLength(event);
				break;
			} else if (this.operationListener != null) {
				this.operationListener.onDeleteClick();
				break;
			}
			break;
		case 1:
		case 3:
			this.inResize = false;
			this.inSide = false;
			this.pointer_down = false;
			break;
		case 2:
			float scale;
			if (!this.pointer_down) {
				if (!this.inResize) {
					if (this.inSide) {
						float x = event.getX(0);
						float y = event.getY(0);
						this.matrix.postTranslate(x - this.lastX, y
								- this.lastY);
						this.lastX = x;
						this.lastY = y;
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
						/ this.halfDiagonal_length > ((double) this.MIN_SCALE) || scale >= 1.0f)
						&& (((double) diagonalLength(event))
								/ this.halfDiagonal_length < ((double) this.MAX_SCALE) || scale <= 1.0f)) {
					this.last_length = diagonalLength(event);
				} else {
					scale = 1.0f;
					if (!isInResize(event)) {
						this.inResize = false;
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
			float scaleTemp = (((float) Math.abs(this.rect_flipV.left
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
			this.inSide = false;
			this.inResize = false;
			break;
		}
		if (handled && this.operationListener != null) {
			this.operationListener.onEdit(this);
		}
		return handled;
	}

	private boolean isInBitmap(final MotionEvent motionEvent) {
		final float[] array = new float[9];
		this.matrix.getValues(array);
		final float n = array[0];
		final float n2 = array[1];
		final float n3 = array[2];
		final float n4 = array[3];
		final float n5 = array[4];
		final float n6 = array[5];
		final float n7 = 0.0f;
		final float n8 = 0.0f;
		final float n9 = 0.0f;
		final float n10 = 0.0f;
		final float n11 = 0.0f;
		float n13;
		final float n12 = n13 = 0.0f;
		float n14 = n11;
		float n15 = n10;
		float n16 = n9;
		float n17 = n8;
		float n18 = n7;
		if (this.bitmap != null) {
			n13 = n12;
			n14 = n11;
			n15 = n10;
			n16 = n9;
			n17 = n8;
			n18 = n7;
			if (this.bitmap.getWidth() != 0) {
				n13 = n12;
				n14 = n11;
				n15 = n10;
				n16 = n9;
				n17 = n8;
				n18 = n7;
				if (this.bitmap.getHeight() != 0) {
					n13 = array[0] * this.bitmap.getWidth() + 0.0f * array[1]
							+ array[2];
					n14 = array[3] * this.bitmap.getWidth() + 0.0f * array[4]
							+ array[5];
					n15 = 0.0f * array[0] + array[1] * this.bitmap.getHeight()
							+ array[2];
					n16 = 0.0f * array[3] + array[4] * this.bitmap.getHeight()
							+ array[5];
					n17 = array[0] * this.bitmap.getWidth() + array[1]
							* this.bitmap.getHeight() + array[2];
					n18 = array[3] * this.bitmap.getWidth() + array[4]
							* this.bitmap.getHeight() + array[5];
				}
			}
		}
		return this.pointInRect(new float[] { 0.0f * n + 0.0f * n2 + n3, n13,
				n17, n15 }, new float[] { 0.0f * n4 + 0.0f * n5 + n6, n14, n18,
				n16 }, motionEvent.getX(0), motionEvent.getY(0));
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
		this.operationListener = operationListener;
	}

	public void setInEdit(boolean isInEdit) {
		this.inEdit = isInEdit;
		invalidate();
	}

	@TargetApi(21)
	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomTextView(Context context) {
		super(context);
		init();
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.Custom_TextView);
			String fontName = a.getString(0);
			if (fontName != null) {
				try {
					setTypeface(Typeface.createFromAsset(getContext()
							.getAssets(), fontName));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			a.recycle();
		}
	}
}
