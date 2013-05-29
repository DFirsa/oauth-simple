package org.oauthsimple.utils;

import org.oauthsimple.http.Parameter;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Utils {
    static final char QUERY_STRING_SEPARATOR = '?';
    static final String PARAM_SEPARATOR = "&";
    static final String PAIR_SEPARATOR = "=";
    static final String EMPTY_STRING = "";

    public static String asFormUrlEncodedString(List<Parameter> params) {
        if (params == null || params.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Parameter param : params) {
            builder.append('&').append(param.asUrlEncodedPair());
        }
        return builder.toString().substring(1);
    }

    public static String appQueryString(String url, List<Parameter> params) {
        if (isEmpty(url) || params == null || params.isEmpty()) {
            return url;
        }
        String queryString = asFormUrlEncodedString(params);
        if (Utils.isEmpty(queryString)) {
            return url;
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(url);
            boolean hasQuery = url.indexOf(Utils.QUERY_STRING_SEPARATOR) != -1;
            builder.append(hasQuery ? PARAM_SEPARATOR : QUERY_STRING_SEPARATOR);
            builder.append(queryString);
            return builder.toString();
        }
    }

    public static String extractQueryString(String url, List<Parameter> params) {
        if (isEmpty(url) || params == null) {
            return url;
        }
        String cleanUrl = url;
        URL aUrl = null;
        try {
            aUrl = new URL(url);
            String queryString = aUrl.getQuery();
            if (queryString != null && queryString.length() > 0) {
                for (String param : queryString.split(PARAM_SEPARATOR)) {
                    String pair[] = param.split(PAIR_SEPARATOR);
                    String key = OAuthEncoder.decode(pair[0]);
                    String value = pair.length > 1 ? OAuthEncoder.decode(pair[1])
                            : EMPTY_STRING;
                    params.add(new Parameter(key, value));
                }
            }
            URL newUrl = new URL(aUrl.getProtocol(), aUrl.getHost(), aUrl.getPort(), aUrl.getPath());
            cleanUrl = newUrl.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        System.out.println("url is " + url);
        System.out.println("clean url is " + cleanUrl);
        return cleanUrl;
    }

    public static String percentEncode(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
            encoded = value;
            // throw new RuntimeException(ignore);
        }
        StringBuffer buf = new StringBuffer(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7'
                    && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }

    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static <T> String convertToString(Iterable<T> iterable) {
        StringBuilder builder = new StringBuilder();
        Iterator<T> iter = iterable.iterator();
        while (iter.hasNext()) {
            builder.append(", ").append(iter.next().toString());
        }
        return "[" + builder.substring(1) + "]";
    }

    public static <K, V> String convertToString(Map<K, V> map) {
        if (map == null)
            return "";
        if (map.isEmpty())
            return "{}";

        StringBuilder result = new StringBuilder();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.append(String.format(", %s -> %s ", entry.getKey()
                    .toString(), entry.getValue().toString()));
        }
        return "{" + result.substring(1) + "}";
    }

    public static void forceClose(Closeable close) {
        if (close != null) {
            try {
                close.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
