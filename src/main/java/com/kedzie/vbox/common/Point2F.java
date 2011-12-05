package com.kedzie.vbox.common;

public class Point2F {
	public float x;
	public float y;
	public long timestamp;
	
	public Point2F(float x, float y) {
		this.x=x;
		this.y=y;
	}
	
	public Point2F(float x, float y, long timestamp) {
		this.x=x;
		this.y=y;
		this.timestamp=timestamp;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
