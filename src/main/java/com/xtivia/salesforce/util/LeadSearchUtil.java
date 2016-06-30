package com.xtivia.salesforce.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.xtivia.salesforce.model.Lead;
import com.xtivia.salesforce.model.SearchCriteria;
import com.xtivia.salesforce.singleton.exceptions.FailedSearchException;

@SuppressWarnings("rawtypes")
public class LeadSearchUtil {

    private static final Log log = LogFactoryUtil.getLog(LeadSearchUtil.class);

    public static List<Lead> listLeads(String baseUrl, String accessToken) throws IOException {
        return searchLeads(baseUrl, accessToken, SearchCriteria.EMPTY_CRITERIA);
    }

    public static List<Lead> searchLeads(String baseUrl, String accessToken, SearchCriteria criteria) throws IOException {
        return salesForceApiCall(String.format("%s/query", baseUrl), accessToken, criteria.searchQuery());
    }

    private static List<Lead> salesForceApiCall(String leadUrl, String accessToken, String query) throws IOException {
        RequestBuilder builder = RequestBuilder.get(leadUrl).addParameter("q", query)
                .addHeader(new BasicHeader("Authorization", String.format("Bearer %s", accessToken)));
        CloseableHttpResponse response = HttpClients.createDefault().execute(builder.build());
        String body = EntityUtils.toString(response.getEntity());
        if (log.isDebugEnabled()) {
            log.debug(String.format("SalesForce Search URL: %s\nSearch Query: %s\nResponse from SalesForce: %s", leadUrl, query, body));
        }
        return convertToLeads(new ObjectMapper().readValue(body, Map.class));
    }

    private static List<Lead> convertToLeads(Map leadResponse) {
        if (!leadResponse.containsKey("done")) {
            throw new FailedSearchException("SalesForce Search did not return success.");
        }
        List<Lead> leads = leads(leadResponse);
        log.info(String.format("Successfully searched %s leads.", leads.size()));
        return leads;
    }

    @SuppressWarnings({ "unchecked" })
    private static List<Lead> leads(Map leadResponse) {
        List<Lead> leads = new ArrayList<Lead>();
        for (Map leadAttr : (List<Map>) leadResponse.get("records")) {
            leads.add(new Lead(val(leadAttr, "Id"), val(leadAttr, "Company"), val(leadAttr, "Name"),
                    val(leadAttr, "Status"), val(leadAttr, "Email"), val(leadAttr, "City")));
        }
        return leads;
    }

    private static String val(Map attrs, String key) {
        return (String) attrs.get(key);
    }
}
