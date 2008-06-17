/*
 * ~ TROBOT ~
 * 
 * $Id: TrobotHtmlCleanerImpl.java 27 2008-05-18 17:54:09Z jhsea3do $
 */

package com.javaws.trobot.task;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlcleaner.HtmlCleaner;

import com.javaws.trobot.Configuration;
import com.javaws.trobot.HtmlUtils;
import com.javaws.trobot.Trobot;

public class BatchPrivateMessageTask extends TrobotTask {

	private static Log log = LogFactory.getLog(BatchPrivateMessageTask.class);

	private static Configuration config = Configuration.getConfiguration();

	/**
	 * HtmlUtils Tool
	 */
	private static HtmlUtils htmlUtils = new HtmlUtils();

	public void send(String username, String subject, String message) {

		Trobot trobot = this.getTrobot();
		try {
			String url_pm = config.getUrl("/nachrichten.php");
			trobot.debug("<<< post url " + url_pm);
			Map<String, String> input_params = new LinkedHashMap<String, String>();
			Map<String, String> header_params = new LinkedHashMap<String, String>();
			input_params.put("c", "ae3");
			input_params.put("an", username);
			input_params.put("be", subject);
			input_params.put("t", "2");
			input_params.put("message", message);
			for (String key : input_params.keySet()) {
				String debug_value = input_params.get(key);
				trobot.debug("<<< post param [key=" + key + ", value="
						+ debug_value + "]",
						config.getCharsets()[Configuration.CHARSET_DEFAULT_ID],
						config.getCharsets()[Configuration.CHARSET_REMOTE_ID]);
				// trobot.debug("<<< post param [key=" + key + ", value="
				// + debug_value + "]", Configuration.CHARSET_DEFAULT,
				// Configuration.CHARSET_REMOTE);
			}
			header_params.put("accept-charset", "utf-8");
			header_params.put("Cookie", trobot.getCookie());
			HttpMethod method_pm = htmlUtils.load(url_pm, input_params,
					header_params, "post");
			String html_pm = htmlUtils.getResponseText(method_pm, config
					.getCharsets()[Configuration.CHARSET_REMOTE_ID]);
			HtmlCleaner hc_pm = new HtmlCleaner(html_pm);
			hc_pm.clean();
			/*
			 * <p class="txt_menue"> <a href="nachrichten.php">收件箱</a> | <a
			 * href="nachrichten.php?t=1">撰写</a> | <a
			 * href="nachrichten.php?t=2">已发送</a></p><p class="c5"><b>AntiSpam:</b>
			 * Please wait 10 minutes then try again</p><form method="post"
			 * action="nachrichten.php" name="msg">
			 */
		} catch (Exception ex) {
			log.error("send pm to user[" + username + "] errors: "
					+ ex.getMessage());
		}
	}

	public void invite(String username) {

		Trobot trobot = this.getTrobot();
		try {
			String url_invite = config.getUrl("/allianz.php");
			trobot.debug("<<< post url " + url_invite);
			Map<String, String> input_params = new LinkedHashMap<String, String>();
			Map<String, String> header_params = new LinkedHashMap<String, String>();
			input_params.put("a", "4");
			input_params.put("o", "4");
			input_params.put("s", "5");
			input_params.put("s1", "ok");
			input_params.put("a_name", username);
			for (String key : input_params.keySet()) {
				String debug_value = input_params.get(key);
				trobot.debug("<<< post param [key=" + key + ", value="
						+ debug_value + "]",
						config.getCharsets()[Configuration.CHARSET_DEFAULT_ID],
						config.getCharsets()[Configuration.CHARSET_REMOTE_ID]);
				// trobot.debug("<<< post param [key=" + key + ", value="
				// + debug_value + "]", Configuration.CHARSET_DEFAULT,
				// Configuration.CHARSET_REMOTE);
			}
			header_params.put("accept-charset", "utf-8");
			header_params.put("Cookie", trobot.getCookie());
			HttpMethod method_invite = htmlUtils.load(url_invite, input_params,
					header_params, "post");
			String html_invite = htmlUtils.getResponseText(method_invite,
					config.getCharsets()[Configuration.CHARSET_REMOTE_ID]);
			HtmlCleaner hc_invite = new HtmlCleaner(html_invite);
			hc_invite.clean();
		} catch (Exception ex) {
			log.error("send invite to user[" + username + "] errors: "
					+ ex.getMessage());
		}
	}

	@Override
	public void run() {

		try {
			Trobot trobot = this.getTrobot();
			if (trobot.login()) {
				@SuppressWarnings("unused")
				String[] az = new String[] { "东方不败", "circle", "zizou",
						"血契-亡灵的复仇", "小白‰", "天狼", "言灵雪", "dodo!!!", "脑震荡的猪",
						"dola", "ytshuye", "克利斯", "黛大人", "blackc", "矿工舍",
						"光枪枪", "0麦风吹雪0", "逍遥小生", "tutuluobo", "noripearl",
						"南征北战", "wsxskm1", "酸菜鱼", "板凳杀手", "gunnium",
						"darkangellcc", "邪恶滴乐乐", "CC 富商", "飘渺随风", "涵羞草",
						"么黑么黑", "绝の爱恋", "emilylove", "倒霉", "天狐", "三砣", "冥域",
						"血契莱德亚特", "深白色の爱", "小宝", "铭ming" };
				String[] uu = new String[] { "小刀", "qiqi6161", "大忽悠", "bbs",
						"曙光天使", "痛心", "daqimouse", "史蒂芬Z", "kensei", "xuan",
						"forkman", "yifufing", "末末LOVE灵儿", "可可99" };
				/*
				 * String[] uu = new String[] {"jhsea3do", "浪漫的猪", "糨糊罐罐" };
				 */
				String s = "总盟整编令";
				String m = "各位兄弟姐妹们，大家好，鉴于西南最近的局势，总盟决定开始整编，把大家团结到一起增强战斗力以及协助防能力"
						+ "，见此PM者，请退盟加入新的团，具体QQ群另行通知，荣耀是强大的，正是因为大家的积极配合而强大，谢谢。\r\n"
						+ "                                          荣耀西南外交：人间地狱";
				int iCount = 0;
				for (String u : uu) {
					// String u = "jhsea3do";
					if (iCount % 2 == 0) {
						this.send("jhsea3do", "qqe" + iCount
								+ System.currentTimeMillis(), "xt" + iCount
								+ System.currentTimeMillis());
						Thread.sleep(10000);
					}
					// String xs = s + "(" + System.currentTimeMillis() + ")";
					// String xm = m + "\r\n\r\n\r\n" + "("
					// + System.currentTimeMillis() + ")";
					this.send(new String(u.getBytes("utf-8"), "iso-8859-1"),
							new String(s.getBytes("utf-8"), "iso-8859-1"),
							new String(m.getBytes("utf-8"), "iso-8859-1"));
					// this.invite(new String(u.getBytes("utf-8"),
					// "iso-8859-1"));
					iCount++;
					Thread.sleep(10000);
				}
			}
			log.info("run task done ;-)");
		} catch (Exception ex) {
			log.error("run task failed...", ex);
		}

	}
}
