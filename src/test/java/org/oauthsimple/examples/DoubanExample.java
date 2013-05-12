package org.oauthsimple.examples;

import java.io.IOException;
import java.util.Scanner;

import org.oauthsimple.builder.ServiceBuilder;
import org.oauthsimple.builder.api.DoubanApi20;
import org.oauthsimple.model.GrantType;
import org.oauthsimple.model.OAuthRequest;
import org.oauthsimple.model.OAuthToken;
import org.oauthsimple.model.Response;
import org.oauthsimple.model.ResponseType;
import org.oauthsimple.model.SignatureType;
import org.oauthsimple.model.Verb;
import org.oauthsimple.model.Verifier;
import org.oauthsimple.oauth.OAuthService;

public class DoubanExample {
	private static final String NETWORK_NAME = "Douban";
	private static final String PROTECTED_RESOURCE_URL = "https://api.douban.com/v2/user/~me";
	private static final OAuthToken EMPTY_TOKEN = null;

	public static void main(String[] args)  throws IOException{
		// Replace these with your own api key and secret
		String apiKey = "06fd45e1d2ad245a14f7d1a578076d0e";
		String apiSecret = "b29538157b254bd1";
		OAuthService service = new ServiceBuilder()
				.signatureType(SignatureType.HEADER_BEARER)
				.grantType(GrantType.AUTHORIZATION_CODE)
				.responseType(ResponseType.CODE)
				.provider(DoubanApi20.class)
				.apiKey(apiKey)
				.apiSecret(apiSecret)
				.callback("http://www.baidu.com")
				.scope("shuo_basic_r,shuo_basic_w,community_basic_online,event_basic_r,douban_basic_common,music_basic_r,movie_basic,book_basic_r,community_basic_photo,community_basic_note,community_basic_user")
				.build();

		// SocketAddress address=new InetSocketAddress("127.0.0.1", 1080);
		// Proxy proxy=new Proxy(Proxy.Type.SOCKS, address);
		// service.setProxy(proxy);
		Scanner in = new Scanner(System.in);

		System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
		System.out.println();

		// Obtain the Authorization URL
		System.out.println("Fetching the Authorization URL...");
		String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
		System.out.println("Got the Authorization URL!");
		System.out.println("Now go and authorize Scribe here:");
		System.out.println(authorizationUrl);
		System.out.println("And paste the authorization code here");
		System.out.print(">>");
		Verifier verifier = new Verifier(in.nextLine());
		System.out.println();

		// Trade the Request Token and Verifier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");
		OAuthToken accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
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
		System.out.println(response.getCode());
		System.out.println(response.getBody());

		System.out.println();
		System.out
				.println("Thats it man! Go and build something awesome with Scribe! :)");

	}
}
