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
// Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
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

package org.opennms.web.alarm.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.opennms.netmgt.EventConstants;

/**
 * <p>BeforeLastEventTimeFilter class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.6.12
 */
public class BeforeLastEventTimeFilter extends Object implements Filter {
    /** Constant <code>TYPE="beforelasteventtime"</code> */
    public static final String TYPE = "beforelasteventtime";

    protected Date date;

    /**
     * <p>Constructor for BeforeLastEventTimeFilter.</p>
     *
     * @param date a {@link java.util.Date} object.
     */
    public BeforeLastEventTimeFilter(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Cannot take null parameters.");
        }

        this.date = date;
    }

    /**
     * <p>Constructor for BeforeLastEventTimeFilter.</p>
     *
     * @param epochTime a long.
     */
    public BeforeLastEventTimeFilter(long epochTime) {
        this(new Date(epochTime));
    }

    /**
     * <p>getSql</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSql() {
        return (" LASTEVENTTIME < to_timestamp(\'" + this.date.toString() + "\'," + EventConstants.POSTGRES_DATE_FORMAT + ")");
    }
    
    /**
     * <p>getParamSql</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getParamSql() {
        return (" LASTEVENTTIME < ?");
    }
    
    /** {@inheritDoc} */
    public int bindParam(PreparedStatement ps, int parameterIndex) throws SQLException {
    	ps.setTimestamp(parameterIndex, new java.sql.Timestamp(this.date.getTime()));
    	return 1;
    }

    /**
     * <p>getDescription</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return (TYPE + "=" + this.date.getTime());
    }

    /**
     * <p>getTextDescription</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTextDescription() {
        return ("date before \"" + this.date.toString() + "\"");
    }

    /**
     * <p>toString</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString() {
        return ("<BeforeLastEventTimeFilter: " + this.getDescription() + ">");
    }

    /**
     * <p>Getter for the field <code>date</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getDate() {
        return (this.date);
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        return (this.toString().equals(obj.toString()));
    }
}
