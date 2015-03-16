package com.zmy.handwritingdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.zmy.handwritingdemo.R;
import com.zmy.handwritingdemo.other.HandDrawListener;
import com.zmy.handwritingdemo.utils.CompatUtils;
import com.zmy.handwritingdemo.utils.ImageUtils;

public class HandDrawView2 extends SurfaceView {
	private static final int MAX_WIDTH = 40;
	private static final double MAX_WIDTH_STEP = 0.1D;
	private static final int STAND_WIDTH = 20;
	private int autoCommitTime = 1200;
	private HandDrawListener mHandDrawListener;
	private Handler mHandler;
	private PointTracker mLastPoint;
	private double mLastSpeed = 0.0D;
	private Matrix matrix = new Matrix();
	private Bitmap tmpBitmap;
	private Paint mForeGroundPaint = new Paint(5);
	private Rect dstRect = new Rect();
	private Paint mBackGroundPaint = new Paint(5);
	private Bitmap mDot;
	private Bitmap mDot2;
	private double mMaxWidth;
	private PointTracker mDrawPoint = new PointTracker();

	private double mStandWidth;// 标准宽度
	private double mDensity;

	private QuadraticBezierSpline2 mBezier = new QuadraticBezierSpline2();

	public void setHandDrawListener(HandDrawListener mHandDrawListener,
			Handler handler) {
		this.mHandDrawListener = mHandDrawListener;
		mHandler = handler;
	}

	public void setHandDrawListener(HandDrawListener mHandDrawListener) {
		this.mHandDrawListener = mHandDrawListener;
	}

	public void setAutoCommitTime(int min) {
		this.autoCommitTime = min;
	}

	public HandDrawView2(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
//		init(context);
	}

	public HandDrawView2(Context context, AttributeSet attrs) {
		super(context, attrs);
//		TypedArray a = context.obtainStyledAttributes(attrs,
//				R.styleable.HandDrawView);
//		paintBeforeBackRes = a.getResourceId(
//				R.styleable.HandDrawView_paintBeforeSrc, -1);
//		a.recycle();
		//init(context);
	}

	public HandDrawView2(Context context) {
		super(context);
//		init(context);
	}

	private Paint mPaint = new Paint(5);
	private MaskFilter mEmboss;
	private MaskFilter mBlur;

	private Context mContext;

	private int paintBeforeBackRes = -1;
	private Bitmap paintBeforeBit = null;

	public void setDrawColor(int color) {
		mPaint.setColor(color);
	}

	public void setDrawWidth(int width) {
		mPaint.setStrokeWidth(width);
	}

