package org.bettercontainers.junit;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.bettercontainers.betterlinkedlist.BetterLinkedList;
import org.junit.Before;
import org.junit.Test;

public class CollectionTests {
	BetterLinkedList<Integer> bll;
	
	@Before
	public void Init(){
		bll=new BetterLinkedList<Integer>();
	}
	
	@Test
	public void addingOne(){
		bll.add(1);
		assertTrue(bll.size()==1);
		assertTrue(bll.getFirst().equals(1));
		assertTrue(bll.getLast().equals(1));
	}
	
	@Test
	public void iterating(){
		for(int i=0; i<100; i++)
			bll.add(i);
		assertTrue(bll.size()==100);
		
		Iterator<Integer> itr=bll.iterator();
		int i=0;
		while(itr.hasNext())
			assertTrue(itr.next().equals(i++));
	}
	
	@Test
	public void foreach(){
		for(int i=0; i<100; i++)
			bll.add(i);
		assertTrue(bll.size()==100);
		
		int i=0;
		for(Integer in : bll)
			assertTrue(in.equals(i++));
	}
}
