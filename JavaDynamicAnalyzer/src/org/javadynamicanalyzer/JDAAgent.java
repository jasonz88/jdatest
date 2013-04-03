package org.javadynamicanalyzer;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.BadBytecode;


public class JDAAgent implements ClassFileTransformer {
	//System Libraries
	final static String[] ignore = new String[] { "sun/", "java/", "javax/" };
	
	//Statically load javaagent at startup
    public static void premain(String args, Instrumentation inst) {
    	JDAAgent jda = new JDAAgent(inst);
    	JarFile jf=null;
    	try { jf=new JarFile("JDAtools.jar"); }
    	catch (IOException e) { e.printStackTrace(); }
    	inst.appendToBootstrapClassLoaderSearch(jf);
    	inst.addTransformer(jda);
    }
    //Dynamic load javaagent when application is already running
    public static void agentmain(String args, Instrumentation inst) throws Exception {
    	JDAAgent jda = new JDAAgent(inst);
    	inst.addTransformer(jda);
    }
   
    //Instrumentation Strings
    //Package names
    static final String toolImport="";
    //static final String timerPrefix="";
    //static final String jdaPrefix="org.javadynamicanalzyer.";
    static final String toolPrefix="JDAtool";
    
    //Variable names
    static final String varPrefix="_JDA_";
    static final String tslStr=toolPrefix+".tsl";
    
    //Command Lines
    static final String tslStart=tslStr+".start(); ";
    static final String tslStop=tslStr+".stop(); ";
    static final String stopwatchStart=var("sw")+".start(); ";
    static final String stopwatchStop=var("sw")+".stop(); ";
    static final String stopwatchGetTime="long "+var("dt")+"="+var("sw")+".getTime(); ";
    //stopwatchMake() is a function
    
    //Command Functions
    static String var(String v){ return varPrefix+v; }
    static String stopwatchMake(String methodName){ 
    	return "Stopwatch "+var("sw")+"="+tslStr+".makeStopwatch("+methodName+"); "; 
    }
    
    static Instrumentation inst;
    
    public JDAAgent(Instrumentation inst){ JDAAgent.inst=inst; }
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> clazz, ProtectionDomain pd, byte[] classfileBuffer) {
		for (String ign : ignore)
			if(className.startsWith(ign))
				return classfileBuffer;
		
		byte[] out=classfileBuffer;
		ClassPool pool=ClassPool.getDefault(); //sets the library search path to the default
		pool.importPackage("javadyanmicanalyzer.JDAtool");
		CtClass cc=null;
		try { 
			cc = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
			System.out.println("Analyzing "+cc.getName());
			CtClassDetails.getMethodsDerived(cc);
			for(CtMethod m : CtClassDetails.getMethodsDerived(cc)){
				System.out.println("XFORM BITCH: "+className);
				System.out.println(m.getName());
				addTimer(m);
			}
			System.out.println("Done Analyzing!");
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
		String methodEntry="{ ";
		methodEntry+=tslStop;
		methodEntry+=stopwatchMake(m.getName());
		methodEntry+=stopwatchStart;
		methodEntry+=tslStart;
		methodEntry+="}";
		
		String methodExit="{ ";
		//methodExit+=tslStop;
		//methodExit+=stopwatchStop;
		//methodExit+=stopwatchGetTime;
		//methodExit+=tslStart;
		methodExit+="} ";
		//ControlFlow flow=new ControlFlow(m);
		//Block[] block=flow.basicBlocks();
		//block[0].
		//ClassPool cp=ClassPool.getDefault();
		//m.insertBefore("System.out.println(\""+m.getName()+"\":);");
		System.out.println("Instrumenting: "+methodEntry);
		m.insertBefore(methodEntry);
		System.out.println("Instrumenting: "+methodExit);
		m.insertAfter(methodExit);
	}
	
}