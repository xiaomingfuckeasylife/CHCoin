package nxt;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import nxt.Peer.State;
import nxt.util.JSON;
import nxt.util.Listener;
import nxt.util.Listeners;
import nxt.util.Logger;
import nxt.util.StrKit;
import nxt.util.ThreadPool;

/**
 * 
 * @author clark
 * 
 * 2017年12月14日 上午10:48:33
 * 
 * peer util class . 
 * 
 */
public class Peers {
	
	public static enum Event{
		BLACKLIST,UNBLACKLIST,DEACTIVE,REMOVE,
		DOWNLOAD,VOLUME,UPLOAD_VOLUME,WEIGHT,
		ADDED_ACTIVE_PEER,CHANGE_ACTIVE_PEER,
		NEW_PEER
	}
	
	static final int LOGGING_MASK_EXCEPTIONS = 1;
    static final int LOGGING_MASK_NON200_RESPONSES = 2;
    static final int LOGGING_MASK_200_RESPONSES = 4;
    static final int communicationLoggingMask;

    static final Set<String> wellKnownPeers;
    static final Set<String> knownBlacklistedPeers;
	
    private static final int connectWellKnownFirst;
    private static boolean connectWellKnownFinished;

    static final Set<String> rebroadcastPeers;
    private static final Map<String, Integer> priorityBlockFeeders = new ConcurrentHashMap<>();
    private static final int priorityFeederInterval = Nxt.getIntProperty("burst.priorityFeederInterval") != 0 ? Nxt.getIntProperty("burst.priorityFeederInterval") : 300;

    static final int connectTimeout;
    static final int readTimeout;
    static final int blacklistingPeriod;
    static final boolean getMorePeers;
    
    static final int DEFAULT_PEER_PORT = 8123;
    static final int TESTNET_PEER_PORT = 7123;
    private static final String myPlatform;
    private static final String myAddress;
    private static final int myPeerServerPort;
    private static final boolean shareMyAddress;
    private static final int maxNumberOfConnectedPublicPeers;
    private static final int pushThreshold;
    private static final int pullThreshold;
    private static final int sendToPeersLimit;
    private static final boolean usePeersDb;
    private static final boolean savePeers;
    private static final String dumpPeersVersion;
    static final JSONStreamAware myPeerInfoRequest;
    static final JSONStreamAware myPeerInfoResponse;
    private static final Listeners<Peer,Event> listeners = new Listeners<>();

    private static final ConcurrentMap<String, PeerImpl> peers = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> announcedAddresses = new ConcurrentHashMap<>();

    static final Collection<PeerImpl> allPeers = Collections.unmodifiableCollection(peers.values());

    private static final ExecutorService sendToPeersService = Executors.newCachedThreadPool();
    private static final ExecutorService sendingService = Executors.newFixedThreadPool(10);
    
