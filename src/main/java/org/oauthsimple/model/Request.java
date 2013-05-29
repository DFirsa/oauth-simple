package org.oauthsimple.model;

import com.squareup.mimecraft.FormEncoding;
import org.oauthsimple.exceptions.OAuthException;
import org.oauthsimple.utils.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    private static final String ENCODING_DEFAULT = "UTF-8";

    private static final int CONNECT_TIMEOUT = 10 * 1000;
    private static final int READ_TIMEOUT = 10 * 1000;

    private final String originalUrl;
    private String url;
    private Verb verb;
    private List<Parameter> params;
    private Map<String, String> headers;
    private Map<String, FileBody> bodys;
    private HttpURLConnection connection;
    private String charset;
    private boolean keepAlive;
    private long connectTimeout;
    private long readTimeout;
    private Proxy proxy;

    /**
     * Creates a new Http Request
     *
     * @param verb Http Verb (GET, POST, etc)
     * @param url  url with optional querystring parameters.
     */
    public Request(Verb verb, String url) {
        this.verb = verb;
        this.originalUrl = url;
        this.params = new ArrayList<Parameter>();
        this.bodys = new HashMap<String, FileBody>();
        this.headers = new HashMap<String, String>();
        this.charset = ENCODING_DEFAULT;
        this.keepAlive = false;
        this.connectTimeout = CONNECT_TIMEOUT;
        this.readTimeout = READ_TIMEOUT;
        this.proxy = null;
        this.url = Utils.extractQueryString(originalUrl, this.params);
    }

    /**
     * Execute the request and return a {@link Response}
     *
     * @return Http Response
     * @throws RuntimeException if the connection cannot be created.
     */
    public Response send() throws IOException {
        createConnection();
        return doSend();
    }

    private void createConnection() throws IOException {
        String completeUrl = getCompleteUrl();
        System.out.println("complete url is " + completeUrl);
        if (connection == null) {
            System.setProperty("http.keepAlive", keepAlive ? "true"
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
        return Utils.appQueryString(url, params);
    }

    private Response doSend() throws IOException {
        connection.setRequestMethod(this.verb.name());
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);
        connection.setConnectTimeout((int) connectTimeout);
        connection.setReadTimeout((int) readTimeout);
        addHeaders(connection);
        if (Verb.POST.equals(this.verb) || Verb.PUT.equals(this.verb)) {
            connection.setDoOutput(true);
            addBody(connection);
        } else {
            connection.setDoOutput(false);
        }

        System.out.println("doSend() verb=" + verb);
        System.out.println("doSend() originalUrl=" + originalUrl);
        System.out.println("doSend() url=" + url);
        System.out.println("doSend() params=" + params);

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
        // 首先必须写入CONTENT_TYPE
        SimpleMultipart multipart = new SimpleMultipart();
        for (String key : bodys.keySet()) {
            FileBody fileBody = bodys.get(key);
            multipart.addPart(key, fileBody.fileName, fileBody.inputStream, fileBody.contentType);
            System.out.println("name=" + key + " fileBody=" + fileBody);
        }
        for (Parameter param : params) {
            multipart.addPart(param.getName(), param.getValue());
        }
        conn.setRequestProperty(CONTENT_TYPE, multipart.getContentType());
        multipart.writeTo(conn.getOutputStream());
    }

    private void addFormEncodedBody(HttpURLConnection conn) {
        // 首先必须写入CONTENT_TYPE
        if (conn.getRequestProperty(CONTENT_TYPE) == null) {
            conn.setRequestProperty(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        }
        if (!params.isEmpty()) {
            FormEncoding.Builder builder = new FormEncoding.Builder();
            for (Parameter param : params) {
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
     * @param key   the header name
     * @param value the header value
     */
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void addBody(String key, File file) throws FileNotFoundException {
        FileBody fileBody = new FileBody(key, file);
        this.bodys.put(key, fileBody);
    }

    public void addParameter(String key, String value) {
        this.params.add(new Parameter(key, value));
    }

    public void addParameter(Parameter param) {
        this.params.add(param);
    }

    public void addParameters(Map<String, String> params) {
        for (String key : params.keySet()) {
            this.params.add(new Parameter(key, params.get(key)));
        }
    }

    /**
     * Obtains the body parameters.
     *
     * @return containing the body parameters.
     */
    public List<Parameter> getParameters() {
        return params;
    }

    public boolean isMultipartContent() {
        return !isFormEncodedContent();
    }

    public boolean isFormEncodedContent() {
        return bodys.isEmpty();
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
     * @throws OAuthException if the charset chosen is not supported
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
     * @param duration duration of the timeout
     * @param unit     unit of time (milliseconds, seconds, etc)
     */
    public void setConnectTimeout(int duration, TimeUnit unit) {
        this.connectTimeout = unit.toMillis(duration);
    }

    /**
     * Sets the read timeout for the underlying {@link HttpURLConnection}
     *
     * @param duration duration of the timeout
     * @param unit     unit of time (milliseconds, seconds, etc)
     */
    public void setReadTimeout(int duration, TimeUnit unit) {
        this.readTimeout = unit.toMillis(duration);
    }

    /**
     * Set the charset of the body of the request
     *
     * @param charsetName name of the charset of the request
     */
    public void setCharset(String charsetName) {
        this.charset = charsetName;
    }

    /**
     * Sets whether the underlying Http Connection is persistent or not.
     */
    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
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

    @Override
    public String toString() {
        return String.format("@Request(%s %s)", getVerb(), getUrl());
    }
}
