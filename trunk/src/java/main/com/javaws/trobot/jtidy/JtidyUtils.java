/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot.jtidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.tidy.Tidy;

import com.javaws.trobot.Configuration;

public class JtidyUtils {

	private static Log log = LogFactory.getLog(JtidyUtils.class);

	private static JtidyUtils self = new JtidyUtils();

	public static JtidyUtils getInstance() {
		return self;
	}

	/**
	 * tidy
	 * 
	 * @param htmlContents
	 * @param charset
	 * @return
	 */
	public String tidy(String htmlText) {
		String tidyText = null;
		try {
			Tidy tidy = this.getTidy();
			tidy.setInputEncoding(Configuration.CHARSET_REMOTE);
			tidy.setOutputEncoding(Configuration.CHARSET_DEFAULT);
			InputStream is = new ByteArrayInputStream(htmlText
					.getBytes(Configuration.CHARSET_REMOTE));
			OutputStream os = new ByteArrayOutputStream();
			tidy.parseDOM(is, os);
			tidyText = os.toString();
		} catch (Exception ex) {
			log.error("tidy document failed..", ex);
		}
		return tidyText;
	}

	/**
	 * get tidy
	 * 
	 * @return
	 */
	public Tidy getTidy() {
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);
		tidy.setIndentContent(true);
		tidy.setSmartIndent(true);
		tidy.setIndentAttributes(false);
		tidy.setWraplen(1024);
		// output type XHTML
		// tidy.setXHTML(true);
		tidy.setXmlOut(true);
		tidy.setTidyMark(false);
		tidy.setErrout(new PrintWriter(System.err));
		return tidy;
	}
}
