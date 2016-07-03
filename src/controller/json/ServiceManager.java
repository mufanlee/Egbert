package controller.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import model.Service;
import controller.FloodlightProvider;
import controller.util.HTTPUtil;
import controller.util.JacksonJsonUtil;
import controller.util.StringUtils;

public class ServiceManager {

	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	private static Future<Object> futurelist,futureadd,futuredelete;
	
	public static boolean getService()throws IOException{
		
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			return false;
		}
		String listurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/service/json";
		futurelist = HTTPUtil.getJsonFromURL(listurl);
		
		try {
			String jsonServices = (String) futurelist.get(5,TimeUnit.SECONDS);
			jsonServices = StringUtils.strip(jsonServices, "[]");
			jsonServices.replace("},[{]", "}|{");
			String [] servicesArray = jsonServices.split("\\|");
			ArrayList<Service> services = new ArrayList<Service>();
			for (int i = 0; i < servicesArray.length; i++) {
				if (servicesArray[0].length()!=0) {
					System.out.println(servicesArray[0].length());
					Service service = (Service)JacksonJsonUtil.jsonToObject(servicesArray[i], Service.class);
					services.add(service);
					System.out.println(service.toString());
				}
			}
			floodlightProvider.setServices(services);
		} catch (InterruptedException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public static boolean addService(Service service)throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			return false;
		}
		String addurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/service/json";
		futureadd = HTTPUtil.getJsonFromURL(addurl);
		String json = "";
		try {
			json = JacksonJsonUtil.objectToJson(service);
			if (json == "") {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		futureadd = HTTPUtil.postJsonToURL(addurl, json);
		try {
			String jsonRespond = (String) futureadd.get(5, TimeUnit.SECONDS);
			System.out.println(jsonRespond);
			if (jsonRespond!="") {
				return true;
			} else {
				return false;
			}
		} catch (InterruptedException e) {
			// TODO: handle exception
		} catch (TimeoutException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}
	
	public static boolean deleteService(Service service) throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			return false;
		}
		
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/service/json";
		
		String json = "";
		try {
			json = JacksonJsonUtil.objectToJson(service);
			if (json == "") {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
			// TODO: handle exception
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}
		
}
