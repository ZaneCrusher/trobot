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

public class VillagesReportTask extends TrobotTask {

	private static Log log = LogFactory.getLog(VillagesReportTask.class);

	public void previewAllVillages(Trobot trobot) throws Exception {

		for (String did : trobot.getVillages().keySet()) {
			trobot.setActiveVillage(did);
		}
		InfoVillage activeVillage = trobot.getActiveVillage();
		trobot.debug(">>> active village " + activeVillage.getName());
	}

	@Override
	public void run() {

		Trobot trobot = this.getTrobot();

		try {
			if (trobot.login()) {
				this.previewAllVillages(trobot);
			}
			log.info("run task done ;-)");
		} catch (Exception ex) {
			log.error("run task failed...", ex);
		}

	}

}
