package org.oauthsimple.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.util.Map;

import org.oauthsimple.exceptions.OAuthException;
import org.oauthsimple.utils.StreamUtils;

/**
 * Represents an HTTP Response.
 * 
 * @author Pablo Fernandez
 */
public class Response {
	private int code;
	private String message;
	private String body;
	private int contentLength;
	private String contentType;
	private InputStream stream;
	private Headers headers;

	Response(HttpURLConnection connection) throws IOException {
		try {
			connection.connect();
			code = connection.getResponseCode();
			message = connection.getResponseMessage();
			contentLength = connection.getContentLength();
			contentType = connection.getContentType();
			headers = new Headers(connection.getHeaderFields());
			stream = isSuccessful() ? connection.getInputStream() : connection
					.getErrorStream();
		} catch (UnknownHostException e) {
			throw new OAuthException(
					"The IP address of a host could not be determined.", e);
		}
	}

	private String parseBodyContents() {
		body = StreamUtils.getStreamContents(getInputStream());
		return body;
	}

	public boolean isSuccessful() {
		return getCode() >= 200 && getCode() < 400;
	}

	/**
	 * Obtains the HTTP Response body
	 * 
	 * @return response body
	 */
	public String getBody() {
		return body != null ? body : parseBodyContents();
	}

	/**
	 * Obtains the meaningful stream of the HttpUrlConnection, either
	 * inputStream or errorInputStream, depending on the status code
	 * 
	 * @return input stream / error stream
	 */
	public InputStream getInputStream() {
		return stream;
	}

	/**
	 * Obtains the HTTP status code
	 * 
	 * @return the status code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Obtains the HTTP status message. Returns <code>null</code> if the message
	 * can not be discerned from the response (not valid HTTP)
	 * 
	 * @return the status message
	 */
	public String getMessage() {
		return message;
	}

	public int getContentLength() {
		return contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	/**
	 * Obtains a {@link Map} containing the HTTP Response Headers
	 * 
	 * @return headers
	 */
	public Headers getHeaders() {
		return headers;
	}

	/**
	 * Obtains a single HTTP Header value, or null if undefined
	 * 
	 * @param name
	 *            the header name.
	 * 
	 * @return header value or null.
	 */
	public String getHeader(String name) {
		return headers.getHeader(name);
	}

}