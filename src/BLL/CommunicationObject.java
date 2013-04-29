package BLL;

import nu.xom.Element;

public class CommunicationObject {

	public CommunicationObject(String session, byte[] price) {
		this(true, session, "", null, price);
	}

	public CommunicationObject(String session, Element content) {
		this(false, session, "", content, null);
	}

	public CommunicationObject(String session, String invokeID, Element content) {
		this(false, session, invokeID, content, null);
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
	private byte[] price;

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

}
