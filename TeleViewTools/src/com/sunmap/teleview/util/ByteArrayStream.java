package com.sunmap.teleview.util;

import java.io.EOFException;
import java.io.IOException;

public class ByteArrayStream {

	private int offset;
	private int length;
	private byte[] buffer;
	
	public ByteArrayStream(byte[] buffer) {
		this.buffer = buffer;
		this.offset = 0;
		this.length = buffer.length;
	}
	
	public void read(byte[] buffer) throws IOException {
		if (buffer != null && this.waitData(buffer.length)) {
			System.arraycopy(this.buffer, this.offset, buffer, 0, buffer.length);
			this.offset += buffer.length;
			return;
		}
		throw new EOFException();
	}
	
	public byte readByte() throws IOException {
		if (this.waitData(1)) {
			byte result = (byte) (buffer[this.offset]  & 0xFF);
			this.offset += 1;
			return result;
		}
		throw new EOFException();
	}
	
	public short readUnsignedByte() throws IOException {
		if (this.waitData(1)) {
			short result = (short) (buffer[this.offset]  & 0xFF);
			this.offset += 1;
			return result;
		}
		throw new EOFException();
	}
	
	public short readHLShort() throws IOException {
		if (this.waitData(2)) {
			short result = (short) ((buffer[this.offset]  & 0xFF) << 8 | buffer[this.offset + 1] & 0xFF);
			this.offset += 2;
			return result;
		}
		throw new EOFException();
	}

	public short readLHShort() throws IOException {
		if (this.waitData(2)) {
			short result = (short) ((buffer[this.offset + 1]  & 0xFF) << 8 | buffer[this.offset] & 0xFF);
			this.offset += 2;
			return result;
		}
		throw new EOFException();
	}
	
	public int readHLInt() throws IOException {
		if (this.waitData(4)) {
			int result = (buffer[this.offset] << 24) & 0xFF000000 |
						 (buffer[this.offset + 1] << 16) & 0xFF0000 |
						 (buffer[this.offset + 2] << 8) & 0xFF00 |
						 (buffer[this.offset + 3]) & 0xFF;
			this.offset += 4;
			return result;
		}
		throw new EOFException();
	}
	
	public int readLHInt() throws IOException {
		if (this.waitData(4)) {
			int result = (buffer[this.offset + 3] << 24) & 0xFF000000 |
						 (buffer[this.offset + 2] << 16) & 0xFF0000 |
						 (buffer[this.offset + 1] << 8) & 0xFF00 |
						 (buffer[this.offset]) & 0xFF ;
			this.offset += 4;
			return result;
		}
		throw new EOFException();
	}
	
	public void position(int position) {
		this.offset = position;
	}
	
	public void skip(int count) {
		this.offset += count;
	}
	
	public int offset() {
		return this.offset;
	}
	
	private boolean waitData(int length) {
		return length <= this.length - this.offset;
	}
}

