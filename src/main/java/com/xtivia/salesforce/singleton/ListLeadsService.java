package com.xtivia.salesforce.singleton;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.xtivia.salesforce.util.LeadSearchUtil;
import com.xtivia.salesforce.util.LeadsUtil;


@SuppressWarnings({ "unchecked", "rawtypes" })
@Path("/leadslist")
public class ListLeadsService {

    private static final Log log = LogFactoryUtil.getLog(ListLeadsService.class);

    @GET
    @Path("/showall")
    @Produces("application/json")
    public Response getAllLeads(@Context HttpServletRequest httpServletRequest, @QueryParam("plidParam") String plid, @QueryParam("portletIdParam") String portletId) throws Exception {
    	ResponseBuilder builder;
    	long userId = Long.parseLong(httpServletRequest.getRemoteUser());
    	PrincipalThreadLocal.setName(userId);
    	
    	User user = UserLocalServiceUtil.getUserById(userId);
    	
    	PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user);
    	boolean hasPermission = LayoutPermissionUtil.contains(permissionChecker, Long.parseLong(plid), ActionKeys.VIEW);
    	javax.portlet.PortletPreferences jPreferences = LeadsUtil.getPreferences(plid, portletId);
    	if(hasPermission) {
	    	String accessToken = LeadsUtil.getAccessTokenFromPreferences(jPreferences);
	    	String serviceUrl = jPreferences.getValue(LeadsUtil.SERVICE_URL, StringPool.BLANK);
	        try {
	            if (log.isDebugEnabled()) {
	                log.debug("About to fetch the list of leads. Using an empty search criteria and the search API to fetch the list.");
	            }
	            builder = Response.ok(LeadSearchUtil.listLeads(serviceUrl, accessToken));
	        } catch (Exception e) {
	            log.error("Failed to get a list of leads using the empty search criteria.", e);
	            builder = Response.serverError().entity("Failed to get a list of leads.");
	        }
    	} else {
    		builder = Response.serverError().entity("You do not have permission to access. Contact admin.");
    	}
        return builder.build();
    }
}
