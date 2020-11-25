/*******************************************************************************
 * Copyright (c) 2018, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.kerberos.auth;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.kernel.service.util.JavaInfo;
import com.ibm.ws.kernel.service.util.JavaInfo.Vendor;

public class Krb5LoginModuleWrapper implements LoginModule {
    private static final TraceComponent tc = Tr.register(Krb5LoginModuleWrapper.class);

    public static final String COM_IBM_SECURITY_AUTH_MODULE_KRB5LOGINMODULE = "com.ibm.security.auth.module.Krb5LoginModule";
    public static final String COM_SUN_SECURITY_AUTH_MODULE_KRB5LOGINMODULE = "com.sun.security.auth.module.Krb5LoginModule";
    public static final String COM_SUN_SECURITY_JGSS_KRB5_INITIATE = "com.sun.security.jgss.krb5.initiate";
    public static final String COM_SUN_SECURITY_JGSS_KRB5_ACCEPT = "com.sun.security.jgss.krb5.accept";

    @FFDCIgnore(Throwable.class)
    private static boolean isIBMLoginModuleAvailable() {
        try {
            Class.forName(COM_IBM_SECURITY_AUTH_MODULE_KRB5LOGINMODULE);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    // Cannot rely purely on JavaInfo.vendor() because IBM JDK 8 for Mac OS reports vendor = Oracle and only has some IBM API available
    private static final boolean isIBMJdk8 = JavaInfo.majorVersion() <= 8 &&
                                             (JavaInfo.vendor() == Vendor.IBM || isIBMLoginModuleAvailable());

    public CallbackHandler callbackHandler;
    public Subject subject;
    public Map<String, Object> sharedState;
    public Map<String, Object> options;

    private final Class<? extends LoginModule> krb5LoginModuleClass;
    private final LoginModule krb5loginModule;
    private boolean login_called = false;

    /**
     * <p>Construct an uninitialized Krb5LoginModuleWrapper object.</p>
     */
    public Krb5LoginModuleWrapper() {
        String targetClass = isIBMJdk8 //
                        ? COM_IBM_SECURITY_AUTH_MODULE_KRB5LOGINMODULE //
                        : COM_SUN_SECURITY_AUTH_MODULE_KRB5LOGINMODULE;
        if (TraceComponent.isAnyTracingEnabled()) {
            Tr.debug(tc, "Using target class: " + targetClass);
        }

        krb5LoginModuleClass = getClassForName(targetClass);
        try {
            krb5loginModule = krb5LoginModuleClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> opts) {
        Object useKeytabValue = null;
        this.callbackHandler = callbackHandler;
        this.subject = subject;
        this.sharedState = (Map<String, Object>) sharedState;
        this.options = new HashMap<>(opts);

        final String IBM_JDK_USE_KEYTAB = "useKeytab"; // URL
        final String OPENJDK_USE_KEYTAB = "useKeyTab"; // boolean

        if (!isIBMJdk8)
            useKeytabValue = options.get(OPENJDK_USE_KEYTAB);

        if (isIBMJdk8) {
            // Sanitize any OpenJDK-only config options
            if (options.containsKey("isInitiator")) {
                String isInitiator = (String) options.remove("isInitiator");
                if ("true".equalsIgnoreCase(isInitiator)) {
                    options.put("credsType", "both");
                }
            }
            options.remove("doNotPrompt");
            options.remove("refreshKrb5Config");

            options.remove(OPENJDK_USE_KEYTAB);
            if (options.containsKey("keyTab")) {
                String keytab = (String) options.remove("keyTab");
                // IBM JDK requires they keytab option to be a valid URL
                String keytabURL = coerceToURL(keytab);
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "Coerced keytab path from " + keytab + " to " + keytabURL);
                options.put(IBM_JDK_USE_KEYTAB, keytabURL);
            }
            options.remove("clearPass");
            boolean useTicketCache = Boolean.valueOf((String) options.remove("useTicketCache"));
            String ticketCache = (String) options.remove("ticketCache");
            if (useTicketCache) {
                if (ticketCache != null) {
                    // IBM JDK requires they ticketCache option to be a valid URL
                    String ticketCacheURL = coerceToURL(ticketCache);
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "Coerced ticketCache path from " + ticketCache + " to " + ticketCacheURL);
                    options.put("useCcache", ticketCacheURL);
                } else {
                    options.put("useDefaultCcache", "true");
                }
            }
        }

        if (useKeytabValue != null && useKeytabValue.equals("true") && options.get("keyTab") == null) {
            options.put("keyTab", getSystemProperty("KRB5_KTNAME"));
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            options.put("debug", "true");
        }

        krb5loginModule.initialize(subject, callbackHandler, sharedState, options);
    }

    @Override
    public boolean login() throws LoginException {
        krb5loginModule.login();
        login_called = true;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean commit() throws LoginException {
        if (login_called)
            krb5loginModule.commit();
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean abort() throws LoginException {
        if (login_called)
            krb5loginModule.abort();
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean logout() throws LoginException {
        if (login_called)
            krb5loginModule.logout();
        return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String getSystemProperty(final String propName) {
        String value = (String) java.security.AccessController.doPrivileged(new java.security.PrivilegedAction() {
            @Override
            public Object run() {
                return System.getProperty(propName);
            }
        });

        return value;
    }

    private static String coerceToURL(String path) {
        URI uri = URI.create(path);
        if (!uri.isAbsolute()) {
            uri = URI.create("file:/").resolve(uri);
        }
        try {
            return uri.toURL().toString();
        } catch (MalformedURLException e) {
            // if we cannot return the path as a URL, return the original path
            // to let IBM JDK handle the error messaging
            return path;
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends LoginModule> getClassForName(String tg) {
        try {
            return (Class<? extends LoginModule>) Class.forName(tg);
        } catch (ClassNotFoundException e) {
            Tr.error(tc, "Exception performing class for name.", e.getLocalizedMessage());
            throw new IllegalStateException(e);
        }
    }

}
