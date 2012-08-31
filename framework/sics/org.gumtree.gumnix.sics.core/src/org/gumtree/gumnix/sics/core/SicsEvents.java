package org.gumtree.gumnix.sics.core;

public class SicsEvents {
	
	public static final String TOPIC_BASE = "org/gumtree/gumnix/sics";
	
	public static interface Proxy {
		
		public static final String TOPIC_ALL = TOPIC_BASE + "/proxy/*";
		
		public static final String TOPIC_CONNECTED = TOPIC_BASE + "/proxy/connected";
		
		public static final String TOPIC_DISCONNECTED = TOPIC_BASE + "/proxy/disconnected";
		
		public static final String PROXY = "proxy";
		
		public static final String PROXY_ID = "proxyId";
		
	}
	
}