# DraggableView
Draggable views with rotation and skew/scale effects.

![animation](docs/skewview.gif)
![animation](docs/rotateview.gif)

### Usage
1. Implement [`DragController.IDragViewGroup`](library/src/main/java/su/levenetc/android/draggableview/DragController.java)
2. Create instance of [`DragController`](library/src/main/java/su/levenetc/android/draggableview/DragController.java)
3. Override `onTouchEvent` of your `ViewGroup` and call `DragController#onTouchEvent`:
```Java
@Override public boolean onTouchEvent(MotionEvent event) {
	return dragController.onTouchEvent(event);
}
```
See full sample at [`SampleGridContainer`](sample/src/main/java/su/levenetc/android/sample/SampleGridContainer.java)
### Animation adjustments
[`SkewView`](library/src/main/java/su/levenetc/android/draggableview/SkewView.java) and [`RotateView`](library/src/main/java/su/levenetc/android/draggableview/RotateView.java) containe multipliers which change rotation, skew and scale values.
### Licence
http://www.apache.org/licenses/LICENSE-2.0
