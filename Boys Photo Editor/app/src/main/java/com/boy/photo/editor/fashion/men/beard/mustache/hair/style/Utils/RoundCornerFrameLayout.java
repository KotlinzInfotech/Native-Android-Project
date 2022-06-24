package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;

public class RoundCornerFrameLayout extends FrameLayout {
	private float cornerRadius;
	private final Path stencilPath;

	public RoundCornerFrameLayout(Context context) {
		this(context, null);
	}

	public RoundCornerFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundCornerFrameLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.stencilPath = new Path();
		this.cornerRadius = 0.0f;
		TypedArray attrArray = context.obtainStyledAttributes(attrs,
				R.styleable.RoundCornerFrameLayout, 0, 0);
		try {
			this.cornerRadius = attrArray.getDimension(0, 0.0f);
		} finally {
			attrArray.recycle();
		}
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.stencilPath.reset();
		this.stencilPath.addRoundRect(0.0f, 0.0f, (float) w, (float) h,
				this.cornerRadius, this.cornerRadius, Direction.CW);
		this.stencilPath.close();
	}

	protected void dispatchDraw(@NonNull Canvas canvas) {
		int save = canvas.save();
		canvas.clipPath(this.stencilPath);
		super.dispatchDraw(canvas);
		canvas.restoreToCount(save);
	}
}