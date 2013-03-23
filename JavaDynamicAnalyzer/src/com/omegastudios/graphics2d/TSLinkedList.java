package com.omegastudios.graphics2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("serial")
public class TSLinkedList<T> extends LinkedList<T> {
	//CONSTRUCTS
	static class Command<T>{
		enum Type {ADD_LAST,ADD_FIRST,REMOVE};
		Type type;
		T ele;
		Command(T e, Type t){
			type=t;
			ele=e;
		}
	}
	
	//VARIABLES
	ConcurrentLinkedQueue<Command<T>> cmdQ; //the multithreaded instruction queue
	ArrayList<T> rmQ; //an array of removals, will retain the same memory space to prevent reallocs
	
	//METHODS
	public TSLinkedList(Collection<? extends T> c) {
		super(c);
		cmdQ=new ConcurrentLinkedQueue<Command<T>>();
		rmQ=new ArrayList<T>();
	}
	public TSLinkedList(){
		super();
		cmdQ=new ConcurrentLinkedQueue<Command<T>>();
		rmQ=new ArrayList<T>();
	}

	public void sync(){
		//Check if there are any commands waiting
		if(cmdQ.peek()==null) 
			return;
		
		Command<T> cCmd=cmdQ.poll();
		do{
			
			if(cCmd.type==Command.Type.REMOVE)//if we need to remove stuff
				rmQ.add(cCmd.ele); //let's save a buffer of removals and do it all at once later
			else{ //we want to add stuff
				if(rmQ.size()>0){ //anything in the removal buffer?
					removeAll(rmQ); //remove everything in the buffer all at once
					rmQ.clear(); //clear the buffer
				}
				//now we can add stuff
				if(cCmd.type==Command.Type.ADD_LAST)
					addLast(cCmd.ele);
				else if(cCmd.type==Command.Type.ADD_FIRST)
					addFirst(cCmd.ele);
			}
			cCmd=cmdQ.poll();
		} while(cCmd!=null);
		
		//make sure we remove any remaining items in the removal buffer!
		if(rmQ.size()>0){
			removeAll(rmQ); //remove everything in the buffer all at once
			rmQ.clear(); //clear the buffer
		}
	}
	public void mtRemove(T e)	{ cmdQ.add(new Command<T>(e, Command.Type.REMOVE));		}
	public void mtAddFirst(T e)	{ cmdQ.add(new Command<T>(e, Command.Type.ADD_FIRST));	}
	public void mtAddLast(T e)	{ cmdQ.add(new Command<T>(e, Command.Type.ADD_LAST));	}
}