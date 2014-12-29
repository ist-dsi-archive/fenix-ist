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
package pt.ist.fenix.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.groups.Group;

import pt.ist.fenix.domain.accessControl.MembersLinkGroup;
import pt.ist.fenixframework.dml.runtime.RelationAdapter;

public class UnitFile extends UnitFile_Base {

    static {
        getRelationFileTagging().addListener(new RelationAdapter<UnitFileTag, UnitFile>() {

            @Override
            public void afterRemove(UnitFileTag tag, UnitFile file) {
                super.afterRemove(tag, file);
                if (file != null && tag != null && tag.getTaggedFilesSet().size() == 0) {
                    tag.delete();
                }
            }

        });
    }

    public UnitFile(Unit unit, Person person, String description, String tags, String filename, String displayName,
            byte[] content, Group group) {
        super();
        setUnit(unit);
        setUploader(person);
        setDescription(description);
        if (tags != null && tags.length() > 0) {
            setUnitFileTags(tags);
        }
        init(filename, displayName, content, group);
    }

    public void setUnitFileTags(String tag) {
        getUnitFileTagsSet().clear();
        String[] tagNames = tag.split("\\p{Space}+");
        for (String tagName : tagNames) {
            UnitFileTag unitFileTag = UnitFileTag.getUnitFileTag(getUnit(), tagName.trim());
            addUnitFileTags((unitFileTag != null) ? unitFileTag : new UnitFileTag(getUnit(), tagName.trim()));
        }
    }

    @Override
    public void delete() {
        if (isEditableByCurrentUser()) {
            setUnit(null);
            for (; !getUnitFileTagsSet().isEmpty(); getUnitFileTagsSet().iterator().next().removeTaggedFiles(this)) {
                ;
            }
            setUploader(null);
            super.delete();
        } else {
            throw new DomainException("error.cannot.delete.file");
        }
    }

    public boolean isEditableByUser(Person person) {
        return getUploader().equals(person);
    }

    public boolean isEditableByCurrentUser() {
        return isEditableByUser(AccessControl.getPerson());
    }

    public void updatePermissions(MembersLinkGroup group) {
        setPermittedGroup(getPermittedGroup().minus(group));
    }

    public boolean hasUnitFileTags(Collection<UnitFileTag> tags) {
        return getUnitFileTagsSet().containsAll(tags);
    }

    public static List<UnitFile> getAccessibileFiles(Unit unit, Person person) {
        List<UnitFile> files = new ArrayList<UnitFile>();
        for (UnitFile file : unit.getFilesSet()) {
            if (file.isPersonAllowedToAccess(person)) {
                files.add(file);
            }
        }
        return files;
    }

    public static List<UnitFile> getAccessibileFiles(Unit unit, Person person, Collection<UnitFileTag> tag) {
        List<UnitFile> files = new ArrayList<UnitFile>();
        for (UnitFile file : getAccessibileFiles(unit, person)) {
            if (file.hasUnitFileTags(tag)) {
                files.add(file);
            }
        }
        return files;
    }

    public static List<UnitFile> getAccessibileFiles(Person person, UnitFileTag tag) {
        List<UnitFile> files = new ArrayList<UnitFile>();
        if (tag != null) {
            for (UnitFile file : tag.getTaggedFilesSet()) {
                if (file.isPersonAllowedToAccess(person)) {
                    files.add(file);
                }
            }
        }
        return files;

    }

    public static List<UnitFile> getAccessibileFiles(Unit unit, Person person, String tagName) {
        return getAccessibileFiles(person, UnitFileTag.getUnitFileTag(unit, tagName));
    }
}
