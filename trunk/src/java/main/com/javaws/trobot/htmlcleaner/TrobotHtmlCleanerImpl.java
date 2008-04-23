/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot.htmlcleaner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.htmlcleaner.HtmlCleaner;

import com.javaws.trobot.Configuration;
import com.javaws.trobot.HtmlUtils;
import com.javaws.trobot.InfoAllianz;
import com.javaws.trobot.InfoKarte;
import com.javaws.trobot.InfoSpieler;
import com.javaws.trobot.Trobot;

public class TrobotHtmlCleanerImpl implements Trobot {

	private static Log log = LogFactory.getLog(TrobotHtmlCleanerImpl.class);

	private static Configuration config = Configuration.getConfiguration();

	private static HtmlUtils htmlUtils = HtmlUtils.getInstance();

	public TrobotHtmlCleanerImpl() {
		super();
		this.username = config.getSiteUser();
		this.password = config.getSitePass();
		this.debug = config.isDebug();
	}

	public TrobotHtmlCleanerImpl(String uri) {
		this();
	}

	@SuppressWarnings("unused")
	private String cookie = null;

	private String username = "no_user";

	private String password = "no_pass";

	private boolean debug = false;

	public void debug(String message, String fromCharset, String toCharset) {
		if (debug) {
			try {
				String line = new String(message.getBytes(fromCharset),
						toCharset);
				System.out.println(line);
			} catch (Exception ex) {
				log.warn("convert message failed!");
				System.out.println(message);
			}
		}
	}

	public void debug(String message) {
		if (debug) {
			System.out.println(message);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean login() throws Exception {

		boolean login_successful = false;
		try {
			String url_login = config.getUrl("/login.php");
			debug("<<< login from " + url_login);
			debug("<<< login username " + this.username,
					Configuration.CHARSET_DEFAULT, Configuration.CHARSET_REMOTE);
			debug("<<< login password " + this.password,
					Configuration.CHARSET_DEFAULT, Configuration.CHARSET_REMOTE);
			String html_login = htmlUtils.getResponseText(htmlUtils
					.load(url_login));
			HtmlCleaner hc_login = new HtmlCleaner(html_login);
			hc_login.clean();
			String xml_login = hc_login.getXmlAsString();
			// debug(">>> login response " + xml_login);
			Document page_login = htmlUtils.getResponseDocument(xml_login);
			Element form_login = (Element) page_login
					.selectSingleNode("//form[@name=\"snd\"]");
			// debug(">>> login form " + form_login.asXML());
			// debug(">>> login response " + page_login.asXML());
			List<Element> elem_inputs = form_login.selectNodes("//input");
			Map<String, String> input_params = new LinkedHashMap<String, String>();
			Map<String, String> header_params = new LinkedHashMap<String, String>();
			for (Element elem_input : elem_inputs) {
				// debug(">>> input field " + elem_input.asXML());
				String key = elem_input.attributeValue("name");
				String value = elem_input.attributeValue("value");
				String input_type = elem_input.attributeValue("type");
				if ("text".equals(input_type) || "password".equals(input_type)) {
					String input_class = elem_input.attributeValue("class");
					if ("fm fm110".equals(input_class)) {
						if (input_type.equals("text")) {
							value = this.username;
						} else if (input_type.equals("password")) {
							value = this.password;
						}
					}
				}
				debug("<<< post param key=" + key + " ,value=" + value);
				input_params.put(key, value);
			}
			HttpMethod method_submit = htmlUtils.load(config
					.getUrl("/dorf1.php"), input_params, header_params, "post");
			// String html_submit = htmlUtils.getResponseText(method_submit);
			// Header[] method_submit_headers =
			// method_submit.getResponseHeaders();
			this.cookie = htmlUtils.getCookieText(method_submit);
			debug(">>> set-cookie = " + cookie);
			login_successful = true;
		} catch (Exception ex) {
			this.cookie = null;
			login_successful = false;
			log.error("login failed by " + ex.getMessage());
		}
		return login_successful;
	}

	public InfoAllianz allianz(String uri) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public InfoAllianz allianz(int aid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public InfoKarte karte(String uri) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public InfoKarte karte(String d, String c) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public InfoSpieler spieler(String uri) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public InfoSpieler spieler(int uid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
