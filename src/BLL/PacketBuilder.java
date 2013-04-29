package BLL;


import common.RequestConstants;

import nu.xom.Element;
import Util.IntegerHelper;
import Util.PacketHelper;
import Util.StringHelper;
import Util.XmlElementHelper;

public class PacketBuilder {
	public static byte[] Build(CommunicationObject target) {
		if(!StringHelper.IsNullOrEmpty(target.getInvokeID())){
			appendInvokeIDToContent(target.getContent(), target.getInvokeID());
		}
		byte isPriceByte = target.getIsPrice() ? (byte) 1 : (byte) 0;
		byte[] sessionBytes = getSessionBytes(target.getSession());
		byte[] contentBytes = getContentBytes(target.getContent().toXML());
		
		byte[] contentLengthBytes = IntegerHelper
				.toCustomerBytes(contentBytes.length);
		byte sessionLengthByte = (byte) sessionBytes.length;
		int packetLength = PacketContants.HeadTotalLength + sessionBytes.length
				+ contentBytes.length;
		byte[] packet = new byte[packetLength];
		addHeaderToPacket(packet, isPriceByte, sessionLengthByte,contentLengthBytes);
		addSessionToPacket(packet, sessionBytes, PacketContants.HeadTotalLength);
		addContentToPacket(packet, contentBytes,
				PacketContants.HeadTotalLength + sessionBytes.length);
		return packet;

	}
	private static void appendInvokeIDToContent(Element content, String invokeID){
		Element invokeIdNode = XmlElementHelper.create(RequestConstants.InvokeIdName, invokeID);
		content.appendChild(invokeIdNode);
	}

	private static byte[] getSessionBytes(String session) {
	   return PacketHelper.encode(PacketContants.SessionEncoding, session);
	}


	private static byte[] getContentBytes(String xml) {
		return PacketHelper.encode(PacketContants.ContentEncoding, xml);
	}

	private static void addHeaderToPacket(byte[] packet, byte isPriceByte,
			byte sessionLengthByte, byte[] contentLengthBytes) {
		packet[PacketContants.IsPriceIndex] = isPriceByte;
		packet[PacketContants.SessionHeaderLengthIndex] = sessionLengthByte;
		System.arraycopy(contentLengthBytes, 0, packet,
				PacketContants.ContentHeaderLengthIndex,
				PacketContants.ContentHeaderLength);
	}

	private static void addSessionToPacket(byte[] packet, byte[] sessionBytes,
			int index) {
		System.arraycopy(sessionBytes, 0, packet, index, sessionBytes.length);
	}

	private static void addContentToPacket(byte[] packet, byte[] contentBytes,
			int index) {
		System.arraycopy(contentBytes, 0, packet, index, contentBytes.length);
	}

}
