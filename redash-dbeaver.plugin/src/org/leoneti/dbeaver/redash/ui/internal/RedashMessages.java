/*****************************************************************************************
* Copyright (C) 2023-2023  Ricardo Leoneti
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v2.0
* which accompanies this distribution, and is available at
* https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
*
* Contributors:
*     Ricardo Leoneti <ricardo.leoneti@gmail.com>    - initial API and implementation
*****************************************************************************************/
package org.leoneti.dbeaver.redash.ui.internal;

import org.eclipse.osgi.util.NLS;

public class RedashMessages extends NLS {
    private static final String BUNDLE_NAME = RedashMessages.class.getName();
    
    public static String redash_connection_page_label_host;
    public static String redash_connection_page_label_database;
    public static String redash_connection_page_label_connection;
    public static String redash_connection_page_label_token;
    public static String redash_connection_page_label_optional;
    public static String redash_connection_page_label_ssl;
    
    static {
        NLS.initializeMessages(BUNDLE_NAME, RedashMessages.class);
    }

    private RedashMessages() {
    }
}
