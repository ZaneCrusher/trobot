/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot;

import java.util.Map;

/**
 * 
 * Trobot means Travian Robot~
 * 
 * @author jhsea3do
 * 
 */
public interface Trobot {

	/**
	 * 
	 * get trobot instance's id
	 * <em>every trobot instance use <trobot-${id}.properties> as it's settings</em>
	 * 
	 * @return
	 * 
	 */
	String getId();

	/**
	 * 
	 * set trobot instance's id
	 * 
	 * @param id
	 * 
	 */
	void setId(String id);

	/**
	 * initial trobot instance settings
	 * 
	 * @throws Exception
	 * 
	 */
	void init() throws Exception;

	/**
	 * 
	 * write debug message into the debug writer with convert charset method
	 * 
	 * @param message
	 * @param fromCharset
	 * @param toCharset
	 * 
	 */
	void debug(String message, String fromCharset, String toCharset);

	/**
	 * 
	 * write debug message into the debug writer
	 * 
	 * @param message
	 * 
	 */
	void debug(String message);

	/**
	 * 
	 * is trobot use a plus account
	 * 
	 * @return
	 * 
	 */
	boolean isPlus();

	/**
	 * 
	 * get trobot login cookie
	 * 
	 * @return
	 * 
	 */
	String getCookie();

	/**
	 * 
	 * get allianz infomations
	 * 
	 * @param uri
	 * @return
	 * @throws Exception
	 * 
	 */
	InfoAllianz allianz(String uri) throws Exception;

	/**
	 * 
	 * get allianz infomations
	 * 
	 * @param aid
	 * @return
	 * @throws Exception
	 * 
	 */
	InfoAllianz allianz(int aid) throws Exception;

	/**
	 * 
	 * get spieler infomations
	 * 
	 * @param uri
	 * @return
	 * @throws Exception
	 * 
	 */
	InfoSpieler spieler(String uri) throws Exception;

	/**
	 * 
	 * get spieler infomations
	 * 
	 * @param uid
	 * @return
	 * @throws Exception
	 * 
	 */
	InfoSpieler spieler(int uid) throws Exception;

	/**
	 * 
	 * get karte infomations
	 * 
	 * @param uri
	 * @return
	 * @throws Exception
	 * 
	 */
	InfoKarte karte(String uri) throws Exception;

	/**
	 * 
	 * get karte infomations
	 * 
	 * @param d
	 * @param c
	 * @return
	 * @throws Exception
	 * 
	 */
	InfoKarte karte(String d, String c) throws Exception;

	/**
	 * 
	 * set and switch current village
	 * 
	 * @param did
	 * @throws Exception
	 * 
	 */
	void setActiveVillage(String did) throws Exception;

	/**
	 * 
	 * get current village
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	InfoVillage getActiveVillage() throws Exception;

	/**
	 * 
	 * get all villages
	 * 
	 * @return
	 * 
	 */
	Map<String, InfoVillage> getVillages();

	/**
	 * 
	 * login
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	boolean login() throws Exception;

	/**
	 * 
	 * send a private message to a player
	 * 
	 * @param username
	 * @param subject
	 * @param message
	 * 
	 */
	void send(String username, String subject, String message);

	/**
	 * 
	 * invite a player join our allian
	 * 
	 * @param username
	 * @param subject
	 * @param message
	 * 
	 */
	void invite(String username);

	/**
	 * 
	 * run trobot tasks
	 * 
	 * @throws Exception
	 * 
	 */
	void run() throws Exception;

}
