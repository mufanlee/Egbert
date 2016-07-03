package controller.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import model.AttachmentPoint;
import model.Device;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.FloodlightProvider;
import controller.util.HTTPUtil;

public class JsonToDevices {

	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	private static JSONObject jsonObject;
	private static JSONArray jsonArray;
	private static Future<Object> future;
	
	private static Logger log = LoggerFactory.getLogger(JsonToDevices.class);
	public static List<Device> getDevices()throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return null;
		}
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/device/";
		
		future = HTTPUtil.getJsonFromURL(url);
		List<Device> devices = new ArrayList<Device>();
		try {
			String jsonDevices = (String)future.get(5,TimeUnit.SECONDS);
			jsonArray = new JSONArray(jsonDevices);
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				Device device = new Device(jsonObject.getJSONArray("mac").getString(0));
				if (!jsonObject.getJSONArray("ipv4").isNull(0)) {
					device.setIpv4_addr(jsonObject.getJSONArray("ipv4").getString(0));
				}
				if (!jsonObject.getJSONArray("vlan").isNull(0)) {
					String s = jsonObject.getJSONArray("vlan").getString(0);
					s = s.replace("0x", "");
					device.setVlan(Integer.valueOf(s,16));
				}
				if (!jsonObject.getJSONArray("attachmentPoint").isNull(0)) {
					AttachmentPoint attachmentPoint = new AttachmentPoint(jsonObject.getJSONArray("attachmentPoint")
							.getJSONObject(0).getString("switchDPID"));
					attachmentPoint.setPort(jsonObject.getJSONArray("attachmentPoint")
							.getJSONObject(0).getInt("port"));
					attachmentPoint.setErrorStatus(jsonObject.getJSONArray("attachmentPoint")
							.getJSONObject(0).getString("errorStatus"));
					device.setAttachmentPoint(attachmentPoint);
				}
				Date d = new Date(jsonObject.getLong("lastSeen"));
				device.setLastSeen(d);
				devices.add(device);
			}
		} catch (InterruptedException e) {
			log.error("Failed to get Devices information from Controller: {}",e.getMessage());
			return null;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get Devices information from Controller: {}",e.getMessage());
			return null;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get Devices information from Controller: {}",e.getMessage());
			return null;
			//e.printStackTrace();
		} catch (JSONException e) {
			log.error("Failed to get Devices information from Controller: {}",e.getMessage());
			return null;
			//e.printStackTrace();
		}
		return devices;
	}
}
