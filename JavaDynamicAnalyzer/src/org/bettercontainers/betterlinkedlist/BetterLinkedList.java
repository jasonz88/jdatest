package org.bettercontainers.betterlinkedlist;

import java.util.Collection;
import java.util.Iterator;

import org.bettercontainers.BetterIterable;
import org.bettercontainers.BetterIterator;


public class BetterLinkedList<T> implements BetterIterable<T>, Collection<T>{
	int size=0;
	Node<T> head=new Node<T>();
	Node<T> last=head;
	
	public void dump(){
		Node<T> itr=head;
		while(itr.next!=null){
			System.out.println(itr.e);
			itr=itr.next;
		}
	}
	
	public boolean add(T e){
		last.next=new Node<T>();
		last.next.prev=last;
		last=last.next;
		last.e=e;
		last.next=null;
		++size;
		return true;
	}
	public void addLast(T e){ add(e); }
	public void addFirst(T e){
		Node<T> n=new Node<T>();
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
	
	class iterator implements BetterIterator<T>{
		Node<T> current;
		
		iterator(Node<T> n){ current=n; }
		
		//standard
		public boolean hasNext(){ return isValid() && current.next!=null; }
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
			Node<T> newNode=new Node<T>();
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
	}
	
	@Override
	public BetterIterator<T> betterIterator() { return new iterator(head); }
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
		head=new Node<T>();
		last=head;
		size=0;	
	}
}
