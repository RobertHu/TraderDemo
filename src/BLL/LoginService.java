package BLL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.log4j.Logger;

import common.RequestConstants;

import framework.Guid;
import framework.data.DataRow;
import framework.data.DataRowCollection;
import framework.data.DataSet;
import framework.data.DataTable;
import framework.data.DataTableCollection;
import framework.xml.XmlNode;

import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import Util.RequestCommandHelper;
import Util.SignalHelper;
import Util.WaitTimeoutHelper;
import Util.XmlElementHelper;
import XmlTest.log4jDemo;

public class LoginService {
	private Logger logger = Logger.getLogger(LoginService.class);
	private OutputStream stream;
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

	private void process(SignalObject signal,String invokeId) throws ValidityException, ParsingException, IOException {
		String data = signal.get_contentString();
		DataSet dataSet = XmlElementHelper.convertToDataSet(data);
		DataTableCollection tables = dataSet.get_Tables();
		DataTable table = tables.get_Item("Instrument");
		DataRowCollection dataRowCollection = table.get_Rows();
		for(int row_index = 0;row_index<dataRowCollection.get_Count();row_index++){
			DataRow row = dataRowCollection.get_Item(row_index);
			Guid id = (Guid)row.get_Item("ID");
			String code = row.get_Item("Code").toString();
			int mappingId= (Integer)row.get_Item("SequenceForQuotatoin");
			String rowdataString = String.format("id: %s, code: %s, mappingId: %d", id.toString(),code,mappingId);
			//System.out.println(rowdataString);
		}
		DataTable commadSequenceTable=tables.get_Item("CommandSequence");
		DataRowCollection commandSequenceCollection=commadSequenceTable.get_Rows();
		DataRow dRow=commandSequenceCollection.get_Item(0);
		int commandSequence = (Integer)dRow.get_Item("CommandSequenceCol");
		
		
		DataTable loginTable=tables.get_Item("LoginTable");
		DataRowCollection loginDataRowCollection = loginTable.get_Rows();
		DataRow loginRow = loginDataRowCollection.get_Item(0);
		String loginDataString = (String)loginRow.get_Item("LoginColumn");
		Element result =  XmlElementHelper.parse(loginDataString);
		
		if(result==null){
			System.out.println("login error");
			this.logger.error("login error invokeid "+invokeId);
			return;
		}
		String session = result
				.getFirstChildElement("session").getValue();

		this.loginInfoManager.setSession(session);
		Element targetElement = result
				.getFirstChildElement("recoverPasswordData");
		if (targetElement != null) {
			DataSet dataSet2 = XmlElementHelper.convertToDataset(targetElement);
		}
		Element accountEle = result
				.getFirstChildElement("tradingAccountData");
		if (accountEle != null) {
			DataSet accountData = XmlElementHelper.convertToDataset(accountEle);
		} else {
		
		};
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
