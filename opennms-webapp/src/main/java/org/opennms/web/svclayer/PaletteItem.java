/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2006-2008 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * Modifications:
 * 
 * Created: July 18, 2006
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

/**
 * <p>PaletteItem class.</p>
 *
 * @author <a href="mailto:brozow@opennms.org">Mathew Brozowski</a>
 * @author <a href="mailto:dj@opennms.org">DJ Gregor</a>
 * @author <a href="mailto:brozow@opennms.org">Mathew Brozowski</a>
 * @author <a href="mailto:dj@opennms.org">DJ Gregor</a>
 * @version $Id: $
 * @since 1.6.12
 */
public class PaletteItem {
	
	/** Constant <code>SPACER</code> */
	public static final PaletteItem SPACER = new PaletteItem(null, null, true);
	
	private String m_label;
	private String m_id;
	private boolean m_spacer = false;
	
	/**
	 * <p>Constructor for PaletteItem.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 * @param label a {@link java.lang.String} object.
	 * @param spacer a boolean.
	 */
	protected PaletteItem(String id, String label, boolean spacer) {
		m_id = id;
		m_label = label;
		m_spacer = spacer;
	}
	
	/**
	 * <p>Constructor for PaletteItem.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 * @param label a {@link java.lang.String} object.
	 */
	public PaletteItem(String id, String label) {
		this(id, label, false);
	}
	
	/**
	 * <p>getLabel</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getLabel() {
		return m_label;
	}
	
	/**
	 * <p>getId</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getId() {
		return m_id;
	}
	
	/**
	 * <p>isSpacer</p>
	 *
	 * @return a boolean.
	 */
	public boolean isSpacer() {
		return m_spacer;
	}
	
	/**
	 * <p>toString</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() {
		if (isSpacer()) {
			return "SPACER";
		}
		else {
			return m_label+"<"+m_id+">";
		}
	}
	
}
