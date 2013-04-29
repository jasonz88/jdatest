package org.javadynamicanalyzer;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.analysis.ControlFlow;
import javassist.bytecode.analysis.ControlFlow.Block;
import cern.colt.Arrays;


public class JDAAgent implements ClassFileTransformer {
	//System Libraries
	final static String[] ignore = new String[]{ 
		"sun/", "java/", "javax/", "javassist/", "javadynamicanalyzer/", "jung/", "apache/"};
	
    //Package names
    static final String[] toolImport={"org.javadynamicanalyzer.JDAtool",
    								  "org.javadynamicanalyzer.MethodStackEntry",
    								  "org.javadynamicanalyzer.BasicBlockPath",
    								  "org.javadynamicanalyzer.timer.Stopwatch"};
	
	//Statically load javaagent at startup
    public static void premain(String args, Instrumentation inst) {
    	
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
			if(JDAtool.verbose)
				System.out.println("Analyzing "+cc.getName());
			
			//Read the methods
			CtClassDetails.getMethodsDerived(cc);
			for(CtMethod m : CtClassDetails.getMethodsDerived(cc)){
				if(JDAtool.verbose)
					System.out.println("Instrumenting: " + m.getName());
				instrumentMethod(m);
			}
			
			if(JDAtool.verbose)
				System.out.println("Instrumentation done!");
			
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
		
		String mse=var("mse");
		try { m.addLocalVariable(mse, ClassPool.getDefault().get("org.javadynamicanalyzer.MethodStackEntry")); } 
		catch (NotFoundException e) { e.printStackTrace(); }
		int mseCSindex=m.getMethodInfo().getCodeAttribute().getMaxLocals()-1;
		//System.out.println("mse: "+mseCSindex);
		
		String sw=null;
		if(JDAtool.trackTime){
			sw=var("sw");
			try { m.addLocalVariable(sw, ClassPool.getDefault().get("org.javadynamicanalyzer.timer.Stopwatch")); } 
			catch (NotFoundException e) { e.printStackTrace(); }
		}
		
		String lmse=null;
		if(JDAtool.trackBlocks){			
			lmse=var("lmse");
			try { m.addLocalVariable(lmse, ClassPool.getDefault().get("org.javadynamicanalyzer.MethodStackEntry")); } 
			catch (NotFoundException e) { e.printStackTrace(); }
		}
		
		//INSERTED AT THE BEGINNING OF THE METHOD
		String methodEntry=new String();
		if(JDAtool.trackTime){
			methodEntry+=tslStop;
			methodEntry+=sw+"= new Stopwatch("+tslStr+"); ";
			methodEntry+=sw+".start(); ";
		}
		if(JDAtool.trackBlocks && !isMain){
			methodEntry+=lmse+"=JDAtool.getLastMSE(); ";
			methodEntry+=mse+"=new MethodStackEntry(\""+methodName+"\"); ";
			methodEntry+=lmse+".mn.addLink("+lmse+".blockIndex, "+mse+".mn); ";
		}
		else 
			methodEntry+=mse+"=new MethodStackEntry(\""+methodName+"\"); ";
		if(JDAtool.trackPaths){
			//Go ahead and update the basic block position before we start the timer
			methodEntry+=mse+".setBlockIndex(0); ";
		}
		if(JDAtool.trackTime){
			methodEntry+=tslStart;
		}
		methodEntry="{ " + methodEntry + "}";
		if(JDAtool.verbose)
			System.out.println("Before: "+methodEntry);
		
		if(JDAtool.trackPaths==false)
			m.insertBefore(methodEntry); //we don't need anything fancy if we don't track paths
		else{ //fancy shit...
			//remember untouched byte array
			byte[] bytes0=m.getMethodInfo2().getCodeAttribute().getCode().clone();
			//insert the beginning instrumentation
			m.insertBefore(methodEntry);
			//now find the difference
			byte[] bytesf=m.getMethodInfo2().getCodeAttribute().getCode();
			List<Byte> inst=diff(bytes0,bytesf);
			
			//System.out.println("Instrument: "+inst);
			
			//We know the stack index of mse, so get the expected bytecode that uses it
			byte[] tag=getSetBlockID0Tag(mseCSindex);
			//now find that bytecode in the instrumented code
			int index=findMatch(inst,tag);
			//from here, we can extrapolate the invokevirtual index for setBlockID
			byte[] invokeVirtualIndex=new byte[]{inst.get(index+tag.length),inst.get(index+tag.length+1)};
			
			//INSERTED AT BASIC BLOCKS
			int len=new ControlFlow(m).basicBlocks().length;
			for(int i=0; i<len; ++i){
				Block thisbb=new ControlFlow(m).basicBlocks()[i];
				CodeIterator itr=m.getMethodInfo().getCodeAttribute().iterator();
				
				//Dynamically Update Method Statistics
				if(thisbb.index()==0) continue; //this one is already done
				int pos=thisbb.position();
				byte[] newCode=bytecodeSetBlockID(thisbb.index(),mseCSindex,invokeVirtualIndex);
				
				if(JDAtool.verbose)
					System.out.print("Basic Block At "+pos+": "+Arrays.toString(newCode));
				
				int n = itr.insertAt(pos, newCode);
				
				if(JDAtool.verbose)
					System.out.println(" -> "+n);
			}
		}
		
		MethodNode mNode=JDAtool.getMethodNode(methodName); //Get my method data structure for this method
		//Now that it's all been instrumented, do one more analysis on the control flow diagram
		for(Block thisbb : new ControlFlow(m).basicBlocks()){
			//Statically Update Method Graph
			mNode.addNode(thisbb);
			int inSize=thisbb.incomings();
			for(int in=0; in<inSize; ++in){
				Block inbb=thisbb.incoming(in);
				mNode.addEdge(inbb,thisbb);
			}
		}

		//INSERTED AT THE END OF THE METHOD
		String methodExit=new String();
		if(JDAtool.trackTime){
			methodExit+=tslStop;
			methodExit+=sw+".stop(); ";
		}
		if(JDAtool.trackPaths){
			methodExit+=mse+".concludePath(); ";
		}
		
		methodExit+="JDAtool.methodStackPop(); ";

		if(JDAtool.trackTime){
			methodExit+=mse+".mn.addTime("+sw+".getTime()); ";
			methodExit+=sw+".remove(); ";
			methodExit+=tslStart;
		}
		if(isMain) 
			methodExit+="JDAtool.gui(); ";
		methodExit="{ " + methodExit + "}";
		
		if(JDAtool.verbose)
			System.out.println("After: "+methodExit);
		
		m.insertAfter(methodExit);
	}
	
