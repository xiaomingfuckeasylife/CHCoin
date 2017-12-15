package nxt;

import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

/**
 * 
 * @author clark
 * 
 * 2017年12月14日 上午10:55:32
 * 
 */
public interface Peer {
	 public static enum State {
	        NON_CONNECTED, CONNECTED, DISCONNECTED
	    }
	 
	 String getPeerAddress();

	    String getAnnouncedAddress();

	    State getState();

	    String getVersion();

	    String getApplication();

	    String getPlatform();

	    String getSoftware();

	    boolean shareAddress();

	    boolean isWellKnown();

	    boolean isRebroadcastTarget();

	    boolean isBlacklisted();

	    void blacklist(Exception cause);

	    void blacklist();

	    void unBlacklist();

	    void deactivate();

	    void remove();

	    long getDownloadedVolume();

	    long getUploadedVolume();

	    int getLastUpdated();

	    JSONObject send(JSONStreamAware request);
}
