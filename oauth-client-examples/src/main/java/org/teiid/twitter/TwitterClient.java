package org.teiid.twitter;


import java.net.URI;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth.client.OAuthClientUtils;

@SuppressWarnings("nls")
public class TwitterClient {
    public static final String HEADER = "Authorization";
    private static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
    private static final String REQUEST_TOKEN_RESOURCE = "https://api.twitter.com/oauth/request_token";
    private static final String ACCESS_TOKEN_RESOURCE = "https://api.twitter.com/oauth/access_token";
    private static final String RESOURCE = "https://api.twitter.com/1.1/statuses/user_timeline.json";
    
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);
        
        String consumerKey = "xxxxxEcB3zLZxIBPo";
        String consumerSecret = "oThxxxxxPWFMM7RjxjHHAcolxkaH3PmdCDrfwMmgO";
        OAuthClientUtils.Consumer consumer = new OAuthClientUtils.Consumer(consumerKey,consumerSecret);

        FormEncodingProvider provider = new FormEncodingProvider<Object>();
        provider.setConsumeMediaTypes(Arrays.asList("text/html"));
        
        WebClient client = null;
        
        client = WebClient.create(REQUEST_TOKEN_RESOURCE, Arrays.asList(provider));
        client.accept("application/x-www-form-urlencoded");
        
        //WebClient.getConfig(client).getInInterceptors().add(new LoggingInInterceptor()); 
        //WebClient.getConfig(client).getOutInterceptors().add(new LoggingOutInterceptor());
        
        OAuthClientUtils.Token requestToken = OAuthClientUtils.getRequestToken(client, consumer, new URI("oob"), null);
        System.out.println("Request Token=" + requestToken.getToken() +" secret="+requestToken.getSecret());
        
        URI authorizeURL = OAuthClientUtils.getAuthorizationURI(AUTHORIZE_URL, requestToken.getToken());
        System.out.println("Authorize URL="+authorizeURL);
        
        System.out.println("Cut and paste above URL in web browser and enter the authcode here");
        System.out.println("Auth Code =");
        String authCode = in.nextLine();

        client = WebClient.create(ACCESS_TOKEN_RESOURCE, Arrays.asList(provider));
        OAuthClientUtils.Token accessToken = OAuthClientUtils.getAccessToken(client, consumer,requestToken, authCode);
        System.out.println("Access Token = "+accessToken.getToken() + " Secret="+accessToken.getSecret());
        
        String header = OAuthClientUtils.createAuthorizationHeader(consumer, accessToken, "GET", RESOURCE);
        System.out.println("Header = " + header);
        
        client = WebClient.create(RESOURCE);
        client.header(HEADER, header);
                    
        String response = client.get(String.class);
        
        System.out.println(response);
    }
}
