package com.xtivia.salesforce.singleton;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.xtivia.salesforce.model.Lead;
import com.xtivia.salesforce.util.LeadsUtil;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Path("/leads")
public class LeadDetailsService {

    private static final Log log = LogFactoryUtil.getLog(LeadDetailsService.class);
    
    @GET
	@Path("/details")
	@Produces("application/json")
    public Response getLeadsDetails(@Context HttpServletRequest httpServletRequest, @QueryParam("id") String id, @QueryParam("plidParam") String plid, @QueryParam("portletIdParam") String portletId) throws Exception {
    	ResponseBuilder builder;
    	long userId = Long.parseLong(httpServletRequest.getRemoteUser());
    	PrincipalThreadLocal.setName(userId);
    	
    	User user = UserLocalServiceUtil.getUserById(userId);
    	
    	PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user);
    	boolean hasPermission = LayoutPermissionUtil.contains(permissionChecker, Long.parseLong(plid), ActionKeys.VIEW);
    	javax.portlet.PortletPreferences jPreferences = LeadsUtil.getPreferences(plid, portletId);
    	String accessToken = LeadsUtil.getAccessTokenFromPreferences(jPreferences);
    	if(hasPermission) {
	        try {
	            String baseUrl = url(jPreferences.getValue(LeadsUtil.SERVICE_URL, StringPool.BLANK));
	            
	            if (log.isDebugEnabled()) {
	                log.debug(String.format("Fetching the details for lead: %s", id));
	            }
	            String body = getContent(String.format("%s%s", baseUrl, id), accessToken);
	            if (log.isDebugEnabled()) {
	                log.debug(String.format("SalesForce Search URL: %s\nResponse from SalesForce: %s", String.format("%s%s", baseUrl, id), body));
	            }
	            builder = Response.ok(leadFrom(responseAttrs(body)));
	        } catch (Exception e) {
	            log.error("There was an error while searching for leads.", e);
	            builder = Response.serverError().entity("Failed to fetch the leads object.");
	        }
	    } else {
			builder = Response.serverError().entity("You do not have permission to access. Contact admin.");
		}
	        return builder.build();
	    }

    private String getContent(String leadUrl, String accessToken) throws IOException, ClientProtocolException {
        RequestBuilder builder = RequestBuilder.get(leadUrl).addHeader(
                new BasicHeader("Authorization", String.format("Bearer %s", accessToken)));
        CloseableHttpResponse response = HttpClients.createDefault().execute(builder.build());
        return EntityUtils.toString(response.getEntity());
    }

    private Map responseAttrs(String body) throws IOException, JsonParseException, JsonMappingException {
        Map leadAttrs = new ObjectMapper().readValue(body, Map.class);
        if (!leadAttrs.containsKey("Id")) {
            log.error(String.format("Got the following response from SalesForce.\n%s", body));
            throw new RuntimeException("Failed to get lead details from SalesForce.");
        }
        return leadAttrs;
    }

    private String url(String url) {
        return String.format("%s/sobjects/Lead/", url);
    }

    private Lead leadFrom(Map leadAttr) {
        Lead lead = new Lead(val(leadAttr, "Id"), (String) leadAttr.get("Company"), (String) leadAttr.get("Name"),
                (String) leadAttr.get("Status"), (String) leadAttr.get("Email"), (String) leadAttr.get("City"));
        lead.setTitle(val(leadAttr, "Title"));
        lead.setPhone(val(leadAttr, "Phone"));
        lead.setMobilePhone(val(leadAttr, "MobilePhone"));
        setAddress(lead, leadAttr);
        lead.setDescription(val(leadAttr, "Description"));
        lead.setWebsite(val(leadAttr, "Website"));
        if (leadAttr.get("NumberOfEmployees") != null) {
            lead.setNumberOfEmployees(String.valueOf(leadAttr.get("NumberOfEmployees")));
        }
        return lead;
    }

    private static String val(Map attrs, String key) {
        return (String) attrs.get(key);
    }

    private static void setAddress(Lead lead, Map leadAttr) {
        Map<String, String> address = (Map<String, String>) leadAttr.get("Address");
        lead.setState(address.get("state"));
        lead.setCountry(address.get("country"));
    }
    
    
}
