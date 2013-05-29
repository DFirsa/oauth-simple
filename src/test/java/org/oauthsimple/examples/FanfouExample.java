package org.oauthsimple.examples;

import org.oauthsimple.builder.ServiceBuilder;
import org.oauthsimple.builder.api.FanfouApi;
import org.oauthsimple.model.*;
import org.oauthsimple.oauth.OAuthService;

import java.io.*;
import java.util.Scanner;

public class FanfouExample {
    private static final String UPLOAD_URL = "http://api.fanfou.com/photos/upload.json";
    private static final String STATUS_URL = "http://api.fanfou.com/statuses/update.json";
    private static final String USER_URL = "http://api.fanfou.com/statuses/home_timeline.json";

    public static void main(String[] args) throws IOException {
        OAuthService service = new ServiceBuilder().debug()
                .debugStream(System.out).provider(FanfouApi.class)
                .signatureType(SignatureType.HEADER_OAUTH)
                .apiKey("8c914ca373d877f3e74799e795aa73bf")
                .apiSecret("6b7c0f1b88ef15a43ca169a3b3fe349b").build();
        Scanner in = new Scanner(System.in);

        System.out.println("=== Fanfou OAuth Workflow ===");
        System.out.println();
        OAuthToken accessToken = null;
        File tokenFile = new File(".", "token.dat");
        if (tokenFile.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                    tokenFile));
            try {
                accessToken = (OAuthToken) ois.readObject();
                System.out.println("read Access Token from file, result="
                        + accessToken);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ois.close();
        }
        boolean useXAuth = true;
        if (accessToken == null) {
            if (useXAuth) {
                System.out.println("Use XAuth to retrieve an Access Token...");
                accessToken = service.getAccessToken("test", "test");
            } else {
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
                System.out
                        .println("Trading the Request Token for an Access Token...");
                accessToken = service.getAccessToken(requestToken, verifier);
            }

            System.out.println("read Access Token from network, result="
                    + accessToken);
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(tokenFile));
            oos.writeObject(accessToken);
            oos.flush();
            oos.close();
        }

        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: "
                + accessToken + " )");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
//		OAuthRequest request = new OAuthRequest(Verb.POST, UPLOAD_URL);
//		request.addParameter("status",
//				"this is sparta! *" + System.currentTimeMillis());
//		request.addParameter("a", "b");
//		request.addBody("photo", new File("cat.jpg"));

        OAuthRequest request = new OAuthRequest(Verb.GET, USER_URL);
//        request.addParameter("count", "2");
//        request.addParameter("mode", "lite");
//        request.addParameter("format", "html");


//        OAuthRequest request = new OAuthRequest(Verb.POST, STATUS_URL);
//        request.addParameter("status",
//                "this is sparta! *" + System.currentTimeMillis());

        service.signRequest(accessToken, request);
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.err.println(response.getBody());


        System.out.println();
        System.out
                .println("Thats it man! Go and build something awesome with Scribe! :)");
    }

}