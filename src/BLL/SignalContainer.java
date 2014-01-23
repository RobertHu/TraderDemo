package BLL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SignalContainer {
  private Map<String, SignalObject> map=new ConcurrentHashMap<String,SignalObject>();
  
  private SignalContainer(){}
  
  public final static SignalContainer Default=new SignalContainer();
  
  public SignalObject get(String key){
	  if(this.map.containsKey(key))
		  return this.map.get(key);
		return null;
  }
  
  public void add(String key,SignalObject value){
	  if(this.map.containsKey(key)){
		  this.map.remove(key);
	  }
	  this.map.put(key, value);
  }
  
  
  public void remove(String key){
	  if(!this.map.containsKey(key)){
		  return;
	  }
	  this.map.remove(key);
  }
  
  public void notifyAllSignal(){
	  try {
		for(String key:this.map.keySet()){
			this.map.get(key).notify();
		}
	} catch (Exception e) {
		// TODO: handle exception
	}
  }
  
}
