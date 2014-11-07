package org.teiid.saml.jaxrs;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class HelloWorldImpl implements HelloWorld {
    @Context 
    SecurityContext sc;
    
    public String greet(String greet) {
        return sc.getUserPrincipal().getName()+" = "+greet;
    }
}
