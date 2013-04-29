package BLL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;



public class KeepAliveTimer implements Runnable {
	private long maxTime=0;
	private long minTime = Long.MAX_VALUE;
	private long avgTime=0;
	private long count = 0;
	private List<Long> timeList= new ArrayList<>();
	private Logger logger = Logger.getLogger(KeepAliveTimer.class);
	private final int INTERVAL= 10000;
	
	public static final KeepAliveTimer DEFALUT_ALIVE_TIMER = new KeepAliveTimer();
	
	private KeepAliveTimer() {
		
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
	
	
	public synchronized void add(long time){
		this.timeList.add(time);
	}
	
	public void run(){
		while (true) {
			
			try {
				synchronized (this) {
					if(this.timeList.size()!=0){
					
					long total = 0;
					for (long time : this.timeList) {
						total += time;
						if(this.maxTime < time ){
							this.maxTime = time;
						}
						if(this.minTime > time){
							this.minTime= time;
						}
					}
					this.count = this.timeList.size();
					this.avgTime = total / this.count;
					this.timeList.clear();
				}
				}
				System.out.println(String.format("\n%d keep alives time: max=%d, avg=%d, min=%d", this.count,this.maxTime,this.avgTime,this.minTime));
				TimeUnit.MILLISECONDS.sleep(INTERVAL);
			} catch (InterruptedException e) {
			
				e.printStackTrace();
			}
		}
	}
	
	
	
	public long getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}
	public long getMinTime() {
		return minTime;
	}
	public void setMinTime(long minTime) {
		this.minTime = minTime;
	}
	public long getAvgTime() {
		return avgTime;
	}
	public void setAvgTime(long avgTime) {
		this.avgTime = avgTime;
	}
	
	
}
