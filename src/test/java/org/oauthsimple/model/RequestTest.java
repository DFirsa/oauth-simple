package org.oauthsimple.model;

import org.junit.Before;
import org.junit.Test;
import org.oauthsimple.http.Parameter;
import org.oauthsimple.http.Request;
import org.oauthsimple.http.Verb;

import java.io.IOException;

import static org.junit.Assert.*;

public class RequestTest {
	private Request getRequest;
	private Request postRequest;
	private ConnectionStub connection;

	@Before
	public void setup() throws Exception {
		connection = new ConnectionStub();
		postRequest = new Request(Verb.POST, "http://example.com");
		postRequest.addParameter("param", "value");
		postRequest.addParameter("param with spaces", "value with spaces");
		postRequest.setConnection(connection);
		getRequest = new Request(Verb.GET,
				"http://example.com?qsparam=value&other+param=value+with+spaces");
		getRequest.setConnection(connection);
	}

	@Test
	public void shouldSetRequestVerb() {
		try {
			getRequest.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("GET", connection.getRequestMethod());
	}

	@Test
	public void shouldGetQueryStringParameters() {
		assertEquals(2, getRequest.getParameters().size());
		assertEquals(0, postRequest.getParameters().size());
		assertTrue(getRequest.getParameters().contains(
				new Parameter("qsparam", "value")));
	}

	@Test
	public void shouldAddRequestHeaders() {
		getRequest.addHeader("Header", "1");
		getRequest.addHeader("Header2", "2");
		try {
			getRequest.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(2, getRequest.getHeaders().size());
		assertEquals(2, connection.getHeaders().size());
	}

	@Test
	public void shouldSetBodyParamsAndAddContentLength() {
		assertEquals("param=value&param%20with%20spaces=value%20with%20spaces",
				postRequest.getBodyContents());
		try {
			postRequest.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(connection.getHeaders().containsKey("Content-Length"));
	}

	@Test
	public void shouldSetPayloadAndHeaders() {
		// postRequest.addPayload("PAYLOAD");
		// postRequest.send();
		// assertEquals("PAYLOAD", postRequest.getBodyContents());
		// assertTrue(connection.getHeaders().containsKey("Content-Length"));
	}

	@Test
	public void shouldAllowAddingQuerystringParametersAfterCreation() {
		Request request = new Request(Verb.GET, "http://example.com?one=val");
		request.addParameter("two", "other val");
		request.addParameter("more", "params");
		assertEquals(3, request.getParameters().size());
	}

	@Test
	public void shouldReturnTheCompleteUrl() {
		Request request = new Request(Verb.GET, "http://example.com?one=val");
		request.addParameter("two", "other val");
		request.addParameter("more", "params");
		assertEquals("http://example.com?one=val&two=other%20val&more=params",
				request.getCompleteUrl());
	}

	@Test
	public void shouldHandleQueryStringSpaceEncodingProperly() {
		assertTrue(getRequest.getParameters().contains(
				new Parameter("other param", "value with spaces")));
	}

	@Test
	public void shouldAutomaticallyAddContentTypeForPostRequestsWithBytePayload() {
		// postRequest.addPayload("PAYLOAD".getBytes());
		// postRequest.send();
		// assertEquals(Request.DEFAULT_CONTENT_TYPE,
		// connection.getHeaders().get("Content-Type"));
	}

	@Test
	public void shouldAutomaticallyAddContentTypeForPostRequestsWithStringPayload() {
		// postRequest.addPayload("PAYLOAD");
		// postRequest.send();
		// assertEquals(Request.DEFAULT_CONTENT_TYPE,
		// connection.getHeaders().get("Content-Type"));
	}

	@Test
	public void shouldAutomaticallyAddContentTypeForPostRequestsWithBodyParameters() {
		try {
			postRequest.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(Request.DEFAULT_CONTENT_TYPE,
				connection.getHeaders().get("Content-Type"));
	}

	@Test
	public void shouldBeAbleToOverrideItsContentType() {
		postRequest.addHeader("Content-Type", "my-content-type");
		try {
			postRequest.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("my-content-type",
				connection.getHeaders().get("Content-Type"));
	}

	@Test
	public void shouldNotAddContentTypeForGetRequests() {
		try {
			getRequest.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertFalse(connection.getHeaders().containsKey("Content-Type"));
	}
}