/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot.task;

import java.util.Properties;
import java.util.TimerTask;

import com.javaws.trobot.Trobot;

public abstract class TrobotTask extends TimerTask {

	private TrobotTask() {

		super();
	}

	public TrobotTask(Trobot trobot, Properties taskProps) {

		this();
		this.setTrobot(trobot);
		this.setTaskProps(taskProps);
	}

	public TrobotTask(Trobot trobot) {

		this();
		this.setTrobot(trobot);
		try {
			Properties taskProps = new Properties();
			taskProps.load(this.getClass().getResourceAsStream("/trobot.task"));
			this.setTaskProps(taskProps);
		} catch (Exception ex) {
			this.setTaskProps(null);
		}
	}

	private Trobot trobot;

	private Properties taskProps;

	/**
	 * @return the trobot
	 */
	public Trobot getTrobot() {

		return trobot;
	}

	/**
	 * @param trobot
	 *            the trobot to set
	 */
	public void setTrobot(Trobot trobot) {

		this.trobot = trobot;
	}

	/**
	 * @return the taskProps
	 */
	public Properties getTaskProps() {

		return taskProps;
	}

	/**
	 * @param taskProps
	 *            the taskProps to set
	 */
	public void setTaskProps(Properties taskProps) {

		this.taskProps = taskProps;
	}

}
