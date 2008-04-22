/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.w3c.tidy.Tidy;

/**
 * 
 * @author jhsea3do
 * 
 */
public class HtmlUtils {

	private static Log log = LogFactory.getLog(HtmlUtils.class);

	private static HtmlUtils self = new HtmlUtils();

	public static HtmlUtils getInstance() {
		return self;
	}

	private HttpClient httpClient = new HttpClient();

	public HttpMethod load(String url) {
		return load(url, null);
	}

	public HttpMethod load(String url, Map<String, String> headers) {
		HttpMethod method = null;
		try {
			GetMethod getMethod = new GetMethod(url);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			if (null != headers) {
				for (String headerName : headers.keySet()) {
					getMethod.setRequestHeader(headerName, headers
							.get(headerName));
				}
			}
			int retval = httpClient.executeMethod(getMethod);
			if (retval != HttpStatus.SC_OK) {
				throw new Exception("http request failed: "
						+ getMethod.getStatusLine());
			}
			method = getMethod;
		} catch (Exception ex) {
			log.error("load page failed " + ex.getMessage());
			method = null;
		}
		return method;
	}

	public HttpMethod load(String url, Map<String, String> params,
			Map<String, String> headers, String methodType) {
		HttpMethod method = null;
		try {
			if ("POST".equalsIgnoreCase(methodType)) {
				PostMethod postMethod = new PostMethod(url);
				for (String key : params.keySet()) {
					String value = params.get(key);
					log.debug("add parameter: key=" + key + ",value=" + value);
					postMethod.addParameter(key, value);
				}
				postMethod.getParams().setParameter(
						HttpMethodParams.RETRY_HANDLER,
						new DefaultHttpMethodRetryHandler());
				int retval = httpClient.executeMethod(postMethod);
				if (retval != HttpStatus.SC_OK) {
					throw new Exception("http request failed: "
							+ postMethod.getStatusLine());
				}
				method = postMethod;
			} else {
				GetMethod getMethod = new GetMethod(url);
				getMethod.getParams().setParameter(
						HttpMethodParams.RETRY_HANDLER,
						new DefaultHttpMethodRetryHandler());
				int retval = httpClient.executeMethod(getMethod);
				if (retval != HttpStatus.SC_OK) {
					throw new Exception("http request failed: "
							+ getMethod.getStatusLine());
				}
				method = getMethod;
			}
		} catch (Exception ex) {
			log.error("load page failed " + ex.getMessage());
			method = null;
		}
		return method;
	}

	public String getResponseText(HttpMethod method) {
		String responseText = null;
		try {
			InputStreamReader reader = new InputStreamReader(method
					.getResponseBodyAsStream(), Configuration.CHARSET_REMOTE);
			BufferedReader br = new BufferedReader(reader);
			StringBuffer sb = new StringBuffer();
			String nextLine;
			while ((nextLine = br.readLine()) != null) {
				sb.append(nextLine).append(Configuration.LINE_SEPARATOR);
			}
			responseText = sb.toString();
			// responseText = method.getResponseBodyAsString();
		} catch (Exception ex) {
			log.error("read response text failed!", ex);
			responseText = null;
		}
		return responseText;
	}

	public Document getResponseDocument(String htmlText) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(tidy(htmlText));
		} catch (Exception ex) {
			log.error("read response document failed!", ex);
			document = null;
		}
		return document;
	}

	public String getCookieText(HttpMethod method) {
		Header[] headers_setcookie = method.getResponseHeaders("Set-Cookie");
		String cookie = null;
		if (null != headers_setcookie) {
			cookie = headers_setcookie[0].getValue();
		}
		return cookie;
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
