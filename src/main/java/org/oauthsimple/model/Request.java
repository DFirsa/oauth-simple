package org.oauthsimple.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.oauthsimple.exceptions.OAuthException;
import org.oauthsimple.utils.MimeUtils;
import org.oauthsimple.utils.Utils;

import com.squareup.mimecraft.FormEncoding;

/**
 * Represents an HTTP Request object
 * 
 * @author Pablo Fernandez
 */
public class Request {
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final String CONTENT_TYPE = "Content-Type";
	public static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String EMPTY_STRING = "";

	private final String url;
	private Verb verb;
	private List<Parameter> queryParams;
	private List<Parameter> bodyParams;
	private Map<String, String> headers;
	private Map<String, File> streamParams;
	private HttpURLConnection connection;
	private String charset;
	private boolean connectionKeepAlive = false;
	private Long connectTimeout = null;
	private Long readTimeout = null;
	private Proxy proxy;

	/**
	 * Creates a new Http Request
	 * 
	 * @param verb
	 *            Http Verb (GET, POST, etc)
	 * @param url
	 *            url with optional querystring parameters.
	 */
	public Request(Verb verb, String url) {
		this.verb = verb;
		this.url = url;
		this.queryParams = new ArrayList<Parameter>();
		this.bodyParams = new ArrayList<Parameter>();
		this.streamParams = new HashMap<String, File>();
		this.headers = new HashMap<String, String>();
	}

	/**
	 * Execute the request and return a {@link Response}
	 * 
	 * @return Http Response
	 * @throws RuntimeException
	 *             if the connection cannot be created.
	 */
	public Response send() throws IOException {
		createConnection();
		return doSend();
	}

	private void createConnection() throws IOException {
		String completeUrl = getCompleteUrl();
		System.out.println("complete url is " + completeUrl);
		if (connection == null) {
			System.setProperty("http.keepAlive", connectionKeepAlive ? "true"
					: "false");
			if (proxy != null) {
				connection = (HttpURLConnection) new URL(completeUrl)
						.openConnection(proxy);
			} else {
				connection = (HttpURLConnection) new URL(completeUrl)
						.openConnection();
			}
		}
	}

	/**
	 * Returns the complete url (host + resource + encoded querystring
	 * parameters).
	 * 
	 * @return the complete url.
	 */
	public String getCompleteUrl() {
		return appendTo(url);
	}

	private String appendTo(String url) {
		return Utils.appQueryString(url, queryParams);
	}

	private Response doSend() throws IOException {
		connection.setRequestMethod(this.verb.name());
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setDefaultUseCaches(false);
		if (connectTimeout != null) {
			connection.setConnectTimeout(connectTimeout.intValue());
		}
		if (readTimeout != null) {
			connection.setReadTimeout(readTimeout.intValue());
		}
		addHeaders(connection);
		if (Verb.POST.equals(this.verb) || Verb.PUT.equals(this.verb)) {
			addBody(connection);

		}
		return new Response(connection);
	}

	private void addHeaders(HttpURLConnection conn) {
		for (String key : headers.keySet())
			conn.setRequestProperty(key, headers.get(key));
	}

	private void addBody(HttpURLConnection conn) throws IOException {
		if (isMultipartContent()) {
			addMultipartBody(conn);
		} else {
			addFormEncodedBody(conn);
		}
	}

	private void addMultipartBody(HttpURLConnection conn) throws IOException {
		String boundary = MultiPartOutputStream.createBoundary();
		String contentType = MultiPartOutputStream.getContentType(boundary);
		// 首先必须写入CONTENT_TYPE
		conn.setRequestProperty(CONTENT_TYPE, contentType);
		MultiPartOutputStream mos = new MultiPartOutputStream(
				conn.getOutputStream(), boundary);
		for (String key : streamParams.keySet()) {
			File file = streamParams.get(key);
			String mimeType = MimeUtils.getMimeTypeFromPath(file.getPath());
			mos.writeFile(key, mimeType, file);
			System.out.println("name=" + key + " file=" + file.getPath());
		}
		for (Parameter param : bodyParams) {
			mos.writeField(param.getName(), param.getValue());
		}
		// 调用close是必须的，写入结束标志
		mos.close();
	}

	private void addFormEncodedBody(HttpURLConnection conn) {
		// 首先必须写入CONTENT_TYPE
		if (conn.getRequestProperty(CONTENT_TYPE) == null) {
			conn.setRequestProperty(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
		}
		if (!bodyParams.isEmpty()) {
			FormEncoding.Builder builder = new FormEncoding.Builder();
			for (Parameter param : bodyParams) {
				builder.add(param.getName(), param.getValue());
			}
			ByteArrayOutputStream bos = null;
			try {
				bos = new ByteArrayOutputStream();
				builder.build().writeBodyTo(bos);
				bos.writeTo(conn.getOutputStream());
				bos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				Utils.forceClose(bos);
			}
		}
	}

	/**
	 * Add an HTTP Header to the Request
	 * 
	 * @param key
	 *            the header name
	 * @param value
	 *            the header value
	 */
	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}

