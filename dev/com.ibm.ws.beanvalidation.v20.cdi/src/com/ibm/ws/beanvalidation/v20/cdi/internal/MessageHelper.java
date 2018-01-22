/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.beanvalidation.v20.cdi.internal;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageHelper {

    public static final String BV_RESOURCE_BUNDLE = "com.ibm.ws.beanvalidation.v20.cdi.internal.BVNLSMessages";

    public static String getMessage(String key) {
        try {
            return getResourceBundle().getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public static String getMessage(String key, Object... args) {
        return MessageFormat.format(getResourceBundle().getString(key), args);
    }

    private static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle(BV_RESOURCE_BUNDLE, Locale.getDefault());
    }
}
