package Util;

import common.RequestConstants;

import BLL.PacketContants;
import BLL.WaitTimeoutException;

public class WaitTimeoutHelper {
	public static void wait(Object signal) throws WaitTimeoutException,
			InterruptedException {
		synchronized (signal) {
			long endTimeMillis = System.currentTimeMillis()
					+ RequestConstants.WaitTimeout;
			signal.wait(RequestConstants.WaitTimeout);
			if (System.currentTimeMillis() >= endTimeMillis) {
				System.out.println("wait time out");
				throw new WaitTimeoutException();
			}
		}
	}
}
