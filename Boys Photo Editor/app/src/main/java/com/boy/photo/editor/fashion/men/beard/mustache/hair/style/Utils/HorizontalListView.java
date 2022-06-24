package com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.Utils.HorizontalListView.OnScrollStateChangedListener.ScrollState;
import com.boy.photo.editor.fashion.men.beard.mustache.hair.style.R;

public class HorizontalListView extends AdapterView<ListAdapter> {
	private static final String BUNDLE_ID_CURRENT_X = "BUNDLE_ID_CURRENT_X";
	private static final String BUNDLE_ID_PARENT_STATE = "BUNDLE_ID_PARENT_STATE";
	private static final float FLING_DEFAULT_ABSORB_VELOCITY = 30.0f;
	private static final float FLING_FRICTION = 0.009f;
	private static final int INSERT_AT_END_OF_LIST = -1;
	private static final int INSERT_AT_START_OF_LIST = 0;
	protected ListAdapter list_adapter;
	private DataSetObserver adapter_dataObserver = new dataset_observer();
	private GestureDetector gestureDetector;
	private final GestureListener gesture_Listener = new GestureListener();
	private boolean Notified_RunningLowOnData = false;
	private int height_measureSpec;
	private int currently_selectedAdapterIndex;
	private int display_offset;
	private Runnable delayed_layout = new delay_layout();
	private boolean block_touchAction = false;
	private ScrollState currentScrollState = ScrollState.SCROLL_STATE_IDLE;
	protected int currentX;
	private Drawable divider = null;
	private int divider_width = 0;
	private int rightView_adapterIndex;
	private RunningOutOfDataListener runningOut_dataListener = null;
	private int runningOut_dataThreshold = 0;
	private View viewBeing_touched = null;
	private boolean data_changed = false;
	private EdgeEffectCompat edge_glowLeft;
	private EdgeEffectCompat edge_glowRight;
	protected Scroller fling_tracker = new Scroller(getContext());
	private boolean parentVerticiallyScrollableViewDisallowingInterceptTouchEvent = false;
	private int leftView_adapterIndex;
	private int maxX = Integer.MAX_VALUE;
	protected int nextX;
	private OnClickListener onClickListener;
	private OnScrollStateChangedListener onScrollStateChangedListener = null;
	private Rect rect = new Rect();
	private List<Queue<View>> removed_viewsCache = new ArrayList();
	private Integer restore_X = null;

	class gesture_detectoreTouch implements OnTouchListener {
		gesture_detectoreTouch() {
		}

		public boolean onTouch(View v, MotionEvent event) {
			return HorizontalListView.this.gestureDetector.onTouchEvent(event);
		}
	}

	class dataset_observer extends DataSetObserver {
		dataset_observer() {
		}

		public void onChanged() {
			HorizontalListView.this.data_changed = true;
			HorizontalListView.this.Notified_RunningLowOnData = false;
			HorizontalListView.this.unpressTouchedChild();
			HorizontalListView.this.invalidate();
			HorizontalListView.this.requestLayout();
		}

		public void onInvalidated() {
			HorizontalListView.this.Notified_RunningLowOnData = false;
			HorizontalListView.this.unpressTouchedChild();
			HorizontalListView.this.reset();
			HorizontalListView.this.invalidate();
			HorizontalListView.this.requestLayout();
		}
	}

	class delay_layout implements Runnable {
		delay_layout() {
		}

		public void run() {
			HorizontalListView.this.requestLayout();
		}
	}

	private class GestureListener extends SimpleOnGestureListener {
		private GestureListener() {
		}

