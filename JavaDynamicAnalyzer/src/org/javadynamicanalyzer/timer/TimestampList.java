package org.javadynamicanalyzer.timer;


public class TimestampList extends BetterLinkedList<Timestamp> {
	final String master;
	
	public TimestampList(String name)	{ this.master=name; }
	public TimestampList()				{ master="?"; }
	
	//Stopwatch commands
	public Stopwatch makeStopwatch(String name){
		Stopwatch sw=new Stopwatch(this,name);
		sw.start=end();
		sw.start.prev();
		return sw;
	}
	
	//Global list commands
	public void stop(){ //stop ASAP, then add to the list
		long time=System.nanoTime();
		Timestamp ts=new Timestamp(master,false,time);
		add(ts);
	}
	public void start(){ //add to the list, then start
		Timestamp ts=new Timestamp(master,true);
		add(ts);
		ts.time=System.nanoTime();
	}

}
