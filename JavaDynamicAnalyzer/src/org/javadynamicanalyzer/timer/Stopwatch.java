package org.javadynamicanalyzer.timer;

import org.bettercontainers.BetterIterator;


public class Stopwatch {
	final TimestampList tsl;
	final String name;
	BetterIterator<Timestamp> start=null;
	Timestamp stop=null;
	
	//only TimestampList is allowed to call this
	Stopwatch(TimestampList tsl, String name){
		this.tsl=tsl;
		this.name=name;
	} 
	
	public void start(){ start=tsl.current.clone(); }
	public void stop(){ stop=tsl.current.deref(); }
	
	public long getTime(){ 
		long out=0;
		
		if(stop==null) return out;
		if(start.hasNext()==false) return out;
		
		BetterIterator<Timestamp> itr=start.clone();
		
		Timestamp ts=itr.next();
		long startTime=ts.time;
		boolean counting=ts.flag;
		while(ts.equals(stop)==false){
			if(counting==true)
				out=out+(ts.time-startTime);
			startTime=ts.time;
			counting=ts.flag;
			ts=itr.next();
		}
		if(counting==true)
			out=out+(ts.time-startTime);
		
		return out;
	}
}
