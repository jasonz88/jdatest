package com.omegastudios.inputbuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DualArrayList<T> {
	ArrayList<T> buffer;
	ArrayList<T> output;
	
	public DualArrayList(){
		buffer=new ArrayList<T>();
		output=new ArrayList<T>();
	}
	public void swap(){
		output.clear();
		
		ArrayList<T> tmp;
		tmp=buffer;
		buffer=output;
		output=tmp;
	}
	
	//DELEGATE FUNCTIONS: buffer
	/**
	 * @param arg0
	 * @param arg1
	 * @see java.util.ArrayList#add(int, java.lang.Object)
	 */
	public void add(int arg0, T arg1) {
		buffer.add(arg0, arg1);
	}
	/**
	 * @param arg0
	 * @return
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	public boolean add(T arg0) {
		return buffer.add(arg0);
	}
	/**
	 * @param arg0
	 * @return
	 * @see java.util.ArrayList#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends T> arg0) {
		return buffer.addAll(arg0);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see java.util.ArrayList#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int arg0, Collection<? extends T> arg1) {
		return buffer.addAll(arg0, arg1);
	}
	
	//DELEGATE FUNCTIONS: output
	/**
	 * 
	 * @see java.util.ArrayList#clear()
	 */
	public void clear() {
		output.clear();
	}
	/**
	 * @return
	 * @see java.util.ArrayList#clone()
	 */
	public Object clone() {
		return output.clone();
	}
	/**
	 * @param o
	 * @return
	 * @see java.util.ArrayList#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return output.contains(o);
	}
	/**
	 * @param arg0
	 * @return
	 * @see java.util.AbstractCollection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> arg0) {
		return output.containsAll(arg0);
	}
	/**
	 * @param minCapacity
	 * @see java.util.ArrayList#ensureCapacity(int)
	 */
	public void ensureCapacity(int minCapacity) {
		output.ensureCapacity(minCapacity);
	}
	/**
	 * @param arg0
	 * @return
	 * @see java.util.AbstractList#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		return output.equals(arg0);
	}
	/**
	 * @param index
	 * @return
	 * @see java.util.ArrayList#get(int)
	 */
	public T get(int index) {
		return output.get(index);
	}
	/**
	 * @return
	 * @see java.util.AbstractList#hashCode()
	 */
	public int hashCode() {
		return output.hashCode();
	}
	/**
	 * @param o
	 * @return
	 * @see java.util.ArrayList#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return output.indexOf(o);
	}
	/**
	 * @return
	 * @see java.util.ArrayList#isEmpty()
	 */
	public boolean isEmpty() {
		return output.isEmpty();
	}
	/**
	 * @return
	 * @see java.util.ArrayList#iterator()
	 */
	public Iterator<T> iterator() {
		return output.iterator();
	}
	/**
	 * @param o
	 * @return
	 * @see java.util.ArrayList#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		return output.lastIndexOf(o);
	}
	/**
	 * @return
	 * @see java.util.ArrayList#listIterator()
	 */
	public ListIterator<T> listIterator() {
		return output.listIterator();
	}
	/**
	 * @param index
	 * @return
	 * @see java.util.ArrayList#listIterator(int)
	 */
	public ListIterator<T> listIterator(int index) {
		return output.listIterator(index);
	}
	/**
	 * @param index
	 * @return
	 * @see java.util.ArrayList#remove(int)
	 */
	public T remove(int index) {
		return output.remove(index);
	}
	/**
	 * @param o
	 * @return
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return output.remove(o);
	}
	/**
	 * @param c
	 * @return
	 * @see java.util.ArrayList#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		return output.removeAll(c);
	}
	/**
	 * @param c
	 * @return
	 * @see java.util.ArrayList#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		return output.retainAll(c);
	}
	/**
	 * @param index
	 * @param element
	 * @return
	 * @see java.util.ArrayList#set(int, java.lang.Object)
	 */
	public T set(int index, T element) {
		return output.set(index, element);
	}
	/**
	 * @return
	 * @see java.util.ArrayList#size()
	 */
	public int size() {
		return output.size();
	}
	/**
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 * @see java.util.ArrayList#subList(int, int)
	 */
	public List<T> subList(int fromIndex, int toIndex) {
		return output.subList(fromIndex, toIndex);
	}
	/**
	 * @return
	 * @see java.util.ArrayList#toArray()
	 */
	public Object[] toArray() {
		return output.toArray();
	}
	/**
	 * @param a
	 * @return
	 * @see java.util.ArrayList#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) {
		return output.toArray(a);
	}
	/**
	 * @return
	 * @see java.util.AbstractCollection#toString()
	 */
	public String toString() {
		return output.toString();
	}
	/**
	 * 
	 * @see java.util.ArrayList#trimToSize()
	 */
	public void trimToSize() {
		output.trimToSize();
	}
	
}
