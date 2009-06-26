/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javaws.trobot.InfoVillage;
import com.javaws.trobot.Trobot;

public class MarketExchangeTask extends TrobotTask {

	private static Log log = LogFactory.getLog(MarketExchangeTask.class);

	@Override
	public void run() {

		try {
			Trobot trobot = this.getTrobot();
			if (trobot.login()) {
				trobot.setActiveVillage("131355");
				InfoVillage v = trobot.getActiveVillage();
				log.info("v:" + v.getName());
				// if (trobot.isPlus()) {
				// trobot.setActiveVillage(this.getTaskProps().getProperty(
				// "trobot.task.village.did"));
				// InfoVillage v = trobot.getActiveVillage();
				// log.info("v:" + v.getName());
				// }
				// ;
			}
			log.info("run task done ;-)");
		} catch (Exception ex) {
			log.error("run task failed...", ex);
		}

	}

}
