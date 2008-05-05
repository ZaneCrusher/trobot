/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot.jtidy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import com.javaws.trobot.Configuration;
import com.javaws.trobot.HtmlUtils;
import com.javaws.trobot.InfoAllianz;
import com.javaws.trobot.InfoKarte;
import com.javaws.trobot.InfoSpieler;
import com.javaws.trobot.InfoVillage;
import com.javaws.trobot.Trobot;

/**
 * 
 * @author jhsea3do
 * 
 * @deprecated
 * 
 */
public class TrobotJtidyImpl implements Trobot {

	private static Log log = LogFactory.getLog(TrobotJtidyImpl.class);

	private static Configuration config = Configuration.getConfiguration();

	private static HtmlUtils htmlUtils = HtmlUtils.getInstance();

	private static JtidyUtils jtidyUtils = JtidyUtils.getInstance();

	public TrobotJtidyImpl() {

		super();
		this.username = config.getSiteUser();
		this.password = config.getSitePass();
		this.debug = config.isDebug();
	}

	public TrobotJtidyImpl(String uri) {

		this();
		// this.uri = uri;
	}

	// private String uri;

	private String cookie = null;

	private String username = "no_user";

	private String password = "no_pass";

	private boolean debug = false;

	private Map<String, InfoVillage> villages = null;

	private List<String> notify_messages = null;

	/**
	 * @see Trobot#getVillages()
	 */
	public Map<String, InfoVillage> getVillages() {

		return villages;
	}

