package gui;

import main.Game;
import main.GameState;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class MenuMain {
    public static ArrayList<GUIComponent> guiList = new ArrayList<GUIComponent>();
    static double x;
    static double y;
    private static boolean isGameStarted = false;

    public static void setUpMenu() {
        guiList.add( new GUIButton( 0, 0, 0, 100, "Play Game", "Play Game", 20 ) );
        guiList.add( new GUIButton( 0, 0, 0, 100, "Settings", "Settings", 20 ) );
        guiList.add( new GUIButton( 0, 0, 0, 100, "Exit", "Exit", 20 ) );
        manageLayout();
    }

    public static void destroyMainMenu() {
        guiList.clear();
    }

    public static void manageLayout() {
        double padding = 10;
        double numberOfComponents = guiList.size();
        double tempX = padding;
        double numberOfSpaces = numberOfComponents + 1;
        double regulatedWidth = (Display.getWidth() - (padding * numberOfSpaces)) / numberOfComponents;
        for ( int a = 0; a < numberOfComponents; a++ ) {
            guiList.get( a ).w = regulatedWidth;
            double x = tempX;
            guiList.get( a ).x = x;
            tempX += padding + guiList.get( a ).w;
            guiList.get( a ).y = (Display.getHeight() / 2.0) - (guiList.get( a ).h / 2.0);
        }
    }

    public static void onRender() {
        glClearColor( 0.5f, 0.5f, 0.5f, 1 );
        glClear( GL_COLOR_BUFFER_BIT );
        glPushMatrix();
        {
            for ( GUIComponent g : guiList ) {
                g.onRender();
            }
        }
        glPopMatrix();
    }

    public static void onUpdate() {
        if ( Display.wasResized() ) {
            manageLayout();
        }
        x = Mouse.getX();
        y = Display.getHeight() - Mouse.getY();
        for ( GUIComponent g : guiList ) {
            g.onUpdate( x, y );
            checkForAction( g );
        }
        if ( isGameStarted ) {
            startGame();
        }
    }

    public static void checkForAction( GUIComponent g ) {
        if ( g.isMouseClicked ) {
            if ( g.ID.equals( "Play Game" ) ) {
                isGameStarted = true;
            }
        }
    }

    public static void startGame() {
        Game.gameState = GameState.GAME;
        destroyMainMenu();
    }
}
