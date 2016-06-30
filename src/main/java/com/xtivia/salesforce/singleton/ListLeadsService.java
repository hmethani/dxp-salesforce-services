package com.xtivia.salesforce.singleton;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.xtivia.salesforce.util.LeadSearchUtil;
import com.xtivia.salesforce.util.LeadsUtil;


@SuppressWarnings({ "unchecked", "rawtypes" })
@Path("/leadslist")
public class ListLeadsService {

    private static final Log log = LogFactoryUtil.getLog(ListLeadsService.class);

    @GET
    @Path("/showall")
    @Produces("application/json")
    public Response getAllLeads() throws Exception {
    	ResponseBuilder builder;
        try {
            String serviceUrl = LeadsUtil.SERVICE_URL_SESSION_KEY;
            String accessToken = LeadsUtil.getAccessToken();
            if (log.isDebugEnabled()) {
                log.debug("About to fetch the list of leads. Using an empty search criteria and the search API to fetch the list.");
            }
            builder = Response.ok(LeadSearchUtil.listLeads(serviceUrl, accessToken));
        } catch (Exception e) {
            log.error("Failed to get a list of leads using the empty search criteria.", e);
            builder = Response.serverError().entity("Failed to get a list of leads.");
        }
        return builder.build();
    }
}
