package BLL;

import java.io.OutputStream;

import nu.xom.Element;

import org.apache.log4j.Logger;

import common.RequestConstants;

import sun.misc.SignalHandler;

import Util.RequestCommandHelper;
import Util.SignalHelper;
import Util.XmlNodeHelper;

import framework.time.TimeInfo;
import framework.xml.XmlNode;
import framework.xml.XmlNodeList;

public class GetTimerInfoService {
	private Logger logger=Logger.getLogger(GetTimerInfoService.class);
	private OutputStream stream;
	private LoginInfoManager loginInfoManager;
	public GetTimerInfoService(OutputStream stream,LoginInfoManager loginInfoManager){
		this.stream=stream;
		this.loginInfoManager=loginInfoManager;
	}
	public TimeInfo getTimeInfo(){
		try {
			CommunicationObject command=BuildRequestCommand();
			String invokeId=command.getInvokeID();
			SignalObject signal=SignalHelper.Add(invokeId);
			byte[] packet = PacketBuilder.Build(command);
			this.stream.write(packet);
			this.stream.flush();
			synchronized (signal) {
				signal.wait();
				//this.logger.debug("get time info return");
			}
			Element result=signal.getResult();
			
			//logger.debug(result.toXML());
			Element timeInfoElement=result.getFirstChildElement("Time");
			if(timeInfoElement==null) {
				return null;
			}
			TimeInfo timeInfo=new TimeInfo();
			timeInfo.readXml(XmlNodeHelper.Parse(timeInfoElement.getValue()));
			//logger.debug(String.format("%s %s", timeInfo.get_AdjustedTime().toString(),timeInfo.get_DateTime().toString()));
			return timeInfo;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private CommunicationObject BuildRequestCommand(){
		Element root = new Element(RequestConstants.CommandRootName);
		return RequestCommandHelper.NewCommand(this.loginInfoManager.getSession(), "GetTimeInfo", root);
	}
}
