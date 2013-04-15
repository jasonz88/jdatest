package org.javadynamicanalyzer.timer;

import org.javadynamicanalyzer.JDAtool;



public class TimestampList extends BetterLinkedList<Timestamp> {
	long start;

	//Stopwatch commands
	public Stopwatch makeStopwatch(){
		Stopwatch sw=new Stopwatch(this);
		return sw;
	}
	
	//Global list commands
	public void stop(){ //stop ASAP, then add to the list
		long stop=System.nanoTime();
		Timestamp ts=new Timestamp(stop-start);
		add(ts);
	}
	public void start(){ start=System.nanoTime(); }
	
	public String toString(){
		String out=new String();
		for(Timestamp ts : this)
			out+=ts.toString()+" ";
		return out;
	}
}
