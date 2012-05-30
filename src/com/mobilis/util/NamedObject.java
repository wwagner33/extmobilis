package com.mobilis.util;

public class NamedObject<T> {

	private final String name;
	private final T object;

	public NamedObject(String name, T object) {
		this.name = name;
		this.object = object;
	}

	public String getName() {
		return name;
	}

	public T getObject() {
		return object;
	}
}
