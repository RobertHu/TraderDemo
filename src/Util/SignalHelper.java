package Util;

import BLL.SignalContainer;
import BLL.SignalObject;

public final class SignalHelper {
	public static SignalObject Add(String invokeId){
		SignalObject signal=new SignalObject();
		signal.setInvokeID(invokeId);
		SignalContainer.Default.add(invokeId, signal);
		return signal;
	}
}
