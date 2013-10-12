package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.ShapeRenderer;

public class GUIRadioButton extends GUIComponent {
	String text;
	private Font font;
	private UnicodeFont TTF;
	private float fontSize;
	public boolean isSelected;
	public GUIButtonGroup linkedButtonGroup = null;
	public GUIRadioButton(double x, double y, double w, double h, String text, String ID, float fontSize) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.text = text;
		this.ID = ID;
		this.fontSize = fontSize;
		this.boundingBox.setRect(x, y, w, h);
		try {
			this.font = Font.createFont(Font.TRUETYPE_FONT, new File("data/fonts/font.ttf")).deriveFont(fontSize);
		} catch (FontFormatException e) {} catch (IOException e) {}
		this.TTF = new UnicodeFont(font);

		TTF.getEffects( ).add( new ColorEffect() );
		TTF.addAsciiGlyphs( );
		try {
			TTF.loadGlyphs( );
		} catch ( SlickException e ) {}
	}
	public void onRender() {
		glPushMatrix();
		{
			glTranslated(x, y, 0);
			{

				double t = 0;
				double s = 0;
				if (isMouseOn) {
					t = (2 * Math.random() - 1) * 2;
					s = (2 * Math.random() - 1) * 2;
				}
				//draw fill
				if (isMouseOn || isSelected){
					glColor4f(1f, 1f, 1f, 1f);
					ShapeRenderer.fill(new Rectangle((float)t, (float)s, (float)w, (float)h));
				}
				else {
					glColor4f(0.1f, 0.1f, 0.1f, 1f);
					ShapeRenderer.fill(new Rectangle((float)t, (float)s, (float)w, (float)h));
				}

				//draw border.
				glColor4f(0.0f, 0.0f, 0.0f, 1f);
				ShapeRenderer.draw(new Rectangle((float)t, (float)s, (float)w, (float)h));
				//draw text
				double tx = (w / 2.0) - (TTF.getWidth(text) / 2.0) + t;
				double ty = (h / 2.0) - (TTF.getLineHeight() / 2.0) + s;
				if (isMouseOn || isSelected)
					TTF.drawString((float)tx, (float)ty, text, new org.newdawn.slick.Color(0.1f, 0.1f, 0.1f, 1f));
				else 
					TTF.drawString((float)tx, (float)ty, text, new org.newdawn.slick.Color(1f, 1f, 1f, 1f));
			}
		}
		glPopMatrix();
	}
	public void onUpdate(double x, double y) {
		super.onUpdate(x, y);
	}
	public void mouseClicked() {
		super.mouseClicked();
		if (this.linkedButtonGroup != null) {
			for (GUIRadioButton b : this.linkedButtonGroup.buttonList) {
				b.isSelected = false;
			}
		}
		isSelected = !isSelected;
	}
}
