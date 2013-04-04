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
		try { 
			superClass=cc.getSuperclass();
			for(CtMethod m : superClass.getMethods())
				methodsBase.add(m);
		} 
		catch (NotFoundException e) { e.printStackTrace(); }
		
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
