package controller.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import model.Memory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.FloodlightProvider;
import controller.util.HTTPUtil;


public class JsonToController {

	private static Future<Object> futureRole,futureTables,futureHealth,futureAllModules,futureloadedModules,futureMemory,futureUptime;
	private static JSONObject jsonObject;
	private static JSONArray jsonArray;
	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	
	private static Logger log = LoggerFactory.getLogger(JsonToController.class);
	public static boolean getControllerInfo()throws IOException{
		
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		String roleurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/role/json";
		
		String tablesurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/storage/tables/json";
		
		String healthurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/health/json";
		
		String allModulesurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/module/all/json";
		
		String loadedModulesurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/module/loaded/json";
		
		String memoryurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/memory/json";
		
		String uptimeurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/system/uptime/json";
		
		futureRole = HTTPUtil.getJsonFromURL(roleurl);
		futureTables = HTTPUtil.getJsonFromURL(tablesurl);
		futureAllModules = HTTPUtil.getJsonFromURL(allModulesurl);
		futureHealth = HTTPUtil.getJsonFromURL(healthurl);
		futureloadedModules = HTTPUtil.getJsonFromURL(loadedModulesurl);
		futureMemory = HTTPUtil.getJsonFromURL(memoryurl);
		futureUptime = HTTPUtil.getJsonFromURL(uptimeurl);
		
		try {
			String jsonRole = (String)futureRole.get(5, TimeUnit.SECONDS);
			jsonObject = new JSONObject(jsonRole);
			floodlightProvider.getController().setRole(jsonObject.getString("role"));
			//System.out.println("Role:"+jsonObject.getString("role"));
		} catch (InterruptedException e) {
			log.error("Failed to get role of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get role of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get role of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (JSONException e) {
			log.error("Failed to get role of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		
		try {
			String jsonTables = (String)futureTables.get(5,TimeUnit.SECONDS);
			jsonArray = new JSONArray(jsonTables);
			List<String> tables = new ArrayList<String>();
			for (int i = 0; i < jsonArray.length(); i++) {
				tables.add(jsonArray.getString(i));
			}
			floodlightProvider.getController().setTables(tables);
		} catch (InterruptedException e) {
			log.error("Failed to get tables of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get tables of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get tables of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (JSONException e) {
			log.error("Failed to get tables of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		
		try {
			String jsonHealth = (String)futureHealth.get(5, TimeUnit.SECONDS);
			jsonObject = new JSONObject(jsonHealth);
			if (jsonObject.getBoolean("healthy")) {
				floodlightProvider.getController().setHealth("Yes");
				//System.out.println("healthy: Yes");
			} else{
				floodlightProvider.getController().setHealth("No");
				//System.out.println("healthy: No");
			}
		} catch (InterruptedException e) {
			log.error("Failed to get healthy of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get healthy of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get healthy of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (JSONException e) {
			log.error("Failed to get healthy of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}

		try {
			String jsonMemory = (String)futureMemory.get(5, TimeUnit.SECONDS);
			jsonObject = new JSONObject(jsonMemory);
		    Memory memory = new Memory(jsonObject.getLong("free"), jsonObject.getLong("total"));
		    floodlightProvider.getController().setMemory(memory);
		    //System.out.println(memory.toString());
		} catch (InterruptedException e) {
			log.error("Failed to get memory of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get memory of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get memory of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (JSONException e) {
			log.error("Failed to get memory of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		
		try {
			String jsonLoadedModules = (String)futureloadedModules.get(5, TimeUnit.SECONDS);
			jsonObject = new JSONObject(jsonLoadedModules);
			List<String> modules = new ArrayList<String>();
			Iterator<?> i = jsonObject.keys();
			while (i.hasNext()) {
				String key = (String) i.next();
				if (jsonObject.get(key) instanceof JSONObject) {
					modules.add(key);
				}
			}
			floodlightProvider.getController().setLoadedModules(modules);
		} catch (InterruptedException e) {
			log.error("Failed to get loaded-modules of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get loaded-modules of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get loaded-modules of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (JSONException e) {
			log.error("Failed to get loaded-modules of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		
		try {
			String jsonAllModules = (String)futureAllModules.get(5, TimeUnit.SECONDS);
			jsonObject = new JSONObject(jsonAllModules);
			List<String> modules = new ArrayList<String>();
			Iterator<?> i = jsonObject.keys();
			while (i.hasNext()) {
				String key = (String) i.next();
				if (jsonObject.get(key) instanceof JSONObject) {
					modules.add(key);
				}
			}
			floodlightProvider.getController().setAllModules(modules);
		} catch (InterruptedException e) {
			log.error("Failed to get all-modules of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get all-modules of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get all-modules of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (JSONException e) {
			log.error("Failed to get all-modules of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		
		try {
			String jsonUptime = (String)futureUptime.get(5, TimeUnit.SECONDS);
			jsonObject = new JSONObject(jsonUptime);
			floodlightProvider.getController().setUptime(jsonObject.getLong("systemUptimeMsec"));
			//System.out.println("Uptime: " + jsonObject.getLong("systemUptimeMsec"));
		} catch (InterruptedException e) {
			log.error("Failed to get uptime of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get uptime of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get uptime of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (JSONException e) {
			log.error("Failed to get uptime of controller: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		return true;
	}
}
