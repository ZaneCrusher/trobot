/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot.htmlcleaner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
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
import com.javaws.trobot.InfoVillage;
import com.javaws.trobot.Trobot;

/**
 * <code>Trobot</code> <a
 * href="http://htmlcleaner.sourceforge.net/">HtmlCleaner Library</a>
 * Implementation
 * 
 * @author jhsea3do
 * 
 */
public class TrobotHtmlCleanerImpl implements Trobot {

	/**
	 * Logger
	 */
	private static Log log = LogFactory.getLog(TrobotHtmlCleanerImpl.class);

	/**
	 * Configuration
	 */
	private static Configuration config = Configuration.getConfiguration();

	/**
	 * HtmlUtils Tool
	 */
	private static HtmlUtils htmlUtils = HtmlUtils.getInstance();

	// set custom browser user-agent
	static {
		htmlUtils.setUserAgent(config.getString("trobot.agent"));
	}

	/**
	 * Constructs a <code>TrobotHtmlCleanerImpl</code> from the config file
	 */
	public TrobotHtmlCleanerImpl() {

		super();
		this.username = config.getSiteUser();
		this.password = config.getSitePass();
		this.debug = config.isDebug();
	}

	/**
	 * Constructs a <code>TrobotHtmlCleanerImpl</code> using the username and
	 * password
	 */
	public TrobotHtmlCleanerImpl(String username, String password) {

		super();
		this.debug = config.isDebug();
	}

	/**
	 * Constructs a <code>TrobotHtmlCleanerImpl</code> from a uri string
	 * <b>Note:</b> this method is deprecated
	 * 
	 * @param uri
	 */
	public TrobotHtmlCleanerImpl(String uri) {

		this();
	}

	private String cookie = null;

	private String username = "no_user";

	private String password = "no_pass";

	private boolean debug = false;

	private String active_village_did = null;

	private String server_time = null;

	private String client_time = null;

	private boolean plus = false;

	private Map<String, InfoVillage> villages = null;

	/**
	 * @see Trobot#getVillages()
	 */
	public Map<String, InfoVillage> getVillages() {

		return villages;
	}

	public String getCookie() {

		return cookie;
	}

