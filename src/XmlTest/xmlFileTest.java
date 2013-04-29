package XmlTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import nu.xom.*;

public class xmlFileTest {
	public static void main(String[] args) throws ValidityException, ParsingException, IOException{
		File file = new File("src/config.xml");
		if(!file.exists()) return;
		FileInputStream stream = new FileInputStream(file);
		Builder builder = new Builder();
		Document document = builder.build(stream);
		Element root = document.getRootElement();
		Element server= root.getFirstChildElement("server");
		Element ip = server.getFirstChildElement("ip");
		Element port= server.getFirstChildElement("port");
		System.out.println(String.format("ip=%s, port=%s",ip.getValue(),port.getValue()));
			
		
	}
}
