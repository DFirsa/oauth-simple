package org.oauthsimple.examples;

import java.io.IOException;
import java.util.Scanner;

import org.oauthsimple.builder.ServiceBuilder;
import org.oauthsimple.builder.api.Px500Api;
import org.oauthsimple.http.OAuthRequest;
import org.oauthsimple.model.OAuthToken;
import org.oauthsimple.http.Response;
import org.oauthsimple.http.Verb;
import org.oauthsimple.model.Verifier;
import org.oauthsimple.oauth.OAuthService;

public class Px500Example {
	private static final String PROTECTED_RESOURCE_URL = "https://api.500px.com/v1/";

	public static void main(String[] args) throws IOException {
		OAuthService service = new ServiceBuilder().provider(Px500Api.class)
				.apiKey("your-api-key").apiSecret("your-api-secret").build();
		Scanner in = new Scanner(System.in);

		System.out.println("=== 500Px's OAuth Workflow ===");
		System.out.println();

		// Obtain the Request Token
		System.out.println("Fetching the Request Token...");
		OAuthToken requestToken = service.getRequestToken();
		System.out.println("Got the Request Token!");
		System.out.println();

		System.out.println("Now go and authorize Scribe here:");
		System.out.println(service.getAuthorizationUrl(requestToken));
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