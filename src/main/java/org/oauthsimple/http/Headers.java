package org.oauthsimple.http;

import org.oauthsimple.utils.HttpDate;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Headers {
	private Date servedDate;
	private Date lastModified;
	private Date expires;
	private String cacheControl;
	private boolean noCache;
	private int maxAgeSeconds = -1;
	private int sMaxAgeSeconds = -1;
	private String etag;
	private int ageSeconds = -1;
	private String contentEncoding;
	private String transferEncoding;
	private int contentLength = -1;
	private String contentType;
	private List<String> rawCookies;
	private final Map<String, List<String>> rawHeaders;

	public Headers(Map<String, List<String>> rawHeaders) {
		this.rawHeaders = rawHeaders;

		for (String key : rawHeaders.keySet()) {
			String fieldName = key;
			String value = rawHeaders.get(key).get(0);
			if (CACHE_CONTROL.equalsIgnoreCase(fieldName)) {
				cacheControl = value;
			} else if (DATE.equalsIgnoreCase(fieldName)) {
				servedDate = HttpDate.parse(value);
			} else if (EXPIRES.equalsIgnoreCase(fieldName)) {
				expires = HttpDate.parse(value);
			} else if (LAST_MODIFIED.equalsIgnoreCase(fieldName)) {
				lastModified = HttpDate.parse(value);
			} else if (ETAG.equalsIgnoreCase(fieldName)) {
				etag = value;
			} else if (PRAGMA.equalsIgnoreCase(fieldName)) {
				if ("no-cache".equalsIgnoreCase(value)) {
					noCache = true;
				}
			} else if (SET_COOKIE.equals(fieldName)) {
				rawCookies = rawHeaders.get(fieldName);
			} else if ("Age".equalsIgnoreCase(fieldName)) {
				ageSeconds = parseSeconds(value);
			} else if (CONTENT_TYPE.equalsIgnoreCase(fieldName)) {
				contentType = value;
			} else if (CONTENT_ENCODING.equalsIgnoreCase(fieldName)) {
				contentEncoding = value;
			} else if (TRANSFER_ENCODING.equalsIgnoreCase(fieldName)) {
				transferEncoding = value;
			} else if (CONTENT_LENGTH.equalsIgnoreCase(fieldName)) {
				contentLength = parseInt(value);
			}
		}
	}

	private static int parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ignored) {
			return 0;
		}
	}

	private static int parseSeconds(String value) {
		try {
			long seconds = Long.parseLong(value);
			if (seconds > Integer.MAX_VALUE) {
				return Integer.MAX_VALUE;
			} else if (seconds < 0) {
				return 0;
			} else {
				return (int) seconds;
			}
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public Date getServedDate() {
		return servedDate;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public Date getExpires() {
		return expires;
	}

	public String getCacheControl() {
		return cacheControl;
	}

	public boolean isNoCache() {
		return noCache;
	}

	public int getMaxAgeSeconds() {
		return maxAgeSeconds;
	}

	public int getsMaxAgeSeconds() {
		return sMaxAgeSeconds;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public int getAgeSeconds() {
		return ageSeconds;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public String getTransferEncoding() {
		return transferEncoding;
	}

	public int getContentLength() {
		return contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public List<String> getRawCookies() {
		return rawCookies;
	}

	public int size() {
		return rawHeaders.size();
	}

	public String getHeader(String filedName) {
		List<String> headers = rawHeaders.get(filedName);
		if (headers == null || headers.isEmpty()) {
			return null;
		}
		return rawHeaders.get(filedName).get(0);
	}

	public int getHeaderInt(String filedName, int defaultValue) {
		List<String> headers = rawHeaders.get(filedName);
		if (headers == null || headers.isEmpty()) {
			return defaultValue;
		}
		String value = rawHeaders.get(filedName).get(0);
		try {
			return Integer.valueOf(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static final String ACCEPT = "Accept";
	public static final String ACCEPT_CHARSET = "Accept-Charset";
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	public static final String AUTHORIZATION = "Authorization";
	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String CONTENT_ENCODING = "Content-Encoding";
	public static final String CONTENT_LANGUAGE = "Content-Language";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_LOCATION = "Content-Location";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String DATE = "Date";
	public static final String ETAG = "ETag";
	public static final String EXPIRES = "Expires";
	public static final String HOST = "Host";
	public static final String IF_MATCH = "If-Match";
	public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	public static final String IF_NONE_MATCH = "If-None-Match";
	public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	public static final String LAST_MODIFIED = "Last-Modified";
	public static final String LOCATION = "Location";
	public static final String PRAGMA = "Pragma";
	public static final String TRANSFER_ENCODING = "Transfer-Encoding";
	public static final String USER_AGENT = "User-Agent";
	public static final String VARY = "Vary";
	public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
	public static final String COOKIE = "Cookie";
	public static final String SET_COOKIE = "Set-Cookie";
}