		public boolean onDown(MotionEvent e) {
			return HorizontalListView.this.onDown(e);
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return HorizontalListView.this
					.onFling(e1, e2, velocityX, velocityY);
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			HorizontalListView.this
					.requestParentListViewToNotInterceptTouchEvents(Boolean
							.valueOf(true));
			HorizontalListView.this
					.setCurrentScrollState(ScrollState.SCROLL_STATE_TOUCH_SCROLL);
			HorizontalListView.this.unpressTouchedChild();
			HorizontalListView horizontalListView = HorizontalListView.this;
			horizontalListView.nextX += (int) distanceX;
			HorizontalListView.this.updateOverscrollAnimation(Math
					.round(distanceX));
			HorizontalListView.this.requestLayout();
			return true;
		}

		public boolean onSingleTapConfirmed(MotionEvent e) {
			HorizontalListView.this.unpressTouchedChild();
			OnItemClickListener onItemClickListener = HorizontalListView.this
					.getOnItemClickListener();
			int index = HorizontalListView.this.getChildIndex((int) e.getX(),
					(int) e.getY());
			if (index >= 0 && !HorizontalListView.this.block_touchAction) {
				View child = HorizontalListView.this.getChildAt(index);
				int adapterIndex = HorizontalListView.this.leftView_adapterIndex
						+ index;
				if (onItemClickListener != null) {
					onItemClickListener.onItemClick(HorizontalListView.this,
							child, adapterIndex,
							HorizontalListView.this.list_adapter
									.getItemId(adapterIndex));
					return true;
				}
			}
			if (!(HorizontalListView.this.onClickListener == null || HorizontalListView.this.block_touchAction)) {
				HorizontalListView.this.onClickListener
						.onClick(HorizontalListView.this);
			}
			return false;
		}

		public void onLongPress(MotionEvent e) {
			HorizontalListView.this.unpressTouchedChild();
			int index = HorizontalListView.this.getChildIndex((int) e.getX(),
					(int) e.getY());
			if (index >= 0 && !HorizontalListView.this.block_touchAction) {
				View child = HorizontalListView.this.getChildAt(index);
				OnItemLongClickListener onItemLongClickListener = HorizontalListView.this
						.getOnItemLongClickListener();
				if (onItemLongClickListener != null) {
					int adapterIndex = HorizontalListView.this.leftView_adapterIndex
							+ index;
					if (onItemLongClickListener.onItemLongClick(
							HorizontalListView.this, child, adapterIndex,
							HorizontalListView.this.list_adapter
									.getItemId(adapterIndex))) {
						HorizontalListView.this.performHapticFeedback(0);
					}
				}
			}
		}
	}

	@TargetApi(11)
	private static final class HoneycombPlus {
		private HoneycombPlus() {
		}

		static {
			if (VERSION.SDK_INT < 11) {
				throw new RuntimeException(
						"Should not get to HoneycombPlus class unless sdk is >= 11!");
			}
		}

		public static void setFriction(Scroller scroller, float friction) {
			if (scroller != null) {
				scroller.setFriction(friction);
			}
		}
	}

	@TargetApi(14)
	private static final class IceCreamSandwichPlus {
		private IceCreamSandwichPlus() {
		}

		static {
			if (VERSION.SDK_INT < 14) {
				throw new RuntimeException(
						"Should not get to IceCreamSandwichPlus class unless sdk is >= 14!");
			}
		}

		public static float getCurrVelocity(Scroller scroller) {
			return scroller.getCurrVelocity();
		}
	}

	public interface OnScrollStateChangedListener {

		public enum ScrollState {
			SCROLL_STATE_IDLE, SCROLL_STATE_TOUCH_SCROLL, SCROLL_STATE_FLING
		}

		void onScrollStateChanged(ScrollState scrollState);
	}

