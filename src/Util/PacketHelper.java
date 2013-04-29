package Util;

import java.nio.CharBuffer;
import java.nio.charset.Charset;


public class PacketHelper {
	public static byte[] encode(Charset encoding,String source){
		if(StringHelper.IsNullOrEmpty(source)){
			return new byte[0];
		}
		byte[] result = encoding.encode(CharBuffer.wrap(source.toCharArray())).array();
		return result;
	}
}
