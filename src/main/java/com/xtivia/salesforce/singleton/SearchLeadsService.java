package com.xtivia.salesforce.singleton;


import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.xtivia.salesforce.model.SearchCriteria;
import com.xtivia.salesforce.util.LeadSearchUtil;
import com.xtivia.salesforce.util.LeadsUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/leads")
public class SearchLeadsService {

    private static final Log log = LogFactoryUtil.getLog(SearchLeadsService.class);
    
    @GET
    @Path("/search")
    public Response searchLeads(@QueryParam("name") String name, @QueryParam("email") String email, @QueryParam("company")String company, @Context HttpServletRequest httpServletRequest, @QueryParam("plidParam") String plid, @QueryParam("portletIdParam") String portletId) throws Exception {
    	
    	ResponseBuilder builder;
    	javax.portlet.PortletPreferences jPreferences = LeadsUtil.getPreferences(plid, portletId);
    	String accessToken = LeadsUtil.getAccessTokenFromPreferences(jPreferences);
    	String serviceUrl = jPreferences.getValue(LeadsUtil.SERVICE_URL, StringPool.BLANK);
        try {
            SearchCriteria criteria = new SearchCriteria(name, email, company);
            if (log.isDebugEnabled()) {
                log.debug(String.format("Searching for leads using criteria: %s", criteria));
            }
            builder = Response.ok(LeadSearchUtil.searchLeads(serviceUrl, accessToken, criteria));
        } catch (Exception e) {
            log.error("There was an error while searching for leads.", e);
            builder = Response.serverError().entity("Failed to fetch the leads object.");
        }
        return builder.build();
    }
}
