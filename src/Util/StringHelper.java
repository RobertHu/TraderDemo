package Util;

public class StringHelper {
	
	public static boolean IsNullOrEmpty(String input) {
		return input == null || input.length() == 0;
	}
	
	public static String join(String[] source,String separator){
		if(source==null || source.length==0) return "";
		StringBuilder sb=new StringBuilder();
		for (String item : source) {
			sb.append(item);
			sb.append(separator);
		}
		return sb.toString().substring(0, sb.length() - 1);
	}
	

}
