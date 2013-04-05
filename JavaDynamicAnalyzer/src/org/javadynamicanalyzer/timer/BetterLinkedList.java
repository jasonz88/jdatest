package org.javadynamicanalyzer.timer;

import java.util.Collection;
import java.util.Iterator;



public class BetterLinkedList<T> implements Collection<T>{
	class Node {
		T e=null;
		Node next=null;
		Node prev=null;
		
		Node(){}
		Node(T e, Node next, Node prev){
			this.e=e;
			this.next=next;
			this.prev=prev;
		}
	}
	
	int size=0;
	Node head; //will be an invalid node at the head of the list
	Node last; //will be an invalid node at the end of the list to make sure end iterators don't fall off
	
	public BetterLinkedList(){
		head=new Node();
		last=new Node();
		
		head.next=last;
		last.prev=head;
	}
	
	public boolean add(T e){
		last.e=e; //set this node's element to the new item
		last.next=new Node(); //make a new node
		last.next.prev=last; //link the new node to the old node
		last=last.next; //go to the new node
		++size;
		return true;
	}
	public void addLast(T e){ add(e); }
	public void addFirst(T e){
		Node n=new Node();
		n.e=e;
		head.prev=n;
		n.next=head;
		head=n;
		++size;
	}
	public T getFirst(){
		if(head.next==null) return null;
		return head.next.e;
	}
	public T getLast(){
		if(last==head) return null;
		return last.e;
	}
	
	class iterator implements Iterator<T>{
		Node current;
		
		iterator(Node n){ current=n; }
		
		//standard
		public boolean hasNext(){ return current.next!=last; }
		public T next(){ 
			current=current.next;
			return current.e;
		}
		public void remove(){
			if(current==head) return;
			current.e=null;
			current.prev.next=current.next;
			if(current.next!=null){
				current.next.prev=current.prev;
				current=current.next;
			}
			else
				current=current.prev;
			--size;
		}
		//better stuff
		public boolean hasPrev(){ return isValid() && current.prev!=null; }
		public T prev(){
			current=current.prev;
			return current.e;
		}
		public T deref(){ return current.e; }
		public void insert(T e){
			Node newNode=new Node();
			newNode.e=e;
			newNode.prev=current;
			newNode.next=current.next;
			
			current.next=newNode;
			newNode.next.prev=newNode;
			++size;
		}
		public void insertNext(T e){ insert(e); }
		public void insertPrev(T e){
			if(current==head){
				addFirst(e);
				current=head;
				return;
			}
			iterator itr=new iterator(current.prev);
			itr.insertNext(e);
		}
		
		public iterator clone(){ return new iterator(current); }
		public boolean isValid(){ return current.e!=null; }
		public boolean equals(iterator itr){ return this.current==itr.current; }
	}
	public iterator begin() { return new iterator(head); }
	public iterator end() { return new iterator(last); }
	
	@Override
	public Iterator<T> iterator(){ return new iterator(head); }
	
	//Container Stuff
	@Override
	public boolean addAll(Collection<? extends T> collection) {
		for(T e : collection)
			addLast(e);
		return true;
	}
	@Override
	public boolean contains(Object o) {
		for(Object obj : this)
			if(obj.equals(o))
				return true;
		return false;
	}
	@Override
	public boolean containsAll(Collection<?> collection) {
		for(Object that : collection)
			if(contains(that)==false)
				return false;
		return true;
	}
	@Override
	public boolean isEmpty() { return size==0; }
	@Override
	public boolean remove(Object obj) {
		iterator itr=new iterator(head);
		while(itr.hasNext()){
			if(itr.next().equals(obj)){
				itr.remove();
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean removeAll(Collection<?> collection) {
		boolean out=false;
		for(Object o : collection)
			out|=remove(o);
		return out;
	}
	@Override
	public boolean retainAll(Collection<?> collection) {
		boolean out=false;
		iterator itr=new iterator(head);
		while(itr.hasNext()){
			if(collection.contains((Object)itr.next())==false){
				itr.remove();
				out=true;
			}
		}
		return out;
	}
	@Override
	public int size() { return size; }
	@Override
	public Object[] toArray() {
		if(size==0) return null;
		Object[] oarr=new Object[size];
		int count=0;
		for(Object o : this)
			oarr[count++]=o;
		return oarr;
	}
	@SuppressWarnings({ "hiding", "unchecked" })
	@Override
	public <T> T[] toArray(T[] arg0) {
		return (T[])toArray();
	}
	@Override
	public void clear() {
		head=new Node();
		last=head;
		size=0;	
	}
}
