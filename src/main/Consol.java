package main;

//~--- non-JDK imports --------------------------------------------------------

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import static org.lwjgl.opengl.GL11.*;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Font;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Consol {
	public static boolean[ ]                           viewRays       = { false, false, false };
	public static boolean                              wallCollisions = false;
	private static ArrayList< String >                 storedLines    = new ArrayList< String >( );
	private static ArrayList< ArrayList< Character > > commandHistory = new ArrayList< ArrayList< Character > >( );
	private static ArrayList< Character >              inputArray     = new ArrayList< Character >( );
	private static String                              inputString    = "";
	private static Font                                AWTFont        = new Font( Font.MONOSPACED, Font.PLAIN, 15 );
	private static UnicodeFont                         TTF            = new UnicodeFont( AWTFont );
	private static long                                blinkerTime    = System.currentTimeMillis( );
	private static boolean                             blinkerVisible = true;
	private static final String                        allowedChars   = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ?|<>[]{};:'\"\\/,.1234567890!@#$%^&*()+_-=";
	private static int counter = 0;

	public static void enterConsol( ) {
		inputString = "";
		Game.charPressed.clear( );
		Game.keyPressed.clear( );
		Keyboard.enableRepeatEvents( true );
		TTF = new UnicodeFont( AWTFont );
		TTF.getEffects( ).add( new ColorEffect( java.awt.Color.white ) );
		TTF.addAsciiGlyphs( );

		try {
			TTF.loadGlyphs( );
		} catch ( SlickException e ) {}
	}

	public static void exitConsol( ) {
		inputString = "";
		Game.charPressed.clear( );
		Game.keyPressed.clear( );
		Keyboard.enableRepeatEvents( false );
	}

	public static void updateConsol( ) {
		inputString = "";

		if ( System.currentTimeMillis( ) - blinkerTime > 500 ) {
			blinkerVisible = !blinkerVisible;
			blinkerTime    = System.currentTimeMillis( );
		}

		for ( int a = 0; a < Game.charPressed.size( ); a++ ) {
			if ( Game.charPressed.get( a ) == '' ) {
				if ( inputArray.size( ) > 0 ) {
					inputArray.remove( inputArray.size( ) - 1 );
				}
			} else {
				if ( allowedChars.indexOf( Game.charPressed.get( a ) ) != -1 ) {
					inputArray.add( Game.charPressed.get( a ) );
				}
				
			}
		}
		for ( int a = 0; a < inputArray.size( ); a++ ) {
			inputString += inputArray.get( a );
		}

		if ( Game.keyPressed.contains( Keyboard.KEY_RETURN ) ) {
			if (inputArray.size() > 0) {
				storedLines.add( inputString );
				commandHistory.add((ArrayList<Character>) inputArray.clone());
				counter = commandHistory.size() - 1;
				readCommand( inputString );
				inputArray.clear( );
				inputString = "";
			}
		}
	}

	public static void readCommand( String command ) {
		StringTokenizer     st     = new StringTokenizer( command, "(,)" );
		ArrayList< String > tokens = new ArrayList< String >( );

		while ( st.hasMoreTokens( ) ) {
			tokens.add( st.nextToken( ).trim( ) );
		}

		if ( tokens.get( 0 ).equalsIgnoreCase( "Sina Ghaffari" ) || tokens.get( 0 ).equalsIgnoreCase( "Tristan Homsi" ) ) {
			storedLines.add( "Did you mean: God?" );
		} else if ( tokens.get( 0 ).equalsIgnoreCase( "toggleRays" ) ) {
			if ( tokens.size( ) == 2 ) {
				if ( tokens.get( 1 ).equalsIgnoreCase( "ENABLE_ALL" ) ) {
					viewRays = new boolean[ ] { true, true, true };
				} else if ( tokens.get( 1 ).equalsIgnoreCase( "DISABLE_ALL" ) ) {
					viewRays = new boolean[ ] { false, false, false };
				}
			} else if ( tokens.size( ) == 4 ) {
				if ( tokens.get( 1 ).equalsIgnoreCase( "true" ) ) {
					viewRays[ 0 ] = true;
				} else if ( tokens.get( 1 ).equalsIgnoreCase( "false" ) ) {
					viewRays[ 0 ] = false;
				}

				if ( tokens.get( 2 ).equalsIgnoreCase( "true" ) ) {
					viewRays[ 1 ] = true;
				} else if ( tokens.get( 1 ).equalsIgnoreCase( "false" ) ) {
					viewRays[ 1 ] = false;
				}

				if ( tokens.get( 3 ).equalsIgnoreCase( "true" ) ) {
					viewRays[ 2 ] = true;
				} else if ( tokens.get( 1 ).equalsIgnoreCase( "false" ) ) {
					viewRays[ 2 ] = false;
				}
			} else {
				storedLines.add("Invalid number of parameters.");
			}
			storedLines.add("FacingRays  = " + viewRays[0]);
			storedLines.add("LookingRays = " + viewRays[1]);
			storedLines.add("RecoilRays  = " + viewRays[2]);
		} else {
			storedLines.add("No such command found");
		}
	}

	public static void renderConsol( ) {
		int inputStringLength = TTF.getWidth( inputString ) + 5;

		glEnable(GL_SCISSOR_TEST);
		glScissor(0, Display.getHeight() - 200, Display.getWidth(), Display.getHeight() - 200);
		glColor4f( 0, 0, 0, 0.5f );
		glBegin( GL_QUADS );
		{
			glVertex2d( 0, 0 );
			glVertex2d( Display.getWidth( ), 0 );
			glVertex2d( Display.getWidth( ), 200 );
			glVertex2d( 0, 200 );
		}
		glEnd( );
		glColor3f( 1, 1, 1 );
		glEnable( GL_TEXTURE_2D );
		TTF.drawString( 5, 5, inputString );

		for ( int a = 0; a < storedLines.size( ); a++ ) {
			TTF.drawString( 5, 5 + ( TTF.getLineHeight( ) * ( ( storedLines.size( ) - a ) ) - 5 ), storedLines.get( a ) );
		}

		glDisable( GL_TEXTURE_2D );

		if ( blinkerVisible ) {
			glBegin( GL_QUADS );

			{
				glVertex2d( inputStringLength, 5 + 4 );
				glVertex2d( inputStringLength + 4, 5 + 4 );
				glVertex2d( inputStringLength + 4, 9 + 14 );
				glVertex2d( inputStringLength, 9 + 14 );
			}

			glEnd( );
		}
		glDisable(GL_SCISSOR_TEST);
	}
}
