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
public class InfoSpieler {

	private String uid;

	private InfoKarte mainKarte;

	private String uri;

	private String allianzRank;

	private String username;

	private String status;

	private int villages;

	private int population;

	protected Map<String, InfoKarte> kartes = new LinkedHashMap<String, InfoKarte>();

	public static final String STATUS_BLUE = "1";

	public static final String STATUS_GREEN = "2";

	public static final String STATUS_YELLOW = "3";

	public static final String STATUS_RED = "4";

	public static final String STATUS_GRAY = "5";

	/**
	 * @return the mainKarte
	 */
	public InfoKarte getMainKarte() {

		return mainKarte;
	}

	/**
	 * @param mainKarte
	 *            the mainKarte to set
	 */
	public void setMainKarte(InfoKarte mainKarte) {

		this.mainKarte = mainKarte;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {

		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {

		this.uid = uid;
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

	/**
	 * @return the allianzRank
	 */
	public String getAllianzRank() {

		return allianzRank;
	}

	/**
	 * @param allianzRank
	 *            the allianzRank to set
	 */
	public void setAllianzRank(String allianzRank) {

		this.allianzRank = allianzRank;
	}

	/**
	 * @return the population
	 */
	public int getPopulation() {

		return population;
	}

	/**
	 * @param population
	 *            the population to set
	 */
	public void setPopulation(int population) {

		this.population = population;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {

		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {

		this.username = username;
	}

	/**
	 * 
	 * @return
	 */
	public String getStatus() {

		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(String status) {

		this.status = status;
	}

	/**
	 * @return the villages
	 */
	public int getVillages() {

		return villages;
	}

	/**
	 * @param villages
	 *            the villages to set
	 */
	public void setVillages(int villages) {

		this.villages = villages;
	}

	public String format() {

		StringBuffer sb = new StringBuffer("");
		sb.append(allianzRank);
		sb.append("\t");
		sb.append(username);
		sb.append("\t");
		sb.append(mainKarte.getPosX());
		sb.append("\t");
		sb.append(mainKarte.getPosY());
		sb.append("\t");
		sb.append(mainKarte.getKarteType());
		sb.append("\t");
		sb.append(villages);
		sb.append("\t");
		sb.append(population);
		sb.append("\t");
		sb.append(status);
		return sb.toString();
	}

}
