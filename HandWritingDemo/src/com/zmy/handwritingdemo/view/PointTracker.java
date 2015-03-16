package com.zmy.handwritingdemo.view;

public class PointTracker
{
  public long time;
  public double width;
  public double x;
  public double y;
  
  public PointTracker() {}
  
  public PointTracker(double paramDouble1, double paramDouble2)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
  }

	public PointTracker(double paramDouble1, double paramDouble2,
			double paramDouble3) {
		this.x = paramDouble1;
		this.y = paramDouble2;
		this.width = paramDouble3;
	}
  
  public void set(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.width = paramDouble3;
  }
  
  public void set(PointTracker paramPointTracker)
  {
    this.x = paramPointTracker.x;
    this.y = paramPointTracker.y;
    this.width = paramPointTracker.width;
  }
}
