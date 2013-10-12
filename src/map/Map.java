package map;

import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;

import main.Util;


import static org.lwjgl.opengl.GL11.*;

/*
 * Current Map legend:
 * 
 * 0-Transparent walkable surface (default)
 * 1-Regular Wall
 * 2-Block an enemy is currently on
 * 9-A transparent surface enemies can't spawn/walk on
 */




public class Map {
	public static final int BLOCK_SIZE = 20;
	private static String mapName = "";
	public static int length = 0;
	public static int height = 0;
	private static byte[][] block=null;
	public static ArrayList<Rectangle2D.Double> rectangles = new ArrayList<Rectangle2D.Double>();
	public static ArrayList<Rectangle2D.Double> newRectangles = new ArrayList<Rectangle2D.Double>();
	private static byte[] blockId = null;
	public static int[][] scoredBlock = null;
	public static int numberOfChunksX, numberOfChunksY;
	public static Dimension chunkSize;
	
	public Map(String fileName){
		try {	
			createMapFromFile(fileName);
		}
		catch (IOException e) { e.printStackTrace();}
	}
	
	public static void onRender(Set<Rectangle2D.Double> set) {
		  glPushMatrix();
		  glColor4f(0,0,0,1);
		  for(Iterator<Rectangle2D.Double> i = set.iterator(); i.hasNext();){
			  Rectangle2D rec = i.next();
			  glBegin(GL_QUADS);
			  {
				glVertex2d(rec.getMinX(), rec.getMinY());
				glVertex2d(rec.getMaxX(), rec.getMinY());
				glVertex2d(rec.getMaxX(), rec.getMaxY());
				glVertex2d(rec.getMinX(), rec.getMaxY());	
			  }
			  glEnd();
		  }
		  glPopMatrix();
	}
	public static void onRender() {
		  glPushMatrix();
		  glColor4f(0,0,0,1);
		  for(Rectangle2D.Double rec : newRectangles){
			  glBegin(GL_QUADS);
			  {
				glVertex2d(rec.getMinX(), rec.getMinY());
				glVertex2d(rec.getMaxX(), rec.getMinY());
				glVertex2d(rec.getMaxX(), rec.getMaxY());
				glVertex2d(rec.getMinX(), rec.getMaxY());	
			  }
			  glEnd();
		  }
		  glPopMatrix();
	}
	
	private void createMapFromFile(String fileName) throws IOException{
		BufferedReader inFile = new BufferedReader(new FileReader("data/maps/"+fileName)); 
		
		parseMapName(inFile.readLine());
		parseLength(inFile.readLine());
		parseHeight(inFile.readLine());		
		inFile.readLine(); //For Line saying "Map Data:", dont need it
		
		System.out.println(height+" "+length);
		
		block=new byte[height][length];
		blockId=new byte[height*length];
		scoredBlock = new int[height][length];
		
		for(int ypos=0;ypos<height;++ypos)
			parseLineIntoPosition(inFile.readLine(), ypos);
		
		buildRectangles();
		buildBigRectangles();
		setEnemyBoundaries();
		System.out.println("Old num of rectangles:"+rectangles.size());
		System.out.println("New num of rectangles:"+newRectangles.size());
		printMap();
		setUpChunks();
			
	}
	private void setUpChunks() {
		int[] lengthMiddleFactors = Util.findMiddleFactors(length);
		int[] heightMiddleFactors = Util.findMiddleFactors(height);
		boolean lengthB = lengthMiddleFactors[0] <= lengthMiddleFactors[1];
		boolean heightB = heightMiddleFactors[0] <= heightMiddleFactors[1];
		if (lengthB) {
			numberOfChunksX = lengthMiddleFactors[0];
			if (heightB) {
				numberOfChunksY = heightMiddleFactors[0];
				chunkSize = new Dimension(lengthMiddleFactors[1], heightMiddleFactors[1]);
			} else {
				numberOfChunksY = heightMiddleFactors[1];
				chunkSize = new Dimension(lengthMiddleFactors[1], heightMiddleFactors[0]);
			}
		} else {
			numberOfChunksX = lengthMiddleFactors[1];
			if (heightB) {
				numberOfChunksY = heightMiddleFactors[0];
				chunkSize = new Dimension(lengthMiddleFactors[0], heightMiddleFactors[1]);
			} else {
				numberOfChunksY = heightMiddleFactors[1];
				chunkSize = new Dimension(lengthMiddleFactors[0], heightMiddleFactors[0]);
			}
		}		
	}
	private void parseMapName(String currentLine){
		mapName=currentLine.substring(currentLine.indexOf("\"")+1);
		mapName=mapName.substring(0,mapName.indexOf("\""));
	}
	
