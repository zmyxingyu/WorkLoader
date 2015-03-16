package com.zmy.handwritingdemo.view;

import android.util.Log;

public class QuadraticBezierSpline2 {
	private PointTracker mControlPoint = new PointTracker();
	private PointTracker mFirstPoint = new PointTracker();
	private PointTracker mNextPoint = new PointTracker();
	private PointTracker mSecondPoint = new PointTracker();
	
	private PointTracker mNNPoint = new PointTracker();
	
	
	private boolean mStarted = false;

	private double get(double pointF, double pointC, double pointS, double step) {
//		return pointF + step * (2.0D * pointC - 2.0D * pointF) + step
//				* (step * (pointS + (pointF - 2.0D * pointC)));
		
		return (1-step)*(1-step)*pointF+2*step*(1-step)*pointC+step*step*pointS;
	}
	
	private double get(double pointF, double pointC, double pointS, double pointN,double step){
		return Math.pow((1-step),3)*pointF +3*pointC*step*Math.pow((1-step),2)+3*pointS*step*step*(1-step)
				+ pointN*Math.pow((step),3);
	}

	private double getWidth(double paramDouble) {
		return get(this.mFirstPoint.width, this.mControlPoint.width,
				this.mSecondPoint.width, paramDouble);
	}

	private double getX(double paramDouble) {
		return get(this.mFirstPoint.x, this.mControlPoint.x,
				this.mSecondPoint.x, mNextPoint.x,paramDouble);
	}

	private double getY(double paramDouble) {
		return get(this.mFirstPoint.y, this.mControlPoint.y,
				this.mSecondPoint.y,mNextPoint.y, paramDouble);
	}

	public void addPoint(double pointx, double pointy, double width) {
		if (!this.mStarted) {
			this.mFirstPoint.set(pointx, pointy, width);
			this.mSecondPoint.set(pointx, pointy, width);
			this.mControlPoint.set(pointx, pointy, width);
			this.mNextPoint.set(pointx, pointy, width);
			
			this.mNNPoint.set(pointx, pointy, width);
			this.mStarted = true;
		}
		
		int TOUCH_TOLERANCE = 32;
		
//		if (Math.abs(pointx - mNextPoint.x) >= TOUCH_TOLERANCE
//				|| Math.abs(pointy - mNextPoint.y) >= TOUCH_TOLERANCE) {
//			mSecondPoint.set((pointx+mNextPoint.x)/2,
//					(pointy+mNextPoint.y)/2, (width+mNextPoint.width)/2);
//			mControlPoint.set(mNextPoint);
//			mNextPoint.set(pointx, pointy, width);
//			mFirstPoint.set(mSecondPoint);
//		}else{
//			mFirstPoint.set(mControlPoint);
//			mControlPoint.set(mSecondPoint);
//			mSecondPoint.set(mNextPoint);
//			mNextPoint.set(pointx, pointy, width);
//		}
		
//		this.mFirstPoint.set(this.mSecondPoint);
//		this.mSecondPoint.set(this.mNextPoint);
//		this.mNextPoint
//				.set((pointx + mNNPoint.x) / 2,
//						(pointy + mNNPoint.y) / 2,
//						(width + mNNPoint.width) / 2);
//		this.mControlPoint.set((mFirstPoint.x + mSecondPoint.x) / 2,
//				(mFirstPoint.y + mSecondPoint.y) / 2,
//				(mFirstPoint.width + mSecondPoint.width) / 2);
//		this.mNNPoint.set(pointx, pointy, width);
		
		this.mFirstPoint.set(this.mSecondPoint);
		this.mSecondPoint.set(this.mNextPoint);
		this.mNextPoint.set(pointx, pointy, width);

		this.mNextPoint
				.set((pointx + mSecondPoint.x) / 2,
						(pointy + mSecondPoint.y) / 2,
						(width + mSecondPoint.width) / 2);
		this.mControlPoint.set((mFirstPoint.x + mSecondPoint.x) / 2,
				(mFirstPoint.y + mSecondPoint.y) / 2,
				(mFirstPoint.width + mSecondPoint.width) / 2);
		
	}

	public void clear() {
		this.mStarted = false;
	}

	public void getPoint(PointTracker pointTracker, double stepW) {
		if (this.mStarted) {
			pointTracker.set(getX(stepW), getY(stepW), getWidth(stepW));
		}
	}
}
