package org.javadynamicanalyzer.tests;
import org.javadynamicanalyzer.JDAtool;
import org.javadynamicanalyzer.timer.Stopwatch;
import org.javadynamicanalyzer.timer.Timestamp;

public class JDAtoolTest {
	public static void main(String[] arg) throws InterruptedException{
		long dt=0;
		
		//Initialization
		JDAtool.tsl.stop();
		Stopwatch sw=JDAtool.tsl.makeStopwatch("main");
		sw.start();
		
		//Begin!
		JDAtool.tsl.start();
		long time=System.nanoTime();
		
		//Execute Block
		for(long i=0; i<100000; i++){
			i+=i*i/Math.sqrt(i*i);
			System.out.println(Long.toString(i));
		}
		
		//Pause
		JDAtool.tsl.stop();
		dt+=System.nanoTime()-time;
		
		System.out.println("Break time...");
		Thread.sleep(1000);
		
		//Start Again!
		JDAtool.tsl.start();
		time=System.nanoTime();
		
		//Execute Block
		for(long i=0; i<100000; i++){
			i+=i*i/Math.sqrt(i*i);
			System.out.println(Long.toString(i));
		}
		
		//Conclude
		JDAtool.tsl.stop();
		dt+=System.nanoTime()-time;
		
		System.out.println();
		System.out.print("Real Time:\t");
		System.out.println(dt);
		
		sw.stop();
		System.out.print("Profiled Time:\t");
		System.out.println(sw.getTime());
		
		long err=Math.abs(sw.getTime()-dt);
		System.out.println("Difference:\t" + err);
		System.out.println("Percent Error:\t" + (double)(100*err/dt) + "%");
		System.out.println("\nList Contents:");
		for(Timestamp ts : JDAtool.tsl)
			System.out.println(ts);
	}
}
