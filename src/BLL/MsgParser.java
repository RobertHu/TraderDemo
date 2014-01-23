package BLL;

import java.awt.print.Printable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import Util.IntegerHelper;
import Util.PriceEncoding;

public class MsgParser implements Runnable {
	
	private Logger logger = Logger.getLogger(MsgParser.class);
	private byte[] receiveBuf = new byte[4096];
	private byte[] headerBuf = new byte[PacketContants.HeadTotalLength];
	private InputStream stream;
	private boolean isClosed;
	private AsyncManager asyncManager;
	private volatile boolean isStoped = false;

	public MsgParser(InputStream stream, AsyncManager asyncManager) {
		this.stream = stream;
		this.asyncManager = asyncManager;
	}
	
	public void stop(){
		this.isStoped=true;
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	public void run() {
		try {
			while(true){
				if(this.isStoped){
					break;
				}
				int count=0;
				while(count!=PacketContants.HeadTotalLength){
					int readed=stream.read(headerBuf, count, PacketContants.HeadTotalLength - count);
					count = count + readed;
				}
				int packetLength = getPacketLength(headerBuf, 0);
				int leftLength = packetLength - PacketContants.HeadTotalLength;
				byte[] buff = this.receiveBuf;
				if(packetLength> buff.length){
					buff=new byte[packetLength];
				}
				readContentHelper(buff, 0, leftLength, packetLength);
			}
			
		} catch (Exception e) {
			logger.error("read error",e);
		}
	}
	
	private void readContentHelper(byte[] buff,int count,int leftLength,int packetLength) throws IOException{
		while(count!=leftLength){
			count+=stream.read(buff,count,leftLength - count);
		}
		byte[] packet = new byte[packetLength];
		System.arraycopy(headerBuf, 0, packet, 0, PacketContants.HeadTotalLength);
		System.arraycopy(buff, 0, packet, PacketContants.HeadTotalLength, leftLength);
		//this.logger.debug("packet length: " + packetLength);
		this.asyncManager.add(packet);
	}

	

	private int getContentLength(byte[] source, int index) {
		byte[] bytes = new byte[PacketContants.ContentHeaderLength];
		System.arraycopy(source, index, bytes, 0,
				PacketContants.ContentHeaderLength);
		return IntegerHelper.toCustomerInt(bytes);
	}
	
	

	private int getPacketLength(byte[] source, int index) {
		boolean isPrice =false;
		int sessionLength = source[index
				+ PacketContants.SessionHeaderLengthIndex];
		int contentLength;
		int contentIndex = index + PacketContants.ContentHeaderLengthIndex;
		if(isPrice){
			contentLength = PriceEncoding.getContentLengthForPrice(source, contentIndex);
		}
		else{
			contentLength = getContentLength(source, contentIndex);
		}
		return PacketContants.HeadTotalLength + sessionLength + contentLength;

	}

}
