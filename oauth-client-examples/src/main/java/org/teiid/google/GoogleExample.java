package org.teiid.google;

import java.net.URI;
import java.util.Scanner;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenGrant;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.grants.refresh.RefreshTokenGrant;

@SuppressWarnings("nls")
public class GoogleExample {

    public static void main(String[] args) throws Exception {
        OAuthClientUtils.Consumer consumer = new OAuthClientUtils.Consumer("1006219695202-j0ojq3jrb3q5nedgbsg307g4r5bkkrjb.apps.googleusercontent.com", "3L6-xrMTk6-v9R3iDlznWq-o");
        
        URI uri = OAuthClientUtils.getAuthorizationURI("https://accounts.google.com/o/oauth2/auth", 
                consumer.getKey(), 
                "urn:ietf:wg:oauth:2.0:oob", 
                "Auth URL", 
                "https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile");
        System.out.println("URI="+uri);
        
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the Auth Code=");
        String accessCode = in.nextLine();
        
        String accessTokenURL = "https://www.googleapis.com/oauth2/v3/token";
        WebClient client = WebClient.create(accessTokenURL);
        
        AccessTokenGrant grant = new AuthorizationCodeGrant(accessCode, new URI("urn:ietf:wg:oauth:2.0:oob"));
        ClientAccessToken clientToken = OAuthClientUtils.getAccessToken(client, consumer, grant, null, false);
        System.out.println("Refresh Token="+clientToken.getRefreshToken());
        
        grant = new RefreshTokenGrant("1/TIWwGphUG2U1dd0ALxGOyEZ8DdcH94lFpJz3Ji3K1pAMEudVrK5jSpoR30zcRFq6");
        clientToken = OAuthClientUtils.getAccessToken(client, consumer, grant, null, false);
        
        String resourceURL = "https://www.googleapis.com/plus/v1/people/me";
        client = WebClient.create(resourceURL);
        client.query("key", consumer.getKey());
        client.header("Authorization", "Bearer "+clientToken.getTokenKey());
        WebClient.getConfig(client).getInInterceptors().add(new LoggingInInterceptor()); 
        WebClient.getConfig(client).getOutInterceptors().add(new LoggingOutInterceptor());
        
        System.out.println(client.get(String.class));
    }
}
