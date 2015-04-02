package main;

import bullets.Bullet;
import entities.*;
import gui.MenuMain;
import map.Chunk;
import map.ChunkManager;
import map.Map;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import pathfinding.EnemySpawner;
import pathfinding.MapPathfinding;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Sina Ghaffari
 */
public class Game {
    public static Font AWTFont = new Font( Font.MONOSPACED, Font.PLAIN, 15 );
    public static UnicodeFont TTF = new UnicodeFont( AWTFont );
    public static boolean showHelp = true;
    public static boolean shouldSpawn = true;
    public static boolean gravity = false;
    public static EntityPlayer playerObj;
    public static ArrayList<Integer> keyPressed = new ArrayList<Integer>();
    public static ArrayList<Character> charPressed = new ArrayList<Character>();
    public static int gameState = GameState.MENU;
    public static int fps;
    public static double delta;
    //public static List<Entity> entityList = new ArrayList<Entity>();
    public static List<Bullet> bulletList = new ArrayList<Bullet>();
    public static double displayMouseX;
    public static double displayMouseY;
    public static double mapMouseX;
    public static double mapMouseY;
    public static double slowMouseX = 0, slowMouseY = 0;
    public static double tlcx, tlcy, focusX, focusY;
    public static double tlcxModifier = 0, tlcyModifier = 0;
    public static double screenShootShakeXV, screenShootShakeYV, screenShootShakeXT, screenShootShakeYT;
    public static boolean screenShake = false;
    static long lastFPS;
    static long lastFrame;
    static int tempFPS = 0;
    static Map map = null;

    public static void setUpDisplay() throws LWJGLException {

        // Determine the best DisplayMode for fullscreen.
        DisplayMode displayMode = determineBestDisplayMode();

        // Set up display with determined DisplayMode.
        Display.setDisplayMode( new DisplayMode( 1000, 600 ) );

        //Display.setDisplayMode(displayMode);
        Display.setFullscreen( true );
        Display.setVSyncEnabled( true );
        Display.setTitle( "Enigma 2" );
        Display.create();

        // Sets glOrtho to the Display size.
        glMatrixMode( GL_PROJECTION );
        glOrtho( 0, Display.getWidth(), Display.getHeight(), 0, 10, -10 );
        glMatrixMode( GL_MODELVIEW );

    }

    public static DisplayMode determineBestDisplayMode() throws LWJGLException {
        DisplayMode[] displayModes = Display.getAvailableDisplayModes();
        DisplayMode best = displayModes[0];
        long bestSize = best.getWidth() * best.getHeight() * best.getBitsPerPixel();
        for ( int a = 0; a < displayModes.length; a++ ) {
            DisplayMode current = displayModes[a];
            long currentSize = current.getWidth() * current.getHeight() * current.getBitsPerPixel();
            if ( currentSize > bestSize ) {
                best = current;
                bestSize = best.getWidth() * best.getHeight() * best.getBitsPerPixel();
            }
        }
        return best;
    }

    public static void createPoolTriangle( SVector2D pos, int numberOfLayers, double distanceBetweenBalls ) {
        SVector2D vec = pos;
        for ( int b = 0; b < numberOfLayers; b++ ) {
            for ( int a = 0; a < numberOfLayers - b; a++ ) {
                EntityManager.addEntity( new EntityDummy( vec.add( SVector2D.createVectorGeometrically( Math.toRadians( 30 ), distanceBetweenBalls * a ) ), 0 ) );
            }
            vec = vec.add( SVector2D.createVectorGeometrically( Math.toRadians( -30 ), distanceBetweenBalls ) );
        }
    }

