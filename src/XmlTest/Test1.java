package XmlTest;


import java.io.*;

import Util.XmlElementHelper;

import nu.xom.*;

public class Test1 {
	public static void main(String[] args) throws IOException, ValidityException, ParsingException{
		File file = new File("d:\\test.txt");
		FileInputStream inputStream = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(reader);
		StringBuilder sBuilder=new StringBuilder(10000);
		String line = bufferedReader.readLine();
		while(line!=null && line != "")
		{
			sBuilder.append(line);
			line=bufferedReader.readLine();
		}
		Element element = XmlElementHelper.parse(sBuilder.toString());
		System.out.println(element.toXML());
	}
	
}
