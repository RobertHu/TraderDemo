package BLL;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.log4j.Logger;

import common.RequestConstants;

import framework.data.DataSet;
import framework.xml.XmlNode;

import nu.xom.Element;
import Util.RequestCommandHelper;
import Util.SignalHelper;
import Util.WaitTimeoutHelper;
import Util.XmlElementHelper;
import XmlTest.log4jDemo;

public class LoginService {
	private Logger logger = Logger.getLogger(LoginService.class);
	private OutputStream stream;
	private Element result;
	private LoginInfoManager loginInfoManager;

	public LoginService(OutputStream stream, LoginInfoManager loginInfoManager) {
		this.stream = stream;
		this.loginInfoManager = loginInfoManager;
	}

	public void login(String loginId, String password, String version,int appType) throws WaitTimeoutException {
		CommunicationObject loginCommand = BuildLoginCommand(loginId,
				password, version,appType);
		String invokeId = loginCommand.getInvokeID();
		try {
			SignalObject signal = SignalHelper.Add(invokeId);
			byte[] packet = PacketBuilder.Build(loginCommand);
			this.stream.write(packet);
			this.stream.flush();
			WaitTimeoutHelper.wait(signal);
			if(signal.getIsError()){
				return;
			}
			this.loginInfoManager.setLoginId(loginId);
			this.loginInfoManager.setPassword(password);
			this.process(signal,invokeId);
		}
		catch(WaitTimeoutException ex){
			this.logger.error("login wait time out invoke id " + invokeId);
			throw ex;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Element getResult() {
		return this.result;
	}

	private void process(SignalObject signal,String invokeId) {
//		System.out.println(signal.getResult().toXML());
		this.result = signal.getResult();
		// logger.debug(String.format("login method result: %s",
		// this.result.toXML()));
		if(this.result==null){
			System.out.println("login error");
			this.logger.error("login error invokeid "+invokeId);
			return;
		}
		String session = this.result
				.getFirstChildElement("session").getValue();
		
		//String session = this.result.getFirstChildElement("Settings").getAttributeValue("SessionId");

		this.loginInfoManager.setSession(session);
		Element targetElement = this.result
				.getFirstChildElement("recoverPasswordData");
		if (targetElement != null) {
			DataSet dataSet = XmlElementHelper.convertToDataset(targetElement);
		}
		Element accountEle = this.result
				.getFirstChildElement("tradingAccountData");
		if (accountEle != null) {
			// this.logger.debug("account data is not empty");
			DataSet accountData = XmlElementHelper.convertToDataset(accountEle);
		} else {
			// this.logger.debug("account data is empty");
		}
		// System.out.println("login over");
		SignalContainer.Default.remove(signal.getInvokeID());

	}

	private CommunicationObject BuildLoginCommand(String loginID,
			String password, String version,Integer appType) {
		Element root = new Element(RequestConstants.CommandRootName);
		Element args = new Element(RequestConstants.CommandArgumentName);
		root.appendChild(args);
		args.appendChild(XmlElementHelper.create("LoginID", loginID));
		args.appendChild(XmlElementHelper.create("Password", password));
		args.appendChild(XmlElementHelper.create("Version", version));
		args.appendChild(XmlElementHelper.create("appType", appType.toString()));
		CommunicationObject request = RequestCommandHelper.NewCommand("",
				"Login", root);
		return request;
	}
	
	
	

}
