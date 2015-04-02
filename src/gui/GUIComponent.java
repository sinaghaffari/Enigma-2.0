package gui;

import org.lwjgl.input.Mouse;

import java.awt.geom.Rectangle2D;

public class GUIComponent {
    public double x, y, w, h;
    public boolean isVisible;
    public boolean isEnabled;
    public boolean isMouseOn;
    public boolean isMouseDown;
    public boolean isMouseClicked;
    protected Rectangle2D boundingBox = new Rectangle2D.Double();
    protected String ID = "";

    public void onUpdate( double mx, double my ) {
        updateMouse( mx, my );
        boundingBox.setRect( x, y, w, h );
    }

    public void onRender() {

    }

    public void mouseEntered() {
        isMouseOn = true;
    }

    public void mouseExited() {
        isMouseOn = false;
    }

    public void mousePressed() {
        if ( !isMouseDown ) {
            mouseClicked();
        }
        isMouseDown = true;
    }

    public void mouseReleased() {
        isMouseDown = false;
    }

    public void mouseClicked() {
        isMouseClicked = true;
    }

    public void updateMouse( double x, double y ) {
        isMouseClicked = false;
        if ( boundingBox.contains( x, y ) && !isMouseOn ) {
            mouseEntered();
        }
        if ( !boundingBox.contains( x, y ) && isMouseOn ) {
            mouseExited();
        }
        if ( Mouse.isButtonDown( 0 ) && isMouseOn ) {
            mousePressed();
        }
        if ( isMouseDown && !Mouse.isButtonDown( 0 ) ) {
            mouseReleased();
        }
    }
}
