package org.teiid.test.webservice.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class ServerUsernamePasswordCallback implements CallbackHandler {
	private Map<String, String> passwords = new HashMap<String, String>();

	public ServerUsernamePasswordCallback() {
		passwords.putAll(getInitMap());
	}

	private static Map<String, String> getInitMap() {
		Map<String, String> passwords = new HashMap<String, String>();
		passwords.put("user", "redhat.1");
		return passwords;
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		for (int i = 0; i < callbacks.length; i++) {
			final Callback c = callbacks[i];
			if (c != null && c instanceof WSPasswordCallback) {
				final WSPasswordCallback pc = (WSPasswordCallback) c;

				String pass = passwords.get(pc.getIdentifier());
				if (pass != null) {
					pc.setPassword(pass);
					return;
				}
			}
		}
	}
}