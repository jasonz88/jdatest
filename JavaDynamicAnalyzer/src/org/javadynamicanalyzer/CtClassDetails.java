package org.javadynamicanalyzer;

import java.util.HashSet;
import java.util.Set;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class CtClassDetails {
	static CtClass classCache = new CtClass("NULL") {}; 
	static Set<CtMethod> methodsDerived = null;
	static Set<CtMethod> methodsBase = null;
	
	static void initMethods( CtClass cc){
		classCache=cc;
		methodsDerived=new HashSet<CtMethod>();
		methodsBase=new HashSet<CtMethod>();
		try {
			CtClass superClass=cc.getSuperclass();
			//if(superClass!=null) //not sure if this is needed
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
		if(classCache.equals(cc)==false)
			initMethods(cc);
		return methodsDerived;
	}
	static public Set<CtMethod> getMethodsBase(CtClass cc){
		if(cc==null) return null;
		if(classCache.equals(cc)==false)
			initMethods(cc);
		return methodsBase;
	}
}