    public static void main( String[] args ) throws LWJGLException, URISyntaxException {
        String os = System.getProperty( "os.name" );
        if ( os.startsWith( "Mac" ) )
            os = "macosx";
        else if ( os.startsWith( "Windows" ) )
            os = "windows";
        else if ( os.startsWith( "Linux" ) )
            os = "linux";
        File resourceFile = new File( ClassLoader.getSystemResource( "" ).toURI() );
        String resourcePath = resourceFile.getAbsolutePath();
        if ( resourcePath.endsWith( "bin" ) ) {
            resourcePath = resourceFile.getParentFile().getAbsolutePath();
        }
        System.out.println( resourcePath );
        System.setProperty( "org.lwjgl.librarypath", resourcePath + "/lib/native/" + os );

        map = new Map( "Enigma Survival.map" );
        //map=new Map("Empty.map");
        setUpDisplay();
        MenuMain.setUpMenu();
        HUDComponents.initHUD();
        ChunkManager.initializeChunkManager( Map.numberOfChunksX, Map.numberOfChunksY, Map.chunkSize.width, Map.chunkSize.height );
        EnemySpawner.initializeSpawner();
        playerObj = new EntityPlayer( SVector2D.createVectorAlgebraically( 500, 300 ), 0 );
        EntityManager.addEntity( playerObj );
        EntityManager.addEntity( new EntityTurret( SVector2D.createVectorAlgebraically( 700, 300 ), Math.toRadians( 90 ) ) );
        createPoolTriangle( SVector2D.createVectorAlgebraically( 700, 300 ), 5, 30 );
        glEnable( GL_POLYGON_SMOOTH );
        glEnable( GL_LINE_SMOOTH );
        glEnable( GL_POINT_SMOOTH );
        glEnable( GL_SMOOTH );
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
        glEnable( GL_BLEND );
        TTF = new UnicodeFont( AWTFont );
        TTF.getEffects().add( new ColorEffect( java.awt.Color.white ) );
        TTF.addAsciiGlyphs();
        try {
            TTF.loadGlyphs();
        } catch ( SlickException e ) {
        }
        lastFPS = getMilliTime();
        while ( !Display.isCloseRequested() ) {
            keyPressed.clear();
            charPressed.clear();
            while ( Keyboard.next() ) {
                if ( Keyboard.getEventKeyState() ) {
                    keyPressed.add( Keyboard.getEventKey() );
                    charPressed.add( Keyboard.getEventCharacter() );
                }
            }
            if ( keyPressed.contains( Keyboard.KEY_GRAVE ) ) {
                if ( gameState == GameState.GAME ) {
                    gameState = GameState.CONSOL;
                    Consol.enterConsol();
                } else if ( gameState == GameState.CONSOL ) {
                    gameState = GameState.GAME;
                    Consol.exitConsol();
                }
            }
            if ( keyPressed.contains( Keyboard.KEY_ESCAPE ) ) {
                if ( gameState == GameState.GAME ) {
                    closeGame();
                } else if ( gameState == GameState.CONSOL ) {
                    gameState = GameState.GAME;
                    Consol.exitConsol();
                }
            }
            if ( keyPressed.contains( Keyboard.KEY_T ) )
                shouldSpawn = !shouldSpawn;
            if ( keyPressed.contains( Keyboard.KEY_G ) )
                gravity = !gravity;
            if ( keyPressed.contains( Keyboard.KEY_H ) )
                showHelp = !showHelp;
            if ( keyPressed.contains( Keyboard.KEY_U ) )
                EntityManager.addEntity( new EntityTurret( SVector2D.createVectorAlgebraically( Mouse.getX() + tlcx, Display.getHeight() - Mouse.getY() + tlcy ), Math.toRadians( 90 ) ) );

            delta = getDelta();
            if ( gameState == GameState.GAME ) {
                Mouse.setGrabbed( false );
                Keyboard.enableRepeatEvents( false );
                if ( Mouse.isButtonDown( 1 ) ) {
                    EntityManager.addEntity( new EntityDummy( SVector2D.createVectorAlgebraically( Game.mapMouseX, Game.mapMouseY ), 0 ) );
                }
                focusScreen();
                MapPathfinding.update();
                if ( shouldSpawn )
                    EnemySpawner.update();
                updateEntities();
                render();
                renderHUD();
            } else if ( gameState == GameState.CONSOL ) {
                Mouse.setGrabbed( true );
                render();
                renderHUD();
                Consol.updateConsol();
                Consol.renderConsol();
            } else if ( gameState == GameState.MENU ) {
                Mouse.setGrabbed( false );
                MenuMain.onUpdate();
                MenuMain.onRender();
            }
            Display.update();
            Display.sync( 60 );
        }
        closeGame();
    }

    public static void closeGame() {
        Display.destroy();
        AL.destroy();
        System.exit( 0 );
    }

    public static void setTLCs() {
        focusX = playerObj.position.x;
        focusY = playerObj.position.y;
        tlcx = (focusX - Display.getWidth() / 2.0) + (tlcxModifier);
        tlcy = (focusY - Display.getHeight() / 2.0) + (tlcyModifier);
        updateMouse( tlcx, tlcy );
    }

    public static void setScreenShootShake( double x, double y ) {
        screenShootShakeXV = x / 2.0;
        screenShootShakeYV = y / 2.0;
    }

    public static void focusScreen() {
        double delta = Game.delta / 16.0;

        screenShootShakeXT += screenShootShakeXV;
        screenShootShakeYT += screenShootShakeYV;
        screenShootShakeXV /= 1 + (0.2 * delta);
        screenShootShakeYV /= 1 + (0.2 * delta);
        screenShootShakeXT /= 1 + (0.2 * delta);
        screenShootShakeYT /= 1 + (0.2 * delta);
        double virtualMouseActualMouseDistance = Util.distance( slowMouseX, slowMouseY, Game.displayMouseX, Game.displayMouseY );
        double virtualMouseActualMouseAngle = Util.angleOfLineRad( slowMouseX, slowMouseY, Game.displayMouseX, Game.displayMouseY );
        slowMouseX += (((virtualMouseActualMouseDistance / 20.0) * delta) * Math.cos( virtualMouseActualMouseAngle ));
        slowMouseY += (((virtualMouseActualMouseDistance / 20.0) * delta) * Math.sin( virtualMouseActualMouseAngle ));

        double distanceBetweenPlayerAndVirtualMouse = Util.distance( playerObj.position.x - tlcx, playerObj.position.y - tlcy, slowMouseX, slowMouseY );
        double angleBetweenPlayerAndVirtualMouse = Util.angleOfLineRad( playerObj.position.x - tlcx, playerObj.position.y - tlcy, slowMouseX, slowMouseY );

        tlcxModifier = ((distanceBetweenPlayerAndVirtualMouse * 0.3) * Math.cos( angleBetweenPlayerAndVirtualMouse )) + (screenShootShakeXT * delta);
        tlcyModifier = ((distanceBetweenPlayerAndVirtualMouse * 0.3) * Math.sin( angleBetweenPlayerAndVirtualMouse )) + (screenShootShakeYT * delta);
    }

