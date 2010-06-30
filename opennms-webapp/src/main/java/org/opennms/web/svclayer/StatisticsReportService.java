/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2007-2008 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified 
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * Created: April 10, 2007 (dj)
 * Modifications:
 *
 * 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * For more information contact:
 *      OpenNMS Licensing       <license@opennms.org>
 *      http://www.opennms.org/
 *      http://www.opennms.com/
 */
package org.opennms.web.svclayer;

import java.util.List;

import org.opennms.netmgt.model.StatisticsReport;
import org.opennms.web.command.StatisticsReportCommand;
import org.opennms.web.svclayer.support.StatisticsReportModel;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

/**
 * Web service layer for statistics reports.
 *
 * @author <a href="mailto:dj@opennms.org">DJ Gregor</a>
 * @version $Id: $
 * @since 1.6.12
 */
@Transactional(readOnly=true)
public interface StatisticsReportService {
    /**
     * <p>getStatisticsReports</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<StatisticsReport> getStatisticsReports();

    /**
     * <p>getReport</p>
     *
     * @param command a {@link org.opennms.web.command.StatisticsReportCommand} object.
     * @param errors a {@link org.springframework.validation.BindException} object.
     * @return a {@link org.opennms.web.svclayer.support.StatisticsReportModel} object.
     */
    public StatisticsReportModel getReport(StatisticsReportCommand command, BindException errors);
}
