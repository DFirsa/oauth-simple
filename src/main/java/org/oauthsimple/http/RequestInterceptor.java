package org.oauthsimple.http;

public abstract class RequestInterceptor {
    public abstract void intercept(Request request);
}