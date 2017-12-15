package nxt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;

import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;

import nxt.util.Logger;

public class PeerImpl implements Peer {
	private final String peerAddress;
    private volatile String announcedAddress;
    private volatile int port;
    private volatile boolean shareAddress;
    private volatile String platform;
    private volatile String application;
    private volatile String version;
    private volatile boolean isOldVersion;
    private volatile long adjustedWeight;
    private volatile long blacklistingTime;
    private volatile State state;
    private volatile long downloadedVolume;
    private volatile long uploadedVolume;
    private volatile int lastUpdated;
    
    PeerImpl(String peerAddress, String announcedAddress) {
        this.peerAddress = peerAddress;
        this.announcedAddress = announcedAddress;
        try {
            this.port = new URL("http://" + announcedAddress).getPort();
        } catch (MalformedURLException ignore) {}
        this.state = State.NON_CONNECTED;
        this.shareAddress = true;
    }


    @Override
    public String getPeerAddress() {
        return peerAddress;
    }

    @Override
    public State getState() {
        return state;
    }

    void setState(State state) {
        if (this.state == state) {
            return;
        }
        if (this.state == State.NON_CONNECTED) {
            this.state = state;
            Peers.notifyListeners(this, Peers.Event.ADDED_ACTIVE_PEER);
        } else if (state != State.NON_CONNECTED) {
            this.state = state;
            Peers.notifyListeners(this, Peers.Event.CHANGE_ACTIVE_PEER);
        }
    }

    @Override
    public long getDownloadedVolume() {
        return downloadedVolume;
    }

    void updateDownloadedVolume(long volume) {
        synchronized (this) {
            downloadedVolume += volume;
        }
        Peers.notifyListeners(this, Peers.Event.DOWNLOAD);
    }

    @Override
    public long getUploadedVolume() {
        return uploadedVolume;
    }

    void updateUploadedVolume(long volume) {
        synchronized (this) {
            uploadedVolume += volume;
        }
        Peers.notifyListeners(this, Peers.Event.UPLOAD_VOLUME);
    }

