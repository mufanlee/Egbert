package controller.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import model.Queue;
import controller.FloodlightProvider;
import controller.util.HTTPUtil;
import controller.util.JacksonJsonUtil;
import controller.util.StringUtils;

public class QueueManager {

	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	private static Future<Object> futurelist,futureadd,futuredelete;
	
	public static boolean getQueue()throws IOException{
		
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			return false;
		}
		String listurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/enqueue/json";
		futurelist = HTTPUtil.getJsonFromURL(listurl);
		
		try {
			String jsonQueues = (String) futurelist.get(5,TimeUnit.SECONDS);
			jsonQueues = StringUtils.strip(jsonQueues, "[]");
			jsonQueues.replace("},[{]", "}|{");
			String [] QueuesArray = jsonQueues.split("\\|");
			ArrayList<Queue> queues = new ArrayList<Queue>();
			for (int i = 0; i < QueuesArray.length; i++) {
				if (QueuesArray[0].length()!=0) {
					Queue queue = (Queue)JacksonJsonUtil.jsonToObject(QueuesArray[i], Queue.class);
					queues.add(queue);
					System.out.println(queue.toString());
				}
			}
			floodlightProvider.setQueues(queues);
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
	
	public static boolean addQueue(Queue queue)throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			return false;
		}
		String addurl = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/enqueue/json";
		futureadd = HTTPUtil.getJsonFromURL(addurl);
		String json = "";
		try {
			json = JacksonJsonUtil.objectToJson(queue);
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
	
	public static boolean deleteQueue(Queue queue) throws IOException{
		if (floodlightProvider.getController().getIP()==null||floodlightProvider.getController().getOpenFlowPort()==-1) {
			return false;
		}
		
		String url = "http://" + floodlightProvider.getController().getIP()
				+ ":" + floodlightProvider.getController().getOpenFlowPort()
				+ "/wm/qos/enqueue/json";
		
		String json = "";
		try {
			json = JacksonJsonUtil.objectToJson(queue);
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
