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

public class JDAAgent implements ClassFileTransformer {
	//System Libraries
	final static String[] ignore = new String[]{ 
		"sun/", "java/", "javax/", "javassist/", "javadynamicanalyzer/", "bettercontainers/" };
	
	//Statically load javaagent at startup
    public static void premain(String args, Instrumentation inst) {
    	JDAAgent jda = new JDAAgent(inst);
    	inst.addTransformer(jda);
    	
    	ClassPool cp=ClassPool.getDefault();
    	try {
			cp.get("org.javadynamicanalyzer.JDAtool");
			cp.get("org.javadynamicanalyzer.timer.Stopwatch");
		} 
    	catch (NotFoundException e) { e.printStackTrace(); }
    }
    //Dynamic load javaagent when application is already running
    public static void agentmain(String args, Instrumentation inst) throws Exception {
    	JDAAgent jda = new JDAAgent(inst);
    	inst.addTransformer(jda);
    }
   
    //Instrumentation Strings
    //Package names
    static final String[] toolImport={"org.javadynamicanalyzer.JDAtool",
    								  "org.javadynamicanalyzer.timer.Stopwatch"};
    
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
				addTimer(m);
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
	
	void addTimer(CtMethod m) throws CannotCompileException, BadBytecode{
		String sw=var("sw");
		try { m.addLocalVariable(sw, ClassPool.getDefault().get("org.javadynamicanalyzer.timer.Stopwatch")); } 
		catch (NotFoundException e) { e.printStackTrace(); }
		
		String methodEntry="{ ";
		methodEntry+=tslStop;
		methodEntry+=sw+"="+tslMakeStopwatch(m.getName());
		methodEntry+=sw+".start(); ";
		methodEntry+=tslStart;
		methodEntry+="}";
		
		String methodExit="{ ";
		methodExit+=tslStop;
		methodExit+=sw+".stop(); ";
		methodExit+=println(m.getName()+" took\\t",sw+".getTime()");
		methodExit+=tslStart;
		methodExit+="} ";
		
		//ControlFlow flow=new ControlFlow(m);
		//Block[] block=flow.basicBlocks();
		//block[0].
		//ClassPool cp=ClassPool.getDefault();
		//m.insertBefore("System.out.println(\""+m.getName()+"\":);");
		
		System.out.println("Before: "+methodEntry);
		m.insertBefore(methodEntry);
		System.out.println("After: "+methodExit);
		m.insertAfter(methodExit);
	}
}