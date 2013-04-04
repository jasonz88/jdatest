package org.bettercontainers;


public interface BetterIterable<T> extends Iterable<T> {
	public BetterIterator<T> betterIterator();
}
