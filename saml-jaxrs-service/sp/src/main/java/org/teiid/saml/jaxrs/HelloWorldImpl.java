package org.teiid.saml.jaxrs;

public class HelloWorldImpl implements HelloWorld {
    public String greet(String greet) {
        return "Hello = "+greet;
    }
}
