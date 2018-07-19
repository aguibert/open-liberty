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
package com.ibm.ws.jdbc.internal;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.resource.spi.IllegalStateException;
import javax.sql.DataSource;

public class SyntheticDataSource implements DataSource, Serializable, Referenceable {
    
    private static final long serialVersionUID = -903085189469473309L;
    
    private Driver driver;
    private String url;
    private Properties props;
    
    public SyntheticDataSource() {
        // JDBC spec requires public no-arg constructor
    }
    
    public void setDriver(Driver d) {
        this.driver = d;
    }
    
    public void setURL(String url) {
        this.url = url;
    }
    
    public void setProperties(Properties props) {
        this.props = props;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface != null && iface.isAssignableFrom(getClass());
    }

    @Override
    public Reference getReference() throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(driver == null)
            throw new SQLException("Driver not set.");
        
        return driver.connect(url, props);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Properties p = new Properties(props);
        p.remove("user");
        p.remove("password");
        if(username != null)
            p.setProperty("user", username);
        if (password != null)
            p.setProperty("password", password);
        return driver.connect(url, p);
    }

}