	/**
	 * @see Trobot#getNotifyMessages()
	 */
	public List<String> getNotifyMessages() {

		return notify_messages;
	}

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
			// debug(">>> login response " + html_login);
			int p_s_form_login = html_login
					.indexOf("<form method=\"post\" name=\"snd\" action=\"dorf1.php\">");
			int p_e_form_login = html_login.substring(p_s_form_login).indexOf(
					"</form>")
					+ "</form>".length();
			String form_login = html_login.substring(p_s_form_login,
					p_s_form_login + p_e_form_login);
			Document page_login = htmlUtils.getResponseDocument(jtidyUtils
					.tidy(form_login));
			// debug(">>> login response " + page_login.asXML());
			List<Element> elem_inputs = page_login.selectNodes("//input");
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
			// for (Header header : method_submit_headers) {
			// debug(">>> header name=" + header.getName() + " ,value="
			// + header.getValue());
			// }
			// debug(">>> html_dorf1 " + html_submit);
			// debug("document = " + document);
			login_successful = true;
		} catch (Exception ex) {
			this.cookie = null;
			login_successful = false;
			log.error("login failed by " + ex.getMessage());
		}
		return login_successful;
	}

	@SuppressWarnings("unchecked")
	public InfoAllianz allianz(String uri) throws Exception {

		// http://s6.travian.cn/allianz.php?aid=636
		if (null == this.cookie) {
			if (!this.login()) {
				return null;
			}
		}
		InfoAllianz infoAllianz = null;
		try {
			infoAllianz = new InfoAllianz();
			infoAllianz.setUri(uri);
			Map<String, String> header_params = new LinkedHashMap<String, String>();
			header_params.put("Cookie", this.cookie);
			String allianz_url = config.getUrl(uri);
			debug("<<< set-cookie = " + this.cookie);
			HttpMethod method_allianz = htmlUtils.load(allianz_url,
					header_params);
			String html_allianz = htmlUtils.getResponseText(method_allianz);
			// debug(">>> allianz response = " + html_allianz);
			int p_s_table_tbg = html_allianz
					.indexOf("</p><p><table cellspacing=\"1\" cellpadding=\"2\" class=\"tbg\">")
					+ "</p><p>".length();
			int p_e_table_tbg = html_allianz.substring(p_s_table_tbg).indexOf(
					"</table>")
					+ "</table>".length();
			String table_tbg = html_allianz.substring(p_s_table_tbg,
					p_s_table_tbg + p_e_table_tbg);
			Document page_tbg = htmlUtils.getResponseDocument(jtidyUtils
					.tidy(table_tbg));
			// debug(">>> page tbg " + page_tbg.asXML());
			List<Element> elem_trs = page_tbg.selectNodes("//table/tr");
			for (Element elem_tr : elem_trs) {
				// debug(">>> tr element " + elem_tr.asXML());
				if (!"rbg".equals(elem_tr.attributeValue("class"))) {
					List<Element> elem_tds = elem_tr.elements("td");
					String user_no = "";
					String user_name = "";
					String user_spieler_uri = "";
					String user_population = "";
					String user_villages = "";
					int index = 0;
					for (Element elem_td : elem_tds) {
						// debug(">>> td element " + elem_td.asXML());
						switch (index) {
						case 0:
							user_no = elem_td.getTextTrim();
						case 1:
							List<Element> elem_as = elem_td.elements("a");
							if (null != elem_as && !elem_as.isEmpty()) {
								Element elem_a = (Element) (elem_as.get(0));
								user_name = elem_a.getTextTrim();
								user_spieler_uri = "/"
										+ elem_a.attributeValue("href");
							}
						case 2:
							user_population = elem_td.getTextTrim();
						case 3:
							user_villages = elem_td.getTextTrim();
						}
						index++;
					}
					InfoSpieler infoSpieler = spieler(user_spieler_uri);
					infoSpieler.setAllianzRank(user_no);
					infoSpieler.setUsername(user_name);
					infoSpieler
							.setPopulation(Integer.parseInt(user_population));
					infoSpieler.setVillages(Integer.parseInt(user_villages));
					infoAllianz.addSpieler(user_no, infoSpieler);
					if (config.getBoolean("trobot.output")) {
						log.info(infoSpieler.format());
					}
				}
			}
		} catch (Exception ex) {
			log.error("parse allianz failed!");
			infoAllianz = null;
		}
		return infoAllianz;
	}

	@SuppressWarnings("unchecked")
	public InfoAllianz allianz(int aid) throws Exception {

		return allianz("/allianz.php?aid=" + aid);
	}

	@SuppressWarnings("unchecked")
	public InfoSpieler spieler(String uri) throws Exception {

		if (null == this.cookie) {
			if (!this.login()) {
				return null;
			}
		}
		InfoSpieler infoSpieler = null;
		try {
			Map<String, String> header_params = new LinkedHashMap<String, String>();
			header_params.put("Cookie", this.cookie);
			String spieler_url = config.getUrl(uri);
			debug("<<< set-cookie = " + this.cookie);
			HttpMethod method_spieler = htmlUtils.load(spieler_url,
					header_params);
			String html_spieler = htmlUtils.getResponseText(method_spieler);
			// debug(">>> spieler response = " + html_spieler);
			int p_s_table_tbg = html_spieler
					.indexOf("</p><p>"
							+ Configuration.LINE_SEPARATOR
							+ "<table cellspacing=\"1\" cellpadding=\"2\" class=\"tbg\">")
					+ "</p><p>".length();
			int p_e_table_tbg = html_spieler.substring(p_s_table_tbg).indexOf(
					"</table>")
					+ "</table>".length();
			String table_tbg = html_spieler.substring(p_s_table_tbg,
					p_s_table_tbg + p_e_table_tbg);
			Document page_tbg = htmlUtils.getResponseDocument(jtidyUtils
					.tidy(table_tbg));
			// debug(">>> page tbg " + page_tbg.asXML());
			List<Element> elem_spans = page_tbg
					.selectNodes("//span[@class=\"c\"]");
			Element elem_tr = null;
			Element elem_a = null;
			if (!elem_spans.isEmpty()) {
				Element elem_span = (Element) elem_spans.get(0);
				elem_tr = elem_span.getParent().getParent();
				List<Element> elem_as = elem_span.getParent().elements("a");
				elem_a = (Element) elem_as.get(0);
			} else {
				List<Element> elem_as = page_tbg.selectNodes("//a[@href]");
				elem_a = (Element) elem_as.get(0);
				elem_tr = elem_a.getParent().getParent();
			}
			Element elem_td = (Element) elem_tr.elements("td").get(2);
			String main_village_position = elem_td.getTextTrim();
			String pos = main_village_position.replaceAll("\\(", "")
					.replaceAll("\\)", "");
			String[] position = pos.split("\\|");
			InfoKarte mainKarte = karte("/" + elem_a.attributeValue("href"));
			mainKarte.setPosX(position[0]);
			mainKarte.setPosY(position[1]);
			infoSpieler = new InfoSpieler();
			infoSpieler.setMainKarte(mainKarte);
			infoSpieler.setUri(uri);
		} catch (Exception ex) {
			log.error("parse spieler failed!");
		}
		return infoSpieler;
	}

	public InfoSpieler spieler(int uid) throws Exception {

		return spieler("/spieler.php?uid=" + uid);
	}

	public InfoKarte karte(String uri) throws Exception {

		if (null == this.cookie) {
			if (!this.login()) {
				return null;
			}
		}
		InfoKarte infoKarte = null;
		try {
			infoKarte = new InfoKarte();
			infoKarte.setUri(uri);
			Map<String, String> header_params = new LinkedHashMap<String, String>();
			header_params.put("Cookie", this.cookie);
			String karte_url = config.getUrl(uri);
			debug("<<< set-cookie = " + this.cookie);
			HttpMethod method_karte = htmlUtils.load(karte_url, header_params);
			String html_karte = htmlUtils.getResponseText(method_karte);
			String keywords_lmid2 = "<div id=\"lmid1\"><div id=\"lmid2\"><div class=\"dname\"><h1>";
			int p_s_h1_keywords = html_karte.indexOf(keywords_lmid2)
					+ "<div id=\"lmid1\"><div id=\"lmid2\">".length();
			int p_e_h1_keywords = html_karte.substring(p_s_h1_keywords)
					.indexOf("</h1></div>")
					+ "</h1></div>".length();
			int p_h1 = p_s_h1_keywords + p_e_h1_keywords;
			String keywords_dnamec = "<div class=\"dname c\">";
			int p_s_div = 0;
			int p_e_div = 0;
			if (html_karte.substring(p_h1).startsWith(keywords_dnamec)) {
				p_s_div = html_karte.substring(p_h1).indexOf("</div>")
						+ "</div>".length() + p_h1;
				p_e_div = html_karte.substring(p_s_div).indexOf("</div>")
						+ "</div>".length() + p_s_div;
			} else {
				p_s_div = p_h1;
				p_e_div = html_karte.substring(p_s_div).indexOf("</div>")
						+ "</div>".length() + p_s_div;
			}
			String[] arr_str = html_karte.substring(p_s_div, p_e_div).split(
					"\"");
			String type = arr_str[1];
			infoKarte.setType(type);
		} catch (Exception ex) {
			log.error("parse karte failed!");
		}
		return infoKarte;
	}

	public InfoKarte karte(String d, String c) throws Exception {

		return karte("/karte.php?d=" + d + "&c=" + c);
	}

	public boolean isPlus() {

		// TODO Auto-generated method stub
		return false;
	}

	public InfoVillage getActiveVillage() throws Exception {

		// TODO Auto-generated method stub
		return null;
	}

	public void setActiveVillage(String did) throws Exception {

		// TODO Auto-generated method stub
		
	}

	public void previewAllVillages() throws Exception {

		// TODO Auto-generated method stub
		
	}

}
