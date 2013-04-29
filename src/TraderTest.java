
import java.awt.font.NumericShaper.Range;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import BLL.AliveKeeper;
import BLL.AsyncManager;
import BLL.GetInitDataService;
import BLL.GetTimerInfoService;
import BLL.KeepAliveTimer;
import BLL.LoginInfoManager;
import BLL.LoginService;
import BLL.MsgParser;
import BLL.PriceStatisticor;
import Connection.SSLConnection;
import Util.StringHelper;
import framework.time.TimeInfo;

public class TraderTest {
	private static Logger logger = Logger.getLogger(TraderTest.class);
	private static ArrayList<String[]> accountList = new ArrayList<>();

	public static void main(String[] args) {

		try {
			Initialize();
			loadAccount();
			final Random random = new Random();
			ExecutorService executorService = Executors.newCachedThreadPool();
			int maxLoginClient = 1;
			if (args != null && args.length > 0) {
				maxLoginClient = Integer.parseInt(args[0]);
			}
			final Object waitEvent = new Object();

			final long[] costTimes = new long[4];// 0 max 1 arg 2 min 3 count
			for (long ct : costTimes) {
				ct = 0;
			}
			costTimes[2] = Long.MAX_VALUE;
			final List<Long> costTimeList = new ArrayList<>();
			KeepAliveTimer.DEFALUT_ALIVE_TIMER.start();
			PriceStatisticor.DEFAULT_PRICE_STATISTICOR.start();
			for (int i = 0; i < maxLoginClient; i++) {
				executorService.execute(new Runnable() {

					@Override
					public void run() {
						try {

							TraderDemo demo = new TraderDemo("ws0308", 8888);
							demo.start();
							int index = random.nextInt(accountList.size());
							String[] loginInfo = accountList.get(index);
							try {
								demo.beginTest(loginInfo[0], loginInfo[1], 7);
								long currentCostTime = demo.getCostTime();
								costTimeList.add(currentCostTime);
								if (costTimes[0] < currentCostTime) {
									costTimes[0] = currentCostTime;
								}
								if (costTimes[2] > currentCostTime) {
									costTimes[2] = currentCostTime;
								}

								long currentTotalTime = 0;
								for (long ct : costTimeList) {
									currentTotalTime += ct;
								}
								costTimes[1] = currentTotalTime
										/ costTimeList.size();
								costTimes[3]++;
								PriceStatisticor.DEFAULT_PRICE_STATISTICOR.addClient();
							} catch (Exception ex) {

							}
							synchronized (waitEvent) {
								waitEvent.notify();
							}
							System.out.println(String
									.format("%d clients logined,costTime: max:%d, avg:%d, min:%d",
											costTimes[3], costTimes[0],
											costTimes[1], costTimes[2]));
							System.in.read();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
				synchronized (waitEvent) {
					waitEvent.wait();
				}

			}
			// doWork();
			System.out.println("over");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void Initialize() {
		InputStream stream = Class.class
				.getResourceAsStream("/log4j.properties");
		PropertyConfigurator.configure(stream);
	}

	private static void loadAccount() throws IOException {
		File path = new File("account.txt");
		FileInputStream inputStream = new FileInputStream(path);
		InputStreamReader reader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(reader);
		String spliterString = ",";
		String line = bufferedReader.readLine();
		while (!StringHelper.IsNullOrEmpty(line)) {
			String[] items = line.split(spliterString);
			accountList.add(items);
			line = bufferedReader.readLine();
		}

		bufferedReader.close();
	}

}
