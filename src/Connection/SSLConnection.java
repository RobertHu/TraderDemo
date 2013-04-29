package Connection;

import java.net.Authenticator;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import framework.net.advancedTcp.TcpClient;

public class SSLConnection {
	private SSLSocket sslSocket;
	private Socket socket;
	private String _ip;
	private int _port;
    public SSLConnection(String ip,int port){
    	this._ip=ip;
    	this._port=port;
    	init();
    }
    
    public SSLSocket getSocket(){
    	return this.sslSocket;
    }
    
	
	private void init(){
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null,  new TrustManager[] { new X509TrustManager() {
						@Override
			            public X509Certificate[] getAcceptedIssuers() {
//			                    System.out.println("getAcceptedIssuers =============");
			                    return null;
			            }	         

						@Override
						public void checkClientTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void checkServerTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							// TODO Auto-generated method stub
							
						}
			} }, null);
			System.setProperty("java.net.preferIPv4Stack", "true");
			SSLSocketFactory factory =sslContext.getSocketFactory(); 
//			Authenticator.setDefault(new Authenticator(){
//				  protected  PasswordAuthentication  getPasswordAuthentication(){
//				   PasswordAuthentication p=new PasswordAuthentication("Robert", "Huhuabo43".toCharArray());
//				   return p;
//				  }
//				 });
			//Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1",8087));
//			System.getProperties().setProperty("socksProxySet", "true");
//			System.getProperties().setProperty("socksProxyHost", "10.0.0.1");
//			System.getProperties().setProperty("socksProxyPort", "8080");
//			System.setProperty("Djava.net.preferIPv4Stack", "true");
			
//			this.socket= new Socket();
//			socket.connect(new InetSocketAddress(this._ip,this._port));
			
			sslSocket=(SSLSocket) factory.createSocket(this._ip,this._port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
