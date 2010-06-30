//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Modifications:
//
// 2008 Feb 10: Indent, update to use current Castor and factory patterns. - dj@opennms.org
// 2007 Jul 03: Remove unused static field. - dj@opennms.org
// 2006 Aug 15: Remove unused imports, put initialize in a try/finally block. - dj@opennms.org
// 2004 Dec 21: Changed determination of primary SNMP interface
// 2003 Nov 11: Merged changes from Rackspace project
// 2003 Jan 31: Cleaned up some unused imports.
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.                                                            
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//       
// For more information contact: 
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
package org.opennms.netmgt.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.log4j.Category;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.ConfigFileConstants;
import org.opennms.netmgt.config.collectd.CollectdConfiguration;
import org.opennms.netmgt.dao.castor.CastorUtils;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:jamesz@opennms.com">James Zuo</a>
 * @author <a href="mailto:mike@opennms.org">Mike Davidson</a>
 * @author <a href="mailto:sowmya@opennms.org">Sowmya Nataraj</a>
 * @author <a href="http://www.opennms.org/">OpenNMS</a>
 */

/**
 * This is the singleton class used to load the configuration for the OpenNMS
 * Collection Daemon from the collectd-configuration xml file.
 *
 * A mapping of the configured URLs to the iplist they contain is built at
 * init() time so as to avoid numerous file reads.
 *
 * <strong>Note: </strong>Users of this class should make sure the
 * <em>init()</em> is called before calling any other method to ensure the
 * config is loaded before accessing other convenience methods.
 *
 * @author ranger
 * @version $Id: $
 */
public class CollectdConfigFactory {
    final static String SELECT_METHOD_MIN = "min";

    /**
     * The singleton instance of this factory.  Null if the factory hasn't been
     * initialized.
     */
    private static CollectdConfigFactory m_singleton = null;

    /**
     * A boolean flag to indicate If a filter rule against the local NMS server
     * has to be used.
     */
    //private static boolean m_verifyServer;

    /**
     * Name of the local NMS server.
     */
    //private static String m_localServer;

    private CollectdConfig m_collectdConfig;

    /**
     * This method is used to rebuild the package agaist iplist mapping when
     * needed. When a node gained service event occurs, collectd has to
     * determine which package the ip/service combination is in, but if the
     * interface is a newly added one, the package iplist should be rebuilt so
     * that collectd could know which package this ip/service pair is in.
     */
    public synchronized void rebuildPackageIpListMap() {
        m_collectdConfig.rebuildPackageIpListMap();
    }

    /**
     * Private constructor
     * @param verifyServer 
     * @param localServer 
     * 
     * @exception java.io.IOException
     *                Thrown if the specified config file cannot be read
     * @exception org.exolab.castor.xml.MarshalException
     *                Thrown if the file does not conform to the schema.
     * @exception org.exolab.castor.xml.ValidationException
     *                Thrown if the contents do not match the required schema.
     */
    private CollectdConfigFactory(String configFile, String localServer, boolean verifyServer) throws IOException, MarshalException, ValidationException {
        InputStreamReader rdr = new InputStreamReader(new FileInputStream(configFile));

        try {
            initialize(rdr, localServer, verifyServer);
        } finally {
            rdr.close();
        }
    }

    /**
     * Public constructor
     *
     * @exception java.io.IOException
     *                Thrown if the specified config file cannot be read
     * @exception org.exolab.castor.xml.MarshalException
     *                Thrown if the file does not conform to the schema.
     * @exception org.exolab.castor.xml.ValidationException
     *                Thrown if the contents do not match the required schema.
     * @param rdr a {@link java.io.Reader} object.
     * @param localServer a {@link java.lang.String} object.
     * @param verifyServer a boolean.
     * @throws java.io.IOException if any.
     * @throws org.exolab.castor.xml.MarshalException if any.
     * @throws org.exolab.castor.xml.ValidationException if any.
     */
    public CollectdConfigFactory(Reader rdr, String localServer, boolean verifyServer) throws IOException, MarshalException, ValidationException {
        initialize(rdr, localServer, verifyServer);
    }

    private void initialize(Reader rdr, String localServer, boolean verifyServer) throws MarshalException, ValidationException, IOException {
        // FIXME: These fields are unused; should they be removed?
        //m_verifyServer = verifyServer;
        //m_localServer = localServer;

        CollectdConfiguration config = CastorUtils.unmarshal(CollectdConfiguration.class, rdr);
        m_collectdConfig = new CollectdConfig(config, localServer, verifyServer);
    }

