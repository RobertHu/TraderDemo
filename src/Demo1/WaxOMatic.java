package Demo1;
import java.util.concurrent.*;
public class WaxOMatic {
	public static void main(String[] args) throws Exception{
		Car car= new Car();
		ExecutorService executorService=Executors.newCachedThreadPool();
		executorService.execute(new WaxOff(car));
		executorService.execute(new WaxOn(car));
		TimeUnit.SECONDS.sleep(5);
		executorService.shutdownNow();
		
	}
}
