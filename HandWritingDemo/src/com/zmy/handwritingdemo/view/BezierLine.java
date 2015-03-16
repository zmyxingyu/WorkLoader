package com.zmy.handwritingdemo.view;
/**
 * 
 * @author zhangmy
 *
 */
public class BezierLine {
	private DrawPoint mControlPoint = new DrawPoint();
	private DrawPoint mFirstPoint = new DrawPoint();
	private DrawPoint mNextPoint = new DrawPoint();
	private DrawPoint mSecondPoint = new DrawPoint();

	private DrawPoint mNNPoint = new DrawPoint();

	private boolean mStarted = false;

	private double get(double pointF, double pointC, double pointS, double step) {

		return (1 - step) * (1 - step) * pointF + 2 * step * (1 - step)
				* pointC + step * step * pointS;
	}

	private double get(double pointF, double pointC, double pointS,
			double pointN, double step) {
		return Math.pow((1 - step), 3) * pointF + 3 * pointC * step
				* Math.pow((1 - step), 2) + 3 * pointS * step * step
				* (1 - step) + pointN * Math.pow((step), 3);
	}

	private double getWidth(double paramDouble) {
		return get(this.mFirstPoint.width, this.mControlPoint.width,
				this.mSecondPoint.width, paramDouble);
	}

	private double getX(double paramDouble) {
		return get(this.mFirstPoint.pointX, this.mControlPoint.pointX,
				this.mSecondPoint.pointX, mNextPoint.pointX, paramDouble);
	}

	private double getY(double paramDouble) {
		return get(this.mFirstPoint.pointY, this.mControlPoint.pointY,
				this.mSecondPoint.pointY, mNextPoint.pointY, paramDouble);
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

		this.mFirstPoint.set(this.mSecondPoint);
		this.mSecondPoint.set(this.mNextPoint);
		this.mNextPoint.set(pointx, pointy, width);

		this.mNextPoint.set((pointx + mSecondPoint.pointX) / 2,
				(pointy + mSecondPoint.pointY) / 2,
				(width + mSecondPoint.width) / 2);
		this.mControlPoint.set((mFirstPoint.pointX + mSecondPoint.pointX) / 2,
				(mFirstPoint.pointY + mSecondPoint.pointY) / 2,
				(mFirstPoint.width + mSecondPoint.width) / 2);

	}

	public void clear() {
		this.mStarted = false;
	}

	public void getPoint(DrawPoint pointr, double stepW) {
		if (this.mStarted) {
			pointr.set(getX(stepW), getY(stepW), getWidth(stepW));
		}
	}
}
