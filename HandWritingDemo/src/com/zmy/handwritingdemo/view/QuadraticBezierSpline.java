package com.zmy.handwritingdemo.view;

import android.util.Log;

public class QuadraticBezierSpline {
	private PointTracker mControlPoint = new PointTracker();
	private PointTracker mFirstPoint = new PointTracker();
	private PointTracker mNextPoint = new PointTracker();
	private PointTracker mSecondPoint = new PointTracker();
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
				this.mSecondPoint.x, paramDouble);
	}

	private double getY(double paramDouble) {
		return get(this.mFirstPoint.y, this.mControlPoint.y,
				this.mSecondPoint.y, paramDouble);
	}

	public void addPoint(double pointx, double pointy, double width) {
		if (!this.mStarted) {
			this.mFirstPoint.set(pointx, pointy, width);
			this.mSecondPoint.set(pointx, pointy, width);
			this.mControlPoint.set(pointx, pointy, width);
			this.mNextPoint.set(pointx, pointy, width);
			this.mStarted = true;
		}
		this.mFirstPoint.set(this.mControlPoint);
		this.mControlPoint.set(this.mSecondPoint);
		
		Log.e("zmy", "width:"+width);
		
//		this.mSecondPoint.set(
//				(this.mControlPoint.x + this.mNextPoint.x) / 2.0D,
//				(this.mControlPoint.y + this.mNextPoint.y) / 2.0D,
//				(this.mControlPoint.width + this.mNextPoint.width) / 2.0D);
		double r = 0.3;
		this.mSecondPoint.set(
				((1 - r) * mControlPoint.x + r * mNextPoint.x),
				((1 - r) * mControlPoint.y + r * mNextPoint.y), 
				((1 - r) * mControlPoint.width + r * mNextPoint.width));
		
		this.mNextPoint.set(pointx, pointy, width);
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
