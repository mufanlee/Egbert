package controller.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import model.Port;
import model.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.FloodlightProvider;
import controller.util.HTTPUtil;

public class JsonToSwitches {

	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	private static Future<Object> futuredpid;
	private static JSONArray jsonArray;
	private static JSONObject jsonObject;
	
	private static Logger log = LoggerFactory.getLogger(JsonToSwitches.class);
	public static List<Switch> getSwitches()throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return null;
		}
		List<Switch> switchs = new ArrayList<Switch>();
		List<String> dpids = new ArrayList<String>();		
		dpids = getSwitchDpids();
		
		for(String dpid : dpids){
			Map<String, Future<Object>> futureStats = new HashMap<String, Future<Object>>();
			Switch sw = new Switch(dpid);
			List<Port> ports = new ArrayList<Port>();
			JSONObject descriptionObj = null, aggregateObj = null, portObj = null, portdescObj = null, featuresObj = null;
			try {
				futureStats = switchRestCalls(dpid);
				String jsonstr = (String)futureStats.get("description").get(5, TimeUnit.SECONDS);;
				descriptionObj = new JSONObject(jsonstr);
				jsonstr = (String)futureStats.get("aggregate").get(5, TimeUnit.SECONDS);
				aggregateObj = new JSONObject(jsonstr);
				jsonstr = (String)futureStats.get("port").get(5, TimeUnit.SECONDS);
				portObj = new JSONObject(jsonstr);
				jsonstr = (String)futureStats.get("portdesc").get(5, TimeUnit.SECONDS);
				portdescObj = new JSONObject(jsonstr);
				jsonstr = (String)futureStats.get("features").get(5, TimeUnit.SECONDS);
				featuresObj = new JSONObject(jsonstr);
			} catch (InterruptedException e) {
				log.error("Failed to get Switches information from Controller: {}", e.getMessage());
				return null;
				//e.printStackTrace();
			} catch (ExecutionException e) {
				log.error("Failed to get Switches information from Controller: {}", e.getMessage());
				return null;
				//e.printStackTrace();
			} catch (TimeoutException e) {
				log.error("Failed to get Switches information from Controller: {}", e.getMessage());
				return null;
				//e.printStackTrace();
			} catch (JSONException e) {
				log.error("Failed to get Switches information from Controller: {}", e.getMessage());
				return null;
				//e.printStackTrace();
			}
			
			if (descriptionObj != null) {
				try {
					descriptionObj = descriptionObj.getJSONObject("desc");
					sw.setMfr_desc(descriptionObj.getString("manufacturerDescription"));
		            sw.setHw_desc(descriptionObj.getString("hardwareDescription"));
		            sw.setSw_desc(descriptionObj.getString("softwareDescription"));
		            sw.setSerial_num(descriptionObj.getString("serialNumber"));
		            sw.setDp_desc(descriptionObj.getString("datapathDescription"));
				} catch (JSONException e) {
					log.error("Failed to get Switches Desc information from Controller: {}", e.getMessage());
					return null;
					//e.printStackTrace();
				}
			}
			
			if(aggregateObj != null){
                try {
					//aggregateObj = aggregateObj.getJSONArray(dpid).getJSONObject(0);
					aggregateObj = aggregateObj.getJSONObject("aggregate");
					sw.setPacket_count(Long.parseLong(String.valueOf(aggregateObj.getInt("packetCount"))));
	                sw.setByte_count(Long.parseLong(String.valueOf(aggregateObj.getInt("byteCount"))));
	                sw.setFlow_count(Integer.parseInt(String.valueOf(aggregateObj.getInt("flowCount"))));
				} catch (JSONException e) {
					log.error("Failed to get Switches Aggregate information from Controller: {}", e.getMessage());
					return null;
					//e.printStackTrace();
				}
            }
			
			if (featuresObj != null) {
				try {
					sw.setCapabilities(String.valueOf(featuresObj.getString("capabilities")));
					sw.setBuffers(featuresObj.getInt("buffers"));
					sw.setTables(featuresObj.getInt("tables"));
				} catch (JSONException e) {
					log.error("Failed to get Switches features information from Controller: {}", e.getMessage());
					return null;
					//e.printStackTrace();
				}
			}
			
			 try {
				jsonArray = portObj.getJSONArray("port_reply");
				jsonObject = (JSONObject)jsonArray.get(0);
				jsonArray = jsonObject.getJSONArray("port");
	            JSONArray jsonArrayDesc = portdescObj.getJSONArray("portDesc");
	             
	             //jsonArray = portObj.getJSONArray("port");
	             //JSONArray jsonArrayDesc = portdescObj.getJSONArray("portDesc");
	             for(int i = 0; i < jsonArray.length(); i++)
	             {
	                 jsonObject= (JSONObject)jsonArray.get(i);
	                 Port port = new Port();
	                 if (jsonObject.getString("portNumber").equals("local")) {
						continue;
					}
	                 port.setPortNo(jsonObject.getInt("portNumber"));
	                 port.setRXPackets(jsonObject.getLong("receivePackets"));
	                 port.setTXPackets(jsonObject.getLong("transmitPackets"));
	                 port.setRXBytes(jsonObject.getLong("receiveBytes"));
	                 port.setTXBytes(jsonObject.getLong("transmitBytes"));
	                 port.setRXDropped(jsonObject.getLong("receiveDropped"));
	                 port.setTXDropped(jsonObject.getLong("transmitDropped"));
	                 port.setRXErrors(jsonObject.getLong("receiveErrors"));
	                 port.setTXErrors(jsonObject.getLong("transmitErrors"));
	                 port.setRXFrameErr(jsonObject.getInt("receiveFrameErrors"));
	                 port.setRXOverErr(jsonObject.getInt("receiveOverrunErrors"));
	                 port.setRXCRCErr(jsonObject.getInt("receiveCRCErrors"));
	                 port.setCollisions(jsonObject.getInt("collisions"));
	                 port.setDurationSec(jsonObject.getInt("durationSec"));
	                 port.setDurationNsec(jsonObject.getInt("durationNsec"));
	                 if(!jsonArrayDesc.isNull(i))
	                 {
	                	 jsonObject = (JSONObject)jsonArrayDesc.get(i);
	                     port.setAdvertised(jsonObject.getInt("advertisedFeatures"));
	                     port.setConfig(jsonObject.getInt("config"));
	                     port.setCurr(jsonObject.getInt("currentFeatures"));
	                     port.setHWAddr(jsonObject.getString("hardwareAddress"));
	                     port.setName(jsonObject.getString("name"));
	                     port.setPeer((jsonObject.getInt("peerFeatures")));
	                     port.setState((jsonObject.getInt("state")));
	                     port.setSupported((jsonObject.getInt("supportedFeatures")));
	                     port.setCurrSpeed(jsonObject.getInt("currSpeed"));
	                     port.setMaxSpeed(jsonObject.getInt("maxSpeed"));
	                 }
	                 ports.add(port);
	             }
	             sw.setPorts(ports);
	             switchs.add(sw);
			} catch (JSONException e) {
				log.error("Failed to get Switches Port information from Controller: {}", e.getMessage());
				return null;
				//e.printStackTrace();
			}
		}
		return switchs;
	}
	
	private static Map<String, Future<Object>> switchRestCalls(final String dpid){
		Map<String, Future<Object>> futures = new HashMap<String,Future<Object>>();
		Future<Object> futureDescription = null, futureAggregate = null, futurePort,futurePortdesc, futureFeatures;
		String descurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/switch/" + dpid + "/desc/json";
		String aggregateurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/switch/" + dpid + "/aggregate/json";
		String portcurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/switch/" + dpid + "/port/json";
		String portdesccurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/switch/" + dpid + "/port-desc/json";
		String featurescurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/switch/" + dpid + "/features/json";
		
		
		futureDescription = HTTPUtil.getJsonFromURL(descurl);
		futures.put("description", futureDescription);
		futureAggregate = HTTPUtil.getJsonFromURL(aggregateurl);
		futures.put("aggregate", futureAggregate);
		futurePort = HTTPUtil.getJsonFromURL(portcurl);
		futures.put("port", futurePort);
		futurePortdesc = HTTPUtil.getJsonFromURL(portdesccurl);
		futures.put("portdesc", futurePortdesc);
		futureFeatures = HTTPUtil.getJsonFromURL(featurescurl);
		futures.put("features", futureFeatures);
		return futures;
	}
	
	public static List<String> getSwitchDpids()throws IOException{
		List<String> dpids = new ArrayList<String>();
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return null;
		}
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/core/controller/switches/json";
		futuredpid = HTTPUtil.getJsonFromURL(url);
		
		try {
			String jsonDpids = (String)futuredpid.get(5, TimeUnit.SECONDS);
			jsonArray = new JSONArray(jsonDpids);
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				String dpid = jsonObject.getString("switchDPID");
				dpids.add(dpid);
			}
		} catch (JSONException e) {
			log.error("Failed to get Switches Dpids information from Controller: {}", e.getMessage());
			return null;
			//e.printStackTrace();
		} catch (InterruptedException e) {
			log.error("Failed to get Switches Dpids information from Controller: {}", e.getMessage());
			return null;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get Switches Dpids information from Controller: {}", e.getMessage());
			return null;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get Switches Dpids information from Controller: {}", e.getMessage());
			return null;
			//e.printStackTrace();
		}
		return dpids;
	}
}
