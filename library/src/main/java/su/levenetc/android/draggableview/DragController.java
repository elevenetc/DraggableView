package su.levenetc.android.draggableview;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Eugene Levenetc.
 */
public class DragController<T extends ViewGroup & DragController.IDragViewGroup> {

	private View selectedView;
	private PointF downEventPoint = new PointF();
	private DraggableView draggableView;
	private VelocityTracker velocityTracker = VelocityTracker.obtain();
	private T viewGroup;

	public DragController(T viewGroup) {
		this.viewGroup = viewGroup;
	}

	public boolean onTouchEvent(MotionEvent event) {

		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();
		final boolean isInDragSession = draggableView != null;

		if (action == MotionEvent.ACTION_DOWN && !isInDragSession) {

			selectedView = viewGroup.onDownEvent((int) x, (int) y);

			if (selectedView != null) {
				downEventPoint.set(x, y);
				addDraggable();
				return true;
			}

		} else if (isInDragSession && action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
			velocityTracker.clear();
			viewGroup.onDragEnd();
			return true;
		} else if (isInDragSession && action == MotionEvent.ACTION_MOVE) {
			draggableView.onMoveAction(event);
			return true;
		}

		return false;
	}

	private void addDraggable() {

		viewGroup.onDragStart();

		selectedView.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(selectedView.getDrawingCache());

		float selectedViewX = selectedView.getX();
		float selectedViewY = selectedView.getY();
		downEventPoint.set(downEventPoint.x - selectedViewX, downEventPoint.y - selectedViewY);

		draggableView = viewGroup.createDraggableView(
				bitmap,
				velocityTracker,
				new PointF(selectedViewX, selectedViewY),
				downEventPoint
		);

		viewGroup.getContainerForDraggableView().addView(draggableView);

	}

	public void finishDrag() {
		viewGroup.getContainerForDraggableView().removeView(draggableView);
		draggableView = null;
		selectedView = null;
	}

	public DraggableView getDraggableView() {
		return draggableView;
	}

	public interface IDragViewGroup {

		/**
		 * Should be returned view which can be dragged
		 * or null if there is no such view
		 */
		View onDownEvent(int x, int y);

		/**
		 * @return ViewGroup where draggable view will be added
		 */
		ViewGroup getContainerForDraggableView();

		/**
		 * Calls after draggable view was created.
		 * Some animations could be started.
		 * Also selected view should be hided.
		 */
		void onDragStart();

		/**
		 * Calls after ACTION_UP or ACTION_CANCEL event.
		 * E.g. user finished dragging.
		 * Last position of draggable could be received with {@link IDragViewGroup#onMoveEvent}
		 * <p/>
		 * !!!
		 * Finally must be called {@link DragController#finishDrag()}
		 * !!!
		 */
		void onDragEnd();

		void onMoveEvent(float x, float y);

		DraggableView createDraggableView(Bitmap bitmap,
		                                  VelocityTracker velocityTracker,
		                                  PointF selectedViewPoint,
		                                  PointF downEventPoint
		);
	}

}
