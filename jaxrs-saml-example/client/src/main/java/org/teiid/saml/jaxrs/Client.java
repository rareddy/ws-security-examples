package org.teiid.saml.jaxrs;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.saml.SamlEnvelopedOutInterceptor;
import org.apache.cxf.rs.security.xml.XmlSigOutInterceptor;
import org.apache.ws.security.util.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@SuppressWarnings("nls")
public class Client {

    private static WebClient createSAMLClient(String address, boolean selfSigned) {
        JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
        bean.setAddress(address);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("ws-security.callback-handler", "org.apache.cxf.systest.jaxrs.security.saml.KeystorePasswordCallback");
        properties.put("ws-security.saml-callback-handler", "org.teiid.saml.jaxrs.SamlCallbackHandler");
        properties.put("ws-security.signature.username", "alice");
        properties.put("ws-security.signature.properties", "org/apache/cxf/systest/jaxrs/security/alice.properties");
        if (selfSigned) {
            properties.put("ws-security.self-sign-saml-assertion", "true");
        }
        bean.setProperties(properties);

        bean.getOutInterceptors().add(new SamlEnvelopedOutInterceptor(!selfSigned));
        XmlSigOutInterceptor xmlSig = new XmlSigOutInterceptor();
        if (selfSigned) {
            xmlSig.setStyle(XmlSigOutInterceptor.DETACHED_SIG);
        }
        return bean.createWebClient();
    }
    
    private static WebClient createClient(String address) {
        JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
        bean.setAddress(address);
        return bean.createWebClient();
    }    
    
    
    private static WebClient createClient(String address, String userName, String password) {
        JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
        bean.setAddress(address);
        bean.setUsername(userName);
        bean.setPassword(password);
        
        bean.getOutInterceptors().add(new LoggingOutInterceptor());
        bean.getInInterceptors().add(new LoggingInInterceptor());
        
        return bean.createWebClient();
    }    
    
    public static String getCookie(Response response, String name) {
        List<Object> cookies = response.getMetadata().get("Set-Cookie"); 
        if (cookies != null && !cookies.isEmpty()) {
            String cookie;
            for (Object object : cookies) {
                cookie = (String) object;
                if (cookie.contains(name)) {
                    // cookie looks like that:
                    // JSESSIONID=m4i8fbdufhiy12tlnpd1hfp3f;Path=/
                    return cookie.substring(cookie.indexOf("=") + 1, cookie.indexOf(";"));
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        // issue query to service
        WebClient client = createClient("http://localhost:8080/samlsvc/greet/java-client");
        Response response = client.get();

        // this re-direct from IDP with SAML challenge
        int status = response.getStatus();
        Object locationHeader = response.getMetadata().getFirst("Location");
        if (status != 303 || locationHeader == null) {
            System.out.println("1) Failed to see SAML request");
            return;
        }        
        System.out.println(locationHeader.toString());
        String relayState = getCookie(response, "RelayState");
        
        // send the SAML
        client = createClient(locationHeader.toString());
        response = client.get();
        status = response.getStatus();
        if (status != 200) {
            System.out.println("2) Failed to get password challange page");
            return;
        }        
        String sessionId = getCookie(response, "JSESSIONID");
        
        
        client = createClient("http://localhost:8080/idp/j_security_check");
        client.header("Referer", locationHeader.toString());
        client.header("Content-Type", "application/x-www-form-urlencoded");
        client.cookie(new NewCookie("JSESSIONID", sessionId));
        response = client.post("j_username=tomcat&j_password=tomcat&submit=Login");
        
        System.out.println(asString(response));
        status = response.getStatus();
        locationHeader = response.getMetadata().getFirst("Location");
        if (status != 302 || locationHeader == null) {
            System.out.println("3) Password challange submission failed");
            return;
        }        

        // go back to IDP
        client = createClient(locationHeader.toString());
        client.header("Content-Type", "application/x-www-form-urlencoded");
        client.cookie(new NewCookie("JSESSIONID", sessionId));
        
        response = client.get();
        status = response.getStatus();
        if (status != 200) {
            System.out.println("3) IDP did not accept after password");
            return;
        }        
        
        // parse document, to do sso call
        Document doc = Jsoup.parse(asString(response));
        Element form = doc.getElementsByTag("FORM").get(0);
        String action = form.attr("ACTION");
        String samlResponse = null;
        for (Element in:form.getElementsByTag("INPUT")) {
            if (in.attr("NAME").equals("SAMLResponse")) {
                samlResponse = in.attr("VALUE");
            }
        }
        
        System.out.println("SAMLResponse="+new String(samlResponse));
        
        // racs/sso call
        client = createClient(action);
        client.header("Content-Type", "application/x-www-form-urlencoded");
        client.header("Referer", locationHeader.toString());
        client.cookie(new NewCookie("RelayState", relayState));
        response = client.post("SAMLResponse="+URLEncoder.encode(samlResponse, "UTF-8")+"&RelayState="+relayState);
        locationHeader = response.getMetadata().getFirst("Location");
        status = response.getStatus();
        if (status != 303 || locationHeader == null) {
            System.out.println("4) racs/sso validation failed");
            return;
        }        
        String context = getCookie(response, "org.apache.cxf.websso.context");
        
        // final call
        client = createClient(locationHeader.toString());
        client.cookie(new NewCookie("org.apache.cxf.websso.context", context));
        client.cookie(new NewCookie("RelayState", relayState));
        client.header("Accept", "text/html,application/xhtml+xml,application/xml,application/text;");
        System.out.println(client.get(String.class));
    }


    private static String asString(Response response) throws IOException {
        BufferedInputStream in = new BufferedInputStream((InputStream)response.getEntity());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            int b = in.read();
            if (b == -1) {
                break;
            }
            out.write(b);
        }
        return new String(out.toByteArray());
    }
}
