package controller.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Meter;
import controller.FloodlightProvider;
import controller.util.HTTPUtil;
import controller.util.JacksonJsonUtil;
import controller.util.StringUtils;

public class MeterManager {

	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	private static Future<Object> futurelist,futureadd,futuredelete;
	
	private static Logger log = LoggerFactory.getLogger(MeterManager.class);
	public static boolean getMeter()throws IOException{
		
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		String listurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/addmeters/json";
		futurelist = HTTPUtil.getJsonFromURL(listurl);
		
		try {
			String jsonMeters = (String) futurelist.get(5,TimeUnit.SECONDS);
			jsonMeters = StringUtils.strip(jsonMeters, "[]");
			jsonMeters.replace("},[{]", "}|{");
			String [] metersArray = jsonMeters.split("\\|");
			ArrayList<Meter> meters = new ArrayList<Meter>();
			for (int i = 0; i < metersArray.length; i++) {
				if (metersArray[0].length()!=0) {
					Meter meter = (Meter)JacksonJsonUtil.jsonToObject(metersArray[i], Meter.class);
					meters.add(meter);
					log.debug(meter.toString());
				}
			}
			floodlightProvider.setMeters(meters);
		} catch (InterruptedException e) {
			log.error("Failed to get the meter: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get the meter: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get the meter: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (Exception e) {
			log.error("Failed to get the meter: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		return true;
	}
	
	public static boolean addMeter(Meter meter)throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		String addurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/addmeters/json";
		futureadd = HTTPUtil.getJsonFromURL(addurl);
		String json = "";
		try {
			json = JacksonJsonUtil.objectToJson(meter);
			if (json == "") {
				return false;
			}
		} catch (Exception e) {
			log.error("Failed to change the meter to JSON: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		futureadd = HTTPUtil.postJsonToURL(addurl, json);
		try {
			String jsonRespond = (String) futureadd.get(5, TimeUnit.SECONDS);
			System.out.println(jsonRespond);
			if (jsonRespond != "") {
				return true;
			} else {
				return false;
			}
		} catch (InterruptedException e) {
			log.error("Failed to add the meter: {}",e.getMessage());
			return false;
		} catch (TimeoutException e) {
			log.error("Failed to add the meter: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to add the meter: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (Exception e) {
			log.error("Failed to add the meter: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		//return true;
	}
	
	public static boolean deleteMeter(Meter meter) throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/addmeters/json";
		
		String json = "";
		try {
			json = JacksonJsonUtil.objectToJson(meter);
			if (json == "") {
				return false;
			}
		} catch (Exception e) {
			log.error("Failed to change the meter to JSON: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		futuredelete = HTTPUtil.deleteJsonToURL(url, json);
		try {
			String jsonRespond = (String) futuredelete.get(5, TimeUnit.SECONDS);
			System.out.println(jsonRespond);
			if (jsonRespond != "") {
				return true;
			}else {
				return false;
			}
		} catch (InterruptedException e) {
			log.error("Failed to delete the meter: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to delete the meter: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to delete the meter: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (Exception e) {
			log.error("Failed to delete the meter: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		//return true;
	}
}
