package com.xtivia.salesforce.model;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(category = "foundation", scope = ExtendedObjectClassDefinition.Scope.SYSTEM	)

@Meta.OCD(
		factory = true,
		id = "com.xtivia.salesforce.model.SalesForceConfig",
		name = "Salesforce_Rest"
)		
public interface SalesForceConfig {

	@Meta.AD(description= "username", deflt="unknown", required=false)
	public String username();

}
