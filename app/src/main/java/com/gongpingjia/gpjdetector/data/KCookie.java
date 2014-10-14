package com.gongpingjia.gpjdetector.data;

public class KCookie {
	public KCookie () {

	}
	public KCookie (String sessionid, String expires) {
		this.sessionid = sessionid;
        this.expires = expires;
	}
	public String sessionid;
    public String expires;
}
