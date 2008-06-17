/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot.task;

import com.javaws.trobot.InfoVillage;
import com.javaws.trobot.Trobot;

public class VillagesReportTask extends TrobotTask {

	public void previewAllVillages() throws Exception {

		Trobot trobot = this.getTrobot();

		for (String did : trobot.getVillages().keySet()) {
			trobot.setActiveVillage(did);
		}
		InfoVillage activeVillage = trobot.getActiveVillage();
		trobot.debug(">>> active village " + activeVillage.getName());
	}

	@Override
	public void run() {

		try {
			this.previewAllVillages();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
