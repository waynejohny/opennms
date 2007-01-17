<%--
 
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
// Copyright (C) 2003 Networked Knowledge Systems, Inc.  All rights reserved.
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

--%> 

<%@page language="java" contentType="text/html" session="true" import="org.opennms.web.map.db.MapMenu,org.opennms.web.map.view.*, org.opennms.web.acegisecurity.Authentication"%>


<% String breadcrumb1 = java.net.URLEncoder.encode("Map"); %>
<jsp:include page="/includes/header.jsp" flush="false" >
  <jsp:param name="title" value="Map" />
  <jsp:param name="headTitle" value="Map" />
  <jsp:param name="location" value="map" />  
  <jsp:param name="breadcrumb" value="<%=breadcrumb1%>" />
</jsp:include>

<!-- Body -->
<script language="Javascript" type="text/javascript" >
	
	    function viewMap()
	    	    {
			  var vmf = document.getElementById("viewMapForm");
		        //alert(vmf);
		    	  var fullscreen=vmf.fullscreen.value;
  		    	  var refresh=vmf.refresh.value;	
  		    	  var dimension=vmf.dimension.value;
			
			  var scrollBarOffset=0;
			  var marginOffset=50;  		    	  
			  var width,height;
			  if(dimension=="auto")
				{

				if (navigator.appName=="Netscape") {
					  scrollBarOffset=16;
				}
				if (navigator.appName.indexOf("Microsoft")!=-1) {
					  scrollBarOffset=20;
				}
				//available pixels - 200(=menu width) - scrollbar offset (depending on browser)
                		var availWidth=screen.availWidth-200-scrollBarOffset-marginOffset;
				//var availHeight=screen.availableHeight;
				if(availWidth>=1600){
						width=1600;
						height=1200;
					}else if(availWidth>=1280){
						width=1280;
						height=1024;
						}else if(availWidth>=1024){
							width=1024;
							height=768;
							}else if(availWidth>=800){											
								width=800;
								height=600;
								}else if(availWidth>=640){											
									width=640;
									height=480;			
									}else{
									      width=400;
									      height=300;
									}
				dimension=width+"x"+height;
				}
				vmf.action="map/map.jsp?fullscreen="+fullscreen+"&refresh="+refresh+"&dimension="+dimension;				
			 <%if(!request.isUserInRole(Authentication.ADMIN_ROLE)){%>
				 var mapToOpen=vmf.mapToOpen.value;
				  if(mapToOpen==""){
					alert("Select the map to open.");
					vmf.mapToOpen.focus();
					return;
				  }
				  vmf.action+="&mapToOpen="+mapToOpen;
			<%}%>
	    		  vmf.submit();
	            }
</script>


  <div class="TwoColLeft">
      <h3>Mapping</h3>    
    <div class="boxWrapper">
          <form method="POST" id="viewMapForm">
            <p align="right">View "fullscreen":
            <select name="fullscreen">
                  <option value="y">Yes</option>
                  <option value="n" selected>No</option>
                </select>
            </p>
            <p align="right">Auto-refresh:
            <select name="refresh">
                  <option value="1">1 minute</option>
                  <option value="2">2 minutes</option>
                  <option value="3">3 minutes</option>
                  <option value="5" selected>5 minutes</option>
                  <option value="10">10 minutes</option>
                  <option value="15">15 minutes</option>
                </select>
            </p>
            <p align="right">Dimension:
            <select name="dimension">
		    <option value="auto" selected>Auto</option>
                  <option value="640x480">640x480</option>
                  <option value="800x600">800x600</option>
                  <option value="1024x768">1024x768</option>
                  <option value="1280x1024">1280x1024</option>
                  <option value="1600x1200">1600x1200</option>
                </select>
            </p>
          <%if( !request.isUserInRole(Authentication.ADMIN_ROLE)) {%>
            <p align="right">Open Map:
	    <select name="mapToOpen">
		  <option value="" selected></option>
		<%
			MapMenu[] maps = null;
			Manager m = new Manager();
			
			try{
      				maps = m.getAllMapMenus();
			}catch(Exception e){
				throw new ServletException(e);
				//do nothing, this exception is managed later
			}
			if(maps!=null){
				for(int k=0; k<maps.length; k++){
					%>		  
					  <option value="<%=maps[k].getId()%>"><%=maps[k].getName()%></option>
					<%
				} 
			}
			
            %>
            </select>
	   </p>          
          <%}%>
            <p align="right">
             <input type="button" value="View" onclick="viewMap()"/>
 	    </p>
          </form>
      </div>      
   </div>      


  <div class="TwoColRight">
  <h3>Mapping</h3>
     <div class="boxWrapper">
      <p>
         Mapping provides the management of maps representing the status (links beetwen nodes, status,
         availability ecc.) of a subset of nodes monitored by the system.
         You can create your personal view of the system, just creating a map containing the nodes you
         are interested to. 
       </p>
       
       <p>  
         Also, you can include one or more <i>map nodes</i> into your map. A <i>map node</i> is
         a map (previousely created) containing another subset (not compulsorily disjoined from your map)
         of nodes of system. By this instrument, you can monitor your system grouping nodes in costumized 
         way and easily to jump from a map to another with a double-click.
        </p>
        
        <p>
         At last, you can change the layout of your map, modifying its background, icons of the nodes 
         (by default, the icon corrensponding to the asset category of the nodes) etc.
         </p>
         
         <p>
         To view Maps, choose if view in fullscreen mode, the interval for refreshing node informations and the
         dimensions of the map frame (with <i>Auto</i> the system choose best dimensions for your browser). 
         Note that choosing wrong dimensions you could not visualize correctly maps.
         </p>
    </div>

  </div>
                                     
<hr />

    <jsp:include page="/includes/footer.jsp" flush="false" >
      <jsp:param name="location" value="map" />
    </jsp:include>

