package nxt.util;

import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

/**
 * 
 * @author clark
 * 
 * 2017年12月14日 上午9:19:27
 * 
 * rewrite jsonStream writer to send info to peer.
 */
public final class JSON {
	
	public final static JSONStreamAware empty = prepare(new JSONObject()); 
	
	public static JSONStreamAware prepare(JSONObject jsonObject) {
		
		return new JSONStreamAware() {
			private char[] cArr = jsonObject.toJSONString().toCharArray();
			@Override
			public void writeJSONString(Writer out) throws IOException {
				out.write(cArr);
			}
		};
		
	}
	
	public static JSONStreamAware prepareRequest(JSONObject obj){
		obj.put("protocal", "B1");
		return prepare(obj);
	}
	
	public static void main(String[] args) {
		
		JSONObject json = new JSONObject();
		json.put("asdf", 12);
		System.out.println(json.toJSONString());
		
	}
	
}
