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
package pt.ist.fenix.ui.struts.action.research.researchUnit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.fenixedu.bennu.core.groups.UnionGroup;

import pt.ist.fenix.domain.UnitFile;
import pt.ist.fenix.domain.UnitFileTag;

public class UnitFileBean implements Serializable {

    private final UnitFile file;

    private String name;

    private String description;

    private Group group;

    private String tags;

    protected UnitFileBean() {
        this.file = null;
        group = NobodyGroup.get();
    }

    public UnitFileBean(UnitFile file) {
        this.file = file;
        this.name = file.getDisplayName();
        this.description = file.getDescription();
        this.group = file.getPermittedGroup();
        setupTags(file.getUnitFileTagsSet());
    }

    private void setupTags(Collection<UnitFileTag> unitFileTags) {
        String tags = "";
        int i = unitFileTags.size();
        for (UnitFileTag tag : unitFileTags) {
            tags += tag.getName();
            if (--i > 0) {
                tags += " ";
            }
        }
        setTags(tags);
    }

    public UnitFile getFile() {
        return file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Group getGroup() {
        return group;
    }

    public List<Group> getGroups() {
        if (group instanceof UnionGroup) {
            return new ArrayList<>(((UnionGroup) group).getChildren());
        }
        return Collections.singletonList(group);
    }

    public void setGroups(List<Group> groups) {
        group = UnionGroup.of(groups.stream());
    }

    public Unit getUnit() {
        return getFile().getUnit();
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

}