	private void parseLength(String currentLine){
		String change=currentLine.substring(currentLine.indexOf("\"")+1);
		change=change.substring(0,change.indexOf("\""));
		StringTokenizer st= new StringTokenizer(change);
		
		length=Integer.parseInt(st.nextToken());		
	}
	
	private void parseHeight(String currentLine){
		String change=currentLine.substring(currentLine.indexOf("\"")+1);
		change=change.substring(0,change.indexOf("\""));
		StringTokenizer st= new StringTokenizer(change);
		
		height=Integer.parseInt(st.nextToken());		
	}
	
	private void parseLineIntoPosition(String currentLine, int ypos){
		if(ypos==0)
			currentLine=currentLine.substring(1); //get rid of double quote at beggining
		if(ypos==height-1)
			currentLine=currentLine.substring(0,currentLine.length()-2); //get rid of double quote and semicolon at end
		
		byte current;
		
		for(int x=0;x<currentLine.length();++x){
			current = Byte.parseByte(currentLine.substring(x,x+1));
			block[ypos][x]= current;
			blockId[ypos*length+x]=current;
			scoredBlock[ypos][x]=999;
		}
				
	}
	
	
	
	
	private void buildRectangles(){
		for(int y=0;y<height;++y){
			for(int x=0;x<length;++x){
				if(block[y][x]==1)
					rectangles.add(new Rectangle2D.Double(x*20,y*20,BLOCK_SIZE,BLOCK_SIZE));
			}
		}
	}

	boolean[][] visited;
	int[] limit;//0=right,1=down,2=right2,3=down2
	
	private void buildBigRectangles(){
		
		visited=new boolean[height][length];
		limit=new int[4];
		
		for(int i=0;i<height;++i)
			for(int j=0;j<length;++j){
				if(visited[i][j] || block[i][j]!=1)
					continue;
				expandRight(j,i, 1);
				expandDown(j,i, 1);
				if(limit[0]*limit[1]>=limit[2]*limit[3])
					confirmRectangle(j,i,0,1);
				else
					confirmRectangle(j,i,2,3);
				
					//check rectangle with left the longest, then up, right, down	
					//take one with best area
					//mark rectangle with best area as visited and add the rectangle2D of it
			}		
	}
	private void expandRight(int x,int y, int t){
		int xPlus;
		for(xPlus=0; x+xPlus<Map.length && block[y][x+xPlus]==t && !visited[y][x+xPlus];++xPlus)
			;
		limit[0]=xPlus;
		
		boolean hitLimit=false;
		
		int yPlus;
		
		for(yPlus=1;!hitLimit;++yPlus){
			for(int i=0;i<limit[0];++i){
				if(y+yPlus<=Map.height || block[y+yPlus][x+i]!=t  ||visited[y+yPlus][x+i])
					hitLimit=true;
			}
		}	
		limit[1]=yPlus-1;
	}
	
