package CompressData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressData {

	public static byte[] compress(byte[] data) throws IOException {
		long t1 = System.currentTimeMillis();
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
				data.length);
		deflater.finish();
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer); // returns the generated
													// code... index
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		byte[] output = outputStream.toByteArray();
		System.out.println("Original: " + data.length / 1024 + " Kb");
		System.out.println("Compressed: " + output.length / 1024 + " Kb");
		long t2 = System.currentTimeMillis();
		System.out.println("Time compress:  " + (t2 - t1));
		return output;
	}

	public static byte[] decompress(byte[] data) throws IOException,
			DataFormatException {
		long t1 = System.currentTimeMillis();
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
				data.length);
		byte[] buffer = new byte[1024];
		while (!inflater.finished()) {
			int count = inflater.inflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		byte[] output = outputStream.toByteArray();
		System.out.println("Original: " + data.length);
		System.out.println("Compressed: " + output.length);
		long t2 = System.currentTimeMillis();
		System.out.println("Time compress:  " + (t2 - t1));
		return output;
	}
}
