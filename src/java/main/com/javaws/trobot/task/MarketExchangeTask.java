/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot.task;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javaws.trobot.Configuration;
import com.javaws.trobot.InfoVillage;
import com.javaws.trobot.Trobot;

public class MarketExchangeTask extends TrobotTask {

	private static Log log = LogFactory.getLog(MarketExchangeTask.class);

	private static Configuration config = Configuration.getConfiguration();

	/**
	 * @see TrobotTask#TrobotTask(Trobot, Properties);
	 */
	public MarketExchangeTask(Trobot trobot, Properties taskProps) {

		super(trobot, taskProps);
	}

	/**
	 * @see TrobotTask#TrobotTask(Trobot);
	 */
	public MarketExchangeTask(Trobot trobot) {

		super(trobot);
	}

	private void init() throws Exception {

		if (null == this.getTrobot()) {
			String driver = config.getString("trobot.driver");
			Trobot trobot = (Trobot) Class.forName(driver).newInstance();
			this.setTrobot(trobot);
		}
	}

	@Override
	public void run() {

		try {
			this.init();
			Trobot trobot = this.getTrobot();
			if (trobot.login()) {
				trobot.setActiveVillage("131355");
				InfoVillage v = trobot.getActiveVillage();
				log.info("v:" + v.getName());
			//	if (trobot.isPlus()) {
			//		trobot.setActiveVillage(this.getTaskProps().getProperty(
			//			"trobot.task.village.did"));
			//		InfoVillage v = trobot.getActiveVillage();
			//		log.info("v:" + v.getName());
			//	}
			//	;
			}
			log.info("run task done ;-)");
		} catch (Exception ex) {
			log.error("run task failed...", ex);
		}

	}

}
