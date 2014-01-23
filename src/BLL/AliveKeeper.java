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
	private LoginInfoManager loginInfoManager;
	private OutputStream outputStream;
	private volatile boolean isStoped=false;
	
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
	
	public void stop()
	{
		this.isStoped=true;
	}

	public void run() {
		while (true) {
			if(this.isStoped)
			{
				break;
			}
			try {
				TimeUnit.MILLISECONDS.sleep(this.sleepTime);
				long beginTime = System.currentTimeMillis();
				SignalObject signalObject = request();
				KeepAliveTimer.DEFALUT_ALIVE_TIMER.add(System
						.currentTimeMillis() - beginTime);
				if (signalObject.getIsError()) {
					reset();
				}
				else{
					boolean result = signalObject.isKeepAliveSucess();
					if (!result) {
						this.fireConnectionBroken();
						break;
					}
					if(this.exceptionCount!=0){
						reset();
					}
				}
			}
			catch (Exception e) {
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
		CommunicationObject request = RequestCommandHelper.NewCommand(
				this.loginInfoManager.getSession(), null, null);
		request.setIsKeepAlive(true);
		try {
			byte[] packet = PacketBuilder.Build(request);
			SignalObject signalObject = SignalHelper.Add(request.getInvokeID());
			this.outputStream.write(packet);
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
		this.observers.clear();
	}

	public synchronized void addObserver(Observer observer) {
		this.observers.add(observer);
	}

}