	private void init(Context context) {
		mContext = context;

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);

		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		if (paintBeforeBackRes != -1) {
			paintBeforeBit = getBitmapFormRes(paintBeforeBackRes);
		}
	}
	private Bitmap getBitmapFormRes(int res) {
		return BitmapFactory.decodeResource(getResources(), res);
	}

	private static final float MINP = 0.25f;
	private static final float MAXP = 0.75f;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;

	int width, height;
	private void changeBitmapSize(int w, int h) {
		width = w;
		height = h;
		refreshBitmap();
	}
	private void refreshBitmap() {
		mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		changeBitmapSize(w, h);
		init(getContext().getResources().getDisplayMetrics().density, w, h, ratio);
	}

	@Override
	protected void onDraw(Canvas paramCanvas) {
		paramCanvas.drawBitmap(this.mBitmap, 0.0F, 0.0F, this.mPaint);
		super.onDraw(paramCanvas);
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private CompleteTask lastCompleteTask = null;
	public static final int PAINTED = 0;// 完成一笔画，或开始状态
	public static final int PAINTINT = 1;// 正在涂鸦
	public static final int COMMITED = 2;// 完成一次涂鸦提交

	private int currentState = 0;

	class CompleteTask implements Runnable {
		public boolean isCancel = false;

		public void cancel() {
			isCancel = true;
		}

		@Override
		public void run() {
			if (!isCancel) {
				if (mHandDrawListener != null) {
					mHandDrawListener.onDrawableCommit(mBitmap);
					// mHandDrawListener.onDrawableCommit(getPathBitmap());
					changePaintState(COMMITED);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param paint
	 *            是否正在涂鸦
	 */
	public synchronized void changePaintState(int state) {
		switch (state) {
		// 如果改变为涂鸦状态
		case PAINTINT:
			currentState = PAINTINT;
			// 取消上次涂鸦提交
			if (lastCompleteTask != null) {
				lastCompleteTask.cancel();
			}
			break;
		case PAINTED:
			currentState = PAINTED;
			lastCompleteTask = new CompleteTask();
			// 添加自动提交任务
			if (mHandler != null) {
				mHandler.postDelayed(lastCompleteTask, autoCommitTime);
			} else {
				AsyncHandler.postDelay(lastCompleteTask, autoCommitTime);
			}
			break;
		case COMMITED:
			currentState = COMMITED;
			clear();
		default:
			break;
		}
	}

	/**
	 * 清空前面涂鸦
	 */
	private void clear() {
		refreshBitmap();
		postInvalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("zmy", "on Touch for writeview2 called.");
		int i = CompatUtils.getActionMasked(event);
		if (i == 0) {
			changePaintState(PAINTINT);
			// Action down
			this.mBezier.clear();
		}
		drawToPoint(event.getX(), event.getY(), event.getEventTime());
		double x = event.getX();
		double y = event.getY();
		double d = 10;
//		drawPoint(new PointTracker(x,y,d));
		if (i == 1) {
			changePaintState(PAINTED);
			// Action up
			drawToPoint(event.getX(), event.getY(), event.getEventTime());
//			drawPoint(new PointTracker(x,y,d));
		}

		if (i == 2) {
//			Log.e("zmy", "x:"+event.getX()+" , y:"+event.getY());
		}
		return true;
	}

	float ratio = 0.5f;
	int WORD_HEIGHT = 200;
	
	private void drawToPoint(float pointx, float pointy, long pointTime) {
		if (this.mLastPoint == null) {
			this.mLastPoint = new PointTracker(pointx, pointy);
			this.mLastPoint.time = pointTime;
			this.mLastPoint.width = (2.0D * (this.mStandWidth * ratio));
		}
		
		
		double pointDx = pointx - this.mLastPoint.x; // 相对于上一次位置的X位移
		double pointDy = pointy - this.mLastPoint.y; // 相对于上一次位置的Y位移
		
		
		double disStep = Math.sqrt(pointDx * pointDx + pointDy * pointDy) / this.mDensity / this.mDensity; // 位移距离的函数
		
		double tx = pointDx / this.mDensity / this.mDensity;
		double ty = pointDy / this.mDensity / this.mDensity;
		Log.e("zmy","d:"+tx+"    "+ty);
		
		double stepSpeed; // 本次位移的平均速度
		double stepWidth; // stepWidth
		double nextWidth = 0; // nextWidth
		double stepDx = 0; // stepX
		double stepDy = 0;// stepY
		double lastDrawPointx;// lastCx
		double lastDrawPointy;// lastCy
		int j;
		if (pointTime == this.mLastPoint.time) {
			stepSpeed = 1.0D;
		} else{
			stepSpeed = disStep / (pointTime - this.mLastPoint.time); // d4是用来计算新的speed的，d4
		}
		double lastSpeed = 0.699999988079071D * this.mLastSpeed
				+ 0.300000011920929D * stepSpeed; // 最终速度将由原速度和新的速度合成
		this.mLastSpeed = lastSpeed;
		int drawSteps = (int) disStep*2;
		if (drawSteps == 0) {
			drawSteps = 1;
		}
		// 根据划动的速度计算初始画笔触的宽度（越快则越细）
		double tempWidth = 3.0D * (this.mStandWidth * ratio) / (0.5D + lastSpeed);
		if (tempWidth > this.mMaxWidth) {
			tempWidth = this.mMaxWidth;
		}
		stepWidth = (tempWidth - this.mLastPoint.width) / drawSteps;
		if (Math.abs(stepWidth) > 0.1D) // if(Math.abs(d7) > MAX_WIDTH_STEP)
		{
			if (stepWidth <= 0.0D) {
				stepWidth = -0.1D;
			} else {
				stepWidth = 0.1D; // d7为正数的情况
			}
		}
		nextWidth = this.mLastPoint.width + stepWidth * drawSteps; // 最终绘画笔触点的宽度
		stepDx = pointDx / drawSteps;
		stepDy = pointDy / drawSteps;
		Log.e("Write", "speed is " + lastSpeed + "\n next width is " + nextWidth
				+ "\n diff is " + drawSteps);
		////////////////////////////////////////////////////////
		this.mBezier.addPoint(pointx, pointy, (float) nextWidth);
		
		lastDrawPointx = -1.0D;
		lastDrawPointy = -1.0D;
		for (j = 0; j < drawSteps; j++) {
			if (this.mDensity < 0.9D) {
				// 低密度手机不使用Bezier曲线的方式绘画
				this.mDrawPoint.x = ((int) (this.mLastPoint.x + stepDx * j)); // d9 * j
				// -->
				// d1 *
				// j / i
				// 即x位移的插值
				this.mDrawPoint.y = ((int) (this.mLastPoint.y + stepDy * j)); // 原理同上，y位移的插值
				this.mDrawPoint.width = ((int) (this.mLastPoint.width + stepWidth * j)); // 原理同上，画笔宽度变化的插值
			} else {
				// 通过Bezier曲线算法计算下一个绘画的坐标点
				this.mBezier.getPoint(this.mDrawPoint, 1.0F * j / drawSteps);
			}
			if ((this.mDrawPoint.x != lastDrawPointx) || (this.mDrawPoint.x != lastDrawPointy)) {
				// 此点不同于上一次绘画的点，因此跳转执行drawPoint方法
				lastDrawPointx = this.mDrawPoint.x;
				lastDrawPointy = this.mDrawPoint.y;
				drawPoint(this.mDrawPoint);
//				this.dstRect.bottom += 4;
//				this.dstRect.top -=4;
				invalidate(this.dstRect);
			}
		}
		this.mLastPoint.x = this.mDrawPoint.x;
		this.mLastPoint.y = this.mDrawPoint.y;
		this.mLastPoint.time = pointTime;
		this.mLastPoint.width = nextWidth;

	}

	private void drawPoint(PointTracker paramPointTracker) {
		double pianyi = 0.6;
		
		this.dstRect.left = ((int) (paramPointTracker.x - pianyi * (paramPointTracker.width / 2.0D)));
		this.dstRect.top = ((int) (paramPointTracker.y - pianyi * (paramPointTracker.width / 2.0D)));
		this.dstRect.right = ((int) (paramPointTracker.x + pianyi * (paramPointTracker.width / 2.0D)));
		this.dstRect.bottom = ((int) (paramPointTracker.y + pianyi * (paramPointTracker.width / 2.0D)));
		this.mCanvas.drawBitmap(this.mDot2, null, this.dstRect,
				this.mForeGroundPaint);
		this.dstRect.left = ((int) (paramPointTracker.x - paramPointTracker.width / 2.0D));
		this.dstRect.top = ((int) (paramPointTracker.y - paramPointTracker.width / 2.0D));
		this.dstRect.right = ((int) (paramPointTracker.x + paramPointTracker.width / 2.0D));
		this.dstRect.bottom = ((int) (paramPointTracker.y + paramPointTracker.width / 2.0D));
		this.mCanvas.drawBitmap(this.mDot, null, this.dstRect,
				this.mBackGroundPaint);
	}

	public void init(float density, int width, int height, float ratio) {
		this.mDensity = density;
		this.mStandWidth = (20.0D * this.mDensity);
		if (ratio > 0.0F) {
			this.mStandWidth *= ratio;
		}
		this.mMaxWidth = (40.0D * this.mDensity);
		Log.d("Write", "density is " + this.mDensity);
		this.mBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		this.tmpBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		this.mCanvas = new Canvas(this.tmpBitmap);
		this.mCanvas.drawColor(-1); // Set bitmap to white
		this.mCanvas.setBitmap(this.mBitmap);
		this.mCanvas.drawColor(0);
		this.mDot = ImageUtils.decodeResourcesBitmap(getContext(), R.drawable.shufa4_alpha);
		this.mDot2 = ImageUtils.decodeResourcesBitmap(getContext(), R.drawable.shufa2);
		this.mForeGroundPaint.setAlpha(200);
		this.mBackGroundPaint.setAlpha(40);
		this.mPaint.setColor(-16777216);
		float f = (float) (1.0D * WORD_HEIGHT / this.mBitmap.getHeight());
		this.matrix.postScale(f, f);
	}

}
