package com.kedzie.vbox.common;

public class Point2D {
	public double x;
	public double y;
	public long timestamp;
	
	public Point2D(double x, double y) {
		this.x=x;
		this.y=y;
	}
	
	public Point2D(double x, double y, long timestamp) {
		this.x=x;
		this.y=y;
		this.timestamp=timestamp;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