	//UTILITY FUNCTIONS
	byte[] getSetBlockID0Tag(int mseStackIndex){
		if(mseStackIndex<4) 
			return new byte[]{(byte) (Bytecode.ALOAD_0+mseStackIndex),3,(byte) 0xb6};
		else 
			return new byte[]{Bytecode.ALOAD,(byte) mseStackIndex,3,(byte) 0xb6};
	}
	List<Byte> diff(byte[] init, byte[] end){
		assert(init!=null && end!=null);
		List<Byte> out = new ArrayList<Byte>();
		
		int index=0;
		while(init[index]==end[index]) ++index;
		
		int i=index;
		while(init[index]!=end[i])
			out.add(end[i++]);
		
		return out;
	}
	int findMatch(List<Byte> inst, byte[] tag){
		assert(tag.length>=3);
		for(int i=0; i<inst.size(); ++i){ //for every offset in inst
			boolean match = true;
			for(int c=0; c<tag.length; ++c){ //match the tag array
				if(inst.get(i+c).equals(tag[c])==false){
					match=false;
					break;
				}
			}
			if(match==true) return i;
		}
		assert(false); //GG
		return -1;
	}
	byte[] bytecodeSetBlockID(int val, int mseIndex, byte[] invokevirt){
		byte[] tag=getSetBlockID0Tag(mseIndex);
		ArrayList<Byte> out = new ArrayList<Byte>();
		
		int i=0;
		while(tag[i]!=3) out.add(tag[i++]);
		
		if(val<6) 
			out.add((byte) (val+3));
		else if(val<0xff){
			out.add((byte) Bytecode.BIPUSH);
			out.add((byte) val);
		}
		else if(val<0xffff){
			out.add((byte) Bytecode.SIPUSH); //sipush
			out.add((byte) (val<<8)); 
			out.add((byte) (val & 0xff));
		}
		out.add((byte) Bytecode.INVOKEVIRTUAL); //%invoke virtual
		out.add(invokevirt[0]);
		out.add(invokevirt[1]);
		
		byte[] primitiveOut=new byte[out.size()];
		for(int b=0; b<primitiveOut.length; ++b)
			primitiveOut[b]=out.get(b);
		
		return primitiveOut;
	}
}