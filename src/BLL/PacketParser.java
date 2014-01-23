package BLL;

import java.io.IOException;
import java.util.zip.DataFormatException;

import common.KeepAliveConstants;
import common.RequestConstants;

import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import Util.IntegerHelper;
import Util.PriceEncoding;
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
		int contentLength=IntegerHelper.toCustomerInt(contentLengthBytes);
		int contentIndex = PacketContants.HeadTotalLength + sessionLength;
		byte[] contentBytes = new byte[contentLength];
		System.arraycopy(packet, contentIndex, contentBytes, 0, contentLength);
		CommunicationObject result;
		if (isPrice) {
			result = new CommunicationObject(null, contentBytes);
		} else {
			boolean isKeepAlive=false;
			if((packet[0]&KeepAliveConstants.KEEP_ALIVE_VALUE) == 2){
				isKeepAlive=true;
			}
			boolean isInitData =false;
			if((packet[0] & KeepAliveConstants.PLAIN_STRING)==8){
				isInitData=true;
			}
			
			int sessionIndex = PacketContants.HeadTotalLength;
			byte[] sessionBytes = new byte[sessionLength];
			System.arraycopy(packet, sessionIndex, sessionBytes, 0,
					sessionLength);
			String session = new String(sessionBytes,
					PacketContants.SessionEncoding);
			String content;
			if(isKeepAlive){
				content = new String(contentBytes,PacketContants.InvokeIDEncoding);
				boolean keepAliveResult = (packet[0] & KeepAliveConstants.KEEP_ALIVE_SUCCESS_MASKE) == 4;
				result = new CommunicationObject(content,keepAliveResult);
			}
			else if(isInitData){
				final int invokeIDLength=36;
				String invokeidString=new String(contentBytes,0,invokeIDLength,PacketContants.InvokeIDEncoding);
				byte[] decompressContentBytes = ZlibHelper.Decompress(contentBytes,invokeIDLength,contentBytes.length - invokeIDLength);
				String contentString=new String(decompressContentBytes,PacketContants.ContentEncoding);
				result=new CommunicationObject(invokeidString,contentString,isInitData);
			}
			else{
				content= new String(contentBytes,PacketContants.ContentEncoding);
				//System.out.println(content);
				Element xmlContent = XmlElementHelper.parse(content);
				Element invokeIdElement = xmlContent.getFirstChildElement(RequestConstants.InvokeIdName);
				String invokeId = invokeIdElement==null?"":invokeIdElement.getValue();
				result = new CommunicationObject(session, invokeId, xmlContent);
			}
		}
		return result;
	}
}
