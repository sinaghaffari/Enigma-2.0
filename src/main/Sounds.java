package main;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

public class Sounds { //not currently used

    private static final int NUM_SOUNDS = 3;
    private static HashMap<String, Integer> nameVal = new HashMap<String, Integer>();
    private static HashMap<Integer, String> valName = new HashMap<Integer, String>();
    private static boolean isInitialized = false;
    private static IntBuffer buffer;
    private static IntBuffer source;
    private static FloatBuffer sourcePos;
    private static FloatBuffer sourceVel;
    private static FloatBuffer listenerPos;
    private static FloatBuffer listenerVel;
    private static FloatBuffer listenerOri;


    public static void initalize() {

        nameVal.put( "Walther P99", Integer.valueOf( 0 ) );
        valName.put( Integer.valueOf( 0 ), "Walther P99" );
        nameVal.put( "Colt M16A4", Integer.valueOf( 1 ) );
        valName.put( Integer.valueOf( 1 ), "Walther P99" );
        nameVal.put( "H&K MP5", Integer.valueOf( 2 ) );
        valName.put( Integer.valueOf( 2 ), "H&K MP5" );

        buffer = BufferUtils.createIntBuffer( NUM_SOUNDS );
        source = BufferUtils.createIntBuffer( NUM_SOUNDS );
        sourcePos = BufferUtils.createFloatBuffer( 3 * NUM_SOUNDS );
        sourceVel = BufferUtils.createFloatBuffer( 3 * NUM_SOUNDS );
        listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer( 3 ).put(
                new float[]{ 0.0f, 0.0f, 0.0f } ).rewind();
        listenerVel = (FloatBuffer) BufferUtils.createFloatBuffer( 3 ).put(
                new float[]{ 0.0f, 0.0f, 0.0f } ).rewind();
        listenerOri =
                (FloatBuffer) BufferUtils.createFloatBuffer( 6 ).put( new float[]{ 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f } ).rewind();

        try {
            AL.create();
        } catch ( LWJGLException le ) {
            le.printStackTrace();
            return;
        }
        AL10.alGetError();

        if ( loadALData() == AL10.AL_FALSE ) {
            System.out.println( "Error loading data" );
            return;
        }

        AL10.alListener( AL10.AL_POSITION, listenerPos );
        AL10.alListener( AL10.AL_VELOCITY, listenerVel );
        AL10.alListener( AL10.AL_ORIENTATION, listenerOri );

        isInitialized = true;
    }

    private static int loadALData() {
        // Load wav data into a buffer.
        AL10.alGenBuffers( buffer );

        if ( AL10.alGetError() != AL10.AL_NO_ERROR )
            return AL10.AL_FALSE;

        //add sound from file to buffer, will eventually be a for loop for every NUM_SOUNDS
        for ( int i = 0; i < NUM_SOUNDS; ++i ) {
            try {
                WaveData waveFile = WaveData.create( new BufferedInputStream( new FileInputStream( "data/sounds/" + valName.get( i ) + ".wav" ) ) );
                AL10.alBufferData( buffer.get( i ), waveFile.format, waveFile.data, waveFile.samplerate );
                waveFile.dispose();
            } catch ( FileNotFoundException e ) {
            }
        }

        // Bind buffers into audio sources.
        AL10.alGenSources( source );

        if ( AL10.alGetError() != AL10.AL_NO_ERROR )
            return AL10.AL_FALSE;

        //this will also be a for loop for every NUM_SOUNDS
        for ( int i = 0; i < NUM_SOUNDS; ++i ) {
            AL10.alSourcei( source.get( i ), AL10.AL_BUFFER, buffer.get( i ) );
            AL10.alSourcef( source.get( i ), AL10.AL_PITCH, 1.0f );
            AL10.alSourcef( source.get( i ), AL10.AL_GAIN, 1.0f );
            AL10.alSource( source.get( i ), AL10.AL_POSITION, (FloatBuffer) sourcePos.position( i * 3 ) );
            AL10.alSource( source.get( i ), AL10.AL_VELOCITY, (FloatBuffer) sourceVel.position( i * 3 ) );
            AL10.alSourcei( source.get( i ), AL10.AL_LOOPING, AL10.AL_FALSE );
        }

        if ( AL10.alGetError() == AL10.AL_NO_ERROR )
            return AL10.AL_TRUE;

        return AL10.AL_FALSE;


    }

    public static void play( String fileName, SVector2D position ) {

        sourcePos.put( nameVal.get( fileName ) * 3 + 0, (float) position.x );
        sourcePos.put( nameVal.get( fileName ) * 3 + 1, (float) position.y );
        sourcePos.put( nameVal.get( fileName ) * 3 + 2, (float) 0 );

        listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer( 3 ).put(
                new float[]{ (float) Game.playerObj.position.x, (float) Game.playerObj.position.y, 0.0f } ).rewind();

        //will maybe implement listenerVel and listenerOri in the future


        AL10.alSourcePlay( source.get( nameVal.get( fileName ) ) );
    }

    public static void killEverything() {
        AL10.alDeleteSources( source );
        AL10.alDeleteBuffers( buffer );
        AL.destroy();
    }
}
