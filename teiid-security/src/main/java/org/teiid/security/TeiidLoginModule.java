/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.teiid.security;

import java.security.Principal;
import java.security.acl.Group;

import javax.security.auth.login.LoginException;
import javax.xml.namespace.QName;

import org.jboss.security.SimplePrincipal;
import org.picketbox.datasource.security.AbstractPasswordCredentialLoginModule;
import org.teiid.resource.adapter.ws.SAMLTokenProfile;
import org.teiid.resource.adapter.ws.WSSecurityCredential;



@SuppressWarnings({"unchecked","nls"})
public class TeiidLoginModule extends AbstractPasswordCredentialLoginModule {
	WSSecurityCredential credential = new WSSecurityCredential();

	public TeiidLoginModule() {
	}

	@Override
	public boolean login() throws LoginException {
		// ask the security association class for the principal info making this
		// request
		try {
			Principal user = GetPrincipalInfoAction.getPrincipal();
			char[] password = GetPrincipalInfoAction.getCredential();
			if (user != null) {

				String stsURL = "http://localhost:8080/picketlink-sts/PicketLinkSTSService";
		        final QName stsServiceName = new QName("urn:picketlink:identity-federation:sts", "PicketLinkSTS");
		        final QName stsPortName = new QName("urn:picketlink:identity-federation:sts", "PicketLinkSTSPort");

//		        this.credential.setSTSClient(stsURL, stsServiceName, stsPortName);
//		        this.credential.getStsPropterties().put(SecurityConstants.USERNAME, user.getName());
//		        this.credential.getStsPropterties().put(SecurityConstants.CALLBACK_HANDLER, new ClientCallbackHandler(user.getName(), new String(password)));

		        SAMLTokenProfile profile = new SAMLTokenProfile(false, "saml.properties", new SamlCallbackHandler());
		        profile.handleSecurity(this.credential);
				super.loginOk = true;
				return true;
			}
		} catch (Throwable e) {
			throw new LoginException("Unable to get the calling principal or its credentials for resource association");
		}
		return false;
	}

	@Override
	public boolean commit() throws LoginException {
		SubjectActions.addCredentials(this.subject, this.credential);
		return super.commit();
	}

	@Override
	protected Principal getIdentity() {
		Principal principal = new SimplePrincipal("johndoe");
		return principal;
	}

	@Override
	protected Group[] getRoleSets() throws LoginException {
		return new Group[] {};
	}
}
