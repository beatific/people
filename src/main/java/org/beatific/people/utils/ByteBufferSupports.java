package org.beatific.people.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class ByteBufferSupports {

	public static String btos(ByteBuffer buffer, String encoding, int start, int end) throws UnsupportedEncodingException {

		byte[] bytes = new byte[buffer.position()];
		buffer.flip();
		buffer.get(bytes);
		byte[] newBytes = new byte[buffer.position()];
		for (int i = start; i <= end; i++) {
			newBytes[i] = bytes[i];
		}

		return new String(newBytes, encoding);
	}

	public static String btos(ByteBuffer buffer, String encoding) throws UnsupportedEncodingException {
		byte[] bytes = new byte[buffer.position()];
		buffer.flip();
		buffer.get(bytes);

		return new String(bytes, encoding);
	}
	
	public static String btos(ByteBuffer buffer) throws UnsupportedEncodingException {
		byte[] bytes = new byte[buffer.position()];
		buffer.flip();
		buffer.get(bytes);

		return new String(bytes, Charset.defaultCharset().name());
	}
}
