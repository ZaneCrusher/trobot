/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author jhsea3do
 * 
 */
public class Configuration {

	private static Log log = LogFactory.getLog(Configuration.class);

	private static Configuration self = new Configuration();

	public static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public static final String FILE_SEPARATOR = System
			.getProperty("file.separator");

	public static final String CHARSET_SYSTEM = System
			.getProperty("file.encoding");

	public static final String CHARSET_DEFAULT = self
			.getString("trobot.charset.default");

	public static final String CHARSET_REMOTE = self
			.getString("trobot.charset.remote");

	public static Configuration getConfiguration() {

		return self;
	}

	public Configuration() {

		super();
		this.load();
	}

	private Properties props;

	public void load() {

		if (null == props) {
			props = new Properties();
		} else {
			props.clear();
		}
		try {
			props.load(Configuration.class
					.getResourceAsStream("/trobot.properties"));
		} catch (IOException ioex) {
			log.fatal("trobot properties load failed!");
		}
	}

	public String getString(String propName) {

		return (String) props.get(propName);
	}

	public boolean getBoolean(String propName) {

		return Boolean.parseBoolean(getString(propName));
	}

	public double getDouble(String propName) {

		return Double.parseDouble(getString(propName));
	}

	public String getDataPath() {

		return getString("trobot.data.path");
	}

	public String getSiteHost() {

		return getString("trobot.site.host");
	}

	public String getSiteUser() {

		return getString("trobot.site.user");
	}

	public String getSitePass() {

		return getString("trobot.site.pass");
	}

	public String getBaseUrl() {

		return "http://" + getSiteHost();
	}

	public String getUrl(String uri) {

		return getBaseUrl() + uri;
	}

	public boolean isDebug() {

		return getBoolean("trobot.debug");
	}
}
