package su.levenetc.android.draggableview.utils;

import android.view.MotionEvent;
import android.view.VelocityTracker;

/**
 * Created by Eugene Levenetc.
 */
public class VelocityVector {

	private static final int DENSITY_XXXHIGH = 640;

	private final AverageValueFilter xVelSmooth = new AverageValueFilter(10, false);
	private final AverageValueFilter yVelSmooth = new AverageValueFilter(10, false);
	private final VelocityTracker velocityTracker;
	private final int densityDpi;
	private float xVelocity;
	private float yVelocity;
	private float maxVel = Float.MAX_VALUE;

	public VelocityVector(VelocityTracker velocityTracker, int densityDpi) {
		this.velocityTracker = velocityTracker;
		this.densityDpi = densityDpi;
	}

	public void setMaxVel(float maxVel) {
		this.maxVel = maxVel;
	}

	public void addMovement(MotionEvent event) {
		velocityTracker.addMovement(event);
		velocityTracker.computeCurrentVelocity(DENSITY_XXXHIGH / densityDpi, maxVel);
		xVelocity = xVelSmooth.handle(velocityTracker.getXVelocity());
		yVelocity = yVelSmooth.handle(velocityTracker.getYVelocity());
	}

	public float getXVelocity() {
		return xVelocity;
	}

	public float getYVelocity() {
		return yVelocity;
	}
}
