package org.teiid.test.webservice.impl;


import javax.jws.WebService;

import org.apache.cxf.interceptor.InInterceptors;
import org.jboss.ws.api.annotation.EndpointConfig;
import org.teiid.test.webservice.HelloWorld;

@WebService(
		serviceName = "HelloWorldService", 
		portName = "HelloWorldPort",
		endpointInterface = "org.teiid.test.webservice.HelloWorld", 
		wsdlLocation = "WEB-INF/wsdl/hello-username-security.wsdl",
		targetNamespace = "http://webservices.samples.jboss.org/")
@EndpointConfig(configFile = "WEB-INF/jaxws-endpoint-config.xml", configName = "WS-Security Endpoint")
@InInterceptors(interceptors = {
      "org.jboss.wsf.stack.cxf.security.authentication.SubjectCreatingPolicyInterceptor",
      "org.teiid.test.webservice.impl.POJOEndpointAuthorizationInterceptor"}
)
public class HelloWorldImpl implements HelloWorld {
	public java.lang.String sayHello(java.lang.String arg0) {
		return "Hello "+arg0;
	}
}