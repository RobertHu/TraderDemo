package BLL;

import java.io.OutputStream;

import nu.xom.Element;

import org.apache.log4j.Logger;

import common.RequestConstants;

import Util.RequestCommandHelper;
import Util.SignalHelper;
import Util.WaitTimeoutHelper;
import Util.XmlElementHelper;
import framework.Guid;
import framework.data.DataRow;
import framework.data.DataRowCollection;
import framework.data.DataSet;
import framework.data.DataTable;
import framework.data.DataTableCollection;

public class GetInitDataService {
	private Logger logger=Logger.getLogger(GetInitDataService.class);
	private OutputStream stream;
	private String result;
	private LoginInfoManager loginInfoManager;

	public GetInitDataService(OutputStream stream,LoginInfoManager loginInfoManager) {
		this.stream = stream;
		this.loginInfoManager=loginInfoManager;
	}

	public void getInitData() throws WaitTimeoutException {
		CommunicationObject command = BuildCommand();
		String invokeId=command.getInvokeID();
		try {
			SignalObject signal=SignalHelper.Add(invokeId);
			byte[] packet = PacketBuilder.Build(command);
			this.stream.write(packet);
			this.stream.flush();
			WaitTimeoutHelper.wait(signal);
			if(signal.getIsError()){
				return;
			}
			this.result=signal.get_contentString();
			process(this.result);
			//this.logger.debug(String.format("get init data method result: %s", this.result.toXML()));
		}
		catch(WaitTimeoutException ex){
			this.logger.error("get init data wait time out invokeid "+ invokeId);
			throw ex;
		}
		catch (Exception e) {
			this.logger.error(e.getStackTrace());
		}
	}
	
	
	private void process(String result){
		
		DataSet dataSet = XmlElementHelper.convertToDataSet(result);
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
		//System.out.println(commandSequence);
		
		
	}
	

	private CommunicationObject BuildCommand() {
		Element root = new Element(RequestConstants.CommandRootName);
		CommunicationObject target=RequestCommandHelper.NewCommand(this.loginInfoManager.getSession(), "GetInitData", root);
		return target;
	}

}
