package org.javadynamicanalyzer;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.analysis.ControlFlow;
import javassist.bytecode.analysis.ControlFlow.Block;


public class JDAAgent implements ClassFileTransformer {
	//System Libraries
	final static String[] ignore = new String[]{ 
		"sun/", "java/", "javax/", "javassist/", "javadynamicanalyzer/", "jung/", "apache/"};
	
    //Package names
    static final String[] toolImport={"org.javadynamicanalyzer.JDAtool",
    								  "org.javadynamicanalyzer.MethodStackEntry",
    								  "org.javadynamicanalyzer.graph.BasicBlockPath",
    								  "org.javadynamicanalyzer.timer.Stopwatch"};
    
    //Options
    static boolean trackTime=false;
    static boolean trackBlocks=true;
    static boolean trackPaths=false;
    static boolean verbose=false;
	
	//Statically load javaagent at startup
    public static void premain(String args, Instrumentation inst) {
    	if(trackPaths) trackBlocks=true;
    	
    	JDAAgent jda = new JDAAgent(inst);
    	inst.addTransformer(jda);
    	
    	//Collect my instrumentation tools in the ClassPool
    	ClassPool cp=ClassPool.getDefault();
    	try {
    		for(String imp : toolImport)
    			cp.get(imp);
		} 
    	catch (NotFoundException e) { e.printStackTrace(); }
    }
    //Dynamic load javaagent when application is already running
    public static void agentmain(String args, Instrumentation inst) throws Exception {
    	JDAAgent jda = new JDAAgent(inst);
    	inst.addTransformer(jda);
    }
   
    //Instrumentation Strings
    //Variable names
    static final String varPrefix="_JDA_";
    static final String tslStr="JDAtool.tsl";
    
    //Command Lines
    static final String tslStart=tslStr+".start(); ";
    static final String tslStop=tslStr+".stop(); ";
    static String tslMakeStopwatch(String methodName){
    	return tslStr+".makeStopwatch(\""+methodName+"\"); ";
    }
    static String println(String[] str){ 
    	String out="System.out.println(\"\"";
    	boolean quotes=true;
    	for(String s : str){
    		String builder=s;
    		if(quotes)
    			builder="\""+builder+"\"";
    		builder="+"+builder;
    		out+=builder;
    		quotes=!quotes;
    	}
    	out+="); ";
    	return out;
    }
    static String println(String str, String literal){ return "System.out.println(\""+str+"\"+"+literal+"); "; }
    
    //Command Functions
    static String var(String v, String post){ return varPrefix+v+post; }
    static String var(String v){ return var(v,""); }
    
    static Instrumentation inst;
    
