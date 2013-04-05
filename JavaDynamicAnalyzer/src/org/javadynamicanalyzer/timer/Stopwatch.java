package org.javadynamicanalyzer.timer;

public class Stopwatch {
	final TimestampList tsl;
	final String name;
	BetterLinkedList<Timestamp>.iterator start=null;
	BetterLinkedList<Timestamp>.iterator stop=null;
	
	public Stopwatch(TimestampList tsl, String name){
		this.tsl=tsl;
		this.name=name;
	} 
	
	public void start(){ start=tsl.end(); start.prev(); } //start iterator is off the end of the list, then recursed back to the last element
	public void stop(){ stop=tsl.end(); }
	
	public long getTime(){ 
		long out=0;
		
		if(stop==null) return out;
		if(start.hasNext()==false) return out;
		
		BetterLinkedList<Timestamp>.iterator itr=start.clone();
		
		Timestamp ts=itr.next();
		long startTime=ts.time;
		boolean counting=ts.flag;
		while(itr.equals(stop)==false){
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