    static{
     	 // initial parameters 
    	 myPlatform = Nxt.getStringProperty("nxt.myPlatform");
         myAddress = Nxt.getStringProperty("nxt.myAddress");
         if (myAddress != null && myAddress.endsWith(":" + TESTNET_PEER_PORT) && !Constants.isTestnet) {
             throw new RuntimeException("Port " + TESTNET_PEER_PORT + " should only be used for testnet!!!");
         }
         myPeerServerPort = Nxt.getIntProperty("nxt.peerServerPort");
         if (myPeerServerPort == TESTNET_PEER_PORT && !Constants.isTestnet) {
             throw new RuntimeException("Port " + TESTNET_PEER_PORT + " should only be used for testnet!!!");
         }
         shareMyAddress = Nxt.getBooleanProperties("nxt.shareMyAddress") && ! Constants.isOffline;
         JSONObject json = new JSONObject();
         if (myAddress != null && myAddress.length() > 0) {
             try {
                 URI uri = new URI("http://" + myAddress.trim());
                 String host = uri.getHost();
                 int port = uri.getPort();
                 if (!Constants.isTestnet) {
                     if (port >= 0)
                         json.put("announcedAddress", myAddress);
                     else
                         json.put("announcedAddress", host + (myPeerServerPort != DEFAULT_PEER_PORT ? ":" + myPeerServerPort : ""));
                 } else {
                     json.put("announcedAddress", host);
                 }
             } catch (URISyntaxException e) {
                 Logger.logMessage("Your announce address is invalid: " + myAddress);
                 throw new RuntimeException(e.toString(), e);
             }
         }
         json.put("application", Nxt.APPLICATION);
         json.put("version", Nxt.VERSION);
         json.put("platform", Peers.myPlatform);
         json.put("shareAddress", Peers.shareMyAddress);
         Logger.logDebugMessage("My peer info:\n" + json.toJSONString());
         // response info 
         myPeerInfoResponse = JSON.prepare(json);
         // request info 
         json.put("requestType", "getInfo");
         myPeerInfoRequest = JSON.prepareRequest(json);
         rebroadcastPeers = Collections.unmodifiableSet(new HashSet<>(Nxt.getStringListProperty("burst.rebroadcastPeers")));
         List<String> priorityFeederAddresses = Nxt.getStringListProperty("burst.priorityBlockFeeders");
         for(String address : priorityFeederAddresses) {
             priorityBlockFeeders.put(address, 0);
         }
         
         List<String> wellKnownPeersList = Constants.isTestnet ? Nxt.getStringListProperty("nxt.testnetPeers")
                 : Nxt.getStringListProperty("nxt.wellKnownPeers");
         for(String rePeer : rebroadcastPeers) {
             if(!wellKnownPeersList.contains(rePeer)) {
                 wellKnownPeersList.add(rePeer);
             }
         }
         for(String pPeer : priorityFeederAddresses) {
             if(!wellKnownPeersList.contains(pPeer)) {
                 wellKnownPeersList.add(pPeer);
             }
         }

         if (wellKnownPeersList.isEmpty() || Constants.isOffline) {
             wellKnownPeers = Collections.emptySet();
         } else {
             wellKnownPeers = Collections.unmodifiableSet(new HashSet<>(wellKnownPeersList));
         }

         connectWellKnownFirst = Nxt.getIntProperty("burst.connectWellKnownFirst");
         connectWellKnownFinished = (connectWellKnownFirst == 0);
         
         List<String> knownBlacklistedPeersList = Nxt.getStringListProperty("nxt.knownBlacklistedPeers");
         if (knownBlacklistedPeersList.isEmpty()) {
             knownBlacklistedPeers = Collections.emptySet();
         } else {
             knownBlacklistedPeers = Collections.unmodifiableSet(new HashSet<>(knownBlacklistedPeersList));
         }
         
         maxNumberOfConnectedPublicPeers = Nxt.getIntProperty("nxt.maxNumberOfConnectedPublicPeers");
         connectTimeout = Nxt.getIntProperty("nxt.connectTimeout");
         readTimeout = Nxt.getIntProperty("nxt.readTimeout");
         pushThreshold = Nxt.getIntProperty("nxt.pushThreshold");
         pullThreshold = Nxt.getIntProperty("nxt.pullThreshold");
         
         blacklistingPeriod = Nxt.getIntProperty("nxt.blacklistingPeriod");
         communicationLoggingMask = Nxt.getIntProperty("nxt.communicationLoggingMask");
         sendToPeersLimit = Nxt.getIntProperty("nxt.sendToPeersLimit");
         usePeersDb = Nxt.getBooleanProperties("nxt.usePeersDb") && ! Constants.isOffline;
         savePeers = usePeersDb && Nxt.getBooleanProperties("nxt.savePeers");
         getMorePeers = Nxt.getBooleanProperties("nxt.getMorePeers");
         dumpPeersVersion = Nxt.getStringProperty("nxt.dumpPeersVersion");
      
         final List<Future<String>> unresolvedPeers = new ArrayList<>();
         
         ThreadPool.runBeforeStart(new Runnable() {
        	 
			private void loadPeers(Collection<String> addrCo){
				for(String address : addrCo){
					Future<String> fu = sendToPeersService.submit(new Callable<String>() {
						@Override
						public String call() throws Exception {
							Peer p =  Peers.addPeer(address);
							return p == null ? address : null;
						}
					});
					unresolvedPeers.add(fu);
				}
			}
			
			@Override
			public void run() {
				if(!wellKnownPeers.isEmpty()){
					loadPeers(wellKnownPeers);
				}else if(usePeersDb){
					Logger.logMessage("loading known peers from database ......");
					loadPeers(PeerDb.loadPeers());
				}
			}
			
		}, false);
        
        ThreadPool.runAfterStart(new Runnable() {
			@Override
			public void run() {
				for(Future<String> f : unresolvedPeers){
					try {
						String address = f.get(5,TimeUnit.SECONDS);
						if(StrKit.isBlank(address)){
							 Logger.logDebugMessage("Failed to resolve peer address: " + address);
						}
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						Logger.logDebugMessage("failed to add peer" , e);
					} catch (TimeoutException e) {
					}
				}
				Logger.logDebugMessage("Know peer :" + peers.size());
			}
		});
        
    }
    
    public static Peer addPeer(String announcedAddress){
    	 if (announcedAddress == null) {
             return null;
         }
         announcedAddress = announcedAddress.trim();
         Peer peer;
         if ((peer = peers.get(announcedAddress)) != null) {
             return peer;
         }
         String address;
         if ((address = announcedAddresses.get(announcedAddress)) != null && (peer = peers.get(address)) != null) {
             return peer;
         }
         try {
             URI uri = new URI("http://" + announcedAddress);
             String host = uri.getHost();
             if ((peer = peers.get(host)) != null) {
                 return peer;
             }
             InetAddress inetAddress = InetAddress.getByName(host);
             return addPeer(inetAddress.getHostAddress(), announcedAddress);
         } catch (URISyntaxException | UnknownHostException e) {
             //Logger.logDebugMessage("Invalid peer address: " + announcedAddress + ", " + e.toString());
             return null;
         }
    }
    
