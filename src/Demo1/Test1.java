package Demo1;
import org.omg.CORBA.PUBLIC_MEMBER;


import framework.data.*;

import java.awt.List;
import java.util.concurrent.*;
public class Test1 {
	public static void main(String[] args){
		ExecutorService executorService=Executors.newCachedThreadPool();
		for(int i=0;i<5;i++){
			executorService.execute(new LiftOff());
		}
		
	}
}
