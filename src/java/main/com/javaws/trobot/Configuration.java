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

	private static Configuration conf_default = new Configuration();

	public static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public static final String FILE_SEPARATOR = System
			.getProperty("file.separator");

	public static final int CHARSET_SYSTEM_ID = 0;

	public static final int CHARSET_DEFAULT_ID = 1;

	public static final int CHARSET_REMOTE_ID = 2;

	public static Configuration getConfiguration() {

		return conf_default;
	}

	public static Configuration getConfiguration(String classPath) {

		return new Configuration(classPath);
	}

	public Configuration() {

		super();
		this.init();
	}

	public Configuration(String classPath) {

		super();
		this.init();
		this.load(classPath);
	}

	public void load(String classPath) {

		try {
			log.info("load trobot properties: " + classPath + " ...");
			this.props.load(Configuration.class.getResourceAsStream(classPath));
			this.props.putAll(System.getProperties());
		} catch (IOException ioex) {
			log.fatal("trobot properties load failed!");
		}
	}

	public void init() {

		if (null == props) {
			this.props = new Properties();
		} else {
			this.props.clear();
		}
		this.load("/trobot.properties");
	}

	public String getString(String propName) {

		return (String) this.props.get(propName);
	}

	public boolean getBoolean(String propName) {

		return Boolean.parseBoolean(this.getString(propName));
	}

	public double getDouble(String propName) {

		return Double.parseDouble(this.getString(propName));
	}

	public String getDataPath() {

		return this.getString("trobot.data.path");
	}

	public String getSiteHost() {

		return this.getString("trobot.site.host");
	}

	public String getSiteUser() {

		return this.getString("trobot.site.user");
	}

	public String getSitePass() {

		return this.getString("trobot.site.pass");
	}

	public String getBaseUrl() {

		return "http://" + this.getSiteHost();
	}

	public String getUrl(String uri) {

		return this.getBaseUrl() + uri;
	}

	public boolean isDebug() {

		return this.getBoolean("trobot.debug");
	}

	public String[] getCharsets() {

		return new String[] { System.getProperty("file.encoding"), // CHARSET_SYSTEM
				this.getString("trobot.charset.default"), // CHARSET_DEFAULT
				this.getString("trobot.charset.remote") // CHARSET_REMOTE
		};
	}

	private Properties props;
}
