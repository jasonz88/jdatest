package com.omegastudios.graphics2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GraphicsEngine extends JPanel{
	//CONSTRUCTS
	class WindowMaker extends JFrame {
		WindowMaker(float w, float h, GraphicsEngine ge) {
			super();
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.setTitle("Astrogator - Physics Test");
			this.setSize((int)w, (int)h);
			this.setVisible(true);
			this.setBackground(Color.BLACK);
			this.paint(getGraphics());
			this.setContentPane(ge);

			// Get the screen (monitor) size
			GraphicsConfiguration gc = this.getGraphicsConfiguration();
			Rectangle bounds = gc.getBounds();

			// set the window to the middle of the screen
			this.setLocation((int) ((bounds.width / 2) - (w / 2)),
					(int) ((bounds.height / 2) - (h / 2)));
		}
	}
	
	//VARIABLES
	//graphics
	public WindowMaker window;
	Graphics2D g2d;
	BufferedImage bufferedImage;
	static float canvasWidth = 800; // width and height of the game screen
	static float canvasHeight = 600;
	public float zoom; //basically says 1 meter is X pixels long
	float zoomStart;
	float zoomGoal;
	private static final float MAX_ZOOM = 1280;
	private static final float MIN_ZOOM = 20;
	int frameCount;
	public Vec2 camPos;
	
	TSLinkedList<GraphicsObject> goList; //object list
	
	//info
	float fps;
	float smoothFPS=60;
	long miliTime;

	//precalculation
	AffineTransform zoomAF;
	float xtranslate;
	float ytranslate;
	
	//METHODS
	public GraphicsEngine() {
		super();
		goList= new TSLinkedList<GraphicsObject>();
		
		camPos=new Vec2(0,0);
		zoom=100f; 
		zoomGoal=zoom;
		frameCount=0;
		
		window=new WindowMaker(canvasWidth,canvasHeight,this);
		
		zoomAF=new AffineTransform();
		zoomAF.scale(zoom,zoom);
	}

	public GraphicsObject createObject(){
		GraphicsObject go=new GraphicsObject(this);
		registerObject(go);
		return go;
	}	
	public GraphicsObject createUIObject(){
		GraphicsObject go=new GraphicsObject(this);
		go.setUILayer(true);
		registerObject(go);
		return go;
	}	
	public void destroy(GraphicsObject go){ 
		unregisterObject(go);
		go.clearAll();
	}
	public void registerObject(GraphicsObject newObject){
		if(newObject==null) return;
		if(newObject.uiLayer==true)
			goList.mtAddLast(newObject);
		else
			goList.mtAddFirst(newObject);
		
		newObject.ge=this;
	}
	public void unregisterObject(GraphicsObject go){
		if(go==null) return;
		goList.mtRemove(go);
		go.ge=null;
	}
	
	public void hideMouse(){
		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		window.getContentPane().setCursor(blankCursor);
	}
	
	public float getCanvasWidth()	{ return canvasWidth;  }
	public float getCanvasHeight()	{ return canvasHeight; }
	public float getTranslateX()	{ return xtranslate;   }
	public float getTranslateY()	{ return ytranslate;   }
	
	public GraphicsObject objAtPoint(Vec2 pos){
		GraphicsObject go=null;
		Iterator<GraphicsObject> goListItr=goList.iterator();	
		while(goListItr.hasNext()){
			go=goListItr.next();
			if(go.noCollision==true) continue;
			go.updateColBound();
			if(go.simpleCollision(pos.x, pos.y))
				return go;
		}
		return null;
	}

	//AUXILIARY
	// Override paintComponent to do drawing.
	// Called back by repaint().
	@Override
	public void paintComponent(Graphics g) {
		//DEBUGGING
		fps=1000f/(float)(System.currentTimeMillis()-miliTime);
		smoothFPS=(0.95f*smoothFPS)+(0.05f*fps);
		miliTime=System.currentTimeMillis();
		long beginTime = System.currentTimeMillis();
		//<DEBUGGING>
		
		//Draw buffered image immediately
		g2d=(Graphics2D)g;
		g2d.drawImage(bufferedImage,null,0,0);
		
		//prepare buffered new image
		bufferedImage = new BufferedImage((int)canvasWidth, (int)canvasHeight, BufferedImage.TYPE_INT_ARGB);
	    g2d = bufferedImage.createGraphics();
		
	    //background
	    super.paintComponent(g2d);
		setBackground(Color.BLACK); // may use an image for background

		//draw the game objects
		gameDraw();
		
		//DEBUGGING
		//Draw buffered image immediately
		//g2d=(Graphics2D)g;
		//g2d.drawImage(bufferedImage,null,0,0);
		
		//System.out.printf("GraphicsEngine says: Draw time = %dms\t FPS = %4.2f\n",System.currentTimeMillis() - beginTime,fps);
		//<DEBUGGING>
	}
	public void gameDraw(){
		frameCount+=1;

		//Makes zooming smoother
		if (Math.abs(zoom-zoomGoal)>10){
				zoom-=(zoomStart-zoomGoal)/10;
		}
		else{
			zoom=zoomGoal;
		}
		
		// Use of antialiasing to have nicer lines
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//recalculate offsets
		canvasWidth=this.getWidth();
		canvasHeight=this.getHeight();
		
		drawBG(new Color(0,50,0),2.0f);
		drawRegisteredObjects();
		
		//UI elements:
		drawText("Registered objects: "+goList.size(),10,20);
		drawText("Camera X:"+camPos.x,10,40);
		drawText("Camera Y:"+camPos.y,10,60);
		drawText("Zoom:    "+zoom,10,80);
		drawText("FPS:     "+smoothFPS,10,100);
		//drawCursor();		
	}

	private void drawBG(Color lineColor, float depthFactor) {
		g2d.setPaint(lineColor);
		int linesNeeded=Math.max(
				(int) (canvasWidth*1.1/zoom),
				(int) (canvasHeight*1.1/zoom));
		//Vertical Lines
		for(int i=0; i<linesNeeded; i++){
			
			if(i==linesNeeded/2){
				//Make the origin lines a different color
				g2d.setPaint(lineColor.brighter().brighter().brighter());
			}
			g2d.drawLine(xMapToCanvasWithParallax(i-(linesNeeded/2),depthFactor),
					0,
					xMapToCanvasWithParallax(i-(linesNeeded/2),depthFactor)
					, (int)canvasHeight);
			if(i==linesNeeded/2){
				//now change it back
				g2d.setPaint(lineColor);
			}
		}
		//Horizontal Lines
		for(int i=0; i<linesNeeded; i++){
			if(i==linesNeeded/2){
				//Make the origin lines a different color
				g2d.setPaint(lineColor.brighter().brighter().brighter());
			}
			g2d.drawLine(0, 
					yMapToCanvasWithParallax(i-(linesNeeded/2),depthFactor),
					(int) canvasWidth, 
					yMapToCanvasWithParallax(i-(linesNeeded/2),depthFactor));
			if(i==linesNeeded/2){
				//Make the origin lines a different color
				g2d.setPaint(lineColor);
			}
		}
	}

	private void drawRegisteredObjects(){	
		//Before we draw, we must update the global registration list
		goList.sync();
		
		//Pre-calculate Constants
		xtranslate=(canvasWidth/2)-camPos.x*zoom;
		ytranslate=(canvasHeight/2)+camPos.y*zoom;
		zoomAF.setToTranslation(xtranslate, ytranslate);
		zoomAF.scale(zoom, zoom);
		
		//Draw all objects
		for(GraphicsObject go : goList){
			go.resetColBound(); //previous bounds are not valid anymore
			if(go.show==false) continue; //don't draw hidden stuff
			
			//grab object's transforms 
			AffineTransform objXform=go.getAffineTransform();
			//only apply zoom to objects, not to UI
			if(go.uiLayer==false)
				objXform.preConcatenate(zoomAF);
			
			//draw all polygons in this object
			for(GraphicsObject.Polygon cpoly : go.pList)
				drawPolygon(objXform,cpoly);
		}
	}
	private void drawPolygon(AffineTransform objXform, GraphicsObject.Polygon poly){
		//grab precomputed limb, and transform it around the actual object
		poly.finalPath=(GeneralPath) poly.midPath.createTransformedShape(objXform); 
		
		if(poly.noFill==false){
			g2d.setColor(poly.fill);
			g2d.fill(poly.finalPath);
		}
		
		//draw the Border
		g2d.setColor(poly.bord);
		g2d.draw(poly.finalPath);
		
		//Draw the glow
		//drawGlow(polyPath,1,currentObj.bord);
	}

	private void blurCanvas(BufferedImage canvasImage){
		int size=4;
		float[] blurMatrix = new float[size*size];
		for (int i = 0; i < blurMatrix.length; i++)
			blurMatrix[i] = 1.0f/blurMatrix.length;
		Kernel kernel = new Kernel(size, size, blurMatrix);
		BufferedImageOp op = new ConvolveOp(kernel);
		canvasImage = op.filter(canvasImage, null);
	    g2d.drawImage(canvasImage, 0, 0, this);
	}
	private void drawGlow(Shape circlePath, int glowWidth, Color borderColor){
		Color shadowColor=GraphicsObject.setColorAlpha(borderColor, 15);
		for (int j=0; j<glowWidth;j+=2){
			g2d.setColor(shadowColor);//borderColor
			g2d.draw(circlePath);
			g2d.setStroke(new BasicStroke((float)j, BasicStroke.CAP_ROUND,
			          BasicStroke.JOIN_ROUND));
			g2d.draw(circlePath);
		}
		g2d.setStroke(new BasicStroke(1));
	}
	private void drawGlow(GeneralPath polyPath, int glowWidth, Color borderColor){
		Color shadowColor=GraphicsObject.setColorAlpha(borderColor, 15);
		for (int j=0; j<glowWidth;j+=2){
			g2d.setColor(shadowColor);//borderColor
			g2d.draw(polyPath);
			g2d.setStroke(new BasicStroke((float)j, BasicStroke.CAP_ROUND,
			          BasicStroke.JOIN_ROUND));
			g2d.draw(polyPath);
		}
		g2d.setStroke(new BasicStroke(1));
	}
	private int xMapToCanvasWithParallax(float coord, float depthFactor) {
		return (int) (((canvasWidth / 2) - camPos.x * zoom / depthFactor) + coord
				* zoom);
	}
	private int yMapToCanvasWithParallax(float coord, float depthFactor) {
		return (int) (((canvasHeight / 2) + camPos.y * zoom / depthFactor) + coord
				* zoom);
	}
	public void moveCamera(float xOffset, float yOffset){
		setCamera(camPos.x+xOffset,camPos.y+yOffset);
	}
	public void setCamera(float xLoc, float yLoc){
		camPos.x=xLoc;
		camPos.y=yLoc;
	}
	public void zoomIn(){
		zoom=Math.min(zoom*2,MAX_ZOOM);
	}
	public void zoomInSmoothly(){
		zoomStart=zoom;
		zoomGoal=Math.min(zoom*2,MAX_ZOOM);
	}
	public void zoomOut(){
		zoom=Math.max(zoom/2,MIN_ZOOM);
	}
	public void zoomOutSmoothy(){
		zoomStart=zoom;
		zoomGoal=Math.max(zoom/2,MIN_ZOOM);
	}
	public static Color getRandomColor(){
		int[] rgb=new int[3];
		for (int i=0;i<3;i++){
			rgb[i]=(int) (Math.random()*255);
		}
		float[] hsb= Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], null);
		if (hsb[2]<0.5f){//if the brightness is too dark
			rgb[0]=Math.min(255,rgb[0]+60);
			rgb[1]=Math.min(255,rgb[0]+60);
			rgb[2]=Math.min(255,rgb[0]+60);
		}
		Color output = new Color(rgb[0],rgb[1],rgb[2]);
		return output;
	}
	public static Color lowerAlpha(Color oldColor){
		int alpha=oldColor.getAlpha();
		int red=oldColor.getRed();
		int green=oldColor.getGreen();
		int blue=oldColor.getBlue();
		alpha=Math.max(alpha-100, 40);
		Color newColor= new Color(red,green,blue,alpha);
		
		return newColor;
	}

	//TEMPORARY
	private void drawText(String text, int xPos, int yPos){
		g2d.setFont(new Font("Courier", Font.BOLD, 16));
		g2d.setColor(Color.WHITE);
		g2d.drawString(text, xPos, yPos);
	}
}