    public JDAAgent(Instrumentation inst){ JDAAgent.inst=inst; }
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> clazz, ProtectionDomain pd, byte[] classfileBuffer) {
		for (String ign : ignore)
			if(className.contains(ign))
				return classfileBuffer;
		
		byte[] out=classfileBuffer;
		ClassPool pool=ClassPool.getDefault(); //sets the library search path to the default
		
		//Sort out imports for JDA toolsets to work
		for(String imp : toolImport)
			pool.importPackage(imp);
		
		//Parse the class!
		CtClass cc=null;
		try {
			//Read the byte array
			cc = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
			System.out.println("Analyzing "+cc.getName());
			
			//Read the methods
			CtClassDetails.getMethodsDerived(cc);
			for(CtMethod m : CtClassDetails.getMethodsDerived(cc)){
				System.out.println("Instrumenting: " + m.getName());
				instrumentMethod(m);
			}
			out=cc.toBytecode();
		} 
		catch (IOException | RuntimeException | CannotCompileException | BadBytecode e) { e.printStackTrace(); }
		finally { 
			if(cc!=null)
				cc.detach();
		}
		
        return out;
	}
	
	void instrumentMethod(CtMethod m) throws CannotCompileException, BadBytecode{
		String methodName=m.getLongName();	
		boolean isMain=methodName.contains("main");
		
		String sw=null;
		if(trackTime){
			sw=var("sw");
			try { m.addLocalVariable(sw, ClassPool.getDefault().get("org.javadynamicanalyzer.timer.Stopwatch")); } 
			catch (NotFoundException e) { e.printStackTrace(); }
		}
		
		String mse=null;
		String lmse=null;
		if(trackBlocks){
			mse=var("mse");
			try { m.addLocalVariable(mse, ClassPool.getDefault().get("org.javadynamicanalyzer.MethodStackEntry")); } 
			catch (NotFoundException e) { e.printStackTrace(); }
			lmse=var("lmse");
			try { m.addLocalVariable(lmse, ClassPool.getDefault().get("org.javadynamicanalyzer.MethodStackEntry")); } 
			catch (NotFoundException e) { e.printStackTrace(); }
		}
		
		String path=null;
		if(trackPaths){
			path=var("path");
			try { m.addLocalVariable(path, ClassPool.getDefault().get("org.javadynamicanalyzer.graph.BasicBlockPath")); } 
			catch (NotFoundException e) { e.printStackTrace(); }			
		}
		
		//INSERTED AT THE BEGINNING OF THE METHOD
		String methodEntry=new String();
		if(trackTime){
			methodEntry+=tslStop;
			methodEntry+=sw+"= new Stopwatch("+tslStr+"); ";
			methodEntry+=sw+".start(); ";
		}
		if(trackBlocks){
			if(isMain)
				methodEntry+=mse+"=new MethodStackEntry(\""+methodName+"\"); ";
			else{
				methodEntry+=lmse+"=JDAtool.getLastMSE(); ";
				methodEntry+=mse+"=new MethodStackEntry(\""+methodName+"\"); ";
				methodEntry+=lmse+".mn.addLink("+lmse+".blockIndex, "+mse+".mn); ";
			}
		}
		if(trackPaths){
			methodEntry+=path+"=new BasicBlockPath(); ";
		}
		if(trackTime){
			methodEntry+=tslStart;
		}
		methodEntry="{ " + methodEntry + "}";
		System.out.println("Before: "+methodEntry);
		m.insertBefore(methodEntry);
		
		//INSERTED AT BASIC BLOCKS
		MethodNode mNode=JDAtool.getMethodNode(methodName); //Get my method data structure for this method
		
		ControlFlow flow=new ControlFlow(m);
		Block[] blockArray=flow.basicBlocks();	 
		for(Block thisbb : blockArray){
			//Statically Update Method Graph
			mNode.addNode(thisbb);
			int inSize=thisbb.incomings();
			for(int i=0; i<inSize; ++i){
				Block inbb=thisbb.incoming(i);
				mNode.addEdge(inbb,thisbb);
			}
			
			//Dynamically Update Method Statistics
			String blockUpdate=new String();
			String thisbbIndex=Integer.toString(thisbb.index());
			if(trackBlocks){
				//blockUpdate+=mse+".blockIndex="+thisbbIndex+"; ";
				blockUpdate+=mse+".setBlockIndex("+thisbbIndex+"); ";
			}
			if(trackPaths){
				blockUpdate+=path+".addBlock("+thisbbIndex+"); ";
			}
			blockUpdate="{ " + blockUpdate + "}";
			
			//Insert
			int pos=m.getMethodInfo().getLineNumber(thisbb.position()); //Source code line position from binary line position
			System.out.print("At "+pos+": "+blockUpdate);
			int n=m.insertAt(pos, blockUpdate);
			System.out.println(" -> "+n);
		}			

		//INSERTED AT THE END OF THE METHOD
		String methodExit=new String();
		if(trackTime){
			methodExit+=tslStop;
			methodExit+=sw+".stop(); ";
			methodExit+=println(methodName+" took\\t",sw+".getTime()");
		}
		if(trackPaths){
			methodExit+=mse+".mn.addPath("+path+"); ";
		}
		if(trackBlocks){
			methodExit+="JDAtool.methodStackPop(); ";
		}
		if(trackTime){
			methodExit+=tslStart;
		}
		if(isMain) 
			methodExit+="JDAtool.gui(); ";
		methodExit="{ " + methodExit + "}";
		System.out.println("After: "+methodExit);
		m.insertAfter(methodExit);
	}
}