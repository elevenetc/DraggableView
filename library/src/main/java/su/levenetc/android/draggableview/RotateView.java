package su.levenetc.android.draggableview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.VelocityTracker;

/**
 * Created by Eugene Levenetc.
 */
public class RotateView extends DraggableView {

	protected static final float ROT_MULT = 10f;

	public RotateView(
			Context context,
			Bitmap bitmap,
			VelocityTracker velocityTracker,
			PointF selectedViewPoint,
			PointF downEventPoint,
			DragController.IDragViewGroup viewGroup) {
		super(
				context,
				bitmap,
				velocityTracker,
				selectedViewPoint,
				downEventPoint,
				viewGroup
		);
	}

	@Override protected boolean render(Canvas canvas) {
		float rotY = 0;
		float rotX = 0;

		if (motionEvent != null) {
			velocityVector.addMovement(motionEvent);
			rotY = ROT_MULT * velocityVector.getXVelocity();
			rotX = ROT_MULT * velocityVector.getYVelocity() * -1;
		}

		canvas.save();
		canvas.translate(xCanvasTranslation, yCanvasTranslation);
		final float translateX = bitmap.getWidth() / 2;
		final float translateY = bitmap.getHeight() / 2;

		camera.getMatrix(cameraMatrix);
		camera.save();
		camera.rotateY(rotY);
		camera.rotateX(rotX);
		camera.getMatrix(cameraMatrix);

		cameraMatrix.preTranslate(-translateX, -translateY);
		cameraMatrix.postTranslate(translateX, translateY);

		camera.restore();

		canvas.concat(cameraMatrix);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.restore();

		return !(rotX == 0) || !(rotY == 0);
	}
}
