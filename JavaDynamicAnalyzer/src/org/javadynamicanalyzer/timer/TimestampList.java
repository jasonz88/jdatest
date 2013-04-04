package org.javadynamicanalyzer.timer;

import org.bettercontainers.BetterIterator;
import org.bettercontainers.betterlinkedlist.BetterLinkedList;

public class TimestampList extends BetterLinkedList<Timestamp> {
	BetterIterator<Timestamp> current=betterIterator();
	
	final String master;
	
	public TimestampList(String name)	{ this.master=name; }
	public TimestampList()				{ master="?"; }
	
	//Stopwatch commands
	public Stopwatch makeStopwatch(String name){
		Stopwatch sw=new Stopwatch(this,name);
		sw.start=current.clone();
		return sw;
	}
	
	//Global list commands
	public void stop(){ //stop ASAP, then add to the list
		long time=System.nanoTime();
		Timestamp ts=new Timestamp(master,false,time);
		add(ts);
		current.next();
	}
	public void start(){ //add to the list, then start
		Timestamp ts=new Timestamp(master,true);
		add(ts);
		current.next();
		ts.time=System.nanoTime();
	}

}
