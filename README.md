# dxp-salesforce-services

This is a salesforce service portlet, developed for liferay dxp.

It is developed to expose rest services for dxp-salesforce-lead portlet.

To configure this, you will have to follow these steps :

1). Deploy this portlet on liferay dxp.

2). Go to Control Panel -> Configuration -> System Settings -> Foundation and then select CXF Endpoints from table.

3). Add CXF Enpoint with context path '/rest'

4). To configure a REST extender with the Control Panel, first go to Control Panel → Configuration → System Settings → Foundation. Then select REST Extender from the table

5). Add Rest Extender with context path '/rest'

6). Next to test these services you would require to deploy dxp-salesforce-lead portlet.

These services have a permission check too, you need to pass paramPlid, results will be displayed only if user has permission to view the layout.

Also, these servcies read data from portlet configurations of dxp-salesforce-lead portlet, so portlet instance id will be passed to read configurations.
