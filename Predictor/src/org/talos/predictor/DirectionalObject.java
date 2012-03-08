package org.talos.predictor;

public class DirectionalObject extends SimpleObject {

	float dx, dy;
	
	public DirectionalObject(float x, float y, float ndx, float ndy, int v) {
		super(x, y, v);
		dx = ndx;
		dy = ndy;
	}
	
	public float getDX() {
		return dx;
	}
	
	public float getDY() {
		return dy;
	}

}
