package com.zmy.handwritingdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zmy.handwritingdemo.R;
import com.zmy.handwritingdemo.other.HandDrawListener;

public class HandDrawView extends View {
	private int autoCommitTime = 1200;
	private HandDrawListener mHandDrawListener;
	private Handler mHandler;

	/**
	 * 处理提交图片，如果在主线程使用可以传递handler
	 * 
	 * @param mHandDrawListener
	 * @param handler
	 *            如果为空，程序会新建线程处理提交任务
	 */
	public void setHandDrawListener(HandDrawListener mHandDrawListener,
			Handler handler) {
		this.mHandDrawListener = mHandDrawListener;
		mHandler = handler;
	}

	/**
	 * 设置图片提交监听
	 * 
	 * @param mHandDrawListener
	 */
	public void setHandDrawListener(HandDrawListener mHandDrawListener) {
		this.mHandDrawListener = mHandDrawListener;
	}

	/**
	 * 设置自动提交时间间隔，毫秒
	 * 
	 * @param min
	 */
	public void setAutoCommitTime(int min) {
		this.autoCommitTime = min;
	}

	public HandDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public HandDrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.HandDrawView);
		paintBeforeBackRes = a.getResourceId(
				R.styleable.HandDrawView_paintBeforeSrc, -1);
		a.recycle();
		init(context);
	}

	public HandDrawView(Context context) {
		super(context);
		init(context);
	}

	private Paint mPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;

	private Context mContext;

	private int paintBeforeBackRes = -1;
	private Bitmap paintBeforeBit = null;

	/**
	 * 设置画笔颜色
	 * 
	 * @param color
	 */
	public void setDrawColor(int color) {
		mPaint.setColor(color);
	}

	/**
	 * 设置画笔粗细
	 * 
	 * @param width
	 */
	public void setDrawWidth(int width) {
		mPaint.setStrokeWidth(width);
	}

	/**
	 * 初始化，画笔等。。。
	 * 
	 * @param context
	 */
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

		// 初始话背景图片
		if (paintBeforeBackRes != -1) {
			paintBeforeBit = getBitmapFormRes(paintBeforeBackRes);
		}
	}

	/**
	 * 获取资源图片
	 * 
	 * @param res
	 * @return
	 */
	private Bitmap getBitmapFormRes(int res) {
		return BitmapFactory.decodeResource(getResources(), res);
	}

	private static final float MINP = 0.25f;
	private static final float MAXP = 0.75f;

	private Bitmap mBitmap;// 涂鸦
	private Canvas mCanvas;// 画布
	private Path mPath;
	private Paint mBitmapPaint;

	int width, height;// 图片尺寸

	/**
	 * 修改画布尺寸
	 * 
	 * @param w
	 * @param h
	 */
	private void changeBitmapSize(int w, int h) {
		width = w;
		height = h;
		refreshBitmap();
	}

	/**
	 * 生成新画布
	 */
	private void refreshBitmap() {
		mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		changeBitmapSize(w, h);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawColor(0xFFAAAAAA);
		// 画背景
		if (paintBeforeBit != null && !paintBeforeBit.isRecycled()) {
			canvas.drawBitmap(paintBeforeBit, 0, 0, mBitmapPaint);
		}
		// 画已经画过的涂鸦
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		// 画本次涂鸦
		canvas.drawPath(mPath, mPaint);
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private CompleteTask lastCompleteTask = null;
	public static final int PAINTED = 0;// 完成一笔画，或开始状态
	public static final int PAINTINT = 1;// 正在涂鸦
	public static final int COMMITED = 2;// 完成一次涂鸦提交

	private int currentState = 0;

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

	class CompleteTask implements Runnable {
		public boolean isCancel = false;

		public void cancel() {
			isCancel = true;
		}

		@Override
		public void run() {
			if (!isCancel && currentState == PAINTED) {
				if (mHandDrawListener != null) {
					// mHandDrawListener.onDrawableCommit(mBitmap);
					mHandDrawListener.onDrawableCommit(getPathBitmap());
					changePaintState(COMMITED);
				}
			}
		}
	}

	/**
	 * 清空前面涂鸦
	 */
	private void clear() {
		refreshBitmap();
		reInit();//
		// invalidate();
		postInvalidate();
	}

	private void reInit() {
		Lside = 10000000;
		Rside = 0;// 左右边界
		Tside = 10000000;
		Bside = 0;// 上下边界
	}

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		changePaintState(PAINTINT);
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath.reset();
		changePaintState(PAINTED);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		changeSideIfNeeded(x, y);// 修改确定涂鸦的边界，为后续剪裁做准备

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}

	// 剪裁边界
	private Bitmap getPathBitmap() {
		// 剪裁后的宽度，两边加5像素
		int lx = (Math.round(Lside) - 5) > 0 ? Math.round(Lside) - 5 : 0;
		int rx = Math.round(Rside) + 5 < mBitmap.getWidth() ? Math.round(Rside) + 5
				: mBitmap.getWidth();
		// int y = Math.round(Tside);
		int w = rx - lx;
		Log.d("zmy", "w:" + mBitmap.getWidth());
		Bitmap bit = Bitmap.createBitmap(mBitmap, lx, 0, w, height);
		Log.d("zmy", "w:" + bit.getWidth());
		return bit;
	}

	private float Lside = 10000000, Rside = 0;// 左右边界
	private float Tside = 10000000, Bside = 0;// 上下边界

	// 计算图片边界
	private void changeSideIfNeeded(float x, float y) {
		if (x < 0) {
		} else {
			if (x < Lside) {
				Lside = x;
			}
			if (x > Rside) {
				Rside = x;
			}
		}

		if (y < 0) {
		} else {
			if (y < Tside) {
				Tside = y;
			}
			if (y > Bside) {
				Bside = y;
			}
		}
	}

}
