package main;

import entities.EntityPlayer;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class HUDComponents {
    static LensVignetter LV;

    public static void initHUD() {
        LV = new LensVignetter();
    }

    public static void drawComponents( EntityPlayer playerObj ) {
        drawLensVignetting();
        drawReticle( playerObj );
    }

    private static void drawLensVignetting() {
        LV.drawLensVignette();
    }

    private static void drawReticle( EntityPlayer playerObj ) {
        glPushMatrix();
        {
            double representedRecoil = Util.distance( playerObj.currentTargetX, playerObj.currentTargetY, playerObj.position.x - Game.tlcx, playerObj.position.y - Game.tlcy ) * Math.tan( Math.toRadians( playerObj.totalRecoilBeingFelt ) );
            glLineWidth( 5 );
            glPointSize( 4.5f );
            glColor4f( 0, 0, 0, 1 );
            glBegin( GL_LINES );
            {
                glVertex2d( playerObj.currentTargetX + representedRecoil, playerObj.currentTargetY );
                glVertex2d( playerObj.currentTargetX + representedRecoil + 20, playerObj.currentTargetY );
            }
            glEnd();
            glBegin( GL_POINTS );
            {
                glVertex2d( playerObj.currentTargetX + representedRecoil, playerObj.currentTargetY );
                glVertex2d( playerObj.currentTargetX + representedRecoil + 20, playerObj.currentTargetY );
            }
            glEnd();
            glBegin( GL_LINES );
            {
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY + representedRecoil );
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY + representedRecoil + 20 );
            }
            glEnd();
            glBegin( GL_POINTS );
            {
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY + representedRecoil );
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY + representedRecoil + 20 );
            }
            glEnd();
            glBegin( GL_LINES );
            {
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY - representedRecoil );
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY - representedRecoil - 20 );
            }
            glEnd();
            glBegin( GL_POINTS );
            {
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY - representedRecoil );
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY - representedRecoil - 20 );
            }
            glEnd();
            glBegin( GL_LINES );
            {
                glVertex2d( playerObj.currentTargetX - representedRecoil, playerObj.currentTargetY );
                glVertex2d( playerObj.currentTargetX - representedRecoil - 20, playerObj.currentTargetY );
            }
            glEnd();
            glBegin( GL_POINTS );
            {
                glVertex2d( playerObj.currentTargetX - representedRecoil, playerObj.currentTargetY );
                glVertex2d( playerObj.currentTargetX - representedRecoil - 20, playerObj.currentTargetY );
            }
            glEnd();
            glColor3f( 1, 1, 1 );
            glLineWidth( 2 );
            glBegin( GL_LINES );
            {
                glVertex2d( playerObj.currentTargetX + representedRecoil, playerObj.currentTargetY );
                glVertex2d( playerObj.currentTargetX + representedRecoil + 20, playerObj.currentTargetY );
            }
            glEnd();
            glBegin( GL_LINES );
            {
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY + representedRecoil );
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY + representedRecoil + 20 );
            }
            glEnd();
            glBegin( GL_LINES );
            {
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY - representedRecoil );
                glVertex2d( playerObj.currentTargetX, playerObj.currentTargetY - representedRecoil - 20 );
            }
            glEnd();
            glBegin( GL_LINES );
            {
                glVertex2d( playerObj.currentTargetX - representedRecoil, playerObj.currentTargetY );
                glVertex2d( playerObj.currentTargetX - representedRecoil - 20, playerObj.currentTargetY );
            }
            glEnd();
            glPointSize( 1 );
            glLineWidth( 1 );
        }
        glPopMatrix();
    }

}

class LensVignetter {
    private ArrayList<SVector2D> lensVignettePoints = new ArrayList<SVector2D>();
    private double middleX, middleY;
    private double scaleX = 1, scaleY = 1;

    public LensVignetter() {
        double rx = Display.getWidth() / 2.0 + 300;
        double ry = Display.getHeight() / 2.0 + (300 * Display.getHeight() / (double) Display.getWidth());
        middleX = Display.getWidth() / 2.0;
        middleY = Display.getHeight() / 2.0;
        double firstX = 0, firstY = 0;
        for ( double t = 0; t <= 2 * Math.PI; t += 0.01 ) {
            double x = (rx) * Math.cos( t );
            double y = (ry) * Math.sin( t );
            if ( t == 0 ) {
                firstX = x;
                firstY = y;
            }
            lensVignettePoints.add( SVector2D.createVectorAlgebraically( x, y ) );
            glVertex2d( x, y );
        }
        lensVignettePoints.add( SVector2D.createVectorAlgebraically( firstX, firstY ) );
    }

    public void drawLensVignette() {
        glPushMatrix();
        {
            glTranslated( middleX, middleY, 0 );
            glScaled( scaleX, scaleY, 1 );
            scaleX += (Math.random() * 2 - 1) * 0.03;
            scaleY += (Math.random() * 2 - 1) * 0.03;
            if ( scaleX < 0.85 || scaleX > 1.3 ) {
                scaleX = 1;
            }
            if ( scaleY < 0.85 || scaleY > 1.3 ) {
                scaleY = 1;
            }
            glBegin( GL_TRIANGLE_FAN );
            {
                glColor4f( 0, 0, 0, 0 );
                glVertex2d( 0, 0 );
                glColor4f( 0, 0, 0, 1 );
                for ( int a = 0; a < lensVignettePoints.size(); a++ ) {
                    glVertex2d( lensVignettePoints.get( a ).x, lensVignettePoints.get( a ).y );
                }
            }
            glEnd();
        }
        glPopMatrix();
    }
}