	public void addStreamParamter(String key, File file) {
		this.streamParams.put(key, file);
	}

	/**
	 * Add a body Parameter (for POST/ PUT Requests)
	 * 
	 * @param key
	 *            the parameter name
	 * @param value
	 *            the parameter value
	 */
	public void addBodyParameter(String key, String value) {
		this.bodyParams.add(new Parameter(key, value));
	}

	public void addBodyParameter(Parameter param) {
		this.bodyParams.add(param);
	}

	/**
	 * Add a QueryString parameter
	 * 
	 * @param key
	 *            the parameter name
	 * @param value
	 *            the parameter value
	 */
	public void addQueryStringParameter(String key, String value) {
		this.queryParams.add(new Parameter(key, value));
	}

	public void addQueryStringParameter(Parameter param) {
		this.queryParams.add(param);
	}

	/**
	 * Get a {@link ParameterList} with the query string parameters.
	 * 
	 * @return a {@link ParameterList} containing the query string parameters.
	 * @throws OAuthException
	 *             if the request URL is not valid.
	 */
	public List<Parameter> getQueryStringParams() {
		List<Parameter> params = new ArrayList<Parameter>();
		params.addAll(queryParams);
		Utils.extractQueryString(url, params);
		return params;
	}

	/**
	 * Obtains a {@link ParameterList} of the body parameters.
	 * 
	 * @return a {@link ParameterList}containing the body parameters.
	 */
	public List<Parameter> getBodyParams() {
		return bodyParams;
	}

	public boolean isMultipartContent() {
		return !isFormEncodedContent();
	}

	public boolean isFormEncodedContent() {
		return streamParams.isEmpty();
	}

	/**
	 * Obtains the URL of the HTTP Request.
	 * 
	 * @return the original URL of the HTTP Request
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Returns the URL without the port and the query string part.
	 * 
	 * @return the OAuth-sanitized URL
	 */
	public String getSanitizedUrl() {
		return url.replaceAll("\\?.*", "").replace("\\:\\d{4}", "");
	}

	/**
	 * Returns the body of the request
	 * 
	 * @return form encoded string
	 * @throws OAuthException
	 *             if the charset chosen is not supported
	 */
	public String getBodyContents() {
		try {
			return new String(getByteBodyContents(), getCharset());
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Unsupported Charset: " + charset, uee);
		}
	}

	byte[] getByteBodyContents() {
		return new byte[4096];
	}

	/**
	 * Returns the HTTP Verb
	 * 
	 * @return the verb
	 */
	public Verb getVerb() {
		return this.verb;
	}

	/**
	 * Returns the connection headers as a {@link Map}
	 * 
	 * @return map of headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * Returns the connection charset. Defaults to {@link Charset}
	 * defaultCharset if not set
	 * 
	 * @return charset
	 */
	public String getCharset() {
		return charset == null ? Charset.defaultCharset().name() : charset;
	}

	/**
	 * Sets the connect timeout for the underlying {@link HttpURLConnection}
	 * 
	 * @param duration
	 *            duration of the timeout
	 * 
	 * @param unit
	 *            unit of time (milliseconds, seconds, etc)
	 */
	public void setConnectTimeout(int duration, TimeUnit unit) {
		this.connectTimeout = unit.toMillis(duration);
	}

	/**
	 * Sets the read timeout for the underlying {@link HttpURLConnection}
	 * 
	 * @param duration
	 *            duration of the timeout
	 * 
	 * @param unit
	 *            unit of time (milliseconds, seconds, etc)
	 */
	public void setReadTimeout(int duration, TimeUnit unit) {
		this.readTimeout = unit.toMillis(duration);
	}

	/**
	 * Set the charset of the body of the request
	 * 
	 * @param charsetName
	 *            name of the charset of the request
	 */
	public void setCharset(String charsetName) {
		this.charset = charsetName;
	}

	/**
	 * Sets whether the underlying Http Connection is persistent or not.
	 * 
	 * @see http
	 *      ://download.oracle.com/javase/1.5.0/docs/guide/net/http-keepalive
	 *      .html
	 * @param connectionKeepAlive
	 */
	public void setConnectionKeepAlive(boolean connectionKeepAlive) {
		this.connectionKeepAlive = connectionKeepAlive;
	}

	/*
	 * We need this in order to stub the connection object for test cases
	 */
	void setConnection(HttpURLConnection connection) {
		this.connection = connection;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public Proxy getProxy() {
		return this.proxy;
	}

	@Override
	public String toString() {
		return String.format("@Request(%s %s)", getVerb(), getUrl());
	}
}
