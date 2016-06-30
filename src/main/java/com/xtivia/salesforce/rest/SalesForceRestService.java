package com.xtivia.salesforce.rest;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.liferay.portal.kernel.service.UserLocalService;
import com.xtivia.salesforce.singleton.LeadDetailsService;
import com.xtivia.salesforce.singleton.ListLeadsService;
import com.xtivia.salesforce.singleton.SearchLeadsService;

/*
 * A sample application to demonstrate implementing a JAX-RS endpoint in DXP
 */
@Component(immediate=true, property={"jaxrs.application=true"}, service=Application.class)
public class SalesForceRestService extends Application {
	
	/*
	 * Register our JAX-RS providers and resources
	 */

	@Override
	public Set<Object> getSingletons() {
		
		Set<Object> singletons = new HashSet<Object>();
		
		//add the automated Jackson marshaller for JSON
		singletons.add(new JacksonJsonProvider());
		
		// add our REST endpoints (resources)
		singletons.add(new LeadDetailsService());
		singletons.add(new SearchLeadsService());
		singletons.add(new ListLeadsService());
		
		return singletons;
	}
	
	/*
	 * Management of the Liferay UserLocalService class provided to us an OSGi service. 
	 */
	@Reference
	public void setUserLocalService(UserLocalService userLocalService) {
		this._userLocalService = userLocalService;
	}
	
	UserLocalService getUserLocalService() {
		return this._userLocalService;
	}

	private UserLocalService _userLocalService;
	
	/*
	 * This method demonstrates how you can perform logic when your bundle is activated/updated. For now we simply
	 * print a message to the console--this is particularly useful during update-style deployments.
	 */
	@Activate
	@Modified
	public void notifyWorld() {
		System.out.println("The salesforce DXP REST app has been activated/updated at " + new Date().toString());
	}
}