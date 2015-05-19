package pl.kurbanek.sterownik2;


	import android.graphics.Bitmap;
	import android.graphics.Canvas;
	import android.graphics.PointF;

	public class Segment {
		
		private final Bitmap bitmap;
		
		private float rotation;
		
		private PointF prevPivot;
		
		private PointF nextPivot;
		
		public Segment(Bitmap bitmap, float initialRotation, PointF prevPivot, PointF nextPivot) {
			this.bitmap = bitmap;
			this.rotation = initialRotation;
			this.prevPivot = prevPivot;
			this.nextPivot = nextPivot;
		}
		
		public Bitmap getBitmap() {
			return bitmap;
		}
		
		public float getRotation() {
			return rotation;
		}

		public void draw(Canvas canvas) {
			canvas.rotate(rotation);
			canvas.translate(-prevPivot.x * bitmap.getWidth(), -prevPivot.y * bitmap.getHeight());
			canvas.drawBitmap(bitmap, 0, 0, null);
			canvas.translate(nextPivot.x * bitmap.getWidth(), nextPivot.y * bitmap.getHeight());
		}

		public void rotate(float angle) {
			this.rotation += angle;
		}

	}

