package com.sunmap.teleview.util;

public class ByteElement<T> {

	private T element;
	private boolean endian;
	
	public T getElement() {
		return element;
	}
	
	public void setElement(T element) {
		this.element = element;
	}

	public boolean isEndian() {
		return endian;
	}

	public void setEndian(boolean endian) {
		this.endian = endian;
	}

	
}