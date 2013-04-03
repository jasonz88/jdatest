package org.javadynamicanalyzer;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;


public class JDAAgent implements ClassFileTransformer {
	//System Libraries
	final static String[] ignore = new String[] { "sun/", "java/", "javax/" };
	
	//Statically load javaagent at startup
    public static void premain(String args, Instrumentation inst) {
    	JDAAgent jda = new JDAAgent(inst);
    	inst.addTransformer(jda);
    }
    //Dynamic load javaagent when application is already running
    public static void agentmain(String args, Instrumentation inst) throws Exception {
    	System.out.println("agentmain");
    	/*
        JDAAgent.inst = inst;
        JDAAgent.inst.addTransformer(new Transformer());
        */
    }
    
    Instrumentation inst;
    
    public JDAAgent(Instrumentation inst){ this.inst=inst; }
    
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> clazz, ProtectionDomain pd, byte[] classfileBuffer) {
		for (String ign : ignore)
			if(className.startsWith(ign))
				return classfileBuffer;
		
		ClassPool pool=ClassPool.getDefault(); //sets the library search path to the default
		CtClass cc=null;
		try { 
			cc = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
			System.out.println("Methods for: " + className);
			for(CtMethod m : CtClassDetails.getMethodsDerived(cc))
				System.out.println(m.getName());
		} 
		catch (IOException | RuntimeException e) { e.printStackTrace(); }
		finally { 
			if(cc!=null)
				cc.detach();
		}
		
        return classfileBuffer;
	}
}