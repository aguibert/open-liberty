/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.javaee.v80.fat;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;
import javaee8.web.WebProfile8TestServlet;

/**
 * Test of Application 8 and Servlet 4 parsing using the 'webProfile-8.0'
 * feature.
 *
 * This test puts together a server definition which uses feature
 * 'webProfile-8.0' with a simple EAR which uses Application 8 and which
 * contains a Servlet 4 WAR.
 *
 * The expected test result is that the EAR and WAR start with no errors.
 *
 * The WAR packages a fragment JAR. Both the WAR and the fragment JAR
 * package a servlet (SimpleClassesServlet and SimpleFragmentServlet),
 * however, the test only configures the fragment servlet.
 */
@RunWith(FATRunner.class)
public class WebProfileTest extends FATServletClient {

    public static final String APP_NAME = "javaee8App";

    @Server("javaee8.fat.webProfile")
    @TestServlet(servlet = WebProfile8TestServlet.class, contextRoot = APP_NAME)
    public static LibertyServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "javaee8Ear.ear")
                        .addAsModule(ShrinkHelper.buildDefaultApp(APP_NAME, "javaee8.web.*"));
        ShrinkHelper.addDirectory(ear, "test-applications/javaee8Ear/resources/");
        ShrinkHelper.exportDropinAppToServer(server, ear);
        server.startServer();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stopServer();
    }
}