    @Override
    public String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
            isOldVersion = false;
        if (Nxt.APPLICATION.equals(application) && version != null) {
            String[] versions = version.split("\\.");
            if (versions.length < Constants.MIN_VERSION.length) {
                isOldVersion = true;
            } else {
                for (int i = 0; i < Constants.MIN_VERSION.length; i++) {
                    try {
                        int v = Integer.parseInt(versions[i]);
                        if (v > Constants.MIN_VERSION[i]) {
                            isOldVersion = false;
                            break;
                        } else if (v < Constants.MIN_VERSION[i]) {
                            isOldVersion = true;
                            break;
                        }
                    } catch (NumberFormatException e) {
                        isOldVersion = true;
                        break;
                    }
                }
            }
            if (isOldVersion) {
              // Logger.logDebugMessage("Blacklisting %s version %s", peerAddress, version);
            }
        }
    }

    @Override
    public String getApplication() {
        return application;
    }

    void setApplication(String application) {
        this.application = application;
    }

    @Override
    public String getPlatform() {
        return platform;
    }

    void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String getSoftware() {
        return Convert.truncate(application, "?", 10, false)
                + " (" + Convert.truncate(version, "?", 10, false) + ")"
                + " @ " + Convert.truncate(platform, "?", 10, false);
    }

    @Override
    public boolean shareAddress() {
        return shareAddress;
    }

    void setShareAddress(boolean shareAddress) {
        this.shareAddress = shareAddress;
    }

    @Override
    public String getAnnouncedAddress() {
        return announcedAddress;
    }

    void setAnnouncedAddress(String announcedAddress) {
        String announcedPeerAddress = Peers.normalizeHostAndPort(announcedAddress);
        if (announcedPeerAddress != null) {
            this.announcedAddress = announcedPeerAddress;
            try {
                this.port = new URL("http://" + announcedPeerAddress).getPort();
            } catch (MalformedURLException ignore) {}
        }
    }

    int getPort() {
        return port;
    }

    @Override
    public boolean isWellKnown() {
        return announcedAddress != null && Peers.wellKnownPeers.contains(announcedAddress);
    }

    @Override
    public boolean isRebroadcastTarget() {
        return announcedAddress != null && Peers.rebroadcastPeers.contains(announcedAddress);
    }

    @Override
    public boolean isBlacklisted() {
          return blacklistingTime > 0 || isOldVersion || Peers.knownBlacklistedPeers.contains(peerAddress);
    }

    @Override
    public void blacklist(Exception cause) {
        blacklist();
    }

    @Override
    public void blacklist() {
        blacklistingTime = System.currentTimeMillis();
        setState(State.NON_CONNECTED);
        Peers.notifyListeners(this, Peers.Event.BLACKLIST);
    }

    @Override
    public void unBlacklist() {
        setState(State.NON_CONNECTED);
        blacklistingTime = 0;
        Peers.notifyListeners(this, Peers.Event.UNBLACKLIST);
    }

    void updateBlacklistedStatus(long curTime) {
        if (blacklistingTime > 0 && blacklistingTime + Peers.blacklistingPeriod <= curTime) {
            unBlacklist();
        }
    }

    @Override
    public void deactivate() {
        setState(State.NON_CONNECTED);
        Peers.notifyListeners(this, Peers.Event.DEACTIVE);
    }

    @Override
    public void remove() {
        Peers.removePeer(this);
        Peers.notifyListeners(this, Peers.Event.REMOVE);
    }

    @Override
    public int getLastUpdated() {
        return lastUpdated;
    }

    void setLastUpdated(int lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    
	@Override
	public JSONObject send(JSONStreamAware request) {
		JSONObject response;
        String log = null;
        boolean showLog = false;
        HttpURLConnection connection = null;

        try {

            String address = announcedAddress != null ? announcedAddress : peerAddress;
            StringBuilder buf = new StringBuilder("http://");
            buf.append(address);
            if (port <= 0) {
                buf.append(':');
                buf.append(Constants.isTestnet ? Peers.TESTNET_PEER_PORT : Peers.DEFAULT_PEER_PORT);
            }
            buf.append("/burst"); //	http://10.1.1.199:8123/bursts  sending address 
            URL url = new URL(buf.toString());

            if (Peers.communicationLoggingMask != 0) {
                StringWriter stringWriter = new StringWriter();
                request.writeJSONString(stringWriter);
                log = "\"" + url.toString() + "\": " + stringWriter.toString();
            }

            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(Peers.connectTimeout);
            connection.setReadTimeout(Peers.readTimeout);
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            
            CountingOutputStream cos = new CountingOutputStream(connection.getOutputStream());
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(cos, "UTF-8"))) {
                request.writeJSONString(writer);
            }
            updateUploadedVolume(cos.getCount());
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                CountingInputStream cis = new CountingInputStream(connection.getInputStream());
                InputStream responseStream = cis;
                if("gzip".equals(connection.getHeaderField("Content-Encoding"))){
                	responseStream = new GZIPInputStream(responseStream);
                }
                if ((Peers.communicationLoggingMask & Peers.LOGGING_MASK_200_RESPONSES) != 0) {
                	ByteArrayOutputStream baos = new ByteArrayOutputStream();
                	byte[] buffer = new byte[1024];
                	int numberOfBytes = 0 ;
                	try(InputStream inputStream = responseStream){
	                	while((numberOfBytes = responseStream.read(buffer,0,buffer.length)) != -1){
	                		baos.write(buffer, 0,numberOfBytes);
	                	}
                	}
                	String responseValue = baos.toString("utf-8");
                	if(responseValue.length() > 0 && responseStream instanceof GZIPInputStream){
                		log += String.format("[length :%d , compression ratio : %.2f]", cis.getCount(),(double)cis.getCount()/(double)responseValue.length());
                	}
                    log += " >>> " + responseValue;
                    showLog = true;
                    response = (JSONObject) JSONValue.parse(responseValue);
                } else {
                    try (Reader reader = new BufferedReader(new InputStreamReader(responseStream, "UTF-8"))) {
                        response = (JSONObject)JSONValue.parse(reader);
                    }
                }
                updateDownloadedVolume(cis.getCount());
            } else {

                if ((Peers.communicationLoggingMask & Peers.LOGGING_MASK_NON200_RESPONSES) != 0) {
                    log += " >>> Peer responded with HTTP " + connection.getResponseCode() + " code!";
                    showLog = true;
                }
                if (state == State.CONNECTED) {
                    setState(State.DISCONNECTED);
                } else {
                    setState(State.NON_CONNECTED);
                }
                response = null;

            }

        } catch (RuntimeException|IOException e) {
            if (! (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof SocketException)) {
                Logger.logDebugMessage("Error sending JSON request", e);
            }
            if ((Peers.communicationLoggingMask & Peers.LOGGING_MASK_EXCEPTIONS) != 0) {
                log += " >>> " + e.toString();
                showLog = true;
            }
            if (state == State.CONNECTED) {
                setState(State.DISCONNECTED);
            }
            response = null;
        }

        if (showLog) {
            Logger.logMessage(log + "\n");
        }

        if (connection != null) {
            connection.disconnect();
        }

        return response;
	}

}
