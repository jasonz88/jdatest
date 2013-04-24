package org.javadynamicanalyzer.tests;

import org.javadynamicanalyzer.JDAtool;
import org.javadynamicanalyzer.MethodStackEntry;

public class JDAtoolTestTrackBlock {
	static void foo(){
		MethodStackEntry lmse = JDAtool.getLastMSE();
		MethodStackEntry mse = new MethodStackEntry("foo");
		lmse.mn.addLink(lmse.blockIndex, mse.mn);
		mse.blockIndex=0;
		JDAtool.methodStackPop();
	}
	static void bar(){ 
		MethodStackEntry lmse = JDAtool.getLastMSE();
		MethodStackEntry mse = new MethodStackEntry("bar");
		lmse.mn.addLink(lmse.blockIndex, mse.mn);
		mse.blockIndex=0;
		foo();
		JDAtool.methodStackPop();
	}
	static public void main(String[] arg){
		MethodStackEntry mse = new MethodStackEntry("main");
		mse.blockIndex=0;
		foo();
		bar();
		JDAtool.methodStackPop();
	}
}
