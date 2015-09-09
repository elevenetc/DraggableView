package su.levenetc.android.draggableview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;

import su.levenetc.android.draggableview.utils.VelocityVector;

/**
 * Created by Eugene Levenetc.
 */
public abstract class DraggableView extends View {

	protected static final float MAX_VEL = 30f;

	protected final Paint paint = new Paint();
	protected final Camera camera = new Camera();
	protected final Matrix cameraMatrix = new Matrix();
	protected final VelocityVector velocityVector;

	protected Bitmap bitmap;
	protected PointF downEventPoint;
	private DragController.IDragViewGroup dragViewGroup;
	protected MotionEvent motionEvent;
	protected final float xCanvasTranslation;
	protected final float yCanvasTranslation;

	protected DraggableView(
			Context context,
			Bitmap bitmap,
			VelocityTracker velocityTracker,
			PointF selectedViewPoint,
			PointF downEventPoint,
			DragController.IDragViewGroup dragViewGroup) {
		super(context);
		this.bitmap = bitmap;
		this.downEventPoint = downEventPoint;
		this.dragViewGroup = dragViewGroup;

		velocityVector = new VelocityVector(
				velocityTracker,
				getResources().getDisplayMetrics().densityDpi
		);

		//TODO: replace workaround with scaling
		//it is used to enlarge canvas zone
		//otherwise camera's frustum cuts bitmap
		float scale = 0.9f;
		xCanvasTranslation = (int) (bitmap.getWidth() * scale);
		yCanvasTranslation = (int) (bitmap.getWidth() * scale);

		int width = (int) (bitmap.getWidth() + xCanvasTranslation * 2);
		int height = (int) (bitmap.getHeight() + yCanvasTranslation * 2);

		setX(selectedViewPoint.x - xCanvasTranslation);
		setY(selectedViewPoint.y - yCanvasTranslation);

		setLayoutParams(new ViewGroup.LayoutParams(width, height));

		setWillNotDraw(false);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);

		velocityVector.setMaxVel(MAX_VEL);
	}

	protected abstract boolean render(Canvas canvas);

	@Override protected void onDraw(Canvas canvas) {
		boolean isUpdated = render(canvas);
		if (isUpdated) {
			postInvalidateDelayed(1000 / 60);
		}
	}

	public void onMoveAction(MotionEvent event) {
		this.motionEvent = event;
		setX(event.getX() - downEventPoint.x - xCanvasTranslation);
		setY(event.getY() - downEventPoint.y - yCanvasTranslation);
		invalidate();
		dragViewGroup.onMoveEvent(
				event.getX() - downEventPoint.x,
				event.getY() - downEventPoint.y
		);
	}

	public float getXTranslation() {
		return xCanvasTranslation;
	}

	public float getYTranslation() {
		return yCanvasTranslation;
	}
}
