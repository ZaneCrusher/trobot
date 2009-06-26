/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot.task;

import java.util.TimerTask;

import com.javaws.trobot.Configuration;
import com.javaws.trobot.Trobot;

public abstract class TrobotTask extends TimerTask {

	private Trobot trobot;

	private Configuration config;

	private TrobotTask nextTask;

	public TrobotTask() {

		super();
	}

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
	 * @return the config
	 */
	public Configuration getConfig() {

		return config;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(Configuration config) {

		this.config = config;
	}

	/**
	 * @return the nextTask
	 */
	public TrobotTask getNextTask() {

		return nextTask;
	}

	/**
	 * @param nextTask
	 *            the nextTask to set
	 */
	public void setNextTask(TrobotTask nextTask) {

		this.nextTask = nextTask;
	}

}
