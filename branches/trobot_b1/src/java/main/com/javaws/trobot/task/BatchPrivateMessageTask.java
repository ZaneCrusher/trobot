/*
 * ~ TROBOT ~
 * 
 * $Id: BatchPrivateMessageTask.java 24 2008-05-05 18:37:02Z jhsea3do $
 */

package com.javaws.trobot.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javaws.trobot.Configuration;
import com.javaws.trobot.Trobot;

public class BatchPrivateMessageTask extends TrobotTask {

	private static Log log = LogFactory.getLog(BatchPrivateMessageTask.class);

	@Override
	public void run() {

		try {
			Trobot trobot = this.getTrobot();
			Configuration config = this.getConfig();
			if (trobot.login()) {
				String p_receivers = config
						.getString("trobot.task.batch.receivers");
				String p_subject = config
						.getString("trobot.task.batch.subject");
				String p_message = config
						.getString("trobot.task.batch.message");
				String[] uu = p_receivers.split(",");
				String m = p_message;
				String s = p_subject;
				trobot.debug("<<< private message receivers = " + p_receivers,
						config.getCharsets()[Configuration.CHARSET_DEFAULT_ID],
						config.getCharsets()[Configuration.CHARSET_REMOTE_ID]);
				trobot.debug("<<< private message subject = " + s, config
						.getCharsets()[Configuration.CHARSET_DEFAULT_ID],
						config.getCharsets()[Configuration.CHARSET_REMOTE_ID]);
				trobot.debug("<<< private message message = " + m, config
						.getCharsets()[Configuration.CHARSET_DEFAULT_ID],
						config.getCharsets()[Configuration.CHARSET_REMOTE_ID]);
				int iCount = 0;
				for (String u : uu) {
					Thread.sleep(5000);
					trobot.send(u, s, m);
					// this.invite(u);
					iCount++;
				}
			}
			log.info("run task done ;-)");
		} catch (Exception ex) {
			log.error("run task failed...", ex);
		}

	}
}
