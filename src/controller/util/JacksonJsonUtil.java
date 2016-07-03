package controller.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonJsonUtil {

	private static ObjectMapper mapper;
	
	public static synchronized ObjectMapper getObjectMapper(boolean createNew){
		if (createNew) {
			return new ObjectMapper();
		} else if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}
	
	public static String objectToJson(Object object)throws Exception{
		try {
			ObjectMapper objectMapper = getObjectMapper(false);
			String json = objectMapper.writeValueAsString(object);
			return json;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	public static String objectToJson(Object object,boolean createNew)throws Exception{
		try {
			ObjectMapper objectMapper = getObjectMapper(createNew);
			String json = objectMapper.writeValueAsString(object);
			return json;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	
	public static Object jsonToObject(String json,Class<?> cla)throws Exception{
		try {
			ObjectMapper objectMapper = getObjectMapper(false);
			Object object = objectMapper.readValue(json, cla);
			return object;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	public static Object jsonToObject(String json,Class<?> cla,boolean createNew)throws Exception{
		try {
			ObjectMapper objectMapper = getObjectMapper(createNew);
			Object object = objectMapper.readValue(json, cla);
			return object;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
}
