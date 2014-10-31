package org.teiid.saml.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;



@Path("/greet")
public interface HelloWorld {

    @GET
    @Produces("application/text")
    @Path("{msg}")
    public String greet(@PathParam("msg") String greet);
}