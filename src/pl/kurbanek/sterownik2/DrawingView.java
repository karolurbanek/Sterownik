package pl.kurbanek.sterownik2;

	import static android.graphics.BitmapFactory.decodeResource;

	import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

	public class DrawingView extends View {
		
		private final Segment[] segments = new Segment[3];
		
		private final float[] angles = new float[3];

		public DrawingView(Context context) {
			super(context);
			init(context);
		}
		
		public DrawingView(Context context, AttributeSet attrs) {
			super(context, attrs);
			init(context);
		}
		
		public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
			super(context, attrs, defStyleAttr);
			init(context);
		}

		private void init(Context context) {
			Resources res = context.getResources();
			segments[0] = new Segment(decodeResource(res, R.drawable.podstawa), 0, new PointF(0.5f, 0.5f), new PointF(0.97f, 0.53f));
			segments[1] = new Segment(decodeResource(res, R.drawable.stol), 0, new PointF(0.852f, 0.94f), new PointF(0.03f, 0.05f));
			segments[2] = new Segment(decodeResource(res, R.drawable.ramie), 0, new PointF(0.02f, 0.95f), new PointF(0, 0));
		}
		
		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			if(isInEditMode())
				return;
			canvas.save();
			//canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
			canvas.translate(canvas.getWidth() / 2, canvas.getHeight()-70);

			
			for (Segment s : segments) {
				s.draw(canvas);
			}
			canvas.restore();
			update();
			invalidate();
		}

		private void update() {
			for (int i = 0; i < angles.length; i++) {
				float f = angles[i] - segments[i].getRotation();
				segments[i].rotate(f / 16);
			}
		}
		
		public void rotateSegment(int which, float angle) {
			angles[which] += angle;
		}

	}
