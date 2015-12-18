This working example, to make it work in the Wildfly, change the style to spring to servlet based configuration. 
Need to configure an IdP like Shibboleth.

Typically when user accesses the resource, the filter generates default SAML token, then that is modified by the IdP and 
returns back to RACS which validates and gives it back to the filter.

In a SSO case, as shown in the client, the SAML call back handler creates the saml assertions that is verified by filter before
it is given access. Builing of the assertion based on the metadata in Idp seems to be the real issue in this whole SSO. 
So far did not find any decent example showiing all that.

see http://cxf.apache.org/docs/jax-rs-saml.html
http://cxf.apache.org/docs/saml-web-sso.html 

But I believe web-sso is not for head-less web services.
