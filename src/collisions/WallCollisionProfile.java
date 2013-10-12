package collisions;

import java.awt.geom.Rectangle2D;

import main.SVector2D;

public class WallCollisionProfile {
	public double playerPointingPointX = 0;
	public double playerPointingPointY = 0;
	public SVector2D force = SVector2D.createZeroVector();
	int outcode = 0;
	public void add(double playerPointingPointX, double playerPointingPointY, SVector2D newVelocity, int outcode) {
		this.playerPointingPointX = playerPointingPointX;
		this.playerPointingPointY = playerPointingPointY;
		this.force = newVelocity;
		this.outcode = outcode;
	}
}