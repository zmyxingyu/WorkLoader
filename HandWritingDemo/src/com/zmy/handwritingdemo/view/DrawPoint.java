package com.zmy.handwritingdemo.view;

/**
 * 
 * @author zhangmy
 * 
 */
public class DrawPoint {
	
	public long time;
	public double width;
	public double pointX;
	public double pointY;

	public DrawPoint() {
	}

	public DrawPoint(double pointX, double pointY) {
		this.pointX = pointX;
		this.pointY = pointY;
	}

	public DrawPoint(double pointX, double pointY, double width) {
		this.pointX = pointX;
		this.pointY = pointY;
		this.width = width;
	}

	public void set(double pointX, double pointY, double width) {
		this.pointX = pointX;
		this.pointY = pointY;
		this.width = width;
	}

	public void set(DrawPoint drawPoint) {
		this.pointX = drawPoint.pointX;
		this.pointY = drawPoint.pointY;
		this.width = drawPoint.width;
	}
}
