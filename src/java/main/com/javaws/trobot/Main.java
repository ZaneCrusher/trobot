/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javaws.trobot.task.BatchPrivateMessageTask;
import com.javaws.trobot.task.MarketExchangeTask;
import com.javaws.trobot.task.TrobotTask;

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
		TrobotTask task = new BatchPrivateMessageTask(trobot);
		Timer timer = new Timer();
		// timer.schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		//
		// log.info("waitting...");
		// }
		//
		// }, 5000, 30 * 1000);
		// timer.schedule(task, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		// .parse("2008-05-05 14:28:00"));
		timer.schedule(task, 0 * 1000);
		// trobot.login();
		// log.info("load information from: allianz(" + 36 + ")");
		// trobot.allianz(36);
		// log.info("load information from: allianz(" + 99 + ")");
		// trobot.allianz(99);
		// log.info("load information from: allianz(" + 636 + ")");
		// trobot.allianz(636);
		// log.info("load information from: allianz(" + 190 + ")");
		// trobot.allianz(190);
		// log.info("load information from: allianz(" + 1497 + ")");
		// trobot.allianz(1497);
		// log.info("load information from: allianz(" + 178 + ")");
		// trobot.allianz(178);
		// log.info("load information from: allianz(" + 1307 + ")");
		// trobot.allianz(1307);
		// log.info("load information from: allianz(" + 457 + ")");
		// trobot.allianz(457);
		// InfoKarte k = trobot.karte("/karte.php?d=516861&c=8a");
		// log.info("k.pos" + k.getPosition());
		// log.info("k.type" + k.getKarteType());
		// trobot.karte("/karte.php?d=349621&c=19");
		// trobot.karte("/karte.php?d=512859&c=a1");
		// log.info("job done ;-)");
	}

}
