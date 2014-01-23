package BLL;

import nu.xom.Element;

public class CommunicationObject {

	public CommunicationObject(String session, byte[] price) {
		this(true, session, "", null, price);
	}

	public CommunicationObject(String session, Element content) {
		this(false, session, "", content, null);
	}
	public CommunicationObject(String invokeid,String content,boolean isInitData){
		this.invokeID=invokeid;
		this.contentString=content;
		this.isInitData=isInitData;
	}

	public CommunicationObject(String session, String invokeID, Element content) {
		this(false, session, invokeID, content, null);
	}
	
	public  CommunicationObject(String invokeID,boolean isKeepAliveSuccess) {
		this.invokeID = invokeID;
		this.isKeepAlive=true;
		this.isKeepAliveSuccess= isKeepAliveSuccess;
	}

	public CommunicationObject(boolean isPrice, String session, String invokeID,
			Element content, byte[] price) {
		this.isPrice = isPrice;
		this.session = session;
		this.invokeID = invokeID;
		this.content = content;
		this.price = price;
	}

	private boolean isPrice;
	private String session;
	private String invokeID;
	private Element content;
	private boolean isKeepAlive;
	private boolean isKeepAliveSuccess;
	private byte[] price;
	private String contentString;
	private boolean isInitData;
	

	public boolean getIsPrice() {
		return this.isPrice;
	}

	public String getSession() {
		return this.session;
	}

	public String getInvokeID() {
		return this.invokeID;
	}

	public Element getContent() {
		return this.content;
	}

	public byte[] getPrice() {
		return this.price;
	}
	
	public boolean getIsKeepAlive(){
		return this.isKeepAlive;
	}
	
	public void setIsKeepAlive(boolean value){
		this.isKeepAlive=value;
	}

	public boolean isKeepAliveSuccess() {
		return isKeepAliveSuccess;
	}

	public void setKeepAliveSuccess(boolean isKeepAliveSuccess) {
		this.isKeepAliveSuccess = isKeepAliveSuccess;
	}

	public boolean isInitData() {
		return isInitData;
	}

	public void setInitData(boolean isInitData) {
		this.isInitData = isInitData;
	}

	public String getContentString() {
		return contentString;
	}

	

}