	private void expandDown(int x, int y, int t){
		int yPlus;
		for(yPlus=0; y+yPlus<Map.height &&  block[y+yPlus][x]==t && !visited[y+yPlus][x];++yPlus)
			;
		
		limit[3]=yPlus;
		
		boolean hitLimit=false;		
		int xPlus;
		
		for(xPlus=1;!hitLimit;++xPlus){
			for(int i=0;i<limit[3];++i){
				if(x+xPlus>=Map.length || block[y+i][x+xPlus]!=t  || visited[y+i][x+xPlus])
					hitLimit=true;
			}
		}	
		limit[2]=xPlus-1;
		
	}
	
	private void confirmRectangle(int x, int y,int limx,int limy){
		System.out.println("x: "+x+" "+limit[limx]+" y:"+y+" "+limit[limy]);
		for(int i=0;i<limit[limy];++i){
			for(int j=0;j<limit[limx];++j){
				visited[y+i][x+j]=true;
			}
		}
		//System.out.println("x: "+x+" y:"+y+   " Limx: "+limx+" Limy: "+limy);
		newRectangles.add(new Rectangle2D.Double(x*BLOCK_SIZE,y*BLOCK_SIZE,limit[limx]*BLOCK_SIZE,limit[limy]*BLOCK_SIZE));
	}
	
	private void setEnemyBoundaries(){
		for(int y=0;y<Map.height;++y){
			for(int x=0;x<Map.length;++x){
				if(block[y][x]==0 && hasSurroundingBlock(x,y))
					block[y][x]=9;
			}
		}
	}
	
	private boolean hasSurroundingBlock(int x,int y){
		return block[y+1][x+1]==1 ||
				block[y+1][x]==1 ||
				block[y][x+1]==1 ||
				block[y+1][x-1]==1 ||
				block[y-1][x+1]==1 ||
				block[y][x-1]==1 ||
				block[y-1][x]==1 ||
				block[y-1][x-1]==1;
	}

	public static void printMap(){
		if(block==null)
			System.out.println("No map!");
		else{
			for(int i=0;i<height;++i){
				for(int j=0;j<length;++j)
					System.out.print(block[i][j]);
				System.out.println();
			}

		}
	}
	
	public static void printScoredMap(){
		if(scoredBlock==null)
			System.out.println("No scored map!");
		else{
			for(int i=0;i<height;++i){
				for(int j=0;j<length;++j)
					System.out.printf("%4d",scoredBlock[i][j]);
				System.out.println();
			}

		}
	}
	
	public static int whatBlock(double xPoint, double yPoint){
		return block[(int)xPoint/20][(int)yPoint/20];
	}
	
	public static int whatBlock(int ID){
		return blockId[ID];
	}
	
	public static int whatBlock(int x, int y){
		return block[y][x];
	}
	
	public static boolean isWalkable(int x, int y){
		return block[y][x]==0;
	}
	
	public static boolean isWalkable(int ID){
		return blockId[ID]==0;
	}
	
	public static boolean isWalkable(double xPoint, double yPoint){
		return block[(int)(xPoint/BLOCK_SIZE)][(int)(yPoint/BLOCK_SIZE)]==0; //may become deprecated when there are new block types
	}
	
	public static int blockToID(int x, int y){
		return x + y*length;
	}
	
	public static int pointToID(double x, double y){
		return (int)(x/BLOCK_SIZE) + (int)(y/BLOCK_SIZE)*length;
	}
	public static int getScore(double x, double y){
		return scoredBlock[(int)(y/BLOCK_SIZE)][(int)(x/BLOCK_SIZE)];
	}
	public static int getScore(int x, int y){
		return scoredBlock[y][x];
	}
	public static void setEnemyOn(int x, int y){
		block[y][x]=2;
	}
	public static void setEnemyOff(int x, int y){
		block[y][x]=0;
	}
	public static void setEnemyOn(double x, double y){
	//	block[(int)(y/BLOCK_SIZE)][(int)(x/BLOCK_SIZE)]=2;
	}
	public static void setEnemyOff(double x, double y){
		//block[(int)(y/BLOCK_SIZE)][(int)(x/BLOCK_SIZE)]=0;
	}
	
	
}
