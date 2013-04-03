package org.javadynamicanalyzer.timer;

import bettercontainers.BetterIterator;
import bettercontainers.betterlinkedlist.BetterLinkedList;

public class TimestampList extends BetterLinkedList<Timestamp> {
	BetterIterator<Timestamp> current=betterIterator();
	
	final String master;
	
	public TimestampList(String name)	{ this.master=name; }
	public TimestampList()				{ master="?"; }
	
	//Stopwatch commands
	public Stopwatch makeStopwatch(){
		Stopwatch sw=new Stopwatch();
		sw.tsl=this;
		sw.start=current.clone();
		return sw;
	}
	
	public void stop(){ //stop ASAP, then add to the list
		long time=System.nanoTime();
		Timestamp ts=new Timestamp(master,false,time);
		add(ts);
		current.next();
	}
	public void start(){ //add to the list, then start
		Timestamp ts=new Timestamp(master,false);
		add(ts);
		current.next();
		ts.time=System.nanoTime();
	}

}
