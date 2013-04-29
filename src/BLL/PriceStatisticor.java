package BLL;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;



public class PriceStatisticor implements Runnable {
	private AtomicInteger clientCounts =new AtomicInteger();
	private AtomicLong priceCount = new AtomicLong();
	public static final PriceStatisticor DEFAULT_PRICE_STATISTICOR=new PriceStatisticor();
	private PriceStatisticor(){}
	
	
	public void addClient(){
		this.clientCounts.incrementAndGet();
	}
	
	public void decreaseClient(){
		this.clientCounts.decrementAndGet();
	}
	
	public void addPrice(){
		this.priceCount.incrementAndGet();
	}
	
	public void start(){
		try {
			Thread thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	public void run(){
		while(true){
			String msg = String.format("%d clients -- %d prices ",clientCounts.get(),priceCount.get());
			System.out.println(msg);
			this.priceCount.set(0);
			try {
				TimeUnit.MILLISECONDS.sleep(5000);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
