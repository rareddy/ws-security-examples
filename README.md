Web Service security, SOAP and REST
====================================

The repository contains some of the examples I wrote looking at other peoples work, reading blogs, asking questions etc. I use them to test Teiid web security stuff. 

Working Examples
- SOAP with Kerberos WS-Security 
- REST with Kerberos - for code see https://developer.jboss.org/docs/DOC-52692
- SOAP with User name token
- REST with SAML
- REST with OAuth2 - Talend folks has good example at https://github.com/Talend/tesb-rt-se/tree/master/examples/cxf/jaxrs-oauth2
- SOAP with SAML in WS-Security - example see http://peterarockiaraj.wordpress.com/2009/09/04/developing-cxf-ws-security-with-saml/

You need to setup KDC per http://coheigea.blogspot.com/2011/10/using-kerberos-with-web-services-part-i.html

Useful code related to JBoss Web Services
JBoss integration is at http://anonsvn.jboss.org/repos/jbossws/stack/cxf/trunk/modules/testsuite/cxf-spring-tests/src/test/java/org/jboss/test/ws/jaxws/samples/wsse/kerberos/KerberosTestCase.java

