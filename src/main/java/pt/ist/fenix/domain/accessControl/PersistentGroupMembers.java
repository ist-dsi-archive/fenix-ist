/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenix.domain.accessControl;

import static org.fenixedu.academic.predicate.AccessControl.check;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.util.email.Recipient;
import org.fenixedu.academic.domain.util.email.UnitBasedSender;
import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenix.domain.UnitFile;
import pt.ist.fenix.predicate.PersistentGroupMembersPredicates;
import pt.ist.fenixframework.Atomic;

public class PersistentGroupMembers extends PersistentGroupMembers_Base {

    public PersistentGroupMembers(String name, PersistentGroupMembersType type) {
//        check(this, PersistentGroupMembersPredicates.checkPermissionsToManagePersistentGroups);
        super();
        setRootDomainObject(Bennu.getInstance());
        setName(name);
        setType(type);
        checkIfPersistenGroupAlreadyExists(name, type);
    }

    public void edit(String name, PersistentGroupMembersType type) {
        check(this, PersistentGroupMembersPredicates.checkPermissionsToManagePersistentGroups);
        setName(name);
        setType(type);
        checkIfPersistenGroupAlreadyExists(name, type);
    }

    @Override
    public void setUnit(Unit unit) {
        super.setUnit(unit);
        updateUnitSenders();
    }

    public void delete() {
        check(this, PersistentGroupMembersPredicates.checkPermissionsToManagePersistentGroups);
        if (getMembersLinkGroup() != null) {
            throw new DomainException("error.persistentGroupMembers.cannotDeletePersistentGroupMembersUsedInAccessControl");
        }
        getPersonsSet().clear();
        if (getUnit() != null) {
            MembersLinkGroup group = MembersLinkGroup.get(this);
            for (UnitFile file : getUnit().getFilesSet()) {
                file.updatePermissions(group);
            }
        }
        setUnit(null);
        setRootDomainObject(null);
        deleteDomainObject();
    }

    public void setNewPersonToMembersList(Person person) {
        check(this, PersistentGroupMembersPredicates.checkPermissionsToManagePersistentGroups);
        if (person == null) {
            throw new DomainException("error.PersistentGroupMembers.empty.person");
        }
        addPersons(person);
    }

    @Override
    public void removePersons(Person person) {
        check(this, PersistentGroupMembersPredicates.checkPermissionsToManagePersistentGroups);
        super.removePersons(person);
    }

    // This method is only used for Renderers
    public Person getNewPersonToMembersList() {
        return null;
    }

    @Override
    public void setName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new DomainException("error.PersistentGroupMembers.empty.name");
        }
        super.setName(name);
    }

    @Override
    public void setType(PersistentGroupMembersType type) {
        if (type == null) {
            throw new DomainException("error.PersistentGroupMembers.empty.type");
        }
        super.setType(type);
    }

    private void checkIfPersistenGroupAlreadyExists(String name, PersistentGroupMembersType type) {
        Collection<PersistentGroupMembers> persistentGroupMembers = Bennu.getInstance().getPersistentGroupMembersSet();
        for (PersistentGroupMembers persistentGroup : persistentGroupMembers) {
            if (!persistentGroup.equals(this) && persistentGroup.getName().equalsIgnoreCase(name)
                    && persistentGroup.getType().equals(type)) {
                throw new DomainException("error.PersistentGroupMembers.group.already.exists");
            }
        }
    }

    @Atomic
    private void updateUnitSenders() {
        for (UnitBasedSender sender : getUnit().getUnitBasedSenderSet()) {
            for (PersistentGroupMembers group : getUnit().getPersistentGroupsSet()) {
                if (!hasRecipientWithToName(sender, group.getName())) {
                    sender.addRecipients(new Recipient(null, MembersLinkGroup.get(group)));
                }
            }
            for (Recipient recipient : sender.getRecipientsSet()) {
                if (recipient.getMembers() instanceof MembersLinkGroup) {
                    if (!hasRecipientWithToName(sender, recipient.getToName())) {
                        if (recipient.getMessagesSet().isEmpty()) {
                            recipient.delete();
                        } else {
                            sender.removeRecipients(recipient);
                        }
                    }
                }
            }
        }
    }

    private boolean hasRecipientWithToName(UnitBasedSender sender, final String toName) {
        return sender.getRecipientsSet().stream().filter(r -> r.getToName().equals(toName)).findAny().isPresent();
    }
}
