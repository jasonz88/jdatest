package org.javadynamicanalyzer;

import java.util.HashSet;
import java.util.Set;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class CtClassDetails {
	static CtClass classCache = null; 
	static Set<CtMethod> methodsDerived = null;
	static Set<CtMethod> methodsBase = null;
	
	static void initMethods( CtClass cc){
		classCache=cc;
		methodsDerived=new HashSet<CtMethod>();
		methodsBase=new HashSet<CtMethod>();
		
		CtClass superClass=null;
		System.out.println("Test");
		try {
			System.out.println("Test");
			superClass=cc.getSuperclass();
			System.out.println("Test");
		} 
		catch (NotFoundException e) { e.printStackTrace(); }
		finally{ 
			System.out.println("Test");
		}
		for(CtMethod m : superClass.getMethods())
			methodsBase.add(m);
		for(CtMethod m : cc.getMethods())
			if(methodsBase.contains(m)==false)
				methodsDerived.add(m);
	}

	static public Set<CtMethod> getMethodsDerived(CtClass cc){
		if(cc==null) return null;
		if(classCache!=null)
			if(classCache.equals(cc)==true)
				return methodsDerived;

		initMethods(cc);
		System.out.println("methodsDerived: "+methodsDerived.size());
		System.out.println("methodsBase: "+methodsBase.size());
		return methodsDerived;
	}
	static public Set<CtMethod> getMethodsBase(CtClass cc){
		if(cc==null) return null;
		if(classCache!=null)
			if(classCache.equals(cc)==true)
				return methodsBase;

		initMethods(cc);		
		return methodsBase;
	}
}
