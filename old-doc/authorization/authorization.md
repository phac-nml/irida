Security
========
Security in the API project is managed by Spring Security. The API project enforces security at the method-level, inspecting both the role and some more fine-grained access control of the principle attempting to execute the method.

Role-based Access Control
-------------------------
Role-base access control is one method of securing access used by the IRIDA API project. The system currently uses 3 roles, ROLE\_ADMIN for administrative users, ROLE\_USER for users, and ROLE\_CLIENT for non-human clients consuming the API.

The roles are hierarchical, so ROLE\_USER inherits the privileges of ROLE\_CLIENT and adds some privileges; ROLE\_ADMIN inherits the privieleges of ROLE\_USER and adds some privileges. To modify the definition of roles and their hierarchy, see the configuration file in ```ca.corefacility.bioinformatics.irida.config.applicationContext-security.xml```, specifically the ```roleHierarchy``` bean.

Custom Permissions
------------------
More granular permissions can be implemented using custom permission evaluators.

Custom permission evaluators must implement the interface ```ca.corefacility.bioinformatics.irida.security.permissions.evaluators.IridaPermissionsEvaluator.Permission```.

*IMPORTANT NOTE*: The permission evaluators *CANNOT* have dependencies injected by Spring that depend on Hibernate (see: http://stackoverflow.com/questions/12608212/spring-doesnt-see-transactional). For that reason, we recommend that if your custom permission requires more than access to the authenticated principle, it should implement the ```ApplicationContextAware``` interface and manually pull the required service bean out of the application context. Also note: you can't have these properties initialized in an initializer method that Spring executes, you *MUST* load the beans **outside** of the Spring initialization phase.
