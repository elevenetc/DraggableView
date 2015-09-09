package su.levenetc.android.sample;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import su.levenetc.android.draggableview.DragController;
import su.levenetc.android.draggableview.DraggableView;
import su.levenetc.android.draggableview.SkewView;
import su.levenetc.android.draggableview.utils.Utils;

/**
 * Created by Eugene Levenetc.
 */
public class SampleGridContainer extends FrameLayout implements DragController.IDragViewGroup {

	private DragController<SampleGridContainer> dragController = new DragController<>(this);
	private View selectedView;
	private static final int MAX_COLUMNS = 3;

	public SampleGridContainer(Context context) {
		super(context);
	}

	public SampleGridContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override public boolean onTouchEvent(MotionEvent event) {
		return dragController.onTouchEvent(event);
	}

	@Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		final int width = getWidth();
		final int cellSize = width / MAX_COLUMNS;
		final int childCount = getChildCount();

		int x = 0;
		int y = 0;
		int col = 0;
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.layout(x, y, x + cellSize, y + cellSize);
			col++;
			if (col == MAX_COLUMNS) {
				col = 0;
				y += cellSize;
			}

			x = col * cellSize;
		}
	}

	@Override public View onDownEvent(int x, int y) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (Utils.isViewContains(child, x, y, false)) {
				selectedView = child;
				return child;
			}
		}
		return null;
	}

	@Override public ViewGroup getContainerForDraggableView() {
		Activity context = (Activity) getContext();
		ViewGroup rootView = (ViewGroup) context.getWindow().getDecorView().getRootView();
		return (ViewGroup) ((ViewGroup) rootView.getChildAt(0)).getChildAt(1);
	}

	@Override public void onDragStart() {
		AlphaAnimation alphaAnim = new AlphaAnimation(1, 0.5f);
		alphaAnim.setDuration(500);
		alphaAnim.setFillAfter(true);
		startAnimation(alphaAnim);
		selectedView.setVisibility(View.INVISIBLE);
	}

	@Override public void onDragEnd() {

		clearAnimation();//

		DraggableView draggableView = dragController.getDraggableView();

		AnimatorSet translateSet = new AnimatorSet();

		ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 0.5f, 1f);
		ObjectAnimator transX = ObjectAnimator.ofFloat(
				draggableView,
				"translationX",
				draggableView.getX(),
				selectedView.getX() - draggableView.getXTranslation()
		);

		ObjectAnimator transY = ObjectAnimator.ofFloat(
				draggableView,
				"translationY",
				draggableView.getY(),
				selectedView.getY() - draggableView.getYTranslation()
		);

		transX.addListener(new Animator.AnimatorListener() {

			@Override public void onAnimationStart(Animator animation) {

			}

			@Override public void onAnimationEnd(Animator animation) {
				dragController.finishDrag();
				selectedView.setVisibility(View.VISIBLE);
			}

			@Override public void onAnimationCancel(Animator animation) {

			}

			@Override public void onAnimationRepeat(Animator animation) {

			}
		});

		translateSet.playTogether(transX, transY, alpha);
		translateSet.setInterpolator(new FastOutSlowInInterpolator());
		translateSet.setDuration(300);
		translateSet.start();
	}

	@Override public void onMoveEvent(float x, float y) {

	}

	@Override public DraggableView createDraggableView(
			Bitmap bitmap,
			VelocityTracker velocityTracker,
			PointF selectedViewPoint,
			PointF downEventPoint) {

		return new SkewView(
				getContext(),
				bitmap,
				velocityTracker,
				selectedViewPoint,
				downEventPoint,
				this
		);
	}
}