package controller.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import model.Link;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.FloodlightProvider;
import controller.util.HTTPUtil;

public class JsonToLinks {

	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	private static JSONObject jsonObject;
	private static JSONArray jsonArray;
	private static Future<Object> future;
	
	private static Logger log = LoggerFactory.getLogger(JsonToLinks.class);
	public static List<Link> getLinks()throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return null;
		}
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/topology/links/json";
		
		future = HTTPUtil.getJsonFromURL(url);
		List<Link> links = new ArrayList<Link>();
		try {
			String jsonlink = (String)future.get(5, TimeUnit.SECONDS);
			jsonArray = new JSONArray(jsonlink);
			for(int i = 0;i < jsonArray.length();i++){
				jsonObject = jsonArray.getJSONObject(i);
				Link link = new Link();
				link.setSrcSwitch(jsonObject.getString("src-switch"));
				link.setSrcPort(Integer.parseInt(jsonObject.getString("src-port")));
				link.setDstSwtich(jsonObject.getString("dst-switch"));
				link.setDstPort(Integer.parseInt(jsonObject.getString("dst-port")));
				link.setType(jsonObject.getString("type"));
				link.setDirection(jsonObject.getString("direction"));
				links.add(link);
			}
		} catch (InterruptedException e) {
			// TODO: handle exception
			log.error("Failed to get Links information from Controller: {}", e.getMessage());
			return null;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get Links information from Controller: {}", e.getMessage());
			return null;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get Links information from Controller: {}", e.getMessage());
			return null;
			//e.printStackTrace();
		} catch (JSONException e) {
			log.error("Failed to get Links information from Controller: {}", e.getMessage());
			return null;
			//e.printStackTrace();
		}
		return links;
	}
}
