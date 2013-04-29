package BLL;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class CommandQueue implements Runnable {
	private Socket socket;	
	private Logger logger=Logger.getLogger(CommandQueue.class);
	private final int capacity = 5000;
	private ArrayList<Element> queue = new ArrayList<>(capacity);
	private Object waitEvent = new Object();
	private LoginInfoManager loginInfoManager;
	public CommandQueue(LoginInfoManager loginInfoManager){
		this.loginInfoManager=loginInfoManager;
		
	}
	
	public void start(){
		try {
			//this.socket = new Socket("10.2.10.1",6666);
			Thread thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
			
		} catch (Exception e) {
			this.logger.error(e);
		}
	}
	
	

	public synchronized void add(Element command) {
		this.queue.add(command);
		synchronized (this.waitEvent) {
			this.waitEvent.notify();
		}
	}

	private Element dequeue() {
		if (this.queue.size() == 0)
			return null;
		int lastIndex = this.queue.size() - 1;
		Element target = this.queue.get(lastIndex);
		this.queue.remove(lastIndex);
		return target;
	}

	public void run() {
		while (true) {
			try {
				synchronized(this.waitEvent){
					this.waitEvent.wait();
				}
				while (true) {
					synchronized (this) {
						if (this.queue.size() == 0){
							break;
						}
						try{
							Element result=this.dequeue();
//							this.logger.debug(result.toXML());
							CommunicationObject target = new CommunicationObject(this.loginInfoManager.getSession(), result);
							byte[] packet = PacketBuilder.Build(target);
							//send(packet);
						}
						catch(Exception ex){
							ex.printStackTrace();
							this.socket = new Socket("10.2.10.1",6666);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}

		}
	}
	
	private void send(byte[] packet) throws IOException{
		this.socket.getOutputStream().write(packet);
	}

}
