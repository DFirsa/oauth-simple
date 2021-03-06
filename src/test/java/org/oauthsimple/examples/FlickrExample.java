package org.oauthsimple.examples;

import org.oauthsimple.builder.ServiceBuilder;
import org.oauthsimple.builder.api.FlickrApi;
import org.oauthsimple.http.OAuthRequest;
import org.oauthsimple.http.Response;
import org.oauthsimple.http.Verb;
import org.oauthsimple.model.*;
import org.oauthsimple.oauth.OAuthService;

import java.io.IOException;
import java.util.Scanner;

public class FlickrExample {
	private static final String PROTECTED_RESOURCE_URL = "http://api.flickr.com/services/rest/";

	public static void main(String[] args) throws IOException {
		// Replace these with your own api key and secret
		String apiKey = "your_app_id";
		String apiSecret = "your_api_secret";
		OAuthService service = new ServiceBuilder().provider(FlickrApi.class)
				.apiKey(apiKey).apiSecret(apiSecret).build();
		Scanner in = new Scanner(System.in);

		System.out.println("=== Flickr's OAuth Workflow ===");
		System.out.println();

		// Obtain the Request Token
		System.out.println("Fetching the Request Token...");
		OAuthToken requestToken = service.getRequestToken();
		System.out.println("Got the Request Token!");
		System.out.println();

		System.out.println("Now go and authorize Scribe here:");
		String authorizationUrl = service.getAuthorizationUrl(requestToken);
		System.out.println(authorizationUrl + "&perms=read");
		System.out.println("And paste the verifier here");
		System.out.print(">>");
		Verifier verifier = new Verifier(in.nextLine());
		System.out.println();

		// Trade the Request Token and Verfier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");
		OAuthToken accessToken = service.getAccessToken(requestToken, verifier);
		System.out.println("Got the Access Token!");
		System.out.println("(if your curious it looks like this: "
				+ accessToken + " )");
		System.out.println();

		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		OAuthRequest request = new OAuthRequest(Verb.GET,
				PROTECTED_RESOURCE_URL);
		request.addParameter("method", "flickr.test.login");
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getBody());

		System.out.println();
		System.out
				.println("Thats it man! Go and build something awesome with Scribe! :)");
	}
}
