package com.sunmap.teleview.util;

import java.net.Socket;

public class CloserHelper {
	public static void close(java.io.Closeable... ios) {
		if (ios.length == 0){
			return;
		}
		for (java.io.Closeable in : ios) {
			try {
				if (in != null){
					in.close();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	public static void close(Socket... sockets) {
		if (sockets.length == 0)
			return;
		for (Socket in : sockets) {
			try {
				if (in != null)
					in.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
