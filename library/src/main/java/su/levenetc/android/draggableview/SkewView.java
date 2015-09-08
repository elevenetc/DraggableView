package su.levenetc.android.draggableview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.VelocityTracker;

/**
 * Created by Eugene Levenetc.
 */
public class SkewView extends DraggableView {

	private static final float SKEW_MULT = 15f;
	private static final float SCALE_MULT = 5f;

	/**
	 * Multipliers which depends on down touch event location
	 */
	private float xSkewMult;
	private float ySkewMult;
	private float xScaleMult;
	private float yScaleMult;

	public SkewView(
			Context context,
			Bitmap bitmap,
			VelocityTracker velocityTracker,
			PointF selectedViewPoint,
			PointF downEventPoint,
			DragController.IDragViewGroup dragViewGroup) {
		super(
				context,
				bitmap,
				velocityTracker,
				selectedViewPoint,
				downEventPoint,
				dragViewGroup
		);

		float xLoc = downEventPoint.x / bitmap.getWidth() - 0.5f;
		float yLoc = downEventPoint.y / bitmap.getWidth() - 0.5f;

		xScaleMult = xLoc * -1;
		yScaleMult = yLoc * -1;

		xSkewMult += Math.signum(xLoc) / 2f;
		ySkewMult += Math.signum(yLoc) / 2f;

		if (Math.signum(ySkewMult) != Math.signum(xSkewMult)) {
			xSkewMult *= -1;
			ySkewMult *= -1;
		}

		xSkewMult *= Math.abs(yLoc);
		ySkewMult *= Math.abs(xLoc);
	}

	@Override protected boolean render(Canvas canvas) {

		float scaleVelY = 0;
		float scaleVelX = 0;

		float skewVelY = 0;
		float skewVelX = 0;
		float skewTransX = 0;
		float skewTransY = 0;

		if (motionEvent != null) {
			velocityVector.addMovement(motionEvent);
			float velX = velocityVector.getXVelocity();
			float velY = velocityVector.getYVelocity();

			skewVelX = SKEW_MULT * (velX / MAX_VEL * xSkewMult);
			skewVelY = SKEW_MULT * (velY / MAX_VEL * ySkewMult);

			scaleVelX = SCALE_MULT * (velX / MAX_VEL * xScaleMult);
			scaleVelY = SCALE_MULT * (velY / MAX_VEL * yScaleMult);
		}

		canvas.save();
		canvas.translate(xCanvasTranslation, yCanvasTranslation);
		final float translateX = bitmap.getWidth() / 2;
		final float translateY = bitmap.getHeight() / 2;

		camera.getMatrix(cameraMatrix);
		camera.save();

		camera.getMatrix(cameraMatrix);
		cameraMatrix.setSkew(2f * skewVelX, 2f * skewVelY);
		cameraMatrix.preScale(1f - 1f * scaleVelX, 1f - 1f * scaleVelY);

		cameraMatrix.preTranslate(-translateX, -translateY);
		cameraMatrix.postTranslate(translateX + skewTransX, translateY + skewTransY);
		camera.restore();

		canvas.concat(cameraMatrix);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.restore();

		return !(skewVelX == 0) || !(skewVelY == 0);
	}
}
