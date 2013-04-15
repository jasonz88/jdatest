package org.javadynamicanalyzer.timer;

public class Timestamp {
	long time;
	
	public Timestamp(long time)	{ this.time=time; }
	public Timestamp()			{ this(0); }
	public long getTime()		{ return time; }
	
	public String toString(){ return "[ "+Long.toString(time)+" ]"; }
}
