package org.teiid.test.webservice;  
  
import javax.ws.rs.GET;  
import javax.ws.rs.Path;  
import javax.ws.rs.core.Application;  
import javax.ws.rs.core.Context;  
import javax.ws.rs.core.SecurityContext;  
  
@Path("/hello")  
public class HelloWorld extends Application {  
  
  @Context  
  private SecurityContext mySecurityContext;  
  
    @GET  
    @Path("world")  
    public String helloworld() {  
        return "<message>Hello World! User="+mySecurityContext.getUserPrincipal().getName()+"</message>";  
    }  
} 
