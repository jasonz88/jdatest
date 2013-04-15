package org.javadynamicanalyzer.tests;
import org.javadynamicanalyzer.JDAtool;
import org.javadynamicanalyzer.timer.Stopwatch;
import org.javadynamicanalyzer.timer.Timestamp;

public class JDAtoolTest {
	public static void execBlock(){
		for(long i=0; i<100000; i++){
			i+=i*i/Math.sqrt(i*i);
			System.out.println(Long.toString(i));
		}
	}
	public static void main(String[] arg) throws InterruptedException{
		long dt=0;
		
		//Initialization
		JDAtool.tsl.stop();
		Stopwatch main=JDAtool.tsl.makeStopwatch();
		
		//Begin!
		main.start();
		JDAtool.tsl.start();
		long time=System.nanoTime();
		
		//execBlock(); //main execute block

		//Function 1 starts
		JDAtool.tsl.stop();
		dt+=System.nanoTime()-time;
		Stopwatch f1=JDAtool.tsl.makeStopwatch();
		f1.start();
		JDAtool.tsl.start();
		time=System.nanoTime();
		
		execBlock(); //f1 execute block
		
		//Function 1 stops
		JDAtool.tsl.stop();
		dt+=System.nanoTime()-time;
		f1.stop();
		JDAtool.tsl.start();
		time=System.nanoTime();
		
		//execBlock(); //main execute block
		
		//Function 2 starts
		JDAtool.tsl.stop();
		dt+=System.nanoTime()-time;
		Stopwatch f2=JDAtool.tsl.makeStopwatch();
		f2.start();
		JDAtool.tsl.start();
		time=System.nanoTime();
		
		execBlock(); //f2 execute block
		
		//Function 2 stops
		JDAtool.tsl.stop();
		dt+=System.nanoTime()-time;
		f2.stop();
		JDAtool.tsl.start();
		time=System.nanoTime();
		
		//execBlock(); //main execute block
		
		//Conclude
		JDAtool.tsl.stop();
		main.stop();
		dt+=System.nanoTime()-time;
		
		System.out.println();
		System.out.print("Real Time:\t");
		System.out.println(dt);
		System.out.println();
		
		System.out.println("List Contents: "+JDAtool.tsl);
		long getTime=f1.getTime()+f2.getTime();
		System.out.println("Profiled Time (f1+f2):\t"+getTime);
		long err=Math.abs(getTime-dt);
		System.out.println("Difference:\t" + err);
		System.out.println("Function Time %:\t" + (double)(100-100*err/dt) + "%");
		System.out.println("Compressing Function Timestamps...");
		System.out.println();
		
		f1.remove();
		f2.remove();
		
		System.out.println("List Contents: "+JDAtool.tsl);
		getTime=main.getTime();
		System.out.println("Profiled Time (main):\t"+getTime);
		err=Math.abs(getTime-dt);
		System.out.println("Difference:\t" + err);
		System.out.println("Main Error %:\t" + (double)(100*err/dt) + "%");
		System.out.println("Compressing Main Timestamps...");
		System.out.println();
		
		main.remove();
		System.out.println("List Contents: "+JDAtool.tsl);
	}
}
