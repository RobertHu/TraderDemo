package BLL;

import javax.net.ssl.SSLSocket;

public class LoginServiceTest {
	private SignalObject signal;

	public LoginServiceTest(SignalObject signal) {
		this.signal = signal;
	}

	public void login() {
		this.signal.setInvokeID("111");

		try {
			while (!Thread.interrupted()) {
				synchronized (this.signal) {
					this.signal.wait();
				}
				String info=String.format("login wake up get result length: %s", this.signal.getResult().getFirstChildElement("session").getValue());
				System.out.println(info);
				break;
			}

		} catch (InterruptedException e) {
			
		}

	}

}
