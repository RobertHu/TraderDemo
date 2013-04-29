package XmlTest;
import java.io.*;

import Util.StringHelper;
public class txtFileTest {
	public static void main(String[] args) throws IOException{
		File path = new File("src/account.txt");
		FileInputStream inputStream = new FileInputStream(path);
		InputStreamReader reader=new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(reader);
		String spliterString= "\\|";
		String line=bufferedReader.readLine();
		while(!StringHelper.IsNullOrEmpty(line)){
			String[] items=line.split(spliterString);
			System.out.println(String.format("firstName=%s, lastName=%s, fullName=%s",items[0],items[1],items[2]));
			line=bufferedReader.readLine();
		}
		
		bufferedReader.close();
		
		
	}
}
