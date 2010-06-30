//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2006 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
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
/**
 * 
 */
package org.opennms.secret.service.impl;

import java.util.List;

import org.opennms.secret.dao.DataSourceDao;
import org.opennms.secret.model.DataSource;
import org.opennms.secret.model.InterfaceService;
import org.opennms.secret.model.Node;
import org.opennms.secret.model.NodeInterface;
import org.opennms.secret.service.DataSourceService;

/**
 * <p>DataSourceServiceImpl class.</p>
 *
 * @author Ted Kaczmarek
 * @version $Id: $
 * @since 1.6.12
 */
public class DataSourceServiceImpl implements DataSourceService {
	private DataSourceDao m_dataSourceDao;
	
	/**
	 * <p>setDataSourceDao</p>
	 *
	 * @param dataSourceDao a {@link org.opennms.secret.dao.DataSourceDao} object.
	 */
	public void setDataSourceDao(DataSourceDao dataSourceDao) {
		m_dataSourceDao = dataSourceDao;
	}
	
	/* (non-Javadoc)
	 * @see org.opennms.secret.service.DataSourceService#getDataSourcesByInterface(org.opennms.secret.model.NodeInterface)
	 */
	/** {@inheritDoc} */
	public List getDataSourcesByInterface(NodeInterface iface) {
		return m_dataSourceDao.getDataSourcesByInterface(iface);
	}

    /* (non-Javadoc)
     * @see org.opennms.secret.service.DataSourceService#getDataSourcesByService(org.opennms.secret.model.InterfaceService)
     */
    /** {@inheritDoc} */
    public DataSource getDataSourceByService(InterfaceService service) {
        return m_dataSourceDao.getDataSourceByService(service);
    }

	/* (non-Javadoc)
	 * @see org.opennms.secret.service.DataSourceService#getDataSourcesByNode(org.opennms.secret.model.Node)
	 */
	/** {@inheritDoc} */
	public List getDataSourcesByNode(Node node) {
		return m_dataSourceDao.getDataSourcesByNode(node);
    }
    
    /* (non-Javadoc)
     * @see org.opennms.secret.service.DataSourceService#getDataSourcesById(String)
     */
    /** {@inheritDoc} */
    public DataSource getDataSourceById(String id) {
        return m_dataSourceDao.getDataSourceById(id);
    }


}
