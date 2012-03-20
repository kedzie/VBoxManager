package com.kedzie.vbox.task;

public class Tuple<T,V> {

	public T first;
	public V second;
	
	public Tuple(T t, V v) {
		first = t;
		second = v;
	}
}
