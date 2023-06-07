---
layout: default
search_title: "LDAP"
description: "LDAP and Active Directory Setup and Migration"
---

# LDAP / ADLDAP (Active Directory) Configuration

LDAP or ADLDAP (Active Directory) can be enabled by adding the appropriate properties to your conf file.

```
irida.administrative.authentication.mode=ldap
```
or 
```
irida.administrative.authentication.mode=adldap
```

The default value is `local` and the authentication is handled by `bcrypt`

LDAP and ADLDAP require different configuration properties.

These lines contain example values, your ldap/adldap configuration will vary.
```
# LDAP config
irida.administrative.authentication.ldap.url=ldap://localhost:10389/
irida.administrative.authentication.ldap.base=dc=example,dc=com
irida.administrative.authentication.ldap.userdn=uid=admin,ou=system
irida.administrative.authentication.ldap.password=secret
irida.administrative.authentication.ldap.userdn_search_patterns=uid={0},ou=people
# Optional Argument. change this to 'follow' to handle referrals. See AbstractContextSource javadocs for more info
irida.administrative.authentication.ldap.set_referral=ignore

# Active Directory LDAP config
irida.administrative.authentication.adldap.url=ldaps://some.adldapurl.ca
# Optional Argument. Default `(&(objectClass=user)(userPrincipalName={0}))`
irida.administrative.authentication.adldap.searchfilter=
# Optional Argument, Can be empty depending on your Active Directory configuration
irida.administrative.authentication.adldap.rootdn=ou=USERS,ou=Group,dc=ADLDAPurl,dc=ca
# Optional Argument, Can be empty depending on your Active Directory configuration
irida.administrative.authentication.adldap.domain=adldapurl.ca
```

When LDAP/ADLDAP is enabled, IRIDA automatically creates a new user when they sign in for the first time. Additionally when Users sign in, some of their local user fields are updated to sync them to the values on the ldap/adldap server.

The following lines are required for both LDAP and ADLAP.

These lines contain example values, your ldap/adldap configuration will vary.

```
irida.administrative.authentication.ldap.userInfoEmail=mail
irida.administrative.authentication.ldap.userInfoFirstName=givenName
irida.administrative.authentication.ldap.userInfoLastName=sn
irida.administrative.authentication.ldap.userInfoPhoneNumber=telephoneNumber
```

# Use

When configured, users can sign in via their ldap/adldap credentials

Signing in for the first time will create a user account in the IRIDA system. These users can be seen via the admin panel, but are managed via ldap/adldap. If a user needs to have their password changed, fields updated or access revoked, this must be done via ldap/adldap.

Local users, including admin users and sequencer users are managed by the default model. 

When login issues occur, appropriate error messages display to indicate if authentication failed via ldap/adldap, server connectivity, or local authentication failed.

# Verify Authentication

To verify that authentication is working as expected, you should create a ldap/adldap user and attempt sign in.

You should be able to sign in and a new IRIDA user account will be created for you.

# Migration

Migration of local user accounts to ldap/adldap authentication requires you to manually update IRIDA's SQL `user` and `user_AUD` tables.

Please read these sections carefully.

### Automatic Changes:

When IRIDA is updated to the latest version, an additional column `user_type` is added to `user` and `user_AUD` SQL tables.

The `user_type` column is not `nullable` and accepts the values `TYPE_LOCAL` or `TYPE_DOMAIN`.
* `TYPE_LOCAL`: Authentication will be handled by the default `bcrypt` authentication
* `TYPE_DOMAIN`: Authentication will be handled by the configured ldap/adldap authentication

The `user_type` column for all existing `user` and `user_AUD` rows are automatically set to `TYPE_LOCAL` during the version update.

### Verify and/or Change `username` Fields.

To migrate these existing accounts, first verify that the `username` field needs to match the configured search pattern identifier

Using the above configuration examples: 
* LDAP: `uid={0},ou=people` will expect the local `username` value to match the ldap `uid` field.
* ADLDAP: `(&(objectClass=user)(userPrincipalName={0}))` will expect the local `username` value to match the adldap `userPrincipalName` field

These fields will vary depending on your ldap/adldap configuration

For each of the accounts that do not match, you will need to update the `username` column on both the `user` and `user_AUD` SQL tables.

### Switching Users to LDAP/ADLDAP

Migrating existing local users to domain authentication is done by changing the `user_type` field to `TYPE_DOMAIN` on both the `user` and `user_AUD` table.

The sequencer user accounts, denoted by `system_role="ROLE_SEQUENCER"` on the `user`/`user_AUD` tables should not be migrated.

### Note: The original Administrator user `admin` should not be changed to `TYPE_DOMAIN`