package org.teiid.saml.jaxrs;

import java.io.IOException;
import java.util.Collections;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.ws.security.saml.ext.SAMLCallback;
import org.apache.ws.security.saml.ext.bean.ActionBean;
import org.apache.ws.security.saml.ext.bean.AuthDecisionStatementBean;
import org.apache.ws.security.saml.ext.bean.AuthDecisionStatementBean.Decision;
import org.apache.ws.security.saml.ext.bean.AuthenticationStatementBean;
import org.apache.ws.security.saml.ext.bean.ConditionsBean;
import org.apache.ws.security.saml.ext.bean.SubjectBean;
import org.apache.ws.security.saml.ext.builder.SAML2Constants;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;

/**
 * A CallbackHandler instance that is used by the STS to mock up a SAML Attribute Assertion.
 */
public class SamlCallbackHandler implements CallbackHandler {
    private String confirmationMethod = SAML2Constants.CONF_SENDER_VOUCHES;
    
    
    /*
     
<saml2p:AuthnRequest AssertionConsumerServiceURL="http://localhost:8080/samlsvc/racs/sso" 
    ForceAuthn="false" ID="695814bc-c6e8-4160-a9bd-e5ecca85aeec" 
    IsPassive="false" IssueInstant="2014-11-05T00:28:38.555Z" 
    ProtocolBinding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" 
    Version="2.0" xmlns:saml2p="urn:oasis:names:tc:SAML:2.0:protocol">
    
    <saml2:Issuer xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion">http://localhost:8080/samlsvc/</saml2:Issuer>
    
    <saml2p:NameIDPolicy AllowCreate="true" Format="urn:oasis:names:tc:SAML:2.0:nameid-format:persistent" SPNameQualifier="http://localhost:8080/samlsvc/" />
    <saml2p:RequestedAuthnContext Comparison="exact">
        <saml2:AuthnContextClassRef xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion">urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport</saml2:AuthnContextClassRef>
    </saml2p:RequestedAuthnContext>
    
</saml2p:AuthnRequest>     
     
     */
    
    
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        Message m = PhaseInterceptorChain.getCurrentMessage();
        
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof SAMLCallback) {
                SAMLCallback callback = (SAMLCallback) callbacks[i];
                callback.setSamlVersion(SAMLVersion.VERSION_20);
                callback.setIssuer("http://localhost:8080/samlsvc/");
                
                String subjectName = (String)m.getContextualProperty("saml.subject.name");
                if (subjectName == null) {
                    subjectName = "tomcat";
                }
                String subjectQualifier = "localhost";                
                SubjectBean subjectBean = new SubjectBean(subjectName, subjectQualifier, confirmationMethod);
                callback.setSubject(subjectBean);
                
                ConditionsBean conditions = new ConditionsBean();
                conditions.setAudienceURI("https://sp.example.com/SAML2");
                callback.setConditions(conditions);
                
                AuthDecisionStatementBean authDecBean = new AuthDecisionStatementBean();
                authDecBean.setDecision(Decision.INDETERMINATE);
                authDecBean.setResource("https://sp.example.com/SAML2");
                ActionBean actionBean = new ActionBean();
                actionBean.setContents("Read");
                authDecBean.setActions(Collections.singletonList(actionBean));
                callback.setAuthDecisionStatementData(Collections.singletonList(authDecBean));
                
                AuthenticationStatementBean authBean = new AuthenticationStatementBean();
                authBean.setSubject(subjectBean);
                authBean.setAuthenticationInstant(new DateTime());
                authBean.setSessionIndex("123456");
                // AuthnContextClassRef is not set
                authBean.setAuthenticationMethod(
                        "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
                callback.setAuthenticationStatementData(
                    Collections.singletonList(authBean));
                
                /*
                AttributeStatementBean attrBean = new AttributeStatementBean();
                attrBean.setSubject(subjectBean);
                
                
                List<String> roles = CastUtils.cast((List<?>)m.getContextualProperty("saml.roles"));
                if (roles == null) {
                    roles = Collections.singletonList("user");
                }
                List<AttributeBean> claims = new ArrayList<AttributeBean>();
                AttributeBean roleClaim = new AttributeBean();
                roleClaim.setSimpleName("subject-role");
                roleClaim.setQualifiedName(Claim.DEFAULT_ROLE_NAME);
                roleClaim.setNameFormat(Claim.DEFAULT_NAME_FORMAT);
                roleClaim.setAttributeValues(roles);
                claims.add(roleClaim);
                
                List<String> authMethods = CastUtils.cast((List<?>)m.getContextualProperty("saml.auth"));
                if (authMethods == null) {
                    authMethods = Collections.singletonList("password");
                }
                
                
                AttributeBean authClaim = new AttributeBean();
                authClaim.setSimpleName("http://claims/authentication");
                authClaim.setQualifiedName("http://claims/authentication");
                authClaim.setNameFormat("http://claims/authentication-format");
                authClaim.setAttributeValues(new ArrayList<Object>(authMethods));
                claims.add(authClaim);
                
                
                attrBean.setSamlAttributes(claims);
                
                callback.setAttributeStatementData(Collections.singletonList(attrBean));
                */
            }
        }
    }
    
}
