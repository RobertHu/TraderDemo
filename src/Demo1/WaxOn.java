package Demo1;
import java.util.concurrent.*;
public class WaxOn implements Runnable {
	private Car car;
	public WaxOn(Car c){
		car=c;
	}
	public void run(){
		try {
			while(!Thread.interrupted()){
				System.out.println("Wax On!");
				TimeUnit.MICROSECONDS.sleep(200);
				car.waxed();
				car.waitForBuffing();
			}
			
		} catch (InterruptedException e) {
			System.out.println("Exit via interrupted");
			
		}
		System.out.println("Ending Wax On Task");
	}


}
