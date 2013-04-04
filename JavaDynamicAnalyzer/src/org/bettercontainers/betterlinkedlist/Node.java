package org.bettercontainers.betterlinkedlist;

public class Node<T> {
	T e=null;
	Node<T> next=null;
	Node<T> prev=null;
	
	Node(){}
	Node(T e, Node<T> next, Node<T> prev){
		this.e=e;
		this.next=next;
		this.prev=prev;
	}
}
