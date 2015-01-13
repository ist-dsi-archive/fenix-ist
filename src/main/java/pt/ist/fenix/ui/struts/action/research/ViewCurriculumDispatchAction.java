/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Core.
 *
 * FenixEdu Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenix.ui.struts.action.research;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.thesis.Thesis;
import org.fenixedu.academic.domain.thesis.ThesisEvaluationParticipant;
import org.fenixedu.academic.domain.thesis.ThesisParticipationType;
import org.fenixedu.academic.ui.struts.action.base.FenixAction;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

import pt.ist.fenix.dto.ExecutionYearIntervalBean;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixedu.contracts.domain.organizationalStructure.PersonFunction;
import pt.ist.fenixframework.FenixFramework;

@StrutsFunctionality(app = CurriculumApp.class, path = "curriculum", titleKey = "link.viewCurriculum")
@Mapping(module = "researcher", path = "/viewCurriculum")
@Forwards(value = { @Forward(name = "Success", path = "/researcher/viewCurriculum.jsp") })
public class ViewCurriculumDispatchAction extends FenixAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String personId = request.getParameter("personOID");

        final Person person =
                ((personId != null && personId.length() > 0) ? (Person) FenixFramework.getDomainObject(personId) : getLoggedPerson(request));

        request.setAttribute("person", person);

        ExecutionYearIntervalBean bean = retrieveExecutionYearBeanFromRequest(request);

        ExecutionYear firstExecutionYear = bean.getFirstExecutionYear();
        ExecutionYear finalExecutionYear = bean.getFinalExecutionYear();

        ExecutionYear firstOfAll = ExecutionYear.readFirstExecutionYear();

        if (firstExecutionYear == null) {
            firstExecutionYear = firstOfAll;
        }
        if (finalExecutionYear == null || finalExecutionYear.isBefore(firstExecutionYear)) {
            finalExecutionYear = ExecutionYear.readLastExecutionYear();
        }

        bean.setFinalExecutionYear(finalExecutionYear);
        bean.setFirstExecutionYear(firstExecutionYear);

        putInformationOnRequestForGivenExecutionYear(firstExecutionYear, finalExecutionYear, person, request);

        return mapping.findForward("Success");
    }

    private ExecutionYearIntervalBean retrieveExecutionYearBeanFromRequest(HttpServletRequest request) {
        IViewState viewState = RenderUtils.getViewState("executionYearIntervalBean");
        ExecutionYearIntervalBean bean =
                (viewState != null) ? (ExecutionYearIntervalBean) viewState.getMetaObject().getObject() : new ExecutionYearIntervalBean();
        request.setAttribute("executionYearIntervalBean", bean);
        RenderUtils.invalidateViewState("executionYearIntervalBean");
        return bean;
    }

    private void putInformationOnRequestForGivenExecutionYear(ExecutionYear firstExecutionYear,
            ExecutionYear finaltExecutionYear, Person person, HttpServletRequest request) {

        SortedSet<ExecutionCourse> lectures =
                new TreeSet<ExecutionCourse>(new ReverseComparator(
                        ExecutionCourse.EXECUTION_COURSE_COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME));
        Set<Thesis> orientedThesis = new HashSet<Thesis>();
        Set<PersonFunction> functions = new HashSet<PersonFunction>();

        ExecutionYear stoppageYear = finaltExecutionYear.getNextExecutionYear();
        ExecutionYear iteratorYear = firstExecutionYear;
        Teacher teacher = person.getTeacher();

        while (iteratorYear != stoppageYear) {

            if (teacher != null) {
                lectures.addAll(teacher.getLecturedExecutionCoursesByExecutionYear(iteratorYear));
            }

            orientedThesis.addAll(getOrientedOrCoorientedThesis(iteratorYear, person));

            functions.addAll(PersonFunction.getPersonFuntions(person, iteratorYear.getBeginDateYearMonthDay(),
                    iteratorYear.getEndDateYearMonthDay()));
            iteratorYear = iteratorYear.getNextExecutionYear();
        }

        List<PersonFunction> functionsList = new ArrayList<PersonFunction>(functions);
        Collections.sort(functionsList, new ReverseComparator(new BeanComparator("beginDateInDateType")));
        request.setAttribute("functions", functionsList);

        request.setAttribute("lectures", lectures);
        request.setAttribute("orientedThesis", orientedThesis);
        if (!(orientedThesis.isEmpty())) {
            request.setAttribute("secondCycleThesis", true);
        }
    }

    public Set<Thesis> getOrientedOrCoorientedThesis(final ExecutionYear year, final Person person) {
        final Set<Thesis> thesis = new HashSet<Thesis>();
        for (final ThesisEvaluationParticipant participant : person.getThesisEvaluationParticipantsSet()) {
            if (participant.getThesis().getEnrolment().getExecutionYear().equals(year)
                    && (participant.getType() == ThesisParticipationType.ORIENTATOR || participant.getType() == ThesisParticipationType.COORIENTATOR)) {
                thesis.add(participant.getThesis());
            }
        }
        return thesis;
    }

}