import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

import framework.time.TimeInfo;

import BLL.AliveKeeper;
import BLL.AsyncManager;
import BLL.GetInitDataService;
import BLL.GetTimerInfoService;
import BLL.LoginInfoManager;
import BLL.LoginService;
import BLL.MsgParser;
import BLL.Observer;
import BLL.PriceStatisticor;
import Connection.SSLConnection;


public class TraderDemo implements Observer {
	private SSLConnection connection;
	private AsyncManager asyncManager;
	private MsgParser parser;
	private LoginInfoManager loginInfoManager;
	private AliveKeeper aliveKeeper;
	private Logger logger = Logger.getLogger(TraderDemo.class);
	private boolean isStarted= false;
	private InputStream inputStream;
	private OutputStream outputStream;
	private long costTime=0;
	private volatile boolean isClosed=false;
	
	
	public TraderDemo(String ip,int port) {
		try {
			this.connection = new SSLConnection(ip, port);
			this.loginInfoManager = new LoginInfoManager();
			this.asyncManager = new AsyncManager(this.loginInfoManager);
			SSLSocket socket= this.connection.getSocket();
			this.inputStream =socket.getInputStream();
			this.outputStream =socket.getOutputStream();
			this.parser=new MsgParser(inputStream, asyncManager);
			this.aliveKeeper=new AliveKeeper(outputStream, loginInfoManager);
			
		} catch (Exception e) {
			this.logger.error(e.getStackTrace());
		}
		
	}
	
	public void start(){
		try {
			if(this.isStarted){
				return;
			}
			this.asyncManager.start();
			this.parser.start();
			this.isStarted=true;
		} catch (Exception e) {
			this.logger.error(e.getStackTrace());
			close();
			
		}
	}
	
	private void close() {
		try {
			if(this.isClosed){
				return;
			}
			this.connection.getSocket().close();
			this.isClosed=true;
		} catch (Exception e) {
			System.out.println("close socket");
		}
		
	}
	
	public void beginTest(String loginName,String password,int appType) throws Exception{
		try {
			long beginTime = System.currentTimeMillis();
			LoginService loginService = new LoginService(this.outputStream, loginInfoManager);
			loginService.login(loginName,password,"",appType);
		    GetInitDataService getInitDataService=new GetInitDataService(this.outputStream,loginInfoManager);
			getInitDataService.getInitData();
			this.aliveKeeper.addObserver(this);
			aliveKeeper.start();
			this.costTime = System.currentTimeMillis() - beginTime;
		} catch (Exception e) {
			close();
			throw e;
			
		}
	}
	
	public long getCostTime(){
		return this.costTime;
	}

	@Override
	public void connectionBroken() {
		PriceStatisticor.DEFAULT_PRICE_STATISTICOR.decreaseClient();
		close();
	}
	
	
	
}
