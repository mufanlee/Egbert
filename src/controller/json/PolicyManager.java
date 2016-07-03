package controller.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import model.Meter;
import model.Policy;
import model.Queue;
import model.Service;
import controller.FloodlightProvider;
import controller.util.HTTPUtil;
import controller.util.JacksonJsonUtil;
import controller.util.StringUtils;

public class PolicyManager {

	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	private static Future<Object> futurelist,futureadd,futuredelete;
	
	private static Logger log = LoggerFactory.getLogger(PolicyManager.class);
	public static boolean getPolicy()throws IOException{
		
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		String listurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/policy/json";
		futurelist = HTTPUtil.getJsonFromURL(listurl);
		try {
			String jsonPolicies = (String) futurelist.get(5, TimeUnit.SECONDS);
			jsonPolicies = StringUtils.strip(jsonPolicies, "[]");
			jsonPolicies = jsonPolicies.replaceAll("},[{]", "}|{");
			String [] policiesArray = jsonPolicies.split("\\|");
			ArrayList<Policy> policies = new ArrayList<Policy>();
			//System.out.println(policiesArray.length);
			for (int i=0;i<policiesArray.length;i++) {
				policiesArray[i] = policiesArray[i].replace("-1", "-2");
				if (jsonToPolicy(policiesArray[i])!=null) {
					policies.add(jsonToPolicy(policiesArray[i]));
					//System.out.println(jsonToPolicy(policiesArray[i]).toString());
					log.debug(jsonToPolicy(policiesArray[i]).toString());
				}
			}
			floodlightProvider.setPolicies(policies);
		} catch (InterruptedException e) {
			log.error("Failed to get the Policies: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to get the Policies: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to get the Policies: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (Exception e) {
			log.error("Failed to get the Policies: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		return true;
	}
	
	public static boolean addPolicy(Policy policy) throws IOException{

		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		
		String addurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/policy/json";
		
		String json = "";
		try {
			json = JacksonJsonUtil.objectToJson(policy);
			if (json == "") {
				return false;
			}
			/*int start = json.indexOf("\"type\":");
			int end = start;
			while (!json.substring(end, end+1).equals(",")) {
				end ++;
			}
			String type = json.substring(start, end+1);*/
			if (json.contains("Service")) {
				json = json.replace("\"type\":\"Service\",", "");
				//System.out.println(json);
				json = json.replace("\"sid\":-1", "\"type\":\"Service\",\"sid\":-1");
				//System.out.println(json);
			} else if (json.contains("Enqueue")) {
				json = json.replace("\"type\":\"Enqueue\",", "");
				//System.out.println(json);
				json = json.replace("\"qid\":-1", "\"type\":\"Enqueue\",\"qid\":-1");
				//System.out.println(json);
			} else if (json.contains("Addmeters")) {
				json = json.replace("\"type\":\"Addmeters\",", "");
				//System.out.println(json);
				json = json.replace("\"mid\":-1", "\"type\":\"Addmeters\",\"mid\":-1");
				//System.out.println(json);
			}
		} catch (Exception e) {
			log.error("Failed to change the policy to JSON: {}",e.getMessage());
			return false;
			//e1.printStackTrace();
		}
		
		futureadd = HTTPUtil.postJsonToURL(addurl, json);
		try {
			String jsonRespond = (String) futureadd.get(5, TimeUnit.SECONDS);
			System.out.println(jsonRespond);
			if (jsonRespond!="") {
				return true;
			}else {
				return false;
			}
		} catch (InterruptedException e) {
			log.error("Failed to add the policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to add the policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to add the policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (Exception e) {
			log.error("Failed to add the policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		//return true;
	}
	
	public static boolean deletePolicy(Policy policy) throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/policy/json";
		
		String json = "";
		
		try {
			json = JacksonJsonUtil.objectToJson(policy);
			if (json == "") {
				return false;
			}
			
			if (json.contains("Service")) {
				json = json.replace("\"type\":\"Service\",", "");
				//System.out.println(json);
				json = json.replace("\"sid\":-1", "\"type\":\"Service\",\"sid\":-1");
				//System.out.println(json);
			} else if (json.contains("Enqueue")) {
				json = json.replace("\"type\":\"Enqueue\",", "");
				//System.out.println(json);
				json = json.replace("\"qid\":-1", "\"type\":\"Enqueue\",\"qid\":-1");
				//System.out.println(json);
			} else if (json.contains("Addmeters")) {
				json = json.replace("\"type\":\"Addmeters\",", "");
				//System.out.println(json);
				json = json.replace("\"mid\":-1", "\"type\":\"Addmeters\",\"mid\":-1");
				//System.out.println(json);
			}
		} catch (Exception e) {
			log.error("Failed to change the policy to JSON: {}",e.getMessage());
			return false;
			//e1.printStackTrace();
		}
		
		futuredelete = HTTPUtil.deleteJsonToURL(url, json);
		try {
			String jsonRespond = (String) futuredelete.get(5, TimeUnit.SECONDS);
			System.out.println(jsonRespond);
			if (jsonRespond!="") {
				return true;
			}else {
				return false;
			}
		} catch (InterruptedException e) {
			log.error("Failed to delete the policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to delete the policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to delete the policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (Exception e) {
			log.error("Failed to delete the policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		//return true;
	}
	
	public static Policy jsonToPolicy(String pJson) throws IOException{
		Policy policy = new Policy();
		MappingJsonFactory jsonFactory = new MappingJsonFactory();
		JsonParser jsonParser;
		
		try {
			jsonParser = jsonFactory.createJsonParser(pJson);
		} catch (JsonParseException e) {
			throw new IOException(e);
		}
		JsonToken token = jsonParser.getCurrentToken();
		if (token != JsonToken.START_OBJECT) {
			jsonParser.nextToken();
			if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
//				System.out.println("Did not recieve json start token, current " +
//    					"token is: " + jsonParser.getCurrentToken());
				log.error("Did not recieve json start token, current token is: {}", jsonParser.getCurrentToken());
				return null;
			}
		}
		//System.out.println(pJson);
		while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
			if (jsonParser.getCurrentToken() != JsonToken.FIELD_NAME) {
				//System.out.println(jsonParser.getText().toString());
				//System.out.println(token.toString());
				throw new IOException("FIELD_NAME expected");
			}
			
			try {
				String fieldName = jsonParser.getCurrentName();
				jsonParser.nextToken();
				//System.out.println(jsonParser.getText());
				if (jsonParser.getText().equals("")) {
					continue;
				}
				switch (fieldName) {
				case "id":
					policy.setPid(Long.parseLong(jsonParser.getText()));
					log.info("[JSON PARSER]Policy ID: {}" , jsonParser.getText());	
					break;
				case "name":
					policy.setName(jsonParser.getText());
					log.info("[JSON PARSER]Policy Name: {}" , jsonParser.getText());
					//System.out.println("[JSON PARSER]Policy Name: "+ jsonParser.getText());
					break;
				case "switch":
					policy.setSw(jsonParser.getText());
					log.info("[JSON PARSER]Policy Switch: {}", jsonParser.getText());
					break;
				case "priority":
					policy.setPriority(Short.parseShort(jsonParser.getText()));
					log.info("[JSON PARSER]Policy Priority: {}", jsonParser.getText());
					break;
				case "policyType":
					//IPolicyType policyType = null;
					long id = -1;
					String name = null;
					String type = null;
					int port = -1;
					Service service = new Service();
					Queue enqueue = new Queue();
					Meter addmeters = new Meter();
					//System.out.println(jsonParser.getCurrentToken());
					if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
						//jsonParser.nextToken();
						while (jsonParser.nextToken()!=JsonToken.END_OBJECT) {
							if (jsonParser.getCurrentToken() != JsonToken.FIELD_NAME) {
								throw new IOException("FIELD_NAME expected");
							}
							//System.out.println(jsonParser.getCurrentToken());
							String field = jsonParser.getCurrentName();
							//System.out.println(field);
							jsonParser.nextToken();
							//System.out.println(jsonParser.getText());
							switch (field) {
							case "id":
								id = Long.parseLong(jsonParser.getText());
								log.info("[JSON PARSER]Policy type id: {}", jsonParser.getText());
								break;
							case "name":
								name = jsonParser.getText();
								log.info("[JSON PARSER]Policy type name: "+ jsonParser.getText());
								break;
							case "tos":
								try {
									Integer i = Integer.parseInt(jsonParser.getText());
									service.setTos(i.byteValue());
								} catch (NumberFormatException e) {
									Integer i = Integer.parseInt(jsonParser.getText());
									service.setTos(i.byteValue());
								}
								break;
							case "port":
								port = Integer.parseInt(jsonParser.getText());
								//enqueue.setPort(Short.parseShort(jsonParser.getText()));
								break;
							case "queue":
								enqueue.setQueue(Short.parseShort(jsonParser.getText()));
								break;
							case "rate":
								addmeters.setRate(Long.parseLong(jsonParser.getText()));
								break;
							case "burst":
								addmeters.setBurst(Integer.parseInt(jsonParser.getText()));
								break;
							case "maxrate":
								addmeters.setMaxrate(Long.parseLong(jsonParser.getText()));
								break;
							case "type":
								type = jsonParser.getText();
								break;
							default:
								break;
							}
						}
					}
					log.info(type);
					if ("Service".equalsIgnoreCase(type)) {
						service.setId(id);
						service.setName(name);
						service.setPort(port);
						policy.setPolicyType(service);
					} else if ("Enqueue".equalsIgnoreCase(type)) {
						enqueue.setId(id);
						enqueue.setName(name);
						enqueue.setPort((short)port);
						policy.setPolicyType(enqueue);
					} else if ("AddMeters".equalsIgnoreCase(type)) {
						addmeters.setId(id);
						addmeters.setName(name);
						addmeters.setPort(port);
						policy.setPolicyType(addmeters);
					} else {
						log.info("[JSON PARSER]Policy type not exist");
						return null;
					}
					break;
				case "ingressPort":
					policy.setInport(Short.parseShort(jsonParser.getText()));
					log.info("[JSON PARSER]Policy Ingress-Port: {}", jsonParser.getText());
					//System.out.println("[JSON PARSER]Policy Ingress-Port: " + jsonParser.getText());
					break;
				case "ethSrc":
					policy.setEthsrc(jsonParser.getText());
					log.info("[JSON PARSER]Policy Eth-src: {}", jsonParser.getText());
					break;
				case "ethDst":
					policy.setEthdst(jsonParser.getText());
					log.info("[JSON PARSER]Policy Eth-dst: {}", jsonParser.getText());
					break;
				case "ethType":
					policy.setEthtype(Short.parseShort(jsonParser.getText()));
					log.info("[JSON PARSER]Policy Eth-type: {}", jsonParser.getText());
					break;
				case "vlanID":
					policy.setVlanid(Short.parseShort(jsonParser.getText()));
					log.info("[JSON PARSER]Policy VLAN-ID: {}", jsonParser.getText());
					break;
				case "ipSrc":
					policy.setIpsrc(jsonParser.getText());
					log.info("[JSON PARSER]Policy IP-Src: {}", jsonParser.getText());
					break;
				case "ipdst":
					policy.setIpdst(jsonParser.getText());
					log.info("[JSON PARSER]Policy IP-Dst: {}", jsonParser.getText());		
					break;
				case "protocol":
					policy.setProtocol(Byte.parseByte(jsonParser.getText()));
					log.info("[JSON PARSER]Policy Protocol: {}", jsonParser.getText());	
					break;
				case "tos":
					try {
						Integer integer = Integer.parseInt(jsonParser.getText(),2);
						policy.setTos(integer.byteValue());
					} catch (NumberFormatException e) {
						Integer integer = Integer.parseInt(jsonParser.getText());
						policy.setTos(integer.byteValue());
					}
					log.info("[JSON PARSER]Policy TOS Bits: {}", jsonParser.getText());
					break;
				case "srcPort":
					policy.setSrcport(Short.parseShort(jsonParser.getText()));
					log.info("[JSON PARSER]Policy Src-Port: {}", jsonParser.getText());
					break;
				case "dstPort":
					policy.setDstport(Short.parseShort(jsonParser.getText()));
					log.info("[JSON PARSER]Policy Dst-Port: {}", jsonParser.getText());
					break;
				case "enable":
					policy.setEnable(Boolean.parseBoolean(jsonParser.getText()));
					log.info("[JSON PARSER]Policy enabled: {}", jsonParser.getText());
					break;
				default:
					log.error("[JSON PARSER]Policy field error,{}",fieldName);
					//System.out.println("[JSON PARSER]Policy field error," + fieldName);
					return null;
					//break;
				}
			} catch (JsonParseException e) {
				log.error("Error getting current FIELD_NAME {}", e);
				e.printStackTrace();
			}catch (IOException e) {
				log.error("Error procession Json {}", e);
				e.printStackTrace();
			}
		}
		return policy;
	}
	
	public static boolean addPathPolicy(Policy policy) throws IOException{

		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			log.error("Don't set the controller IP or Port");
			return false;
		}
		
		if (policy.getIpsrc().length() == 0 || policy.getIpdst().length() == 0) {
			log.error("Please set IP source address and IP destination address");
			return false;
		}
		String addurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/path/policy/"+policy.getIpsrc()+ "/" +policy.getIpdst()+"/json";
		
		String json = "";
		try {
			json = JacksonJsonUtil.objectToJson(policy);
			if (json == "") {
				return false;
			}
			log.debug(json);
			/*int start = json.indexOf("\"type\":");
			int end = start;
			while (!json.substring(end, end+1).equals(",")) {
				end ++;
			}
			String type = json.substring(start, end+1);*/
			if (json.contains("Service")) {
				json = json.replace("\"type\":\"Service\",", "");
				//System.out.println(json);
				json = json.replace("\"id\":-1", "\"type\":\"Service\",\"id\":-1");
				//System.out.println(json);
			} else if (json.contains("Enqueue")) {
				json = json.replace("\"type\":\"Enqueue\",", "");
				//System.out.println(json);
				json = json.replace("\"id\":-1", "\"type\":\"Enqueue\",\"id\":-1");
				//System.out.println(json);
			} else if (json.contains("Addmeters")) {
				json = json.replace("\"type\":\"Addmeters\",", "");
				//System.out.println(json);
				json = json.replace("\"id\":-1", "\"type\":\"Addmeters\",\"id\":-1");
				System.out.println(json);
			}
		} catch (Exception e) {
			log.error("Failed to change the policy to JSON: {}",e.getMessage());
			return false;
			//e1.printStackTrace();
		}
		
		futureadd = HTTPUtil.postJsonToURL(addurl, json);
		try {
			String jsonRespond = (String) futureadd.get(5, TimeUnit.SECONDS);
			System.out.println(jsonRespond);
			if (jsonRespond!="") {
				return true;
			}else {
				return false;
			}
		} catch (InterruptedException e) {
			log.error("Failed to add the path's policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Failed to add the path's policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Failed to add the path's policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		} catch (Exception e) {
			log.error("Failed to add the path's policy: {}",e.getMessage());
			return false;
			//e.printStackTrace();
		}
		//return true;
	}
}
