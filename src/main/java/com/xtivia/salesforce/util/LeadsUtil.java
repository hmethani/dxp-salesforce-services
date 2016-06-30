package com.xtivia.salesforce.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class LeadsUtil {

    public static final String SERVICE_URL      = "https://na30.salesforce.com/services/data/v36.0";
    public static final String CLIENT_ID      = "3MVG9uudbyLbNPZMETXtbyOTPABaFc108Zqj2cjg7rHxOi0w0eT65PJpzk5dXB6C2djgsO2_st.K9SAwcF9Sj";
    public static final String CLIENT_SECRET  = "120284533777135696";
    public static final String USERNAME       = "salesforce-portal-devs@xtivia.com";
    public static final String PASSWORD       = "Passw0rd";
    public static final String SECURITY_TOKEN = "Vyjv1i26365NP6zoSafPybanS";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String ACCESS_TOKEN_SESSION_KEY = "";
    public static final String SERVICE_URL_SESSION_KEY = "https://na30.salesforce.com/services/data/v36.0";

    public static final String ERROR_MESSAGE = "errorMessage";
    private static final Log log = LogFactoryUtil.getLog(LeadsUtil.class);
    
    
    public static String getAccessToken() {
    	List<NameValuePair> params = grantTokenparamsFromPreferences();
    	String accessToken = null;
		try {
			accessToken = fetchAccessToken(params);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
    	return accessToken;
    }
    
    private static List<NameValuePair> grantTokenparamsFromPreferences() {
        /*PortletPreferences preferences = renderRequest.getPreferences();
        String clientId = preferences.getValue(LeadsUtil.CLIENT_ID, "");
        String clientSecret = preferences.getValue(LeadsUtil.CLIENT_SECRET, "");
        String username = preferences.getValue(LeadsUtil.USERNAME, "");
        String password = preferences.getValue(LeadsUtil.PASSWORD, "");
        String securityToken = preferences.getValue(LeadsUtil.SECURITY_TOKEN, "");

        if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(clientSecret) || StringUtils.isEmpty(username)
                || StringUtils.isEmpty(password) || StringUtils.isEmpty(securityToken)) {
            throw new IllegalStateException("The required OAuth configuration or credentials have not been configured in the portlet.");
        } */

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("client_id", CLIENT_ID));
        params.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
        params.add(new BasicNameValuePair("username", USERNAME));
        params.add(new BasicNameValuePair("password", PASSWORD + SECURITY_TOKEN));
        if (log.isDebugEnabled()) {
            log.debug(String.format("Post Params:\n%s", ToStringBuilder.reflectionToString(params)));
        }
        return params;
    }
    
    private static String fetchAccessToken(List<NameValuePair> params) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("About to fetch access token");
        }
        HttpPost httpPost = new HttpPost("https://login.salesforce.com/services/oauth2/token");
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = HttpClients.createDefault().execute(httpPost);
        String body = EntityUtils.toString(response.getEntity());
        if (log.isDebugEnabled()) {
            log.debug(String.format("SalesForce Response:\nCode:%s\nBody:%s", response.getStatusLine().getStatusCode(), body));
        }
        Map<String, String> responseKeys = new ObjectMapper().readValue(body, Map.class);
        if (!responseKeys.containsKey(LeadsUtil.ACCESS_TOKEN)) {
            throw new SFAuthFailureException(ToStringBuilder.reflectionToString(responseKeys));
        }
        String accessToken = responseKeys.get(LeadsUtil.ACCESS_TOKEN);
        if (log.isDebugEnabled()) {
            log.debug(String
                    .format("Successfully obtained the accessToken: %s. This is a secure value. Make sure this is not showing up in production log.",
                            accessToken));
        }
        return accessToken;
    }
    
    /**
     * Understands representing an authentication error returned by SalesForce
     * 
     * @author Hitesh Methani
     * 
     */
    private static class SFAuthFailureException extends RuntimeException {

        private static final long serialVersionUID = -6228641469753971763L;

        public SFAuthFailureException(String message) {
            super(message);
        }
    }
}