    public static void updateEntities() {
        // Update all entities;
        for ( int a = EntityManager.globalEntityList.size() - 1; a >= 0; a-- ) { // Note that when removing objects from an ArrayList, iterate through the array backwards.
            EntityManager.globalEntityList.get( a ).onUpdate();
            // Remove dead entities.
            if ( EntityManager.globalEntityList.get( a ).dead ) {
                Entity deadEntity = EntityManager.globalEntityList.get( a );
                EntityManager.removeEntity( EntityManager.globalEntityList.get( a ) );
                deadEntity = null;
            }
        }
        for ( int a = bulletList.size() - 1; a >= 0; a-- ) { // Note that when removing objects from an ArrayList, iterate through the array backwards.
            bulletList.get( a ).onUpdate();
            // Remove dead entities.
            if ( bulletList.get( a ).dead ) {
                Bullet deadBullet = bulletList.get( a );
                bulletList.remove( a );
                deadBullet = null;
            }
        }
    }

    public static void render() {
        glPushMatrix();
        {
            glTranslated( -tlcx, -tlcy, 0 );
            glClearColor( 0.5f, 0.5f, 0.5f, 1 );
            glClear( GL_COLOR_BUFFER_BIT );
            ArrayList<Chunk> chunkList = ChunkManager.whichChunks( new Rectangle2D.Double( tlcx, tlcy, Display.getWidth(), Display.getHeight() ) );
            Set<Rectangle2D.Double> wallSet = new HashSet<Rectangle2D.Double>();
            Set<Entity> entitySet = new HashSet<Entity>();
            for ( Chunk c : chunkList ) {
                wallSet.addAll( c.wallList );
                entitySet.addAll( c.entityList );
            }
            Map.onRender( wallSet );
            ChunkManager.onRender();
            for ( Iterator<Entity> i = entitySet.iterator(); i.hasNext(); ) {
                Entity e = i.next();
                e.onRender();
            }
            for ( int a = bulletList.size() - 1; a >= 0; a-- ) {
                bulletList.get( a ).onRender();
            }
            updateFPS();
        }
        glPopMatrix();
    }

    public static void renderHUD() {
        HUDComponents.drawComponents( playerObj );
        if ( showHelp ) {
            glColor3f( 1, 1, 1 );
            glEnable( GL_TEXTURE_2D );
            int a = 1;
            TTF.drawString( 5, 20 * a++, "To toggle this text press h" );
            TTF.drawString( 5, 20 * a++, "To toggle gravity press g" );
            TTF.drawString( 5, 20 * a++, "To place a turret where you're pointing, press u" );
            TTF.drawString( 5, 20 * a++, "Right click to spawn dummies" );
            TTF.drawString( 5, 20 * a++, "To create a force explosion on the entities around you, press space" );
            TTF.drawString( 5, 20 * a++, "Change weapons with E and Q" );
            TTF.drawString( 5, 20 * a++, "Move with WASD and sprint by holding Shift" );
            TTF.drawString( 5, 20 * a++, "To toggle enemy spawn, press T. This command does not kill existing enemies." );

            glDisable( GL_TEXTURE_2D );
        }
    }

    public static double getDelta() {
        long time = getNanoTime();
        int delta = (int) (time - lastFrame);

        lastFrame = time;

        return delta / 1000000.0;
    }

    public static void updateFPS() {
        if ( getMilliTime() - lastFPS > 1000 ) {
            fps = tempFPS;
            Display.setTitle( "FPS: " + fps );
            tempFPS = 0; //reset the FPS counter
            lastFPS += 1000; //add one second
        }
        tempFPS++;
    }

    public static long getNanoTime() {
        return (System.nanoTime());
    }

    public static long getMilliTime() {
        return (System.nanoTime() / 1000000);
    }

    public static void updateMouse( double tlcx, double tlcy ) {
        displayMouseX = Mouse.getX();
        displayMouseY = Display.getHeight() - Mouse.getY();
        mapMouseX = displayMouseX + tlcx;
        mapMouseY = displayMouseY + tlcy;
    }

    public static double getPlayerX() {
        return playerObj.position.x;
    }

    public static double getPlayerY() {
        return playerObj.position.y;
    }

    public static boolean isOnScreen( int pixX, int pixY ) {
        return pixX >= tlcx && pixX <= Display.getWidth() + tlcx && pixY >= tlcy && pixY < tlcy + Display.getWidth();
    }

}