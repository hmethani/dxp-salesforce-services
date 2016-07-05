package com.xtivia.salesforce.singleton;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.xtivia.salesforce.model.SearchCriteria;
import com.xtivia.salesforce.util.LeadSearchUtil;
import com.xtivia.salesforce.util.LeadsUtil;

@Path("/leads")
public class SearchLeadsService {

    private static final Log log = LogFactoryUtil.getLog(SearchLeadsService.class);
    
    @GET
    @Path("/search")
    public Response searchLeads(@QueryParam("name") String name, @QueryParam("email") String email, @QueryParam("company")String company) throws Exception {
    	
    	ResponseBuilder builder;
        try {
            String serviceUrl = LeadsUtil.SERVICE_URL_SESSION_KEY;
            String accessToken = LeadsUtil.getAccessToken();
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
