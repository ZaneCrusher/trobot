/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author jhsea3do
 * 
 */
public class InfoAllianz {

	private String aid;

	private String uri;

	protected Map<String, InfoSpieler> spielers = new LinkedHashMap<String, InfoSpieler>();

	/**
	 * @return the aid
	 */
	public String getAid() {
		return aid;
	}

	/**
	 * @param aid
	 *            the aid to set
	 */
	public void setAid(String aid) {
		this.aid = aid;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

}
