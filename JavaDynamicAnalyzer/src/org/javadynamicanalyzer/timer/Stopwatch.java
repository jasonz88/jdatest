package org.javadynamicanalyzer.timer;


public class Stopwatch {
	final TimestampList tsl;
	BetterLinkedList<Timestamp>.iterator start=null;
	BetterLinkedList<Timestamp>.iterator stop=null;
	
	long cachedTime=0;
	boolean cacheValid=false;
	
	public Stopwatch(TimestampList tsl){ this.tsl=tsl; }
	
	public void start(){ start=tsl.end(); cacheValid=false; }
	public void stop(){ stop=tsl.end(); cacheValid=false; }
	public void delete(){ remove(); }
	public void remove(){
		if(start==null || stop==null) return;
		if(start.equals(stop)) return;
		
		long getTime=getTime();
		//start.next(); //we do not want to remove the first element
		while(start.equals(stop)==false)
			start.remove();
		
		start.insertPrev(new Timestamp(getTime));
	}
	
	public long getTime(){ 
		if(cacheValid==true) return cachedTime;
		cachedTime=0;
		
		if(stop==null || stop==null) return cachedTime;
		
		BetterLinkedList<Timestamp>.iterator itr=start.clone();
		Timestamp ts=itr.deref(); //we want *this* element, not the next one!
		while(itr.equals(stop)==false){
			cachedTime+=ts.time;
			ts=itr.next();
		}
		
		cacheValid=true;
		return cachedTime;
	}
}
