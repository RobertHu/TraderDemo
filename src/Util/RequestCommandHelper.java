package Util;

import java.util.UUID;

import common.RequestConstants;

import nu.xom.Element;
import BLL.CommunicationObject;
import BLL.PacketContants;

public final class RequestCommandHelper {
	public static CommunicationObject NewCommand(String session,String methodName,Element root)
	{
		UUID invokeId = UUID.randomUUID();
		root.appendChild(XmlElementHelper.create(
				RequestConstants.CommandMethonName, methodName));
		CommunicationObject target= new CommunicationObject(session, invokeId.toString(), root);
		return target;
	}
}
