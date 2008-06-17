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

}
