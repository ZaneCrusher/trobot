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

public class TrobotHtmlCleanerImpl implements Trobot {

	private static Log log = LogFactory.getLog(TrobotHtmlCleanerImpl.class);

	private static Configuration config = Configuration.getConfiguration();

	private static HtmlUtils htmlUtils = HtmlUtils.getInstance();

	static {
		htmlUtils.setUserAgent(config.getString("trobot.agent"));
	}

	public TrobotHtmlCleanerImpl() {

		super();
		this.username = config.getSiteUser();
		this.password = config.getSitePass();
		this.debug = config.isDebug();
	}

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

	private Map<String, InfoVillage> villages = null;

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

	protected InfoVillage getActiveVillage() {

		if (null != this.active_village_did && null != this.villages) {
			return this.villages.get(this.active_village_did);
		} else {
			return null;
		}
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
						// <a href="?newdid=154477">3.¿À¬˛¥Û÷Ì</a>
						String v_did = elem_a.attributeValue("href")
								.replaceAll("\\?newdid=", "");
						String v_name = elem_a.getTextTrim();
						InfoVillage village = new InfoVillage();
						village.setD(v_did);
						village.setName(v_name);
						if ("active_vl".equals(elem_a.attributeValue("class"))) {
							this.active_village_did = v_did;
							InfoVillage v = this.loadActiveVillage(page);
							village.copy(v);
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

		InfoVillage activeVillage = this.getActiveVillage();
		debug(">>> active village " + activeVillage.getName());
	}

	protected void loadServerTiming(Document page) throws Exception {

		Element elem_ltime_tp1 = (Element) page
				.selectSingleNode("//div[@id=\"ltime\"]/span[@id=\"tp1\"]");
		this.server_time = elem_ltime_tp1.getTextTrim();
		this.client_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		debug(">>> server time " + this.server_time);
		debug("<<< client time " + this.client_time);
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
				String rs_ic = elem_rs_ic_td.element("b").getTextTrim()
						.replaceAll(".$", "");
				village.setRsIncrease(i, rs_ic);
			}
		}
		List<Element> elem_rs_tds = page
				.selectNodes("//div[@id=\"lres0\"]/table/tbody/tr/td[@id]");
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
					+ village.getRsLimit(i) + "]");
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
			this.loadVillages(page_submit);
			this.loadServerTiming(page_submit);
			// debug(">>> submit response = " + xml_submit);
			// this.debugHeader(method_submit);
			this.refreshCookie(method_submit);
			login_successful = true;
		} catch (Exception ex) {
			this.cookie = null;
			login_successful = false;
			log.error("login failed: " + ex.getMessage(), ex);
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
