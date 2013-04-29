package BLL;

import java.io.IOException;
import java.util.zip.DataFormatException;

import common.RequestConstants;

import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import Util.IntegerHelper;
import Util.XmlElementHelper;
import Util.ZlibHelper;

public class PacketParser {
	public static CommunicationObject parse(byte[] packet)
			throws ValidityException, ParsingException, IOException, DataFormatException {

		boolean isPrice = packet[PacketContants.IsPriceIndex] == 1 ? true
				: false;
		byte sessionLength = packet[PacketContants.SessionHeaderLengthIndex];
		byte[] contentLengthBytes = new byte[PacketContants.ContentHeaderLength];
		System.arraycopy(packet, PacketContants.ContentHeaderLengthIndex,
				contentLengthBytes, 0, contentLengthBytes.length);
		int contentLength = IntegerHelper.toCustomerInt(contentLengthBytes);
		int contentIndex = PacketContants.HeadTotalLength + sessionLength;
		byte[] contentBytes = new byte[contentLength];
		System.arraycopy(packet, contentIndex, contentBytes, 0, contentLength);
		CommunicationObject result;
//		System.out.println(String.format("packet length: %d , contentLength: %d",packet.length, contentBytes.length));
		if (isPrice) {
			result = new CommunicationObject(null, contentBytes);
		} else {
//			contentBytes = ZlibHelper.Decompress(contentBytes);
			int sessionIndex = PacketContants.HeadTotalLength;
			byte[] sessionBytes = new byte[sessionLength];
			System.arraycopy(packet, sessionIndex, sessionBytes, 0,
					sessionLength);
			String session = new String(sessionBytes,
					PacketContants.SessionEncoding);
			String content = new String(contentBytes,
					PacketContants.ContentEncoding);
			Element xmlContent = XmlElementHelper.parse(content);
			Element invokeIdElement = xmlContent.getFirstChildElement(RequestConstants.InvokeIdName);
			String invokeId = invokeIdElement==null?"":invokeIdElement.getValue();
			result = new CommunicationObject(session, invokeId, xmlContent);
		}
		return result;
	}
}
