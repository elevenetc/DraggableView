package su.levenetc.android.draggableview.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Eugene Levenetc.
 */
public class Utils {

	public static float dpToPx(float dp, Context context) {
		if (dp <= 0) return 0;
		Resources r = context.getResources();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	}

	public static boolean isViewContains(View view, int x, int y, boolean useLocOnScreen) {

		int viewX;
		int viewY;

		if (useLocOnScreen) {
			int[] loc = new int[2];
			view.getLocationOnScreen(loc);
			viewX = loc[0];
			viewY = loc[1];
		} else {
			viewX = (int) view.getX();
			viewY = (int) view.getY();
		}

		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		return !(x < viewX || x > viewX + viewWidth || y < viewY || y > viewY + viewHeight);
	}
}
