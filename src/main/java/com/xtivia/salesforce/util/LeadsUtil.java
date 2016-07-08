package com.xtivia.salesforce.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;

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

public class LeadsUtil {

    public static final String SERVICE_URL      = "serviceUrl";
    public static final String CLIENT_ID      = "clientId";
    public static final String CLIENT_SECRET  = "clientSecret";
    public static final String USERNAME       = "username";
    public static final String PASSWORD       = "password";
    public static final String SECURITY_TOKEN = "securityToken";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String ACCESS_TOKEN_SESSION_KEY = "";
   // public static final String SERVICE_URL_SESSION_KEY = "https://na30.salesforce.com/services/data/v36.0";

    public static final String ERROR_MESSAGE = "errorMessage";
    private static final Log log = LogFactoryUtil.getLog(LeadsUtil.class);
    
    public static String getAccessTokenFromPreferences(PortletPreferences preferences) {
    	List<NameValuePair> params = grantTokenparamsFromPreferences(preferences);
    	String accessToken = null;
		try {
			accessToken = fetchAccessToken(params);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
    	return accessToken;
    }
    private static List<NameValuePair> grantTokenparamsFromPreferences(PortletPreferences preferences) {
        String clientId = preferences.getValue(LeadsUtil.CLIENT_ID, "");
        String clientSecret = preferences.getValue(LeadsUtil.CLIENT_SECRET, "");
        String username = preferences.getValue(LeadsUtil.USERNAME, "");
        String password = preferences.getValue(LeadsUtil.PASSWORD, "");
        String securityToken = preferences.getValue(LeadsUtil.SECURITY_TOKEN, "");

        if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(clientSecret) || StringUtils.isEmpty(username)
                || StringUtils.isEmpty(password) || StringUtils.isEmpty(securityToken)) {
            throw new IllegalStateException("The required OAuth configuration or credentials have not been configured in the portlet.");
        }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password + securityToken));
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
    
    public static PortletPreferences getPreferences(String plid, String portletId) {
    	List<com.liferay.portal.kernel.model.PortletPreferences> portletPreferences = PortletPreferencesLocalServiceUtil.getPortletPreferences(Long.parseLong(plid), portletId);
    	PortletPreferences jPreference = null;
    	if(Validator.isNotNull(portletPreferences) && portletPreferences.size()>0) {
    		com.liferay.portal.kernel.model.PortletPreferences preference = portletPreferences.get(0);
    		jPreference  = PortletPreferencesLocalServiceUtil.getPreferences(preference.getCompanyId(), preference.getOwnerId(), preference.getOwnerType(), preference.getPlid(), portletId);
    	}
    	return jPreference;
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
