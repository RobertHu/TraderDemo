package Demo1;

import java.util.concurrent.TimeUnit;

public class WaxOff implements Runnable {
	private Car car;
	public WaxOff(Car c){
		car=c;
	}
	public void run(){
		try {
			while(!Thread.interrupted()){
				car.waitForWaxing();
				System.out.println("Wax Off");
				TimeUnit.MICROSECONDS.sleep(200);
				car.buffed();
			
			}
		} catch (InterruptedException e) {
			System.out.println("Exit via Interrupted");
		}
		System.out.println("End task of WaxOff");
	}
}
