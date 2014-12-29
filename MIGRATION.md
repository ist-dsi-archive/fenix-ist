# Migration

This file contains the steps required to update from one version to another. The following sections represent the steps required to update from the previous to that version. 

## Migrating from 1.x to 2.0

```sql
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'pt.ist.fenix.domain.accessControl.PersistentGroupMembers' where DOMAIN_CLASS_NAME = 'org.fenixedu.academic.domain.accessControl.PersistentGroupMembers';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'pt.ist.fenix.domain.accessControl.PersistentMembersLinkGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.academic.domain.accessControl.PersistentMembersLinkGroup';

update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = replace(DOMAIN_CLASS_NAME, 'org.fenixedu.academic.domain.accounting.events.export', 'pt.ist.fenix.domain.accounting.events.export');

update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = replace(DOMAIN_CLASS_NAME, 'org.fenixedu.academic.domain.student.importation', 'pt.ist.fenix.domain.student.importation');

update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'pt.ist.fenix.domain.user.management.IstUsernameCounter' where DOMAIN_CLASS_NAME = 'pt.ist.fenix.user.management.IstUsernameCounter';

update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'pt.ist.fenix.domain.UnitFile' where DOMAIN_CLASS_NAME = 'org.fenixedu.academic.domain.UnitFile';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'pt.ist.fenix.domain.UnitFileTag' where DOMAIN_CLASS_NAME = 'org.fenixedu.academic.domain.UnitFileTag';
```