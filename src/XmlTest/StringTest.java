package XmlTest;

import Util.StringHelper;

public class StringTest {
	public static void main(String[] args){
		String[] source={"aaaa","bbbb","cccc","dddd"};
		String separator=",";
		String result=StringHelper.join(source, separator);
		System.out.println(result);
	}
}
