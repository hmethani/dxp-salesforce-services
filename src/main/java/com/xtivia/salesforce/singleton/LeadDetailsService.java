package com.xtivia.salesforce.singleton;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.xtivia.salesforce.model.Lead;
import com.xtivia.salesforce.util.LeadsUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Path("/leads")
public class LeadDetailsService {

    private static final Log log = LogFactoryUtil.getLog(LeadDetailsService.class);
    
    @GET
	@Path("/details/{id}")
	@Produces("application/json")
    public Response getLeadsDetails(@PathParam("id") String id) throws Exception {
    	ResponseBuilder builder;
        try {
            String baseUrl = url();
            String accessToken = LeadsUtil.getAccessToken();
            //String id = (String) context.get("id");
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

    private String url() {
        String serviceUrl = LeadsUtil.SERVICE_URL_SESSION_KEY;
        return String.format("%s/sobjects/Lead/", serviceUrl);
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
