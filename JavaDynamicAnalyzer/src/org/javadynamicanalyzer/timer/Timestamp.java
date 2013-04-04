package org.javadynamicanalyzer.timer;

public class Timestamp {
	long time;
	final boolean flag;
	final String tag;
	
	static void add(TimestampList tsl, Timestamp t){
		tsl.add(t);
		t.time=System.nanoTime();
	}
	
	public Timestamp(String tag, boolean flag, long time){
		this.time=time;
		this.flag=flag;
		this.tag=tag;
	}
	public Timestamp(String tag, boolean flag){
		this.tag=tag;
		this.flag=flag;
	}
	
	public long getTime()	{ return time; }
	public boolean getFlag(){ return flag; }
	public String getTag()	{ return tag; }
	
	public String toString(){ return "["+tag+", "+Boolean.toString(flag)+", "+Long.toString(time)+"]"; }
}
