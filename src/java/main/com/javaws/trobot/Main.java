/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author jhsea3do
 * 
 */
public class Main {

	private static Log log = LogFactory.getLog(Main.class);

	private static Configuration config = Configuration.getConfiguration();

	public static void main(String[] args) throws Exception {

		String driver = config.getString("trobot.driver");

		log.info("use trobot driver: " + driver);

		String id = "test";

		Trobot trobot = (Trobot) Class.forName(driver).newInstance();

		trobot.setId(id);
		trobot.init();
		trobot.login();
		trobot.run();

	}

}
