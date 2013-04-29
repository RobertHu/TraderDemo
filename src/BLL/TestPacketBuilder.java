package BLL;
import Util.IntegerHelper;
import Util.PacketHelper;

import nu.xom.Element;

public final class TestPacketBuilder {
	public static byte[] build(String session,Element content){
		byte[] sessionBytes = PacketHelper.encode(TestPacketContants.SESSION_ENCODING, session);
		byte[] contentBytes = PacketHelper.encode(TestPacketContants.CONTENT_ENCODING, content.toXML());
		byte sessionLengthByte = (byte)sessionBytes.length;
		byte[] contentLengthBytes = IntegerHelper.toCustomerBytes(contentBytes.length);
		int packetLength = TestPacketContants.HEADER_COUNT + sessionBytes.length + contentBytes.length;
		byte[] packet = new byte[packetLength];
		
		packet[TestPacketContants.SESSION_LENGTH_INDEX] = sessionLengthByte;
		System.arraycopy(contentLengthBytes, 0, packet, TestPacketContants.CONTENT_LENGTH_INDEX, TestPacketContants.CONTENT_HEADER_LENGTH);
		System.arraycopy(sessionBytes, 0, packet,TestPacketContants.HEADER_COUNT , sessionBytes.length);
		System.arraycopy(contentBytes, 0, packet, TestPacketContants.HEADER_COUNT + sessionBytes.length, contentBytes.length);
		return packet;
	}
	
}
