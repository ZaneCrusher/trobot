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

	public static void main(String[] args) throws Exception {
		Trobot trobot = new Trobot();
		trobot.login();
		//log.info("load information from: allianz(" + 36 + ")");
		//trobot.allianz(36);
		//log.info("load information from: allianz(" + 99 + ")");
		//trobot.allianz(99);
		//log.info("load information from: allianz(" + 190 + ")");
		//trobot.allianz(190);
		//log.info("load information from: allianz(" + 1497 + ")");
		//trobot.allianz(1497);
		//log.info("load information from: allianz(" + 178 + ")");
		//trobot.allianz(178);
		//log.info("load information from: allianz(" + 1307 + ")");
		//trobot.allianz(1307);
		log.info("load information from: allianz(" + 457 + ")");
		trobot.allianz(457);
		// trobot.karte("/karte.php?d=349621&c=19");
		// trobot.karte("/karte.php?d=512859&c=a1");
		log.info("job done ;-)");
	}

}
