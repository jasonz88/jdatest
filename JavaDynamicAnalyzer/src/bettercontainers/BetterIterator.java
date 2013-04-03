package bettercontainers;

import java.util.Iterator;

public abstract interface BetterIterator<T> extends Iterator<T>{
	public boolean hasPrev();
	public T prev();
	public T deref();
	public void insert(T e);
	public void insertNext(T e);
	public void insertPrev(T e);
	
	public BetterIterator<T> clone();
	public boolean isValid();
}