    /**
     * Load the config from the default config file and create the singleton
     * instance of this factory.
     *
     * @exception java.io.IOException
     *                Thrown if the specified config file cannot be read
     * @exception org.exolab.castor.xml.MarshalException
     *                Thrown if the file does not conform to the schema.
     * @exception org.exolab.castor.xml.ValidationException
     *                Thrown if the contents do not match the required schema.
     * @throws java.io.IOException if any.
     * @throws org.exolab.castor.xml.MarshalException if any.
     * @throws org.exolab.castor.xml.ValidationException if any.
     */
    public static synchronized void init() throws IOException, MarshalException, ValidationException {
        if (isInitialized()) {
            // init already called return; to reload, reload() will need to be called
            return;
        }

        OpennmsServerConfigFactory.init();

        File cfgFile = ConfigFileConstants.getFile(ConfigFileConstants.COLLECTD_CONFIG_FILE_NAME);

        log().debug("init: config file path: " + cfgFile.getPath());

        setInstance(new CollectdConfigFactory(cfgFile.getPath(), OpennmsServerConfigFactory.getInstance().getServerName(), OpennmsServerConfigFactory.getInstance().verifyServer()));
    }

    /**
     * Reload the config from the default config file
     *
     * @exception java.io.IOException
     *                Thrown if the specified config file cannot be read/loaded
     * @exception org.exolab.castor.xml.MarshalException
     *                Thrown if the file does not conform to the schema.
     * @exception org.exolab.castor.xml.ValidationException
     *                Thrown if the contents do not match the required schema.
     * @throws java.io.IOException if any.
     * @throws org.exolab.castor.xml.MarshalException if any.
     * @throws org.exolab.castor.xml.ValidationException if any.
     */
    public static synchronized void reload() throws IOException, MarshalException, ValidationException {
        m_singleton = null;

        init();
    }

    /**
     * Saves the current in-memory configuration to disk and reloads
     *
     * @throws org.exolab.castor.xml.MarshalException if any.
     * @throws java.io.IOException if any.
     * @throws org.exolab.castor.xml.ValidationException if any.
     */
    public synchronized void saveCurrent() throws MarshalException, IOException, ValidationException {
        File cfgFile = ConfigFileConstants.getFile(ConfigFileConstants.COLLECTD_CONFIG_FILE_NAME);

        CollectdConfiguration config = m_collectdConfig.getConfig();

        CastorUtils.marshalViaString(config, cfgFile);

        reload();
    }

    /**
     * Return the singleton instance of this factory.
     *
     * @return The current factory instance.
     * @throws java.lang.IllegalStateException
     *             Thrown if the factory has not yet been initialized.
     */
    public static synchronized CollectdConfigFactory getInstance() {
        Assert.state(isInitialized(), "The factory has not been initialized");

        return m_singleton;
    }

    /**
     * <p>setInstance</p>
     *
     * @param instance a {@link org.opennms.netmgt.config.CollectdConfigFactory} object.
     */
    public static synchronized void setInstance(CollectdConfigFactory instance) {
        m_singleton = instance;
    }

    /**
     * <p>getCollectdConfig</p>
     *
     * @return a {@link org.opennms.netmgt.config.CollectdConfig} object.
     */
    public CollectdConfig getCollectdConfig() {
        return m_collectdConfig;
    }

    /**
     * <p>getPackage</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.config.CollectdPackage} object.
     */
    public CollectdPackage getPackage(String name) {
        return m_collectdConfig.getPackage(name);
    }

    /**
     * Returns true if collection package exists
     *
     * @param name
     *            The package name to check
     * @return True if the package exists
     */
    public synchronized boolean packageExists(String name) {
        return m_collectdConfig.getPackage(name) != null;
    }

    /**
     * Returns true if collection domain exists
     *
     * @param name
     *            The domain name to check
     * @return True if the domain exists
     */
    public boolean domainExists(String name) {
        return m_collectdConfig.domainExists(name);
    }

    /**
     * Returns true if the specified interface is included by at least one
     * package which has the specified service and that service is enabled (set
     * to "on").
     *
     * @param ipAddr
     *            IP address of the interface to lookup
     * @param svcName
     *            The service name to lookup
     * @return true if Collectd config contains a package which includes the
     *         specified interface and has the specified service enabled.
     */
    public boolean isServiceCollectionEnabled(String ipAddr, String svcName) {
        return m_collectdConfig.isServiceCollectionEnabled(ipAddr, svcName);
    }

    private static boolean isInitialized() {
        return m_singleton != null;
    }

    private static Category log() {
        return ThreadCategory.getInstance(CollectdConfigFactory.class);
    }
}
