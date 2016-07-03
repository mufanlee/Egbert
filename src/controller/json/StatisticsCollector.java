package controller.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.FloodlightProvider;
import controller.util.HTTPUtil;
import model.OFPort;
import model.SwitchPortBandwidth;

public class StatisticsCollector {
	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	private static JSONObject jsonObject;
	private static JSONArray jsonArray;
	private static Future<Object> future;
	private static Logger log = LoggerFactory.getLogger(StatisticsCollector.class);
	
	public static boolean enableStatistics() throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/statistics/config/enable/json";
		future = HTTPUtil.getJsonFromURL(url);
		try {
			String json = (String)future.get(5, TimeUnit.SECONDS);
			if(json == null) return false;
			jsonObject = new JSONObject(json);
			if (jsonObject.getString("statistics-collection").equalsIgnoreCase("enabled")) {
				log.info("Enable statistics collection success");
				return true;
			}
			else {
				log.info("Enable statistics collection failed");
				return false;
			}
		} catch (InterruptedException e){
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return false;
		} catch (TimeoutException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return false;
		}catch (ExecutionException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return false;
		} catch (JSONException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return false;
		}
	}
	
	public static boolean disableStatistics() throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/statistics/config/disable/json";
		future = HTTPUtil.getJsonFromURL(url);
		try {
			String json = (String)future.get(5, TimeUnit.SECONDS);
			jsonObject = new JSONObject(json);
			if (jsonObject.getString("statistics-collection").equalsIgnoreCase("disabled")) {
				log.info("Enable statistics collection success");
				return true;
			}
			else {
				log.info("Enable statistics collection failed");
				return false;
			}
		} catch (InterruptedException e){
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return false;
		} catch (TimeoutException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return false;
		}catch (ExecutionException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return false;
		} catch (JSONException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return false;
		}
	}
	
	public static SwitchPortBandwidth getSwitchPortBandwidth(String sw, OFPort port) throws IOException{
		SwitchPortBandwidth switchPortBandwidth = null;
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return null;
		}
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/statistics/bandwidth/" + sw + "/" + port.getPortNumber() +"/json";
		future = HTTPUtil.getJsonFromURL(url);
		try {
			String json = (String)future.get(5, TimeUnit.SECONDS);
			jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject= (JSONObject)jsonArray.get(i);
				if (jsonObject.getString("port").equals("local")) {
					continue;
				}
				if (jsonObject.getInt("port") == port.getPortNumber()) {
					switchPortBandwidth = SwitchPortBandwidth.of(jsonObject.getString("dpid"), OFPort.of(jsonObject.getInt("port")), jsonObject.getLong("bits-per-second-rx"), jsonObject.getLong("bits-per-second-tx"));
					break;
				}
			}
		} catch (InterruptedException e){
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return null;
		} catch (TimeoutException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return null;
		}catch (ExecutionException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return null;
		} catch (JSONException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return null;
		}
		return switchPortBandwidth;
	}
	
	public static List<SwitchPortBandwidth> getSwitchAllPortBandwidth(String sw, String port) throws IOException{
		List<SwitchPortBandwidth> switchPortBandwidths = new ArrayList<>();
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return switchPortBandwidths;
		}
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/statistics/bandwidth/" + sw + "/" + port +"/json";
		future = HTTPUtil.getJsonFromURL(url);
		try {
			String json = (String)future.get(5, TimeUnit.SECONDS);
			jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject= (JSONObject)jsonArray.get(i);
				if (jsonObject.getString("port").equals("local")) {
					continue;
				}
				switchPortBandwidths.add(SwitchPortBandwidth.of(jsonObject.getString("dpid"), OFPort.of(jsonObject.getInt("port")), jsonObject.getLong("bits-per-second-rx"), jsonObject.getLong("bits-per-second-tx")));
			}
		} catch (InterruptedException e){
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return switchPortBandwidths;
		} catch (TimeoutException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return switchPortBandwidths;
		}catch (ExecutionException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return switchPortBandwidths;
		} catch (JSONException e) {
			log.error("Failed to enable statistics collection: {}", e.getMessage());
			return switchPortBandwidths;
		}
		return switchPortBandwidths;
	}
}
