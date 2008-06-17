/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot;

/**
 * 
 * @author jhsea3do
 * 
 */
public class InfoKarte {

	private String d;

	private String c;

	private String posX;

	private String posY;

	private String uri;

	private String type;

	public String getC() {

		return c;
	}

	public void setC(String c) {

		this.c = c;
	}

	public String getD() {

		return d;
	}

	public void setD(String d) {

		this.d = d;
	}

	public String getPosX() {

		return posX;
	}

	public void setPosX(String posX) {

		this.posX = posX;
	}

	public String getPosY() {

		return posY;
	}

	public void setPosY(String posY) {

		this.posY = posY;
	}

	public String getUri() {

		return uri;
	}

	public void setUri(String uri) {

		this.uri = uri;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public String getPosition() {

		return "(" + posX + "|" + posY + ")";
	}

	public String getKarteType() {

		if ("f6".equals(type)) {
			return "15";
		} else if ("f1".equals(type)) {
			return "9";
		} else {
			return "6";
		}
	}

}
