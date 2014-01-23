package BLL;

import common.ResponseConstants;

import nu.xom.Element;

public class SignalObject {
	private String _invokeID;
	private Element _result;
	private String _contentString;
	private boolean isKeepAliveSucess;
	private boolean isError;
	public synchronized String getInvokeID() {
		return _invokeID;
	}
	public synchronized void setInvokeID(String invokeID) {
		this._invokeID = invokeID;
	}
	public synchronized Element getResult() {
		return _result;
	}
	public synchronized void setResult(Element result) {
		this._result = result;
		setIsError(result);
	}
	
	private void setIsError(Element result) {
		if(result!=null){
			Element errorElement = result.getFirstChildElement(ResponseConstants.ERRORRESULTNODENAME);
			if(errorElement!=null){
				this.isError=true;
			}
		}
	}
	
	public synchronized boolean getIsError(){
		return this.isError;
	}
	public synchronized boolean isKeepAliveSucess() {
		return isKeepAliveSucess;
	}
	public synchronized void setKeepAliveSucess(boolean isKeepAliveSucess) {
		this.isKeepAliveSucess = isKeepAliveSucess;
	}
	public String get_contentString() {
		return _contentString;
	}
	public void set_contentString(String _contentString) {
		this._contentString = _contentString;
	}
	

	
	
}
