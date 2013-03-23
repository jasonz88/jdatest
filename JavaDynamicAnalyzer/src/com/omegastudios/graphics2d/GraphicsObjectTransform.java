package com.omegastudios.graphics2d;

import java.awt.geom.AffineTransform;

public class GraphicsObjectTransform {
	final static int SCALE=1;
	final static int ANGLE=2;
	final static int POS=4;
	float fAngle;
	AffineTransform scale;
	AffineTransform angle;
	AffineTransform pos;
	protected AffineTransform at; //used by GraphicsEngine
	
	public GraphicsObjectTransform(){
		scale=new AffineTransform();
		scale.setToScale(1.0,1.0);
		
		angle=new AffineTransform();
		angle.setToRotation(0.0);
		
		pos=new AffineTransform();
		pos.setToTranslation(0,0);
		
		at=new AffineTransform();
	}
	
	public void setScale(float scale){
		this.scale.setToScale(scale,scale);
	}
	public void setScale(float scaleX, float scaleY){
		scale.setToScale(scaleX, scaleY);
	}
	public void setAngle(float angle){
		this.angle.setToRotation(-angle);
		fAngle=angle;
	}
	public void setAngle(float angle, float x, float y){
		this.angle.setToRotation(-angle,x,-y);
		fAngle=angle;
	}
	public void setPosition(float x, float y){
		this.pos.setToTranslation(x, -y);
	}
	public AffineTransform getAffineTransformSPA(){
		at.setTransform(scale);
		at.concatenate(pos);
		at.concatenate(angle);
		return at;
	}
	public AffineTransform getAffineTransformAPS(){ 
		at.setTransform(angle);
		at.concatenate(pos);
		at.concatenate(scale);
		return at;
	}
	public AffineTransform getAffineTransformSAP(){ 
		at.concatenate(scale);
		at.setTransform(angle);
		at.concatenate(pos);
		return at;
	}
	public AffineTransform getAffineTransformAP(){ 
		at.setTransform(angle);
		at.concatenate(pos);
		return at;
	}
	public AffineTransform getAffineTransformSP(){
		at.setTransform(scale);
		at.concatenate(pos);
		return at;
	}
	public AffineTransform getAffineTransformPA(){
		at.setTransform(pos);
		at.concatenate(angle);
		return at;
	}
	
	public AffineTransform getAffineTransformP() { return (AffineTransform) pos.clone();   }
	public AffineTransform getAffineTransformS() { return (AffineTransform) scale.clone(); }
	public AffineTransform getAffineTransformA() { return (AffineTransform) angle.clone(); }
	
	public float getPosX()	{ return (float)at.getTranslateX(); }
	public float getPosY()	{ return (float)at.getTranslateY(); }
	public float getAngle()	{ return fAngle;					}	
}