    private static PeerImpl addPeer(String address , String announcedAddress){
    	 //re-add the [] to ipv6 addresses lost in getHostAddress() above
        String clean_address = address;
        if (clean_address.split(":").length > 2) {
            clean_address = "[" + clean_address + "]";
        }
        PeerImpl peer;
        if ((peer = peers.get(clean_address)) != null) {
            return peer;
        }
        String peerAddress = normalizeHostAndPort(clean_address);
        if (peerAddress == null) {
            return null;
        }
        if ((peer = peers.get(peerAddress)) != null) {
            return peer;
        }

        String announcedPeerAddress = address.equals(announcedAddress) ? peerAddress : normalizeHostAndPort(announcedAddress);

        if (Peers.myAddress != null && Peers.myAddress.length() > 0 && Peers.myAddress.equalsIgnoreCase(announcedPeerAddress)) {
            return null;
        }

        peer = new PeerImpl(peerAddress, announcedPeerAddress);
        if (Constants.isTestnet && peer.getPort() > 0 && peer.getPort() != TESTNET_PEER_PORT) {
            Logger.logDebugMessage("Peer " + peerAddress + " on testnet is not using port " + TESTNET_PEER_PORT + ", ignoring");
            return null;
        }
        peers.put(peerAddress, peer);
        if (announcedAddress != null) {
            updateAddress(peer);
        }
        listeners.notify(peer, Event.NEW_PEER);
        return peer;
    }
    
	public static Peer getBlockFeederPeer(){
		Peer peer = null;
		int curr = Nxt.getEpochTime();
		Set<Map.Entry<String, Integer>> entries = priorityBlockFeeders.entrySet();
		for(Map.Entry<String,Integer> en : entries){
			if(curr - en.getValue() > priorityFeederInterval){
				en.setValue(curr);
				peer = addPeer(en.getKey());
				if(peer != null){
					return peer;
				}
			};
		}
		return getAnyPeer(State.CONNECTED,true);
	}
	
	public static Peer getAnyPeer(Peer.State state , boolean applyFullThreshold){
		if(connectWellKnownFinished == false) {
            int wellKnownConnected = 0;
            for(Peer peer : peers.values()) {
                if(peer.isWellKnown() && peer.getState() == Peer.State.CONNECTED) {
                    wellKnownConnected++;
                }
            }
            if(wellKnownConnected >= connectWellKnownFirst) {
                connectWellKnownFinished = true;
                Logger.logInfoMessage("Finished connecting to " + connectWellKnownFirst + " well known peers.");
                Logger.logInfoMessage("You can open your Burst Wallet in your favorite browser with: http://127.0.0.1:8125 or http://localhost:8125");
            }
        }
		
        List<Peer> selectedPeers = new ArrayList<>();
        for (Peer peer : peers.values()) {
            if (! peer.isBlacklisted() && peer.getState() == state && peer.shareAddress()
                    && (!applyFullThreshold)
                    && (connectWellKnownFinished || peer.getState() == Peer.State.CONNECTED || peer.isWellKnown())) {
                selectedPeers.add(peer);
            }
        }
        
        return selectedPeers.get(ThreadLocalRandom.current().nextInt(selectedPeers.size()));
	}
	
	static String normalizeHostAndPort(String address) {
        try {
            if (address == null) {
                return null;
            }
            URI uri = new URI("http://" + address.trim());
            String host = uri.getHost();
            if (host == null || host.equals("") || host.equals("localhost") ||
                                host.equals("127.0.0.1") || host.equals("[0:0:0:0:0:0:0:1]")) {
                return null;
            }
            InetAddress inetAddress = InetAddress.getByName(host);
            if (inetAddress.isAnyLocalAddress() || inetAddress.isLoopbackAddress() ||
                                                   inetAddress.isLinkLocalAddress()) {
                return null;
            }
            int port = uri.getPort();
            return port == -1 ? host : host + ':' + port;
        } catch (URISyntaxException |UnknownHostException e) {
            return null;
        }
    }
	 
	static PeerImpl removePeer(PeerImpl peer) {
        if (peer.getAnnouncedAddress() != null) {
            announcedAddresses.remove(peer.getAnnouncedAddress());
        }
        return peers.remove(peer.getPeerAddress());
    }
	static void updateAddress(PeerImpl peer) {
        String oldAddress = announcedAddresses.put(peer.getAnnouncedAddress(), peer.getPeerAddress());
        if (oldAddress != null && !peer.getPeerAddress().equals(oldAddress)) {
            //Logger.logDebugMessage("Peer " + peer.getAnnouncedAddress() + " has changed address from " + oldAddress
            //        + " to " + peer.getPeerAddress());
            Peer oldPeer = peers.remove(oldAddress);
            if (oldPeer != null) {
                Peers.notifyListeners(oldPeer, Peers.Event.REMOVE);
            }
        }
    }
	
	public static void addListener(Listener<Peer> listener, Event eventType) {
        Peers.listeners.addListener(listener, eventType);
 	}

    public static boolean removeListener(Listener<Peer> listener, Event eventType) {
        return Peers.listeners.removeListener(listener, eventType);
    }

    static void notifyListeners(Peer peer, Event eventType) {
        Peers.listeners.notify(peer, eventType);
    }
}
