package org.teiid.test.webservice.impl;


import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.teiid.test.webservice.HelloWorld;

@WebService(
		serviceName = "HelloWorldService", 
		portName = "HelloWorldPort",
		endpointInterface = "org.teiid.test.webservice.HelloWorld", 
		wsdlLocation = "WEB-INF/wsdl/hello-kerberos-security.wsdl",
		targetNamespace = "http://webservices.samples.jboss.org/")
public class HelloWorldImpl implements HelloWorld {
	@Resource 
	WebServiceContext wsContext;
	
	public java.lang.String sayHello(java.lang.String arg0) {
		return "Hello = "+ arg0 + " From User = " + wsContext.getUserPrincipal().getName();
	}
}