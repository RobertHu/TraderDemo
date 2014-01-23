package BLL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;

import framework.DateTime;
import framework.xml.XmlDocument;
import framework.xml.XmlElement;
import framework.xml.XmlNode;

import Util.StringHelper;

public class AsyncManager implements Runnable {

	private Logger logger = Logger.getLogger(AsyncManager.class);
	private volatile boolean isStoped=false;
	private ArrayList<byte[]> resultList = new ArrayList<byte[]>();
	private CommandQueue commandQueue;
	
	public AsyncManager(LoginInfoManager loginInfoManager){
		commandQueue= new CommandQueue(loginInfoManager);
	}
	
	public void stop(){
		this.isStoped=true;
	}
	
	public void start(){
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
		this.commandQueue.start();
	}
	

	public synchronized void add(byte[] result) {
		//this.logger.debug("add a record");
		this.resultList.add(result);
		this.notify();
	}
	

	public synchronized byte[] dequeue() {
		return dequeueHelper();
	}

	private byte[] dequeueHelper() {
		try {
			int size = this.resultList.size();
			byte[] result = this.resultList.get(size - 1);
			this.resultList.remove(size - 1);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public void run() {
		try {
			while (!Thread.interrupted()) {
				if(this.isStoped){
					break;
				}
				while (true) {
					if(this.isStoped){
						break;
					}
					byte[] result = null;
					synchronized (this) {
						if (this.resultList.size() == 0) {
							this.wait();
						}
						result = dequeueHelper();
					}
					if (result == null){
						break;
					}
					//this.logger.debug("process");
					try {
						process(result);
					}
					catch (Exception e) {
						logger.error("process response error",e);
					}
					

				}
			}
		} catch (Exception e) {
			logger.error("run method error",e);
		}
	}
	
	private void process(byte[] result) throws ValidityException, ParsingException, IOException, DataFormatException{
	
		CommunicationObject target=null;
		try {
			target= PacketParser.parse(result);
		} catch (Exception e) {
			this.logger.error("packet parse error",e);
		}
		if(target==null){
			return;
		}
		 
		String invokeId=target.getInvokeID();
		//System.out.println("|"+invokeId+"|");
		if (StringHelper.IsNullOrEmpty(invokeId)) {
//			this.logger.debug("is a command");
			if(target.getIsPrice()){
				
				String content= Quotation4BitEncoder.decode(target.getPrice());
			
				XmlNode commandsElement = buildQuotationCommand(content);
				//logger.debug(commandsElement.get_OuterXml());
				PriceStatisticor.DEFAULT_PRICE_STATISTICOR.addPrice();
//				logger.info("get price");
			
			}
			else{
				Element contentElement = target.getContent();
				//System.out.println(contentElement.toXML());
				//this.commandQueue.add(contentElement);
				
			}
		} else {
			SignalObject signal=SignalContainer.Default.get(invokeId);
			if(signal==null){
				return;
			}
			if(target.getIsKeepAlive()){
				signal.setKeepAliveSucess(target.isKeepAliveSuccess());
				
			}
			else if(target.isInitData()){
				signal.set_contentString(target.getContentString());
			}
			else {
				signal.setResult(target.getContent());
			}
			//logger.debug(target.getContent().toXML());
			synchronized (signal) {
				signal.notify();
			}
		}
	}
	
	private XmlNode buildQuotationCommand(String content){
		try{
		String  startAndEndSeparator = "/";
		String quotationSeparator = ";";
		String fieldSeparator = ":";
		char rowSeparator ='\n';
		char colSeparator = '\t';
		String commandSeparator="-";
		String rowSeparatorString = new String(new char[]{rowSeparator});
		String colSeparatorString = new String(new char[]{colSeparator});
		int startIndex = content.indexOf(startAndEndSeparator);
		int endIndex = content.lastIndexOf(startAndEndSeparator);
		String commandSequence=content.substring(0, startIndex);
		String[] commansSeqStrings=commandSequence.split(commandSeparator);
		String quotationContent = content.substring(startIndex + 1 , endIndex);
		String[] quotations =quotationContent.split(quotationSeparator); 
		XmlDocument xmlDocument = new XmlDocument();
		XmlElement commandsElement = xmlDocument.createElement("Commands");
		
		
		DateTime base = new DateTime(2011, 4, 1, 0, 0, 0);
		StringBuilder sBuilder = new StringBuilder();
		for (String quotation : quotations) {
			if(StringHelper.IsNullOrEmpty(quotation)){
				continue;
			}
			String[] quotationCols = quotation.split(fieldSeparator);
			String volumn ="";
			if(quotationCols.length==9){
				volumn= StringHelper.IsNullOrEmpty(quotationCols[7])?"":colSeparator+quotationCols[7]+colSeparator+quotationCols[8];
			}
			DateTime currentDateTime=base.addSeconds(Double.parseDouble(quotationCols[6]));
			String quotationString = quotationCols[0]+colSeparator+
					currentDateTime.toString("yyyy-MM-dd HH:mm:ss")+colSeparator+quotationCols[1]+colSeparator+quotationCols[2]+colSeparator+quotationCols[3]+colSeparator+quotationCols[4]+volumn;
			sBuilder.append(quotationString+rowSeparatorString);
		}
		if(sBuilder.length()>0){
			sBuilder.setLength(sBuilder.length() - rowSeparatorString.length());
		}
		XmlElement quotationElement2 = new XmlDocument().createElement("Quotation");
		quotationElement2.setAttribute("Overrided", sBuilder.toString());
		commandsElement.appendChild(quotationElement2);
		commandsElement.setAttribute("FirstSequence", commansSeqStrings[0]);
		commandsElement.setAttribute("LastSequence", commansSeqStrings[1]);
		return commandsElement;
		}
		catch(Exception ex){
			this.logger.error("parse qotation error",ex);
			return null;
		}
	}
	
}
