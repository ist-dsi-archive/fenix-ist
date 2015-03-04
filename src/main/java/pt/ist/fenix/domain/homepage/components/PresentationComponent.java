/**
 * Copyright © ${project.inceptionYear} Instituto Superior Técnico
 *
 * This file is part of Fenix IST.
 *
 * Fenix IST is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fenix IST is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Fenix IST.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenix.domain.homepage.components;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.contacts.PartyContact;
import org.fenixedu.academic.domain.contacts.PartyContactType;
import org.fenixedu.academic.domain.contacts.WebAddress;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import pt.ist.fenix.domain.homepage.HomepageSite;
import pt.ist.fenixedu.contracts.domain.Employee;
import pt.ist.fenixedu.contracts.domain.organizationalStructure.Contract;
import pt.ist.fenixedu.contracts.domain.organizationalStructure.ResearchUnit;

@ComponentType(name = "Presentation Component", description = "Provides homepage owner's presentation data.")
public class PresentationComponent extends HomepageSiteComponent {

    @Override
    public void handle(Page page, TemplateContext local, TemplateContext global) {
        HomepageSite site = site(page);
        Person owner = owner(page);

        global.put("ownerName", owner.getProfile().getDisplayName());

        if (site.getShowPhoto()) {
            global.put("ownerAvatarUrl", owner.getUser().getProfile().getAvatarUrl());
        }

        Employee employee = owner.getEmployee();
        if (employee != null) {
            Contract contract = owner.getEmployee().getCurrentWorkingContract();
            if (contract != null) {
                if (site.getShowUnit()) {
                    global.put("workingUnit", contract.getWorkingUnit());
                }
            }
        }
        if (site.getShowCategory() && owner.getTeacher() != null && owner.getTeacher().isActiveContractedTeacher()
                && owner.getTeacher().getCategory() != null) {
            global.put("teacherCategory", owner.getTeacher().getCategory().getName().getContent());
        }

        if (site.getShowResearchUnitHomepage()) {
            List<ResearchUnit> researchUnits = ResearchUnit.getWorkingResearchUnits(owner);
            if (researchUnits.isEmpty()) {
                if (owner.getTeacher() != null && owner.getEmployee() != null
                        && owner.getEmployee().getCurrentWorkingContract() != null) {
                    global.put("researchUnitName", site.getResearchUnitName());
                    global.put("researchUnitHomepage", site.getResearchUnitHomepage());
                }
            } else {
                global.put("workingResearchUnits", researchUnits);
            }
        }

        if (site.getShowActiveStudentCurricularPlans()) {
            global.put("activeCurricularPlans", owner.getActiveStudentCurricularPlansSortedByDegreeTypeAndDegreeName());
        }

        if (site.getShowCurrentAttendingExecutionCourses()) {
            global.put(
                    "attendingCourses",
                    owner.getCurrentAttends().stream().sorted(Attends.ATTENDS_COMPARATOR_BY_EXECUTION_COURSE_NAME)
                            .collect(Collectors.toList()));
        }

        if (site.getShowAlumniDegrees()) {
            global.put("completedCurricularPlans", getCompletedStudentCurricularPlansSortedByDegreeTypeAndDegreeName(owner));
        }

        global.put("emails", getSortedFilteredContacts(owner.getEmailAddresses()));
        global.put("personalPhones", getSortedFilteredContacts(owner.getPhones(), PartyContactType.PERSONAL));
        global.put("workPhones", getSortedFilteredContacts(owner.getPhones(), PartyContactType.WORK));
        global.put("mobilePhones", getSortedFilteredContacts(owner.getMobilePhones()));
        global.put(
                "websites",
                getSortedFilteredContacts(owner.getWebAddresses()).stream().map(WebAddress.class::cast)
                        .filter(addr -> !addr.getUrl().equals(page.getSite().getFullUrl())).collect(Collectors.toList()));

        if (site.getShowCurrentExecutionCourses() && owner.getTeacher() != null && owner.getEmployee() != null
                && owner.getEmployee().getCurrentWorkingContract() != null) {
            global.put("teachingCourses", owner.getTeacher().getCurrentExecutionCourses());
        }

        //TODO Unit, ResearchUnit, PartyContact, StudentCurricularPlan, Attends and ExecutionCourse wrappers

    }

    private static SortedSet<StudentCurricularPlan> getCompletedStudentCurricularPlansSortedByDegreeTypeAndDegreeName(Person owner) {
        final SortedSet<StudentCurricularPlan> studentCurricularPlans =
                new TreeSet<StudentCurricularPlan>(
                        StudentCurricularPlan.STUDENT_CURRICULAR_PLAN_COMPARATOR_BY_DEGREE_TYPE_AND_DEGREE_NAME);

        for (final Registration registration : owner.getStudentsSet()) {
            if (registration.isConcluded()) {
                final StudentCurricularPlan lastStudent = registration.getLastStudentCurricularPlan();
                if (lastStudent != null) {
                    studentCurricularPlans.add(lastStudent);
                }
            }
        }
        return studentCurricularPlans;
    }

    private List<PartyContact> getSortedFilteredContacts(Collection<? extends PartyContact> unfiltered, PartyContactType... types) {
        List<PartyContactType> typeList = types.length == 0 ? asList(PartyContactType.values()) : asList(types);

        return unfiltered.stream().filter(PartyContact::isVisible).filter(contact -> typeList.contains(contact.getType()))
                .sorted(PARTY_CONTACT_COMPARATOR).map(PartyContact.class::cast).collect(Collectors.toList());
    }

    private static Comparator<PartyContact> PARTY_CONTACT_COMPARATOR = (contact1, contact2) -> {
        if (contact1.getType().ordinal() > contact2.getType().ordinal()) {
            return -1;
        } else if (contact1.getType().ordinal() < contact2.getType().ordinal()) {
            return 1;
        } else if (contact1.getDefaultContact()) {
            return -1;
        } else if (contact2.getDefaultContact()) {
            return 1;
        } else {
            return contact1.getPresentationValue().compareTo(contact2.getPresentationValue());
        }
    };

}
