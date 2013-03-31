package org.javadynamicanalyzer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class JDA {
    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new Transformer());
    }
}

class Transformer implements ClassFileTransformer {
	
    public byte[] transform(ClassLoader l, String className, Class<?> c, ProtectionDomain pd, byte[] b) 
    throws IllegalClassFormatException {
        System.out.print("Loading class: ");
        System.out.println(className);
        return b;
    }
}
