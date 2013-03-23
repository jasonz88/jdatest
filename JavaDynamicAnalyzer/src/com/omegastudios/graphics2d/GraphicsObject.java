package com.omegastudios.graphics2d;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class GraphicsObject {
	//DATA CONSTRUCTS
	public class Polygon{
		protected GeneralPath initPath; //initial set of a building points
		protected GeneralPath midPath; //polygon transformation points
		protected GeneralPath finalPath; //final points drawn on the canvas
		GraphicsObjectTransform got;
		public Color bord;
		public Color fill;
		boolean noFill;
		
		Polygon(){
			bord=new Color(0,255,0); //Default green
			fill=new Color(0,255,0); //Default green
			initPath=new GeneralPath();
			finalPath=new GeneralPath();
			got=new GraphicsObjectTransform();
			noFill=false;
		}
	
		public void setGeneralPath(GeneralPath gp){
			initPath=gp;
			buildMidpath();
		}
		public void setGeneralPath(Shape s){
			initPath=new GeneralPath();
			initPath.append(s, true);
			buildMidpath();
		}
		void buildMidpath(){
			midPath=(GeneralPath) initPath.clone();
			midPath.transform(got.getAffineTransformSPA());
		}

		//GraphicsObjectTransform Delegate Methods
		/**
		 * @param scale
		 * @see com.omegastudios.graphics2d.GraphicsObjectTransform#setScale(float)
		 */
		public void setScale(float scale) {
			got.setScale(scale);
			buildMidpath();
		}
		/**
		 * @param scaleX
		 * @param scaleY
		 * @see com.omegastudios.graphics2d.GraphicsObjectTransform#setScale(float, float)
		 */
		public void setScale(float scaleX, float scaleY) {
			got.setScale(scaleX, scaleY);
			buildMidpath();
		}
		/**
		 * @param angle
		 * @see com.omegastudios.graphics2d.GraphicsObjectTransform#setAngle(float)
		 */
		public void setAngle(float angle) {
			got.setAngle(angle);
			buildMidpath();
		}
		/**
		 * @param angle
		 * @param x
		 * @param y
		 * @see com.omegastudios.graphics2d.GraphicsObjectTransform#setAngle(float, float, float)
		 */
		public void setAngle(float angle, float x, float y) {
			got.setAngle(angle, x, y);
			buildMidpath();
		}
		/**
		 * @param x
		 * @param y
		 * @see com.omegastudios.graphics2d.GraphicsObjectTransform#setPosition(float, float)
		 */
		public void setPosition(float x, float y) {
			got.setPosition(x, y);
			buildMidpath();
		}
		/**
		 * @return
		 * @see com.omegastudios.graphics2d.GraphicsObjectTransform#getPosX()
		 */
		public float getPosX() {
			return got.getPosX();
		}
		/**
		 * @return
		 * @see com.omegastudios.graphics2d.GraphicsObjectTransform#getPosY()
		 */
		public float getPosY() {
			return got.getPosY();
		}
		/**
		 * @return
		 * @see com.omegastudios.graphics2d.GraphicsObjectTransform#getAngle()
		 */
		public float getAngle() {
			return got.getAngle();
		}
	}
	
	//CONSTANTS
	final static AffineTransform invert=AffineTransform.getScaleInstance(1, -1);
	
	//VARIABLES
	GraphicsEngine ge;
	ArrayList<Polygon> pList;
	boolean uiLayer;
	public boolean noCollision;
	Rectangle2D colBound; //boundary in UI layer for collision
	Rectangle2D statBound; //boundary of initial object
	boolean updateStatBound;
	public boolean show;

	GraphicsObjectTransform got;
	
	//METHODS
	public GraphicsObject(GraphicsEngine nge, Vec2 pos, float angle, boolean uiLayer){
		pList=new ArrayList<Polygon>();
		show=true;
		got=new GraphicsObjectTransform();
		noCollision=false;
		
		colBound=null;
		statBound=null;
		updateStatBound=false;
		
		this.uiLayer=uiLayer;
		this.ge=nge;
	}
	public GraphicsObject(GraphicsEngine nge, Vec2 pos, float angle)	{ this(nge,  pos,		    angle, false);	}
	public GraphicsObject(GraphicsEngine nge, boolean uiLayer)			{ this(nge,  new Vec2(0,0), 0    , uiLayer);}
	public GraphicsObject(GraphicsEngine nge)							{ this(nge,  new Vec2(0,0), 0    , false); 	}
	public GraphicsObject() 											{ this(null, new Vec2(0,0), 0    , false);	} 
	
	public void destroy(){
		ge.destroy(this);
	}
	public void setUILayer(boolean l){
		if(uiLayer!=l){
			uiLayer=l;
			if(ge!=null){
				GraphicsEngine geclone=ge;
				unregister();
				geclone.registerObject(this);
			}
		}
	}
	
	public int lastPolyIndex()		{ return pList.size()-1; 	}
	
	public float getPositionX()		{ return got.getPosX();		} 
	public float getPositionY() 	{ return -got.getPosY();	}
	public float getAngle()			{ return -got.fAngle;		}
	
	//Initial object boundaries
	public float getBoundWidth(){
		if(updateStatBound) updateBound();
		return (float) statBound.getWidth();		
	}
	public float getBoundHeight(){
		if(updateStatBound) updateBound();
		return (float) statBound.getHeight();		
	}
	void updateBound(Polygon newp){
		Rectangle2D r=newp.midPath.getBounds2D();
		if(statBound==null) statBound=r;
		else Rectangle2D.union(r, statBound, statBound);
	}
	void updateBound(){
		updateStatBound=false;
		Iterator<Polygon> itr=pList.iterator();
		if(itr.hasNext()==false) return; //no polygons, unstable behavior
		statBound.setRect(itr.next().midPath.getBounds2D());
		while(itr.hasNext())
			updateBound(itr.next());
	}
	
	//Collision boundaries
	public float getColBoundWidth(){
		if(colBound==null) updateColBound();
		return (float) (colBound.getWidth()/ge.zoom);		
	}
	public float getColBoundHeight(){
		if(colBound==null) updateColBound();
		return (float) (colBound.getHeight()/ge.zoom);		
	}
	
	public void resetColBound(){
		colBound=null;
	}
	public void updateColBound(){
		if(colBound!=null) return; //no need to calculate this again
		Iterator<Polygon> itr=pList.iterator();
		if(itr.hasNext()) colBound=itr.next().finalPath.getBounds();
		while(itr.hasNext())
			updateColBound(itr.next());
	}
	void updateColBound(Polygon newp){
		Rectangle r=newp.finalPath.getBounds();
		Rectangle2D.union(r,colBound,colBound);
	}
	public boolean simpleCollision(float x, float y){
		if(colBound==null) return false;
		return colBound.contains(x, y);
	}
	
	//Making Polygons
	public Polygon createShapePolygon(Vec2 offset, float angle, ArrayList<Vec2> vertices){
		GeneralPath gp=toGeneralPath(vertices);
		gp.closePath();
		
		Polygon newp=new Polygon();
		newp.got.setAngle(angle);
		newp.got.setPosition(offset.x, offset.y);
		newp.setGeneralPath(gp);
		
		addPolygon(newp);
		return newp;
	}
	public Polygon createShapePolygon(Vec2 offset, float angle, Vec2[] vertices){
		return createShapePolygon(offset, angle, new ArrayList<Vec2>(Arrays.asList(vertices)));
	}
	public Polygon createShapeBox(Vec2 offset, float w, float h){
		//convert to half dimensions
		w=w/2;
		h=h/2;
		
		Vec2[] box={new Vec2(w,h),
					new Vec2(-w,h),
					new Vec2(-w,-h),
					new Vec2(w,-h)
		};
		
		return createShapePolygon(offset, 0, box);
	}
	public Polygon createShapeEdge(Vec2 offset, float angle, ArrayList<Vec2> vertices){
		Polygon newp=new Polygon();
		newp.got.setAngle(angle);
		newp.got.setPosition(offset.x, offset.y);
		newp.setGeneralPath(toGeneralPath(vertices));
		newp.noFill=true;
		
		addPolygon(newp);
		return newp;
	}
	public Polygon createShapeEdge(Vec2 offset, float angle, Vec2[] vertices){
		return createShapeEdge(offset, angle, new ArrayList<Vec2>(Arrays.asList(vertices)));
	}
	public Polygon createShapeCircle(Vec2 offset, float radiusX, float radiusY){
		//NOTE: Ellipse2D takes diameters, not radii
		Polygon newp=new Polygon();
		newp.got.setPosition(offset.x, offset.y);
		newp.setGeneralPath(new Ellipse2D.Float(-radiusX,-radiusY,radiusX*2,radiusY*2));
		
		addPolygon(newp);
		return newp;
	}
	void addPolygon(Polygon newp){ //this function is called whenever a new polygon is added from the previous functions
		//newp.midPath.transform(scale); //add GraphicsObject scaling to it
		pList.add(newp);
		updateBound(newp);
	}
	
	//Interface with GraphicsEngine
	public void register(GraphicsEngine ge){
		if(ge==null) return;
		ge.registerObject(this);
	}
	public void unregister(){
		if(ge==null) return;
		ge.unregisterObject(this);
	}
	protected Iterator<Polygon> polyIterator(){ return pList.iterator(); }
	
	//Modifying Polygons
	public void setFillAlpha(int i, int alpha){
		Color fill=pList.get(i).fill;
		fill=setColorAlpha(fill,alpha); 	
	}
	public void setFillAlpha(int alpha){ 
		//Color fill=pList.lastElement().fill;
		Color fill=lastPoly().fill;
		fill=setColorAlpha(fill,alpha); 
	}
	public void setBordAlpha(int i, int alpha){ 
		Color bord=pList.get(i).bord;
		bord=setColorAlpha(bord,alpha);		
	}
	public void setBordAlpha(int alpha){ 
		//Color bord=pList.lastElement().bord;
		Color bord=lastPoly().bord;
		bord=setColorAlpha(bord,alpha);		
	}
	public void setAlpha(int i, int alpha){
		setFillAlpha(alpha);
		setBordAlpha(alpha);
	}
	
	public void setFill(int i, Color c){
		pList.get(i).fill=c;
	}
	public void setFill(Color c){ 
		//pList.lastElement().fill=c;
		lastPoly().fill=c;
	}	
	public void setBorder(int i, Color c){
		pList.get(i).bord=c;
	}
	public void setBorder(Color c){
		//pList.lastElement().bord=c;
		lastPoly().bord=c;
	}
	public void setColor(int i, Color c){
		setFill(i,c);
		setBorder(i,c);
	}
	public void setColor(Color c){ 
		setFill(c);
		setBorder(c);
	}
	
	public int getNumPolygons(){ return pList.size();		}
	public Polygon getPolygon(int p){ 
		updateStatBound=true;
		return pList.get(p);
	}
	public Polygon get(int p){ return getPolygon(p); }

	//Destroying Polygons
	public void clearAll(){
		pList.clear();
	}
	public void clearShape(int i){
		pList.remove(i);
	}
	
	//Auxiliary Functions
	Polygon lastPoly()								{ return pList.get(pList.size()-1);	}
	static Color setColorAlpha(Color c, int alpha)	{ 
		return new Color(c.getRed(),c.getGreen(),c.getBlue(), alpha);
	}
	static GeneralPath toGeneralPath(ArrayList<Vec2> v){
		if(v.size()==0) return new GeneralPath();
		
		GeneralPath gp=new GeneralPath(GeneralPath.WIND_EVEN_ODD,v.size());
		
		gp.moveTo(v.get(0).x,-v.get(0).y); //start the path
		for(int i=1; i<v.size(); i++)
				gp.lineTo(v.get(i).x,-v.get(i).y); //continue the path
		
		return gp;
	}
	static GeneralPath toGeneralPath(Vec2[] v){
		if(v.length==0) return new GeneralPath();
		
		GeneralPath gp=new GeneralPath(GeneralPath.WIND_EVEN_ODD,v.length);
		
		gp.moveTo(v[0].x,-v[0].y); //start the path
		for(int i=1; i<v.length; i++)
				gp.lineTo(v[i].x,-v[i].y); //continue the path
		
		return gp;
	}
	void updatePolygons(){
		for(Polygon p : pList)
			p.buildMidpath();
		updateBound();
	}
	
	//GraphicsObjectTransform Delegate Methods
	public void setPosAngle(float x, float y, float angle){
		got.setPosition(x, y);
		got.setAngle(angle,x,y);
	}
	public void setPosAngle(Vec2 pos, float angle)	{ setPosAngle(pos.x,pos.y,angle);	}
	
	/**
	 * @param scale
	 * @see com.omegastudios.graphics2d.GraphicsObjectTransform#setScale(float)
	 */
	public void setScale(float scale) {
		got.setScale(scale,scale);
	}
	/**
	 * @param scaleX
	 * @param scaleY
	 * @see com.omegastudios.graphics2d.GraphicsObjectTransform#setScale(float, float)
	 */
	public void setScale(float scaleX, float scaleY) {
		got.setScale(scaleX,scaleY);
	}
	/**
	 * @return
	 * @see com.omegastudios.graphics2d.GraphicsObjectTransform#getAffineTransform()
	 */
	public AffineTransform getAffineTransform() {
		AffineTransform af=got.getAffineTransformAPS();
		if(uiLayer) af.preConcatenate(invert);
		return af; //scaling is done in precalculation with scale member 
	}
}