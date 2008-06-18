/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javaws.trobot.Configuration;
import com.javaws.trobot.Trobot;

public class AllianzReportTask extends TrobotTask {

	private static Log log = LogFactory.getLog(AllianzReportTask.class);

	@Override
	public void run() {

		try {
			Trobot trobot = this.getTrobot();
			Configuration config = this.getConfig();
			if (trobot.login()) {
				int aid = Integer.parseInt(config
						.getString("trobot.task.allianz.aid"));
				log.info("load information from: allianz(" + aid + ")");
				log.info("联盟排名\t用户名\t坐标X\t坐标Y\t主村类型\t村庄数量\t人口数");
				trobot.allianz(aid);
			}
			log.info("run task done ;-)");
		} catch (Exception ex) {
			log.error("run task failed...", ex);
		}
	}

}
