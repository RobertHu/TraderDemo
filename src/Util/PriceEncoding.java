package Util;

import BLL.PacketContants;

public class PriceEncoding {
	public static int getContentLengthForPrice(byte[] source, int index) {
		int result = 0;
		for (int i = index; i < index + PacketContants.ContentHeaderLength; i++) {
			if (source[i] > 9) {
				break;
			}
			result = result * 10 + source[i];
		}
		return result;
	}

}
