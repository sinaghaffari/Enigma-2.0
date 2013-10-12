package gui;

import static org.lwjgl.opengl.GL11.*;

import java.awt.geom.Rectangle2D;

import org.newdawn.slick.geom.*;

public class GUISlider extends GUIComponent {
	double minimum, maximum;
	public GUISlider(double x, double y, double w, double h, double maximum, double minimum, String ID) {
		this.isVisible = true;
		this.isEnabled = true;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.maximum = maximum;
		this.minimum = minimum;
		this.ID = ID;
		this.boundingBox = new Rectangle2D.Double(x, y, w, h);
	}
	public double getCenterX() {
		return x + (w/2.0);
	}
	public double getCenterY() {
		return y + (h/2.0);
	}
	private double getRelativeCenterY() {
		return (h/2.0);
	}
	private double getRelativeCenterX() {
		return (w/2.0);
	}
	public void onRender() {
		glPushMatrix();
		{
			glTranslated(x, y, 0);
			{
				// draw fill
				if (!isMouseOn){
					glColor4f(0.5f, 0.5f, 0.5f, 1f);
				} else {
					glColor4f(1f, 1f, 1f, 1f);
				}
				ShapeRenderer.draw(new Line(6, (float)getRelativeCenterY(), (float)(w - 6), (float)getRelativeCenterY()));
				// draw border
				glColor4f(0.0f, 0.0f, 0.0f, 1f);
				ShapeRenderer.draw(new Rectangle(5, (float)getRelativeCenterY() - 1, (float)(w - 10), 3));
				
				// draw slider fill
				if (!isMouseOn){
					glColor4f(0.5f, 0.5f, 0.5f, 1f);
				} else {
					glColor4f(1f, 1f, 1f, 1f);
				}
				
			}
		}
		glPopMatrix();
	}

}