	/**
	 * @return Trobot#isPlus()
	 */
	public boolean isPlus() {

		try {
			if (null == this.cookie) {
				if (!this.login()) {
					return false;
				}
			}
			return plus;
		} catch (Exception ex) {
			return false;
		}
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

	protected void debugHeader(HttpMethod method) {

		if (debug) {
			Header[] requestheaders = method.getRequestHeaders();
			for (Header header : requestheaders) {
				debug("<<< request header [name=" + header.getName()
						+ ", value=" + header.getValue() + "]");
			}
			Header[] responseheaders = method.getResponseHeaders();
			for (Header header : responseheaders) {
				debug(">>> response header [name=" + header.getName()
						+ ", value=" + header.getValue() + "]");
			}
		}
	}

	protected void refreshCookie(HttpMethod method) {

		String lastCookie = this.cookie;
		this.cookie = htmlUtils.getCookieText(method);
		if (debug && null != this.cookie && !this.cookie.equals(lastCookie)) {
			debug(">>> refresh cookie = " + cookie);
		}
	}

	protected void addVillage(InfoVillage village) {

		if (null == this.villages) {
			this.villages = new LinkedHashMap<String, InfoVillage>();
		}
		String did = village.getD();
		if (null == did) {
			log.error("village has no did [village=" + village + "]");
			return;
		}
		InfoVillage v = this.villages.get(did);
		if (null != v) {
			v.copy(village);
		} else {
			v = village;
		}
		debug(">>> user village [did=" + v.getD() + ", name=" + v.getName()
				+ "]");
		this.villages.put(did, v);
	}

	public InfoVillage getActiveVillage() {

		if (null != this.active_village_did && null != this.villages) {
			return this.villages.get(this.active_village_did);
		} else {
			return null;
		}
	}

	public void setActiveVillage(String did) throws Exception {

		if (null != this.villages) {
			InfoVillage v = this.villages.get(did);
			if (null != v) {
				Map<String, String> header_params = new LinkedHashMap<String, String>();
				header_params.put("Cookie", this.cookie);
				String url_village = config.getUrl("/dorf1.php?newdid=" + did);
				debug("<<< active village [did=" + v.getD() + ", name="
						+ v.getName() + "]");
				HttpMethod method_village = htmlUtils.load(url_village,
						header_params);
				String html_village = htmlUtils.getResponseText(method_village);
				this.active_village_did = did;
				HtmlCleaner hc_village = new HtmlCleaner(html_village);
				hc_village.clean();
				String xml_village = hc_village.getXmlAsString();
				// debug(">>> village response " + xml_allianz);
				Document page_village = htmlUtils
						.getResponseDocument(xml_village);
				InfoVillage village = loadActiveVillage(page_village);
				v.copy(village);
			}
		}
	}

	public void previewAllVillages() throws Exception {

		for (String did : this.getVillages().keySet()) {
			this.setActiveVillage(did);
		}
		InfoVillage activeVillage = this.getActiveVillage();
		debug(">>> active village " + activeVillage.getName());
	}

	@SuppressWarnings("unchecked")
	protected void loadVillages(Document page) throws Exception {

		Element elem_lright1 = (Element) page
				.selectSingleNode("//div[@id=\"lright1\"]");
		if (null != elem_lright1) {
			// multi villages
			// debug(">>> elem_lright1 = " + elem_lright1.asXML());
			List<Element> elem_village_trs = elem_lright1
					.selectNodes("//table[@class=\"f10\"]/tbody/tr");
			for (Element elem_village_tr : elem_village_trs) {
				// debug(">>> elem_village_tr = " +
				// elem_village_tr.asXML());
				Element elem_nbr_td = null;
				Element elem_right_td = null;
				List<Element> elem_village_tr_tds = elem_village_tr
						.elements("td");
				// elem_nbr_td = (Element) elem_village_tr
				// .selectSingleNode("/td[@class=\"nbr\"]");
				for (Element elem_village_tr_td : elem_village_tr_tds) {
					String attr_td_class = elem_village_tr_td
							.attributeValue("class");
					if ("nbr".equals(attr_td_class)) {
						elem_nbr_td = elem_village_tr_td;
					} else if ("right".equals(attr_td_class)) {
						elem_right_td = elem_village_tr_td;
					}
				}
				if (null != elem_nbr_td && null != elem_right_td) {
					// debug(">>> elem_nbr_td = " + elem_nbr_td.asXML());
					Element elem_a = elem_nbr_td.element("a");
					if (null != elem_a) {
						String v_did = elem_a.attributeValue("href")
								.replaceAll("\\?newdid=", "");
						String v_name = elem_a.getTextTrim();
						InfoVillage village = new InfoVillage();
						village.setD(v_did);
						village.setName(v_name);
						if ("active_vl".equals(elem_a.attributeValue("class"))) {
							this.active_village_did = v_did;
							// InfoVillage v = this.loadActiveVillage(page);
							// village.copy(v);
						}
						this.addVillage(village);
					}
					// debug(">>> elem_right_td = " +
					// elem_right_td.asXML());
				}
			}
		} else {
			// single village
			this.active_village_did = "single";
			InfoVillage village = this.loadActiveVillage(page);
			village.setD(this.active_village_did);
			this.addVillage(village);
		}

	}

	protected void loadServerTiming(Document page) throws Exception {

		Element elem_ltime_tp1 = (Element) page
				.selectSingleNode("//div[@id=\"ltime\"]/span[@id=\"tp1\"]");
		this.server_time = elem_ltime_tp1.getTextTrim();
		this.client_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		debug(">>> server time " + this.server_time);
		debug("<<< client time " + this.client_time);
	}

	protected void checkPlus(Document page) throws Exception {

		Element elem_lleft_logo = (Element) page
				.selectSingleNode("//div[@id=\"lleft\"]/a/img[@class=\"logo\"]");
		String logo_src = elem_lleft_logo.attributeValue("src");
		debug(">>> travian logo " + logo_src);
		if (logo_src.endsWith("travian1.gif")) {
			this.plus = true;
		} else { // travian0.gif
			this.plus = false;
		}
		debug("<<< travian plus " + this.plus);
	}

	@SuppressWarnings("unchecked")
	protected InfoVillage loadActiveVillage(Document page) throws Exception {

		InfoVillage village = new InfoVillage();
		// Element elem_lmid1 = (Element)
		// page.selectSingleNode("//div[@id=\"lmid1\"]");
		Element elem_dname = (Element) page
				.selectSingleNode("//div[@id=\"lmid2\"]/div[@class=\"dname\"]/h1");
		String v_name = elem_dname.getTextTrim();
		village.setName(v_name);
		debug(">>> user village [name=" + village.getName() + "]");
		List<Element> elem_rs_ic_trs = page
				.selectNodes("//div[@id=\"lrpr\"]/table/tbody/tr");
		if (null != elem_rs_ic_trs
				&& elem_rs_ic_trs.size() == InfoVillage.RS_TYPES.length) {
			for (int i = 0; i < elem_rs_ic_trs.size(); i++) {
				Element elem_rs_ic_td = (Element) elem_rs_ic_trs.get(i)
						.elements("td").get(2);
				String rs_ic = elem_rs_ic_td.element("b").getTextTrim();
				if (!rs_ic.matches("[0-9]$")) {
					rs_ic = rs_ic.replaceAll(".$", "");
				}
				village.setRsIncrease(i, rs_ic);
			}
		}
		// "//div[@id=\"lres0\"]/table/tbody/tr/td[@id]"
		List<Element> elem_rs_tds = page
				.selectNodes("//div[@id=\"lres0\"]//td[@id][@title]");
		if (null != elem_rs_tds
				&& elem_rs_tds.size() == InfoVillage.RS_TYPES.length) {
			for (int i = 0; i < elem_rs_ic_trs.size(); i++) {
				Element elem_rs_td = elem_rs_tds.get(i);
				String[] rs_ac_and_lm = elem_rs_td.getTextTrim().split("/");
				village.setRsActual(i, rs_ac_and_lm[0]);
				village.setRsLimit(i, rs_ac_and_lm[1]);
			}
		}
		for (int i = 0; i < InfoVillage.RS_TYPES.length; i++) {

			debug(">>> " + InfoVillage.RS_TYPES[i].toLowerCase()
					+ " status [increase=" + village.getRsIncrease(i)
					+ ", actual=" + village.getRsActual(i) + ", limit="
					+ village.getRsLimit(i) + ", rate="
					+ village.getRsRateString(i) + "]");
		}
		return village;
	}

	@SuppressWarnings("unchecked")
	public boolean login() throws Exception {

		boolean login_successful = false;
		try {
			String url_login = config.getUrl("/login.php");
			debug("<<< login from " + url_login);
			debug("<<< login username " + this.username,
					Configuration.CHARSET_DEFAULT, Configuration.CHARSET_REMOTE);
			debug("<<< login password " + this.password.replaceAll(".", "*"),
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
			String url_action = "/" + form_login.attributeValue("action");
			debug("<<< post url " + url_action);
			// debug(">>> login form " + form_login.asXML());
			// debug(">>> login response " + page_login.asXML());
			List<Element> elem_inputs = form_login.selectNodes("//input");
			Map<String, String> input_params = new LinkedHashMap<String, String>();
			Map<String, String> header_params = new LinkedHashMap<String, String>();
			for (Element elem_input : elem_inputs) {
				// debug(">>> input field " + elem_input.asXML());
				String key = elem_input.attributeValue("name");
				String value = elem_input.attributeValue("value");
				String debug_value = value;
				String input_type = elem_input.attributeValue("type");
				if ("text".equals(input_type) || "password".equals(input_type)) {
					String input_class = elem_input.attributeValue("class");
					if ("fm fm110".equals(input_class)) {
						if (input_type.equals("text")) {
							value = this.username;
							debug_value = value;
						} else if (input_type.equals("password")) {
							value = this.password;
							debug_value = value.replaceAll(".", "*");
						}
					}
				}
				debug("<<< post param [key=" + key + ", value=" + debug_value
						+ "]", Configuration.CHARSET_DEFAULT,
						Configuration.CHARSET_REMOTE);
				input_params.put(key, value);
			}
			HttpMethod method_submit = htmlUtils.load(
					config.getUrl(url_action), input_params, header_params,
					"post");
			String html_submit = htmlUtils.getResponseText(method_submit);
			HtmlCleaner hc_submit = new HtmlCleaner(html_submit);
			hc_submit.clean();
			String xml_submit = hc_submit.getXmlAsString();
			Document page_submit = htmlUtils.getResponseDocument(xml_submit);
			// debug(">>> submit response = " + xml_submit);
			// this.debugHeader(method_submit);
			this.refreshCookie(method_submit);
			this.checkPlus(page_submit);
			this.loadServerTiming(page_submit);
			this.loadVillages(page_submit);
			// this.previewAllVillages();
			login_successful = true;
		} catch (Exception ex) {
			this.cookie = null;
			login_successful = false;
			log.error("login failed: " + ex.getMessage(), ex);
		}
		return login_successful;
	}

	public InfoAllianz allianz(Document page) throws Exception {

		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public InfoAllianz allianz(String uri) throws Exception {

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
			String url_allianz = config.getUrl(uri);
			HttpMethod method_allianz = htmlUtils.load(url_allianz,
					header_params);
			String html_allianz = htmlUtils.getResponseText(method_allianz);
			HtmlCleaner hc_allianz = new HtmlCleaner(html_allianz);
			hc_allianz.clean();
			String xml_allianz = hc_allianz.getXmlAsString();
			// debug(">>> allianz response " + xml_allianz);
			Document page_allianz = htmlUtils.getResponseDocument(xml_allianz);
			List<Element> elem_tbgs = page_allianz
					.selectNodes("//table[@class=\"tbg\"]");
			Element elem_tbg = elem_tbgs.get(1);
			Document page_tbg = htmlUtils.getResponseDocument(elem_tbg.asXML());
			// debug(">>> page tbg " + page_tbg.asXML());
			List<Element> elem_trs = page_tbg.selectNodes("//table/tbody/tr");
			for (Element elem_tr : elem_trs) {
				// debug(">>> tr element " + elem_tr.asXML());
				if (!"rbg".equals(elem_tr.attributeValue("class"))) {
					List<Element> elem_tds = elem_tr.elements("td");
					String user_no = "";
					String user_name = "";
					String user_spieler_uri = "";
					String user_population = "";
					String user_villages = "";
					String user_status = "";
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
						case 4:
							List<Element> elem_imgs = elem_td.elements("img");
							if (null != elem_imgs && !elem_imgs.isEmpty()) {
								Element elem_img = (Element) (elem_imgs.get(0));
								String elem_img_src = elem_img
										.attributeValue("src");
								user_status = elem_img_src.replaceAll(
										"img/un/a/b", "").replaceAll("\\.gif",
										"");
							}
						}
						index++;
					}
					debug(">>> user_spieler_uri " + user_spieler_uri);
					InfoSpieler infoSpieler = spieler(user_spieler_uri);
					infoSpieler.setAllianzRank(user_no);
					infoSpieler.setUsername(user_name);
					infoSpieler
							.setPopulation(Integer.parseInt(user_population));
					infoSpieler.setVillages(Integer.parseInt(user_villages));
					infoSpieler.setStatus(user_status);
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

	public InfoAllianz allianz(int aid) throws Exception {

		return allianz("/allianz.php?aid=" + aid);
	}

	public InfoKarte karte(Document page) throws Exception {

		// TODO Auto-generated method stub
		return null;
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

	public InfoSpieler spieler(Document page) throws Exception {

		return null;
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
			infoSpieler = new InfoSpieler();
			infoSpieler.setUri(uri);
			Map<String, String> header_params = new LinkedHashMap<String, String>();
			header_params.put("Cookie", this.cookie);
			String url_spieler = config.getUrl(uri);
			HttpMethod method_spieler = htmlUtils.load(url_spieler,
					header_params);
			String html_spieler = htmlUtils.getResponseText(method_spieler);
			HtmlCleaner hc_spieler = new HtmlCleaner(html_spieler);
			hc_spieler.clean();
			String xml_spieler = hc_spieler.getXmlAsString();
			// debug(">>> spieler response " + xml_spieler);
			Document page_spieler = htmlUtils.getResponseDocument(xml_spieler);
			List<Element> elem_tbgs = page_spieler
					.selectNodes("//table[@class=\"tbg\"]");
			Element elem_tbg = elem_tbgs.get(1);
			Document page_tbg = htmlUtils.getResponseDocument(elem_tbg.asXML());
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
}
