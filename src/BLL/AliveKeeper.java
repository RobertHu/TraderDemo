package BLL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import common.RequestConstants;
import common.ResponseConstants;

import Util.RequestCommandHelper;
import Util.SignalHelper;
import Util.WaitTimeoutHelper;

import nu.xom.Element;

public class AliveKeeper implements Runnable {
	private List<Observer> observers = new ArrayList<>();
	private int sleepTime = 10000;
	private int exceptionCount = 0;
	private final String expectedResult = "1";
	private LoginInfoManager loginInfoManager;
	private OutputStream outputStream;
	private Logger logger = Logger.getLogger(AliveKeeper.class);

	public AliveKeeper(OutputStream outputStream,
			LoginInfoManager loginInfoManager) {
		this.loginInfoManager = loginInfoManager;
		this.outputStream = outputStream;
	}

	public void start() {
		try {
			Thread thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				TimeUnit.MILLISECONDS.sleep(this.sleepTime);
				long beginTime = System.currentTimeMillis();
				SignalObject signalObject = request();
				KeepAliveTimer.DEFALUT_ALIVE_TIMER.add(System
						.currentTimeMillis() - beginTime);
				if (signalObject.getIsError()) {
					reset();
					continue;
				}
				Element result = signalObject.getResult();
				if (result == null) {
					this.fireConnectionBroken();
					break;
				}
				String valueString = result.getFirstChildElement(
						ResponseConstants.SINGLERESULTCONTENTNODENAME_STRING)
						.getValue();
				if (!valueString.equals(expectedResult)) {
					this.fireConnectionBroken();
					break;
				}
				reset();
			}

			catch (Exception e) {
				e.printStackTrace();
				this.exceptionCount++;
				if (this.exceptionCount >= 3) {
					this.fireConnectionBroken();
					break;
				}
				sleepTime = 30000 / (2 * this.exceptionCount);
			}
		}
	}

	private SignalObject request() throws IOException, WaitTimeoutException,
			InterruptedException {
		Element root = new Element(RequestConstants.CommandRootName);
		CommunicationObject request = RequestCommandHelper.NewCommand(
				this.loginInfoManager.getSession(), "KeepAlive", root);
		try {
			byte[] packet = PacketBuilder.Build(request);
			SignalObject signalObject = SignalHelper.Add(request.getInvokeID());
			this.outputStream.write(packet);
			this.outputStream.flush();
			WaitTimeoutHelper.wait(signalObject);
			return signalObject;

		} catch (WaitTimeoutException ex) {
			this.logger.error("keep alive wait time out "
					+ request.getInvokeID());
			throw ex;
		} catch (Exception e) {
			return null;
		}

	}

	private void reset() {
		sleepTime = 30000;
		this.exceptionCount = 0;
	}

	private synchronized void fireConnectionBroken() {
		System.out.println("connection broken");
		for (Observer observer : this.observers) {
			observer.connectionBroken();
		}
	}

	public synchronized void addObserver(Observer observer) {
		this.observers.add(observer);
	}

}