	public interface RunningOutOfDataListener {
		void onRunningOutOfData();
	}

	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.edge_glowLeft = new EdgeEffectCompat(context);
		this.edge_glowRight = new EdgeEffectCompat(context);
		this.gestureDetector = new GestureDetector(context,
				this.gesture_Listener);
		bindGestureDetector();
		initView();
		retrieveXmlConfiguration(context, attrs);
		setWillNotDraw(false);
		if (VERSION.SDK_INT >= 11) {
			HoneycombPlus.setFriction(this.fling_tracker, FLING_FRICTION);
		}
	}

	private void bindGestureDetector() {
		setOnTouchListener(new gesture_detectoreTouch());
	}

	private void requestParentListViewToNotInterceptTouchEvents(
			Boolean disallowIntercept) {
		if (this.parentVerticiallyScrollableViewDisallowingInterceptTouchEvent != disallowIntercept
				.booleanValue()) {
			View view = this;
			while (view.getParent() instanceof View) {
				if ((view.getParent() instanceof ListView)
						|| (view.getParent() instanceof ScrollView)) {
					view.getParent().requestDisallowInterceptTouchEvent(
							disallowIntercept.booleanValue());
					this.parentVerticiallyScrollableViewDisallowingInterceptTouchEvent = disallowIntercept
							.booleanValue();
					return;
				}
				view = (View) view.getParent();
			}
		}
	}

	private void retrieveXmlConfiguration(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.HorizontalListView);
		if (attrs != null) {
			Drawable d = a.getDrawable(1);
			if (d != null) {
				setDivider(d);
			}
			int dividerWidth = a.getDimensionPixelSize(3, 0);
			if (dividerWidth != 0) {
				setDividerWidth(dividerWidth);
			}
			a.recycle();
		}
	}

	public Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(BUNDLE_ID_PARENT_STATE,
				super.onSaveInstanceState());
		bundle.putInt(BUNDLE_ID_CURRENT_X, this.currentX);
		return bundle;
	}

	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			this.restore_X = Integer
					.valueOf(bundle.getInt(BUNDLE_ID_CURRENT_X));
			super.onRestoreInstanceState(bundle
					.getParcelable(BUNDLE_ID_PARENT_STATE));
		}
	}

	public void setDivider(Drawable divider) {
		this.divider = divider;
		if (divider != null) {
			setDividerWidth(divider.getIntrinsicWidth());
		} else {
			setDividerWidth(0);
		}
	}

	public void setDividerWidth(int width) {
		this.divider_width = width;
		requestLayout();
		invalidate();
	}

	private void initView() {
		this.leftView_adapterIndex = -1;
		this.rightView_adapterIndex = -1;
		this.display_offset = 0;
		this.currentX = 0;
		this.nextX = 0;
		this.maxX = Integer.MAX_VALUE;
		setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
	}

	private void reset() {
		initView();
		removeAllViewsInLayout();
		requestLayout();
	}

	public void setSelection(int position) {
		this.currently_selectedAdapterIndex = position;
	}

	public View getSelectedView() {
		return getChild(this.currently_selectedAdapterIndex);
	}

	public void setAdapter(ListAdapter adapter) {
		if (this.list_adapter != null) {
			this.list_adapter
					.unregisterDataSetObserver(this.adapter_dataObserver);
		}
		if (adapter != null) {
			this.Notified_RunningLowOnData = false;
			this.list_adapter = adapter;
			this.list_adapter
					.registerDataSetObserver(this.adapter_dataObserver);
		}
		initializeRecycledViewCache(this.list_adapter.getViewTypeCount());
		reset();
	}

	public ListAdapter getAdapter() {
		return this.list_adapter;
	}

	private void initializeRecycledViewCache(int viewTypeCount) {
		this.removed_viewsCache.clear();
		for (int i = 0; i < viewTypeCount; i++) {
			this.removed_viewsCache.add(new LinkedList());
		}
	}

	private View getRecycledView(int adapterIndex) {
		int itemViewType = this.list_adapter.getItemViewType(adapterIndex);
		if (isItemViewTypeValid(itemViewType)) {
			return (View) ((Queue) this.removed_viewsCache.get(itemViewType))
					.poll();
		}
		return null;
	}

	private void recycleView(int adapterIndex, View view) {
		int itemViewType = this.list_adapter.getItemViewType(adapterIndex);
		if (isItemViewTypeValid(itemViewType)) {
			((Queue) this.removed_viewsCache.get(itemViewType)).offer(view);
		}
	}

	private boolean isItemViewTypeValid(int itemViewType) {
		return itemViewType < this.removed_viewsCache.size();
	}

	private void addAndMeasureChild(View child, int viewPos) {
		addViewInLayout(child, viewPos, getLayoutParams(child), true);
		measureChild(child);
	}

	private void measureChild(View child) {
		int childWidthSpec;
		LayoutParams childLayoutParams = getLayoutParams(child);
		int childHeightSpec = ViewGroup.getChildMeasureSpec(
				this.height_measureSpec, getPaddingTop() + getPaddingBottom(),
				childLayoutParams.height);
		if (childLayoutParams.width > 0) {
			childWidthSpec = MeasureSpec.makeMeasureSpec(
					childLayoutParams.width, 1073741824);
		} else {
			childWidthSpec = MeasureSpec.makeMeasureSpec(0, 0);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	private LayoutParams getLayoutParams(View child) {
		LayoutParams layoutParams = child.getLayoutParams();
		if (layoutParams == null) {
			return new LayoutParams(-2, -1);
		}
		return layoutParams;
	}

	@SuppressLint({ "WrongCall" })
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (this.list_adapter != null) {
			invalidate();
			if (this.data_changed) {
				int oldCurrentX = this.currentX;
				initView();
				removeAllViewsInLayout();
				this.nextX = oldCurrentX;
				this.data_changed = false;
			}
			if (this.restore_X != null) {
				this.nextX = this.restore_X.intValue();
				this.restore_X = null;
			}
			if (this.fling_tracker.computeScrollOffset()) {
				this.nextX = this.fling_tracker.getCurrX();
			}
			if (this.nextX < 0) {
				this.nextX = 0;
				if (this.edge_glowLeft.isFinished()) {
					this.edge_glowLeft
							.onAbsorb((int) determineFlingAbsorbVelocity());
				}
				this.fling_tracker.forceFinished(true);
				setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
			} else if (this.nextX > this.maxX) {
				this.nextX = this.maxX;
				if (this.edge_glowRight.isFinished()) {
					this.edge_glowRight
							.onAbsorb((int) determineFlingAbsorbVelocity());
				}
				this.fling_tracker.forceFinished(true);
				setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
			}
			int dx = this.currentX - this.nextX;
			removeNonVisibleChildren(dx);
			fillList(dx);
			positionChildren(dx);
			this.currentX = this.nextX;
			if (determineMaxX()) {
				onLayout(changed, left, top, right, bottom);
			} else if (!this.fling_tracker.isFinished()) {
				ViewCompat.postOnAnimation(this, this.delayed_layout);
			} else if (this.currentScrollState == ScrollState.SCROLL_STATE_FLING) {
				setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
			}
		}
	}

	protected float getLeftFadingEdgeStrength() {
		int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
		if (this.currentX == 0) {
			return 0.0f;
		}
		if (this.currentX < horizontalFadingEdgeLength) {
			return ((float) this.currentX)
					/ ((float) horizontalFadingEdgeLength);
		}
		return 1.0f;
	}

	protected float getRightFadingEdgeStrength() {
		int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
		if (this.currentX == this.maxX) {
			return 0.0f;
		}
		if (this.maxX - this.currentX < horizontalFadingEdgeLength) {
			return ((float) (this.maxX - this.currentX))
					/ ((float) horizontalFadingEdgeLength);
		}
		return 1.0f;
	}

	private float determineFlingAbsorbVelocity() {
		if (VERSION.SDK_INT >= 14) {
			return IceCreamSandwichPlus.getCurrVelocity(this.fling_tracker);
		}
		return FLING_DEFAULT_ABSORB_VELOCITY;
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.height_measureSpec = heightMeasureSpec;
	}

	private boolean determineMaxX() {
		if (!isLastItemInAdapter(this.rightView_adapterIndex)) {
			return false;
		}
		View rightView = getRightmostChild();
		if (rightView == null) {
			return false;
		}
		int oldMaxX = this.maxX;
		this.maxX = (this.currentX + (rightView.getRight() - getPaddingLeft()))
				- getRenderWidth();
		if (this.maxX < 0) {
			this.maxX = 0;
		}
		if (this.maxX != oldMaxX) {
			return true;
		}
		return false;
	}

	private void fillList(int dx) {
		int edge = 0;
		View child = getRightmostChild();
		if (child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);
		edge = 0;
		child = getLeftmostChild();
		if (child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);
	}

	private void removeNonVisibleChildren(int dx) {
		View child = getLeftmostChild();
		while (child != null && child.getRight() + dx <= 0) {
			int measuredWidth;
			int i = this.display_offset;
			if (isLastItemInAdapter(this.leftView_adapterIndex)) {
				measuredWidth = child.getMeasuredWidth();
			} else {
				measuredWidth = this.divider_width + child.getMeasuredWidth();
			}
			this.display_offset = measuredWidth + i;
			recycleView(this.leftView_adapterIndex, child);
			removeViewInLayout(child);
			this.leftView_adapterIndex++;
			child = getLeftmostChild();
		}
		child = getRightmostChild();
		while (child != null && child.getLeft() + dx >= getWidth()) {
			recycleView(this.rightView_adapterIndex, child);
			removeViewInLayout(child);
			this.rightView_adapterIndex--;
			child = getRightmostChild();
		}
	}

	private void fillListRight(int rightEdge, int dx) {
		while ((rightEdge + dx) + this.divider_width < getWidth()
				&& this.rightView_adapterIndex + 1 < this.list_adapter
						.getCount()) {
			this.rightView_adapterIndex++;
			if (this.leftView_adapterIndex < 0) {
				this.leftView_adapterIndex = this.rightView_adapterIndex;
			}
			View child = this.list_adapter.getView(this.rightView_adapterIndex,
					getRecycledView(this.rightView_adapterIndex), this);
			addAndMeasureChild(child, -1);
			rightEdge += (this.rightView_adapterIndex == 0 ? 0
					: this.divider_width) + child.getMeasuredWidth();
			determineIfLowOnData();
		}
	}

	private void fillListLeft(int leftEdge, int dx) {
		while ((leftEdge + dx) - this.divider_width > 0
				&& this.leftView_adapterIndex >= 1) {
			this.leftView_adapterIndex--;
			View child = this.list_adapter.getView(this.leftView_adapterIndex,
					getRecycledView(this.leftView_adapterIndex), this);
			addAndMeasureChild(child, 0);
			leftEdge -= this.leftView_adapterIndex == 0 ? child
					.getMeasuredWidth() : this.divider_width
					+ child.getMeasuredWidth();
			this.display_offset -= leftEdge + dx == 0 ? child
					.getMeasuredWidth() : this.divider_width
					+ child.getMeasuredWidth();
		}
	}

	private void positionChildren(int dx) {
		int childCount = getChildCount();
		if (childCount > 0) {
			this.display_offset += dx;
			int leftOffset = this.display_offset;
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				int left = leftOffset + getPaddingLeft();
				int top = getPaddingTop();
				child.layout(left, top, left + child.getMeasuredWidth(), top
						+ child.getMeasuredHeight());
				leftOffset += child.getMeasuredWidth() + this.divider_width;
			}
		}
	}

	private View getLeftmostChild() {
		return getChildAt(0);
	}

	private View getRightmostChild() {
		return getChildAt(getChildCount() - 1);
	}

	private View getChild(int adapterIndex) {
		if (adapterIndex < this.leftView_adapterIndex
				|| adapterIndex > this.rightView_adapterIndex) {
			return null;
		}
		return getChildAt(adapterIndex - this.leftView_adapterIndex);
	}

	private int getChildIndex(int x, int y) {
		int childCount = getChildCount();
		for (int index = 0; index < childCount; index++) {
			getChildAt(index).getHitRect(this.rect);
			if (this.rect.contains(x, y)) {
				return index;
			}
		}
		return -1;
	}

	private boolean isLastItemInAdapter(int index) {
		return index == this.list_adapter.getCount() + -1;
	}

	private int getRenderHeight() {
		return (getHeight() - getPaddingTop()) - getPaddingBottom();
	}

	private int getRenderWidth() {
		return (getWidth() - getPaddingLeft()) - getPaddingRight();
	}

	public void scrollTo(int x) {
		this.fling_tracker.startScroll(this.nextX, 0, x - this.nextX, 0);
		setCurrentScrollState(ScrollState.SCROLL_STATE_FLING);
		requestLayout();
	}

	public int getFirstVisiblePosition() {
		return this.leftView_adapterIndex;
	}

	public int getLastVisiblePosition() {
		return this.rightView_adapterIndex;
	}

	private void drawEdgeGlow(Canvas canvas) {
		int restoreCount;
		if (this.edge_glowLeft != null && !this.edge_glowLeft.isFinished()
				&& isEdgeGlowEnabled()) {
			restoreCount = canvas.save();
			int height = getHeight();
			canvas.rotate(-90.0f, 0.0f, 0.0f);
			canvas.translate((float) ((-height) + getPaddingBottom()), 0.0f);
			this.edge_glowLeft.setSize(getRenderHeight(), getRenderWidth());
			if (this.edge_glowLeft.draw(canvas)) {
				invalidate();
			}
			canvas.restoreToCount(restoreCount);
		} else if (this.edge_glowRight != null
				&& !this.edge_glowRight.isFinished() && isEdgeGlowEnabled()) {
			restoreCount = canvas.save();
			int width = getWidth();
			canvas.rotate(90.0f, 0.0f, 0.0f);
			canvas.translate((float) getPaddingTop(), (float) (-width));
			this.edge_glowRight.setSize(getRenderHeight(), getRenderWidth());
			if (this.edge_glowRight.draw(canvas)) {
				invalidate();
			}
			canvas.restoreToCount(restoreCount);
		}
	}

	private void drawDividers(Canvas canvas) {
		int count = getChildCount();
		Rect bounds = this.rect;
		this.rect.top = getPaddingTop();
		this.rect.bottom = this.rect.top + getRenderHeight();
		for (int i = 0; i < count; i++) {
			if (i != count - 1
					|| !isLastItemInAdapter(this.rightView_adapterIndex)) {
				View child = getChildAt(i);
				bounds.left = child.getRight();
				bounds.right = child.getRight() + this.divider_width;
				if (bounds.left < getPaddingLeft()) {
					bounds.left = getPaddingLeft();
				}
				if (bounds.right > getWidth() - getPaddingRight()) {
					bounds.right = getWidth() - getPaddingRight();
				}
				drawDivider(canvas, bounds);
				if (i == 0 && child.getLeft() > getPaddingLeft()) {
					bounds.left = getPaddingLeft();
					bounds.right = child.getLeft();
					drawDivider(canvas, bounds);
				}
			}
		}
	}

	private void drawDivider(Canvas canvas, Rect bounds) {
		if (this.divider != null) {
			this.divider.setBounds(bounds);
			this.divider.draw(canvas);
		}
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawDividers(canvas);
	}

	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		drawEdgeGlow(canvas);
	}

	protected void dispatchSetPressed(boolean pressed) {
	}

	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		this.fling_tracker.fling(this.nextX, 0, (int) (-velocityX), 0, 0,
				this.maxX, 0, 0);
		setCurrentScrollState(ScrollState.SCROLL_STATE_FLING);
		requestLayout();
		return true;
	}

	protected boolean onDown(MotionEvent e) {
		this.block_touchAction = !this.fling_tracker.isFinished();
		this.fling_tracker.forceFinished(true);
		setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
		unpressTouchedChild();
		if (!this.block_touchAction) {
			int index = getChildIndex((int) e.getX(), (int) e.getY());
			if (index >= 0) {
				this.viewBeing_touched = getChildAt(index);
				if (this.viewBeing_touched != null) {
					this.viewBeing_touched.setPressed(true);
					refreshDrawableState();
				}
			}
		}
		return true;
	}

	private void unpressTouchedChild() {
		if (this.viewBeing_touched != null) {
			this.viewBeing_touched.setPressed(false);
			refreshDrawableState();
			this.viewBeing_touched = null;
		}
	}

	public void add(int flower) {
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == 1) {
			if (this.fling_tracker == null || this.fling_tracker.isFinished()) {
				setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
			}
			requestParentListViewToNotInterceptTouchEvents(Boolean
					.valueOf(false));
			releaseEdgeGlow();
		} else if (event.getAction() == 3) {
			unpressTouchedChild();
			releaseEdgeGlow();
			requestParentListViewToNotInterceptTouchEvents(Boolean
					.valueOf(false));
		}
		return super.onTouchEvent(event);
	}

	private void releaseEdgeGlow() {
		if (this.edge_glowLeft != null) {
			this.edge_glowLeft.onRelease();
		}
		if (this.edge_glowRight != null) {
			this.edge_glowRight.onRelease();
		}
	}

	public void setRunningOutOfDataListener(RunningOutOfDataListener listener,
			int numberOfItemsLeftConsideredLow) {
		this.runningOut_dataListener = listener;
		this.runningOut_dataThreshold = numberOfItemsLeftConsideredLow;
	}

	private void determineIfLowOnData() {
		if (this.runningOut_dataListener != null
				&& this.list_adapter != null
				&& this.list_adapter.getCount()
						- (this.rightView_adapterIndex + 1) < this.runningOut_dataThreshold
				&& !this.Notified_RunningLowOnData) {
			this.Notified_RunningLowOnData = true;
			this.runningOut_dataListener.onRunningOutOfData();
		}
	}

	public void setOnClickListener(OnClickListener listener) {
		this.onClickListener = listener;
	}

	public void setOnScrollStateChangedListener(
			OnScrollStateChangedListener listener) {
		this.onScrollStateChangedListener = listener;
	}

	private void setCurrentScrollState(ScrollState newScrollState) {
		if (!(this.currentScrollState == newScrollState || this.onScrollStateChangedListener == null)) {
			this.onScrollStateChangedListener
					.onScrollStateChanged(newScrollState);
		}
		this.currentScrollState = newScrollState;
	}

	private void updateOverscrollAnimation(int scrolledOffset) {
		if (this.edge_glowLeft != null && this.edge_glowRight != null) {
			int nextScrollPosition = this.currentX + scrolledOffset;
			if (this.fling_tracker != null && !this.fling_tracker.isFinished()) {
				return;
			}
			if (nextScrollPosition < 0) {
				this.edge_glowLeft.onPull(((float) Math.abs(scrolledOffset))
						/ ((float) getRenderWidth()));
				if (!this.edge_glowRight.isFinished()) {
					this.edge_glowRight.onRelease();
				}
			} else if (nextScrollPosition > this.maxX) {
				this.edge_glowRight.onPull(((float) Math.abs(scrolledOffset))
						/ ((float) getRenderWidth()));
				if (!this.edge_glowLeft.isFinished()) {
					this.edge_glowLeft.onRelease();
				}
			}
		}
	}

	private boolean isEdgeGlowEnabled() {
		if (this.list_adapter == null || this.list_adapter.isEmpty()
				|| this.maxX <= 0) {
			return false;
		}
		return true;
	}
}
