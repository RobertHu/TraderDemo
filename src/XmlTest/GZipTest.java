package XmlTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class GZipTest {
	public static void main(String[] args) throws IOException,
			DataFormatException {
		File path = new File("src/test.bin");
		FileInputStream inputStream = new FileInputStream(path);
		List<Byte> list = new ArrayList<>();
		int read = inputStream.read();
		while (read != -1) {
			list.add((byte) read);
			read = inputStream.read();
		}
		inputStream.close();
		System.out.println("length: " + list.size());

		byte[] output = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) {
			output[i] = list.get(i);
		}
		Inflater decompresser = new Inflater();
		decompresser.setInput(output, 0, output.length);
		byte[] result = new byte[1000];
		int resultLength = decompresser.inflate(result);
		decompresser.end();

		// Decode the bytes into a String
		String outputString = new String(result, 0, resultLength, "UTF-8");
		System.out.println(outputString);
	}
}
