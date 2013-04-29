package BLL;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import Util.XmlElementHelper;

public class AsyncManagerTest implements Runnable {
	private SignalObject signal;

	public AsyncManagerTest(SignalObject signal) {
		this.signal = signal;
	}

	private ArrayList<String> resultList = new ArrayList<String>();

	public synchronized void add(String result) {
		System.out.println(String.format("add a result: %s", result));
		this.resultList.add(result);
		this.notify();
	}

	public synchronized String dequeue() {
		return dequeueHelper();
	}

	private String dequeueHelper() {
		int size = this.resultList.size();
		String result = this.resultList.get(size - 1);
		this.resultList.remove(size - 1);
		return result;
	}

	public static void main(String[] args) throws InterruptedException {
		SignalObject signal = new SignalObject();
		LoginServiceTest loginService = new LoginServiceTest(signal);
		AsyncManagerTest demo = new AsyncManagerTest(signal);
		Thread thread = new Thread(demo);
		thread.setDaemon(false);
		
		thread.start();
		TimeUnit.MILLISECONDS.sleep(1000);
		System.out.println("external add 111 begin");
		demo.add("111");
		System.out.println("external add 111 end");
		loginService.login();
		System.out.println("End");

	}

	public void run() {
		try {
			while (!Thread.interrupted()) {
				while (true) {
					String result = null;
					System.out.println("I'm here");
					synchronized (this) {
						if (this.resultList.size() == 0) {
							System.out.println("going to wait");
							this.wait();
							System.out.println("begin to wake up");
						}
						result = dequeueHelper();
					}
					System.out.println("middle " + result);
					if (result == null) {
						System.out.println("the queue is empty");
						break;
					}
					TimeUnit.MICROSECONDS.sleep(3000);
					if (result == this.signal.getInvokeID()) {
						System.out.println("receive a invoke id  " + result);
						String xml="<Result><session>111111</session></Result>";
						this.signal.setResult(XmlElementHelper.parse(xml));
						System.out.println("begin to notify");
						synchronized (this.signal) {
							this.signal.notify();
						}
					}

				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
