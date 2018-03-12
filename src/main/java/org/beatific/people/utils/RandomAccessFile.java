package org.beatific.people.utils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomAccessFile implements Closeable {

	private FileChannel channel = null;

	private int CAPACITY = 1024;
	private final ByteBuffer buffer = ByteBuffer.allocateDirect(CAPACITY);

	public RandomAccessFile(String name) throws IOException {
		channel = FileChannel.open(Paths.get(name), StandardOpenOption.READ, StandardOpenOption.WRITE);
	}

	public final String read() throws IOException {
		return read(Charset.defaultCharset().name());
	}

	public final String read(String encoding) throws IOException {

		StringBuffer sb = new StringBuffer();
		boolean done = false;

		do {
			buffer.clear();

			channel.read(buffer);
			done = buffer.hasRemaining();
			String str = ByteBufferSupports.btos(buffer, encoding);

			sb.append(str);

		} while (!done);

		return sb.toString();
	}

	public void write(String str) throws IOException {
		write(str, Charset.defaultCharset().name());
	}

	public void write(String str, String encoding) throws IOException {

		ByteBuffer buffer = ByteBuffer.wrap(str.getBytes(encoding));

		while (buffer.hasRemaining()) {
			channel.write(buffer);
		}
	}

	public void seek(long pos) throws IOException {
		channel.position(pos);
	}

	public long length() throws IOException {
		return channel.size();
	}

	public void close() throws IOException {

		if (channel != null) {
			channel.close();
		}
	}

//	public static void main(String[] args) {
//		try {
//			RandomAccessFile file = new RandomAccessFile("c:/DEBUG.TXT");
//			String reads = file.read(Charset.forName("EUC-KR").name());
//			log.debug(reads);
//			file.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
