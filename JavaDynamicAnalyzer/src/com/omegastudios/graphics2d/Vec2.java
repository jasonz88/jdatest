package com.omegastudios.graphics2d;

public class Vec2 {
	float x;
	float y;
	
	public Vec2(){}
	public Vec2(float x, float y){
		this.x=x;
		this.y=y;
	}
	public Vec2(Vec2 v){
		this.x=v.x;
		this.y=v.y;
	}
	
}
