package controller.json;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.FloodlightProvider;
import controller.util.HTTPUtil;

public class QoSManager {

	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	private static Future<Object> futureEnable, futureQoS;
	private static JSONObject jsonObject;
	
	private static Logger log = LoggerFactory.getLogger(QoSManager.class);
	public static boolean getStatusInfo()throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		String qosurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/module/status/json";
		
		futureQoS = HTTPUtil.getJsonFromURL(qosurl);
		try {
			String jsonQoS = (String)futureQoS.get(5, TimeUnit.SECONDS);
			
			jsonObject = new JSONObject(jsonQoS);
			if (jsonObject.getString("status").equalsIgnoreCase("QoS Enabled")) {
				floodlightProvider.getController().setQoSStatus("Yes");
				log.info("QoS Status: Yes");
				return true;
			} else{
				floodlightProvider.getController().setQoSStatus("No");
				log.info("QoS Status: No");
				return false;
			}
		} catch (InterruptedException e) {
			log.error("Failed to get status of QoS: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get status of QoS: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get status of QoS: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (JSONException e) {
			log.error("Failed to get status of QoS: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
	}
	public static boolean enaleQoS()throws IOException{
		
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		String qosurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/module/enable/json";
		
		futureEnable = HTTPUtil.getJsonFromURL(qosurl); 
		try {
			String jsonQoS = (String)futureEnable.get(5, TimeUnit.SECONDS);
			jsonObject = new JSONObject(jsonQoS);
			if (jsonObject.getString("status").equalsIgnoreCase("success")) {
				floodlightProvider.getController().setQoSStatus("Yes");
				//System.out.println("QoS enabled");
				log.info("QoS enabled");
			} else{
				floodlightProvider.getController().setQoSStatus("No");
				log.info("QoS disabled");
				return false;
			}
		} catch (InterruptedException e) {
			log.error("Failed to set the QoS module: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to set the QoS module: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to set the QoS module: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (JSONException e) {
			log.error("Failed to set the QoS module: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		return true;
	}
}
