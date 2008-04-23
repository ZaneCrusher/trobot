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
		Trobot trobot = (Trobot) Class.forName(driver).newInstance();
		trobot.login();
		log.info("load information from: allianz(" + 36 + ")");
		trobot.allianz(36);
		log.info("load information from: allianz(" + 99 + ")");
		trobot.allianz(99);
		log.info("load information from: allianz(" + 636 + ")");
		trobot.allianz(636);
		log.info("load information from: allianz(" + 190 + ")");
		trobot.allianz(190);
		log.info("load information from: allianz(" + 1497 + ")");
		trobot.allianz(1497);
		log.info("load information from: allianz(" + 178 + ")");
		trobot.allianz(178);
		log.info("load information from: allianz(" + 1307 + ")");
		trobot.allianz(1307);
		log.info("load information from: allianz(" + 457 + ")");
		trobot.allianz(457);
		// InfoKarte k = trobot.karte("/karte.php?d=516861&c=8a");
		// log.info("k.pos" + k.getPosition());
		// log.info("k.type" + k.getKarteType());
		// trobot.karte("/karte.php?d=349621&c=19");
		// trobot.karte("/karte.php?d=512859&c=a1");
		log.info("job done ;-)");
	}

}
